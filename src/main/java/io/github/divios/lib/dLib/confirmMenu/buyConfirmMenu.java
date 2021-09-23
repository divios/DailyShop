package io.github.divios.lib.dLib.confirmMenu;

import io.github.divios.core_lib.Events;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.CompareItemUtils;
import io.github.divios.dailyShop.utils.FutureUtils;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings({"ConstantConditions"})
public class buyConfirmMenu extends abstractConfirmMenu {

    private static final Map<UUID, cacheEntry> buyItemsCache = new ConcurrentHashMap<>();

    static {
        createPlayerDeathListener();
        createPlayerJoinListener();
    }

    private static void createPlayerDeathListener() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(event -> buyItemsCache.containsKey(event.getEntity().getUniqueId()))
                .handler(event -> {
                    cacheEntry entry = buyItemsCache.remove(event.getEntity().getUniqueId());
                    if (entry.getQuantity() > 0)
                        addDropsFromCache(event.getDrops(), entry);
                });
    }

    private static void createPlayerJoinListener() {
        Events.subscribe(PlayerJoinEvent.class)
                .filter(event -> buyItemsCache.containsKey(event.getPlayer().getUniqueId()))
                .handler(event -> {
                    cacheEntry entry = buyItemsCache.remove(event.getPlayer().getUniqueId());
                    if (entry.getQuantity() > 0)
                        entry.restore(event.getPlayer());
                });
    }

    private static void addDropsFromCache(List<ItemStack> drops, cacheEntry entry) {
        int quantity = entry.getQuantity();
        while (quantity > 64) {
            drops.add(ItemBuilder.of(entry.getItem()).setCount(64));
            quantity -= 64;
        }
        drops.add(ItemBuilder.of(entry.getItem()).setCount(quantity));
    }

    public static buyConfirmMenuBuilder builder() {
        return new buyConfirmMenuBuilder();
    }

    public static buyConfirmMenu create(dShop shop, Player player, dItem item, Consumer<Integer> onCompleteAction, Runnable fallback) {
        return new buyConfirmMenu(shop, player, item, onCompleteAction, fallback);
    }

    public buyConfirmMenu(dShop shop, Player player, dItem item, Consumer<Integer> onCompleteAction, Runnable fallback) {
        super(shop, player, item, onCompleteAction, fallback);
    }

    @Override
    protected String getTitle() {
        return plugin.configM.getLangYml().CONFIRM_GUI_BUY_NAME;
    }

    @Override
    protected void removeAddedItems() {
        if (!utils.playerIsOnline(player)) return;

        ItemStack[] playerItems = player.getInventory().getContents();
        for (ItemStack item : playerItems) {
            if (ItemUtils.isEmpty(item)) continue;
            if (isMarkedItem(item)) deleteItem(item);
        }
    }

    @Override
    protected boolean addConditions(int quantity) {
        return quantity <= getMinLimit();
    }

    @Override
    protected boolean removeConditions(int quantity) {
        return nAddedItems >= quantity;
    }

    @Override
    protected void addItems(int quantity) {
        ItemUtils.give(player, getMarkedItem(), quantity);
    }

    @Override
    protected void removeItems(int quantity) {
        ItemUtils.remove(player.getInventory(), getMarkedItem(), quantity, this::getRemoveComparison);
    }

    private boolean getRemoveComparison(ItemStack item1, ItemStack item2) {
        if (ItemUtils.isEmpty(item1) || ItemUtils.isEmpty(item2)) return false;
        if (!isMarkedItem(item2)) return false;
        return CompareItemUtils.compareItems(item1, item2);
    }

    @Override
    protected String getConfirmName() {
        return plugin.configM.getLangYml().CONFIRM_GUI_YES;
    }

    @Override
    protected String getBackName() {
        return plugin.configM.getLangYml().CONFIRM_GUI_NO;
    }

    @Override
    protected void setMaxItems() {
        int limit = getMinLimit();
        int nAddedItemsThisItit = 0;
        ItemStack markedItem = getMarkedItem();
        while (player.getInventory().addItem(markedItem).isEmpty()) {
            nAddedItems ++;
            nAddedItemsThisItit ++;
            if (nAddedItemsThisItit>= limit) break;
        }
    }

    @Override
    protected double getItemPrice() {
        return item.getBuyPrice().orElse(null).getPrice();
    }

    private int getMinLimit() {
        int stockLimit = getStockLimit();
        int balanceLimit = getBalanceLimit();
        int inventoryLimit = getPlayerInventoryLimit();
        return getMinimumValue(stockLimit, balanceLimit, inventoryLimit);
    }

    private int getMinimumValue(int... values) {
        int minValue = MAX_INVENTORY_ITEMS;

        for (int value : values)
            minValue = Math.min(minValue, value);

        return minValue;
    }

    private int getStockLimit() {
        return item.hasStock() ? getItemStock() : MAX_INVENTORY_ITEMS;
    }

    private int getBalanceLimit() {
        return (int) Math.floor(item.getEconomy().getBalance(player) / item.getBuyPrice().orElse(null).getPrice());
    }

    private int getPlayerInventoryLimit() {
        int limit = 0;
        Inventory playerMockInventory = Bukkit.createInventory(null, 36);
        for (int i = 0; i < 36; i++)
            playerMockInventory.setItem(i, player.getInventory().getItem(i));

        while (playerMockInventory.addItem(item.getRawItem()).isEmpty()) limit++;

        return limit;
    }

    private int getItemStock() {
        return FutureUtils.waitFor(dStock.searchStock(player, shop, item.getUid()));
    }

    public static final class buyConfirmMenuBuilder {
        protected dShop shop;
        protected Player player;
        protected dItem item;
        protected Consumer<Integer> onCompleteAction;
        protected Runnable fallback;

        private buyConfirmMenuBuilder() {
        }

        public buyConfirmMenuBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public buyConfirmMenuBuilder withPlayer(Player player) {
            this.player = player;
            return this;
        }

        public buyConfirmMenuBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public buyConfirmMenuBuilder withOnCompleteAction(Consumer<Integer> onCompleteAction) {
            this.onCompleteAction = onCompleteAction;
            return this;
        }

        public buyConfirmMenuBuilder withFallback(Runnable fallback) {
            this.fallback = fallback;
            return this;
        }

        public buyConfirmMenu prompt() {
            return new buyConfirmMenu(shop, player, item, onCompleteAction, fallback);
        }
    }

    static final class cacheEntry {

        private final ItemStack item;
        private final int quantity;

        public cacheEntry(ItemStack item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public void restore(Player p) {
            ItemUtils.give(p, item, quantity);
        }
    }

}
