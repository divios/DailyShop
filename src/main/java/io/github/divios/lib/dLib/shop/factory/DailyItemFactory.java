package io.github.divios.lib.dLib.shop.factory;

import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.buttons.Button;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.factory.MultiplePreconditions.DailyItemBuyPreconditions;
import io.github.divios.lib.dLib.shop.factory.exceptions.IllegalPrecondition;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DailyItemFactory {

    private final dShop shop;

    public DailyItemFactory(dShop shop) {
        this.shop = shop;
    }

    public Button createButton(dItem item) {
        return createButton(item, null);
    }

    public Button createButton(dItem item, Player p) {
        return new DailyItemButton(item, p);
    }

    private final class DailyItemButton implements Button {

        private final dItem item;
        private final Player player;

        DailyItemButton(dItem item, Player player) {
            this.item = item;
            this.player = player;
        }

        @Override
        public void execute(InventoryClickEvent e) {
            try {
                executeClickAction(e);
            } catch (IllegalPrecondition err) {
                err.sendErrMsg((Player) e.getWhoClicked());
            }
        }

        private void executeClickAction(InventoryClickEvent e) {
            if (e.isLeftClick()) {

                new DailyItemBuyPreconditions()
                        .validate(shop, (Player) e.getWhoClicked(), item, item.getItem().getAmount());

                Transactions.BuyTransaction()
                        .withShop(shop)
                        .withBuyer((Player) e.getWhoClicked())
                        .withItem(item)
                        .execute();

            } else if (e.isRightClick()) {
                // TODO preconditions
                Transactions.SellTransaction()
                        .withShop(shop)
                        .withVendor((Player) e.getWhoClicked())
                        .withItem(item)
                        .execute();
            }

        }

        @Override
        public ItemStack getItem() {
            return shopItemsLore.applyLore(item, player, shop);
        }
    }

}
