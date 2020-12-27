package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class buyGuiListener implements Listener {

    private DailyRandomShop main;
    private Inventory inv;

    public buyGuiListener(DailyRandomShop main) {
        this.main = main;
        inv = main.BuyGui.getGui();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if ( !(e.getView().getTitle().equals(main.config.BUY_GUI_TITLE)) ) return;

        e.setCancelled(true);

        if(e.getSlot() == (e.getView().getTopInventory().getSize() - 1) &&
                e.getRawSlot() == e.getSlot() && main.getConfig().getBoolean("enable-sell-gui")) {

            if(!p.hasPermission("DailyRandomShop.sell")) {
                try {
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }catch (NoSuchFieldError ignored) { }
                p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_PERMS);
                return;
            }

            try {
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
            }catch (NoSuchFieldError ignored) { }


            //p.openInventory(main.SellGui.createSellInv());
        }

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        if ( !main.utils.isDailyItem(e.getCurrentItem()) ) return;

        ItemStack item = e.getView().getTopInventory().getItem(e.getSlot());

        if (main.getConfig().getBoolean("enable-confirm-gui")) {
            p.openInventory(main.ConfirmGui.getGui(item));

            try {
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
            }catch (NoSuchFieldError ignored) { }

        }
        else {
            Double price = main.listItem.get(item);
            main.utils.giveItem(p, price, e.getView().getBottomInventory(), item);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);

        }
    }
}
