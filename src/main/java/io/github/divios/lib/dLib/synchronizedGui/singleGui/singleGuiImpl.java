package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.WeightedRandom;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.searchStockEvent;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.utils.PlaceholderAPIWrapper;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.synchronizedGui.taskPool.updatePool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that holds a {@link dInventory} for a unique player and
 * also its base.
 * <p>
 * Subscribes to the updatePool to update placeholders
 */

public class singleGuiImpl implements singleGui {

    protected static final DailyShop plugin = DailyShop.getInstance();
    private static final loreStrategy loreStrategy = new shopItemsLore();

    protected final Player p;
    private final dShop shop;
    private final dInventory own;
    private final Set<Subscription> events = new HashSet<>();

    protected singleGuiImpl(Player p, dShop shop, singleGui base) {
        this(p, shop, base.getInventory());
    }

    protected singleGuiImpl(Player p, dShop shop, dInventory base) {
        this.p = p;
        this.shop = shop;
        this.own = base.copy();

        if (p != null) {
            updateTask();
            updatePool.subscribe(this);
            this.own.openInventory(p);
        } else
            ready();
    }

    private void ready() {
        events.add(
                Events.subscribe(searchStockEvent.class)                // Respond to search events
                        .filter(o -> o.getShop().equals(shop))
                        .handler(o -> {
                                    dItem itemToSearch;
                                    if ((itemToSearch = own.getButtons().get(o.getUuid())) != null) {
                                        if (!itemToSearch.hasStock()) o.respond(-1);
                                        else o.respond(itemToSearch.getStock().get(o.getPlayer()));
                                    }
                                }
                        ));
    }

    @Override
    public synchronized void updateItem(updateItemEvent o) {
        dItem toUpdateItem = shop.getItem(o.getUuid()).get().clone();
        updateItemEvent.type type = o.getType();

        switch (type) {
            case UPDATE_ITEM:
                own.updateItem(toUpdateItem.applyLore(loreStrategy, p), false);
                break;
            case NEXT_AMOUNT:
                dStock stock = own.getButtons().get(toUpdateItem.getUid()).getStock();
                stock.decrement(o.getPlayer(), o.getAmount());
                if (stock.get(o.getPlayer()) <= 0) stock.set(o.getPlayer(), -1);
                toUpdateItem.setStock(stock);
                own.updateItem(toUpdateItem.applyLore(loreStrategy, p), false);
                break;
            case DELETE_ITEM:
                own.updateItem(toUpdateItem, true);
                break;
            default:
                throw new UnsupportedOperationException("Invalid updateItemEvent type");
        }
    }

    @Override
    public synchronized void updateTask() {
        loreStrategy strategy = new shopItemsLore();
        Map<Integer, dItem> buttons = own.getButtonsSlots();

        own.getDailyItemsSlots()
                .forEach(integer -> {
                    try {
                        dItem oldItem = shop.getItem(buttons.get(integer).getUid()).orElse(null);
                        if (oldItem == null || buttons.get(integer) == null) return;  // Avoid spam if somehow any error arise
                        oldItem = oldItem.clone();

                        oldItem.setStock(buttons.get(integer).getStock());   // Set the stock of the actual item

                        ItemBuilder newItem = ItemBuilder.of(oldItem.getItem().clone()).setLore(Collections.emptyList());
                        newItem = newItem.setName(PlaceholderAPIWrapper.setPlaceholders(p, ItemUtils.getName(newItem)));

                        for (String s : oldItem.getLore())
                            newItem = newItem.addLore(PlaceholderAPIWrapper.setPlaceholders(p, s));

                        own.getInventory().setItem(integer, strategy.applyLore(newItem, p));
                    } catch (Exception ignored) {
                    }
                });
    }

    @Override
    public synchronized void restock() {
        Set<dItem> newItems = dRandomItemsSelector.of(shop.getItems(), dItem -> dItem.applyLore(loreStrategy, p))
                .roll(own.dailyItemsSlots.size());
        own.restock(newItems);
    }

    @Override
    public Player getPlayer() {
        return p;
    }

    @Override
    public dInventory getInventory() {
        return own;
    }

    @Override
    public dShop getShop() {
        return shop;
    }

    @Override
    public synchronized void destroy() {
        events.forEach(Subscription::unregister);
        own.destroy();
        updatePool.unsubscribe(this);
    }

    @Override
    public synchronized int hash() {
        return Arrays.stream(own.getInventory().getContents())
                .mapToInt(value -> Utils.isEmpty(value) ? 0 : value.hashCode())
                .sum();
    }

    @Override
    public synchronized singleGui clone() {
        return new singleGuiImpl(p, shop, own);
    }


    /**
     * Inner utility class to generate the daily Items
     */
    private final static class dRandomItemsSelector {

        private static final Predicate<dItem> filterItems = item ->
                !(item.getBuyPrice().orElse(null).getPrice() < 0 &&
                        item.getSellPrice().orElse(null).getPrice() < 0) || item.getRarity().getWeight() != 0;

        private static final Function<dItem, Integer> getWeights = dItem -> dItem.getRarity().getWeight();

        private final Map<UUID, dItem> items;
        private final Function<dItem, dItem> action;

        public static dRandomItemsSelector fromItems(Set<dItem> items) {
            return new dRandomItemsSelector(items, Function.identity());
        }

        public static dRandomItemsSelector of(Set<dItem> items, Function<dItem, dItem> action) {
            return new dRandomItemsSelector(items, action);
        }

        private dRandomItemsSelector(Set<dItem> items, Function<dItem, dItem> action) {
            this(items.stream().collect(Collectors.toMap(dItem::getUid, dItem -> dItem)), action);
        }

        private dRandomItemsSelector(Map<UUID, dItem> items, Function<dItem, dItem> action) {
            this.items = items.entrySet().stream()
                    .filter(entry -> filterItems.test(entry.getValue()))
                    .collect(Collectors
                            .toMap(Map.Entry::getKey, Map.Entry::getValue)
                    );
            this.action = action;
        }

        public void add(dItem item) {
            items.put(item.getUid(), item);
        }

        public dItem remove(String id) {
            return remove(UUID.nameUUIDFromBytes(id.getBytes()));
        }

        public dItem remove(UUID uuid) {
            return items.remove(uuid);
        }

        public Set<dItem> getItems() {
            return Collections.unmodifiableSet(new HashSet<>(items.values()));
        }

        public Set<dItem> roll() {
            return roll(54);
        }

        public Set<dItem> roll(int max) {
            Set<dItem> rolledItems = new LinkedHashSet<>();

            WeightedRandom<dItem> randomSelector = WeightedRandom.fromCollection(items.values(), dItem::clone, getWeights::apply);

            for (int i = 0; i < max; i++) {
                dItem rolledItem = randomSelector.roll();
                if (rolledItem == null) break;

                rolledItem.generateNewBuyPrice();
                rolledItem.generateNewSellPrice();
                randomSelector.remove(rolledItem);
                rolledItems.add(action.apply(rolledItem));
            }

            return rolledItems;
        }
    }


}
