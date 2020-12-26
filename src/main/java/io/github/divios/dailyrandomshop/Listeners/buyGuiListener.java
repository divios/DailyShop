package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class buyGuiListener implements Listener {

    private final int[] dailyItemsSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24};
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
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_PERMS);
                return;
            }
            p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
            p.openInventory(main.SellGui.createSellInv());
        }

        if ( !main.utils.isDailyItem(e.getCurrentItem()) ) return;

        ItemStack item = e.getView().getTopInventory().getItem(e.getSlot());

        if (main.getConfig().getBoolean("enable-confirm-gui")) {
            p.openInventory(main.ConfirmGui.getGui(item));
            p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
        }
        else {
            Double price = main.listMaterials.get(item.getType().toString())[0];
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
