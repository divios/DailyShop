package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.WeightedRandom;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.searchStockEvent;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.PlaceholderAPIWrapper;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.dTransaction.SingleTransaction;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.newDItem;
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

public class singleGuiImpl implements singleGui, Cloneable {

    protected static final DailyShop plugin = DailyShop.get();

    protected boolean isDestroyed = false;

    protected Player p;
    private final dShop shop;
    private dInventory own;
    private Set<Subscription> events = new HashSet<>();

    protected singleGuiImpl(Player p, dShop shop, singleGui base) {
        this(p, shop, base.getInventory());
    }

    protected singleGuiImpl(Player p, dShop shop, dInventory base) {
        this.p = p;
        this.shop = shop;
        this.own = base.clone();

        if (p != null) {
            updateTask();
            Schedulers.sync().runLater(() -> updatePool.subscribe(this), 1L);
            this.own.openInventory(p);
        } else
            ready();
    }

    private void ready() {
        events.add(
                Events.subscribe(searchStockEvent.class)                // Respond to search events
                        .filter(o -> o.getShop().equals(shop))
                        .handler(o -> {
                                    newDItem itemToSearch;
                                    if ((itemToSearch = own.getButtons().get(o.getUUID())) != null) {
                                        dStock stock;
                                        o.respond((stock = itemToSearch.getDStock()) == null ? -1 : stock.get(p));
                                    }
                                }
                        ));
        events.add(
                Events.subscribe(TransactionEvent.class)
                        .filter(o -> o.getCaller() == own)
                        .handler(o -> {
                            if (o.getType() == SingleTransaction.Type.BUY)
                                Transactions.createBuyType()
                                        .withShop(shop)
                                        .withVendor(o.getPlayer())
                                        .withItem(o.getItem())
                                        .execute();
                        })
        );

    }

    @Override
    public void updateItem(updateItemEvent o) {
        updateItemEvent.type type = o.getType();
        newDItem toUpdateItem = shop.getItem(o.getUuid());

        switch (type) {
            case UPDATE_ITEM:
                if (toUpdateItem == null) return;
                ItemStack newItem = shopItemsLore.applyLore(toUpdateItem, p, shop);
                toUpdateItem.setItem(newItem);
                DebugLog.info("Updated item from singleGui of id: " + toUpdateItem.getID());
                own.updateDailyItem(toUpdateItem);
                break;
            case NEXT_AMOUNT:
                if (toUpdateItem == null) return;
                newDItem buttonItem = own.buttons.get(toUpdateItem.getUUID());
                buttonItem.decrementStock(o.getPlayer(), o.getAmount());
                DebugLog.info("Decrement stock from singleGui of id: " + toUpdateItem.getID());
                break;
            case DELETE_ITEM:
                own.removeButton(o.getUuid());
                DebugLog.info("Deleted item from singleGui of id: " + o.getUuid());
                break;
            default:
                throw new UnsupportedOperationException("Invalid updateItemEvent type");
        }
    }

    @Override
    public void updateTask() {
        Set<Integer> dailySlots = own.dailyItemsSlots;
        Map<Integer, newDItem> buttons = own.buttonsSlot;

        own.buttonsSlot.forEach((integer, dItem) -> {
            if (dItem.isAir()) return;
            try {
                ItemStack oldItem;
                ItemBuilder newItem;
                if (dailySlots.contains(integer)) {
                    newDItem aux = shop.getItem(dItem.getUUID());
                    if (aux == null || buttons.get(integer) == null) return;
                    aux.setStock(buttons.get(integer).getDStock());   // Set the stock of the actual item
                    aux.setBuyPrice(buttons.get(integer).getDBuyPrice());  // Set buyPrice (randomPrice bug)
                    aux.setSellPrice(buttons.get(integer).getDSellPrice());  // Set sellPrice (randomPrice bug)
                    oldItem = shopItemsLore.applyLore(aux, p, shop);

                } else
                    oldItem = buttons.get(integer).getItemWithId();

                newItem = ItemBuilder.of(oldItem).setLore(Collections.emptyList());
                newItem = newItem.setName(PlaceholderAPIWrapper.setPlaceholders(p, ItemUtils.getName(oldItem)));

                for (String s : ItemUtils.getLore(oldItem))
                    newItem = newItem.addLore(PlaceholderAPIWrapper.setPlaceholders(p, s));

                own.getInventory().setItem(integer, newItem);
            } catch (Exception ignored) {
            }
        });

    }

    @Override
    public void restock() {
        Set<newDItem> newItems = dRandomItemsSelector.of(shop.getItems(), dItem -> {
                    ItemStack aux = shopItemsLore.applyLore(dItem, p, shop);
                    return dItem.setItem(aux);
                })
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
    public singleGui copy(Player p) {
        singleGuiImpl clone = clone();
        clone.p = p;
        if (p != null) clone.own.openInventory(p);

        return clone;
    }

    @Override
    public singleGui deepCopy(Player p) {
        singleGuiImpl clone = clone();
        clone.own = own.deepClone();
        clone.p = p;
        if (p != null) clone.own.openInventory(p);

        return clone;
    }

    @Override
    public void destroy() {
        if (isDestroyed) return;
        isDestroyed = true;
        events.forEach(Subscription::unregister);
        own.destroy();
        updatePool.unsubscribe(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        singleGuiImpl singleGui = (singleGuiImpl) o;
        return isDestroyed == singleGui.isDestroyed
                && Objects.equals(p, singleGui.p)
                && Objects.equals(shop, singleGui.shop)
                && Objects.equals(own, singleGui.own);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDestroyed, p, shop, own);
    }

    @Override
    public singleGuiImpl clone() {
        try {
            singleGuiImpl clone = (singleGuiImpl) super.clone();

            clone.own = own.clone();
            clone.events = new HashSet<>();
            clone.ready();
            updatePool.subscribe(clone);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Inner utility class to generate the daily Items
     */
    @SuppressWarnings("unused")
    private final static class dRandomItemsSelector {

        private static final Predicate<newDItem> filterItems = item ->
                !(item.getBuyPrice() <= 0 && item.getSellPrice() <= 0)
                        || item.getRarity().getWeight() != 0;

        private static final Function<newDItem, Integer> getWeights = dItem -> dItem.getRarity().getWeight();

        public static dRandomItemsSelector fromItems(Collection<newDItem> items) {
            return new dRandomItemsSelector(items, Function.identity());
        }

        public static dRandomItemsSelector of(Collection<newDItem> items, Function<newDItem, newDItem> action) {
            return new dRandomItemsSelector(items, action);
        }

        private final Map<UUID, newDItem> items;
        private final Function<newDItem, newDItem> action;

        private dRandomItemsSelector(Collection<newDItem> items, Function<newDItem, newDItem> action) {
            this(items.stream().collect(Collectors.toMap(newDItem::getUUID, dItem -> dItem)), action);
        }

        private dRandomItemsSelector(Map<UUID, newDItem> items, Function<newDItem, newDItem> action) {
            this.items = items.entrySet().stream()
                    .filter(entry -> filterItems.test(entry.getValue()))
                    .collect(Collectors
                            .toMap(Map.Entry::getKey, Map.Entry::getValue)
                    );
            this.action = action;
        }

        public void add(newDItem item) {
            items.put(item.getUUID(), item);
        }

        public newDItem remove(String id) {
            return remove(UUID.nameUUIDFromBytes(id.getBytes()));
        }

        public newDItem remove(UUID uuid) {
            return items.remove(uuid);
        }

        public Set<newDItem> getItems() {
            return Collections.unmodifiableSet(new HashSet<>(items.values()));
        }

        public Set<newDItem> roll() {
            return roll(54);
        }

        public Set<newDItem> roll(int max) {
            Set<newDItem> rolledItems = new LinkedHashSet<>();

            WeightedRandom<newDItem> randomSelector = WeightedRandom.fromCollection(items.values(), newDItem::clone, getWeights::apply);

            for (int i = 0; i < max; i++) {
                newDItem rolledItem = randomSelector.roll();
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
