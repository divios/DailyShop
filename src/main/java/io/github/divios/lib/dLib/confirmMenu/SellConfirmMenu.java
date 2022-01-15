package io.github.divios.lib.dLib.confirmMenu;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.CompareItemUtils;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
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
    protected boolean addConditions(int quantity) {
        DebugLog.info("Similar items: " + (countSimilarItems() - nAddedItems));
        DebugLog.info("Quantity: " + quantity);
        return (countSimilarItems() - super.nAddedItems) >= quantity;
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
        return countSimilarItems() - super.nAddedItems;
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
