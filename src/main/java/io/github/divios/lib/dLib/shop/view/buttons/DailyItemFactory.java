package io.github.divios.lib.dLib.shop.view.buttons;

import io.github.divios.core_lib.events.Events;
import io.github.divios.dailyShop.events.dailyItemClickEvent;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.view.buttons.Button;
import io.github.divios.lib.dLib.shop.dShop;
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
            if (!(e.isRightClick() || e.isLeftClick())) return;

            Events.callEvent(new dailyItemClickEvent(shop, (Player) e.getWhoClicked(), item, e.getClick()));
        }

        @Override
        public ItemStack getItem() {
            return shopItemsLore.applyLore(item, player, shop);
        }
    }

}
