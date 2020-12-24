package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.ChatColor;
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


        if(e.getSlot() == 35 && e.getRawSlot() == e.getSlot() && main.getConfig().getBoolean("enable-sell-gui")) {
            p.openInventory(main.SellGui.createSellInv());
        }

        if ( !main.utils.IntegerListContains(dailyItemsSlots, e.getSlot()) ) return;

        ItemStack item = inv.getItem(e.getSlot());

        p.openInventory(main.ConfirmGui.getGui(item));

    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);

        }
    }
}
