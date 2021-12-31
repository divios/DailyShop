package io.github.divios.lib.dLib.confirmMenu;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.CompareItemUtils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings({"ConstantConditions", "unused"})
public class sellConfirmMenu extends abstractConfirmMenu {

    private static final Map<UUID, List<ItemStack>> retrievedItemsCache = new ConcurrentHashMap<>();

    /*static {
        //createPlayerDeathListener();
        //createPlayerJoinListener();
    } */

    private static void createPlayerDeathListener() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(event -> retrievedItemsCache.containsKey(event.getEntity().getUniqueId()))
                .handler(event -> {
                    List<ItemStack> entry = retrievedItemsCache.get(event.getEntity().getUniqueId());
                    event.getDrops().addAll(entry);
                });
    }

    private static void createPlayerJoinListener() {
        Events.subscribe(PlayerJoinEvent.class)
                .filter(event -> retrievedItemsCache.containsKey(event.getPlayer().getUniqueId()))
                .handler(event -> retrieveCachedItems(event.getPlayer()));
    }

    private static void retrieveCachedItems(Player player) {
        List<ItemStack> removedItems = retrievedItemsCache.remove(player.getUniqueId());
        if (removedItems == null || removedItems.isEmpty()) return;
        removedItems.forEach(itemStack -> {
            if (!ItemUtils.isEmpty(itemStack))
                ItemUtils.give(player, itemStack);
        });
    }


    public static sellConfirmMenuBuilder builder() {
        return new sellConfirmMenuBuilder();
    }

    private boolean retrievedItemsFlag = false;
    private sellConfirmMenu(dShop shop, Player player, dItem item, Consumer<Integer> onCompleteAction, Runnable fallback) {
        super(shop, player, item, onCompleteAction, fallback);
    }

    @Override
    protected String getTitle() {
        return Lang.CONFIRM_GUI_SELL_NAME.getAsString(player);
    }

    @Override
    protected void removeAddedItems() {
        if (retrievedItemsFlag | nAddedItems == 0) return;
        retrieveItems(MAX_INVENTORY_ITEMS);
        retrievedItemsFlag = true;
        retrievedItemsCache.remove(player.getUniqueId());
    }

    @Override
    protected boolean addConditions(int quantity) {
        return countSimilarItems() >= quantity;
    }

    @Override
    protected boolean removeConditions(int quantity) {
        return nAddedItems >= quantity;
    }

    @Override
    protected void addItems(int quantity) {
        List<ItemStack> removedItems = confiscateItems(quantity);
        addToCache(removedItems);
    }

    @Override
    protected void removeItems(int quantity) {
        retrieveItems(quantity);
    }

    @Override
    protected String getConfirmName() {
        return Lang.CONFIRM_GUI_YES.getAsString(player);
    }

    @Override
    protected String getBackName() {
        return Lang.CONFIRM_GUI_NO.getAsString(player);
    }

    @Override
    protected void setMaxItems() {
        List<ItemStack> removedItems = confiscateItems(MAX_INVENTORY_ITEMS);
        removedItems.forEach(itemStack -> nAddedItems += itemStack.getAmount());
        addToCache(removedItems);
    }

    @Override
    protected double getItemPrice() {
        return item.getDSellPrice().orElse(dPrice.EMPTY()).getPriceForPlayer(player, shop, item.getID(), priceModifier.type.SELL);
    }

    private int countSimilarItems() {
        return ItemUtils.count(player.getInventory(), item.getRealItem(), CompareItemUtils::compareItems);
    }

    private void addToCache(List<ItemStack> items) {
        UUID playerUUID = player.getUniqueId();
        if (retrievedItemsCache.containsKey(playerUUID))
            retrievedItemsCache.get(playerUUID).addAll(items);
        else
            retrievedItemsCache.put(playerUUID, items);
    }

    private List<ItemStack> confiscateItems(int quantity) {
        ItemStack[] playerItems = player.getInventory().getContents();
        List<ItemStack> itemsRemoved = new ArrayList<>();

        for (ItemStack item : playerItems) {
            if (quantity <= 0) break;
            if (!CompareItemUtils.compareItems(item, this.item.getRealItem())) continue;

            if (item.getAmount() <= quantity) {
                quantity -= item.getAmount();
                itemsRemoved.add(item.clone());
                deleteItem(item);
            } else {
                itemsRemoved.add(ItemBuilder.of(item.clone()).setCount(quantity));
                item.setAmount(item.getAmount() - quantity);
                quantity = 0;
            }
        }
        return itemsRemoved;
    }

    private void retrieveItems(int quantity) {
        for (Iterator<ItemStack> iter = retrievedItemsCache.get(player.getUniqueId()).iterator(); iter.hasNext(); ) {
            ItemStack next = iter.next();

            int itemAmount = next.getAmount();
            if (itemAmount < quantity) {
                ItemUtils.give(player, next);
                quantity -= itemAmount;
                iter.remove();
            } else {
                ItemUtils.give(player, ItemBuilder.of(next).setCount(quantity));
                next.setAmount(next.getAmount() - quantity);
                break;
            }
        }
        removeEmptyListsFromCache();
    }

    private void removeEmptyListsFromCache() {
        retrievedItemsCache.entrySet().removeIf(uuidListEntry -> uuidListEntry.getValue().isEmpty());
    }

    public static final class sellConfirmMenuBuilder {
        private dShop shop;
        private Player player;
        private dItem item;
        private Consumer<Integer> onCompleteAction;
        private Runnable fallback;

        private sellConfirmMenuBuilder() {
        }

        public sellConfirmMenuBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public sellConfirmMenuBuilder withPlayer(Player player) {
            this.player = player;
            return this;
        }

        public sellConfirmMenuBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public sellConfirmMenuBuilder withOnCompleteAction(Consumer<Integer> onCompleteAction) {
            this.onCompleteAction = onCompleteAction;
            return this;
        }

        public sellConfirmMenuBuilder withFallback(Runnable fallback) {
            this.fallback = fallback;
            return this;
        }

        public sellConfirmMenu build() {
            return new sellConfirmMenu(shop, player, item, onCompleteAction, fallback);
        }
    }
}
