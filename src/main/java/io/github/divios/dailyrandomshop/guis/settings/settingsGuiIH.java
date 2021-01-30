package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class settingsGuiIH implements Listener, InventoryHolder {

    private final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private final dataManager dbManager = dataManager.getInstance();

    private settingsGuiIH() {
    }

    public static void getInstance(Player p) {
        p.openInventory(new settingsGuiIH().getInventory());
    }

    @Override
    public Inventory getInventory() {
        Bukkit.getPluginManager().registerEvents(this, main);
        Inventory GUI = Bukkit.createInventory(this, 27, conf_msg.SETTINGS_GUI_TITLE);

        ItemStack dailyItemSettings = XMaterial.PAINTING.parseItem();
        utils.setDisplayName(dailyItemSettings, conf_msg.SETTINGS_DAILY_ITEM);

        utils.setLore(dailyItemSettings, conf_msg.SETTINGS_DAILY_ITEM_LORE);
        utils.setLore(dailyItemSettings, Arrays.asList("&7Note: changing current items except from price",
                "&7won't change the current items in shop and furthermore",
                "&7'll make them useless, renovate is a must in this cases"));

        ItemStack sellItemSettings = XMaterial.BOOK.parseItem();
        utils.setDisplayName(sellItemSettings, conf_msg.SETTINGS_SELL_ITEM);

        utils.setLore(sellItemSettings, conf_msg.SETTINGS_SELL_ITEM_LORE);

        for (int i = 0; i < 27; i++) {
            ItemStack item = GUI.getItem(i);
            if (utils.isEmpty(item)) {
                item = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
                utils.setDisplayName(item, "&7");
                GUI.setItem(i, item);
            }
        }

        GUI.setItem(11, dailyItemSettings);
        GUI.setItem(15, sellItemSettings);
        return GUI;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) {
            return;
        }

        e.setCancelled(true);
        if (e.getSlot() != e.getRawSlot()) return;

        Player p = (Player) e.getWhoClicked();
        if (e.getSlot() == 11) {
            dailyGuiSettings.openInventory(p);
        }

        if (e.getSlot() == 15) {
            if (dbManager.listSellItems.isEmpty()) {
                e.getWhoClicked().sendMessage(conf_msg.PREFIX + conf_msg.MSG_SELL_ITEMS_GUI_EMPTY);
                utils.sendSound(p, Sound.BLOCK_DISPENSER_DISPENSE);
                p.closeInventory();
                return;
            }
            //e.getWhoClicked().openInventory(main.SellGuiSettings.getFirstGui());
        }
    }
}
