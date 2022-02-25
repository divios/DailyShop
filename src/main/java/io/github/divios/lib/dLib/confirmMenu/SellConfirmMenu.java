package io.github.divios.lib.dLib.confirmMenu;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.CompareItemUtils;
import io.github.divios.dailyShop.utils.LimitHelper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@SuppressWarnings({"unused"})
public class SellConfirmMenu extends abstractConfirmMenu {

    private static final int MAX_SELL_ITEMS = 9 * 4 * 64;

    public static sellConfirmMenuBuilder builder() {
        return new sellConfirmMenuBuilder();
    }

    private SellConfirmMenu(dShop shop,
                            Player player,
                            dItem item,
                            Consumer<Integer> onCompleteAction,
                            Runnable fallback
    ) {
        super(shop, player, item, onCompleteAction, fallback);
    }

    @Override
    protected String getTitle() {
        return Lang.CONFIRM_GUI_SELL_NAME.getAsString(player);
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
        return getMinLimit() >= quantity;
    }

    @Override
    protected boolean removeConditions(int quantity) {
        return super.nAddedItems >= quantity;
    }

    @Override
    protected void updateMockInventory() {
        ItemUtils.remove(super.clonedPlayerInventory, item.getItem(), super.nAddedItems);
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

    private int getMinLimit() {
        int maxItemsCount = (countSimilarItems() - super.nAddedItems);
        int stockLimit = getStockLimit();
        int limitSellLimit = getSellPlayerLimit();
        int accountLimit = getShopAccountLimit();

        return getMinimumValue(maxItemsCount, stockLimit, limitSellLimit, accountLimit);
    }
    private int getMinimumValue(int... values) {
        int minValue = MAX_INVENTORY_ITEMS;
        for (int value : values)
            minValue = Math.min(minValue, value);

        return minValue;
    }

    private int getStockLimit() {
        if (!item.hasStock() || item.getDStock().allowSellOnMax()) return MAX_SELL_ITEMS;

        int limit = item.getDStock().getMaximum() - item.getPlayerStock(player);
        return Math.max(limit - nAddedItems, 0);
    }

    private int getSellPlayerLimit() {
        int limit = LimitHelper.getPlayerLimit(player, shop, item, Transactions.Type.SELL);
        return limit == -1
                ? MAX_SELL_ITEMS
                : Math.max(0, limit - nAddedItems);
    }

    private int getShopAccountLimit() {
        if (shop.getAccount() == null) return Integer.MAX_VALUE;
        double itemPrice = item.getPlayerSellPrice(player, shop) / item.getItem().getAmount();

        int limit = (int) Math.floor(shop.getAccount().getBalance() / itemPrice);
        return Math.max(0, (limit - nAddedItems));
    }

    @Override
    protected double getItemPrice() {
        return item.getPlayerSellPrice(player, shop) / item.getItem().getAmount();
    }

    private int countSimilarItems() {
        return ItemUtils.count(player.getInventory(), item.getItem(), CompareItemUtils::compareItems);
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

        public SellConfirmMenu prompt() {
            return new SellConfirmMenu(shop, player, item, onCompleteAction, fallback);
        }
    }
}
