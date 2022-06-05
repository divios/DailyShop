package io.github.divios.lib.dLib.shop.view.buttons;

import io.github.divios.core_lib.events.Events;
import io.github.divios.dailyShop.events.dailyItemClickEvent;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DailyItemFactory {

    private final dShop shop;

    public DailyItemFactory(dShop shop) {
        this.shop = shop;
    }

    public Button createButton(dItem item) {
        return new DailyItemButton(item);
    }

    public ItemStack getItem(dItem item, Player p) {
        return shopItemsLore.applyLore(item, p, shop);
    }

    private final class DailyItemButton implements Button {

        private final dItem item;

        DailyItemButton(dItem item) {
            this.item = item;
        }

        @Override
        public void execute(InventoryClickEvent e) {
            if (!((e.getClick() == ClickType.LEFT) || (e.getClick() == ClickType.RIGHT))) return;

            Events.callEvent(new dailyItemClickEvent(shop, (Player) e.getWhoClicked(), item, e.getClick()));
        }

        @Override
        public ItemStack getItem(Player p) {
            return shopItemsLore.applyLore(item, p, shop);
        }
    }

}
