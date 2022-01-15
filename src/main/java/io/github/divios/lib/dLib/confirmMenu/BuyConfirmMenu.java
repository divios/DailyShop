package io.github.divios.lib.dLib.confirmMenu;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
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
    protected boolean addConditions(int quantity) {
        return quantity <= getMinLimit();
    }

    @Override
    protected boolean removeConditions(int quantity) {
        return nAddedItems - 1 >= quantity;
    }

    @Override
    protected void updateMockInventory() {
        clonedPlayerInventory.setContents(Arrays.copyOf(player.getInventory().getContents(), 36));

        int amount = super.nAddedItems;
        int ceil = (int) Math.ceil(amount / 64F);
        DebugLog.info("Total Amount: " + amount);
        for (int i = 0; i <= ceil; i++) {
            DebugLog.info("Itit: " + i);
            int aux = Math.min(amount, 64);
            DebugLog.info("Aux: " + aux);
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

        return getMinimumValue(stockLimit, balanceLimit, inventoryLimit);
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
        return (int) Math.floor(item.getEcon().getBalance(player) / getItemPrice()) - nAddedItems;
    }

    private int getPlayerInventoryLimit() {
        int limit = 0;
        Inventory playerMockInventory = Bukkit.createInventory(null, 36);
        playerMockInventory.setContents(Arrays.copyOf(player.getInventory().getContents(), 36));

        while (playerMockInventory.addItem(item.getItem()).isEmpty()) limit++;

        return limit - nAddedItems;
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
