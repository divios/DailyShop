package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class settingListener implements Listener {

    private DailyRandomShop main;

    public settingListener(DailyRandomShop main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        if(!e.getView().getTitle().equals(main.config.SETTINGS_GUI_TITLE + ChatColor.MAGIC)) {
            return;
        }

        e.setCancelled(true);

        if(e.getSlot() == 11 && e.getSlot() == e.getRawSlot()) {

            //e.getWhoClicked().openInventory(main.)
        }

        if (e.getSlot() == 15 && e.getSlot() == e.getRawSlot()) {

            if(main.listSellItems.isEmpty()) {
                e.getWhoClicked().sendMessage(main.config.PREFIX + ChatColor.GRAY +
                        "There are not items registered for sale, you can add one with /rdshop addSellItem");
                try{
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
                } catch (NoSuchFieldError Ignored) {}

                return;
            }

            e.getWhoClicked().openInventory(main.SellGuiSettings.getFirstGui());
        }

    }

}
