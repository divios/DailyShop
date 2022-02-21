package io.github.divios.lib.dLib.confirmMenu;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.LimitHelper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class BuyConfirmMenu extends abstractConfirmMenu {

    public static buyConfirmMenuBuilder builder() {
        return new buyConfirmMenuBuilder();
    }

    public static BuyConfirmMenu create(dShop shop, Player player, dItem item, Consumer<Integer> onCompleteAction, Runnable fallback) {
        return new BuyConfirmMenu(shop, player, item, onCompleteAction, fallback);
    }

    public BuyConfirmMenu(dShop shop, Player player, dItem item, Consumer<Integer> onCompleteAction, Runnable fallback) {
        super(shop, player, item, onCompleteAction, fallback);
    }

    @Override
    protected String getTitle() {
        return Lang.CONFIRM_GUI_BUY_NAME.getAsString(player);
    }

    @Override
    protected int initialQuantity() {
        int amount;
        return ((amount = item.getItem().getAmount()) == 1)
                ? 0
                : amount;
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
    protected void updateMockInventory() {
        clonedPlayerInventory.setContents(Arrays.copyOf(player.getInventory().getContents(), 36));

        int amount = super.nAddedItems;
        int ceil = (int) Math.ceil(amount / item.getItem().getMaxStackSize());
        //DebugLog.info("Total Amount: " + amount);
        for (int i = 0; i <= ceil; i++) {
            //DebugLog.info("Itit: " + i);
            int aux = Math.min(amount, item.getItem().getMaxStackSize());
            //DebugLog.info("Aux: " + aux);
            clonedPlayerInventory.addItem(ItemUtils.setAmount(item.getItem(), aux));
            amount -= aux;
        }
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
    protected int setMaxItems() {
        return getMinLimit();
    }

    @Override
    protected double getItemPrice() {
        return item.getPlayerBuyPrice(player, shop) / item.getItem().getAmount();
    }

    private int getMinLimit() {
        int stockLimit = getStockLimit();
        int balanceLimit = getBalanceLimit();
        int inventoryLimit = getPlayerInventoryLimit();
        int playerLimit = getBuyPlayerLimit();
        int shopAccountLimit = getShopAccountLimit();

        return getMinimumValue(stockLimit, balanceLimit, inventoryLimit, playerLimit, shopAccountLimit);
    }

    private int getMinimumValue(int... values) {
        int minValue = MAX_INVENTORY_ITEMS;
        for (int value : values)
            minValue = Math.min(minValue, value);

        return minValue;
    }

    private int getStockLimit() {
        return ((item.getDStock() != null) ? getItemStock() : MAX_INVENTORY_ITEMS) - nAddedItems;
    }

    private int getBalanceLimit() {
        double price = getItemPrice();
        return price == 0.0
                ? MAX_INVENTORY_ITEMS
                : (int) Math.floor(item.getEcon().getBalance(player) / price) - nAddedItems;
    }

    private int getPlayerInventoryLimit() {
        int limit = 0;
        Inventory playerMockInventory = Bukkit.createInventory(null, 36);
        playerMockInventory.setContents(Arrays.copyOf(player.getInventory().getContents(), 36));

        ItemStack clone = item.getItem().clone();
        int maxStack = clone.getMaxStackSize();

        Collection<ItemStack> rest;
        while ((rest = playerMockInventory.addItem(ItemUtils.setAmount(clone, maxStack)).values()).size() == 0)
            limit += maxStack;
        limit += (maxStack - rest.iterator().next().getAmount());

        return limit - nAddedItems;
    }

    private int getBuyPlayerLimit() {
        int limit = LimitHelper.getPlayerLimit(player, shop, item, Transactions.Type.BUY);
        return limit == -1
                ? MAX_INVENTORY_ITEMS
                : Math.max(0, limit - nAddedItems);
    }

    private int getShopAccountLimit() {
        if (shop.getAccount() == null) return MAX_INVENTORY_ITEMS;

        double floorPrice = getItemPrice();
        double max = shop.getAccount().getMaxBalance();

        int limit = (int) Math.floor((max - shop.getAccount().getBalance()) / floorPrice);

        return Math.max(0, (limit - nAddedItems));
    }

    private int getItemStock() {
        return item.getPlayerStock(player);
    }

    public static final class buyConfirmMenuBuilder {
        private dShop shop;
        private Player player;
        private dItem item;
        private Consumer<Integer> onCompleteAction;
        private Runnable fallback;

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

        public BuyConfirmMenu prompt() {
            return new BuyConfirmMenu(shop, player, item, onCompleteAction, fallback);
        }
    }

}
