package io.github.divios.dailyrandomshop.GUIs.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class settingsGuiIH implements Listener, InventoryHolder {

    private final DailyRandomShop main;
    private final Player p;

    public settingsGuiIH(DailyRandomShop main, Player p) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
        this.p = p;

        p.openInventory(getInventory());
    }

    @Override
    public Inventory getInventory() {
        Inventory GUI = Bukkit.createInventory(this, 27, main.config.SETTINGS_GUI_TITLE);


        ItemMeta meta;
        List<String> lore;

        ItemStack dailyItemSettings = XMaterial.PAINTING.parseItem();
        meta = dailyItemSettings.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Daily items");

        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Clic to manage daily items");
        meta.setLore(lore);
        dailyItemSettings.setItemMeta(meta);


        ItemStack sellItemSettings = XMaterial.BOOK.parseItem();
        meta = dailyItemSettings.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Sell items");

        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Clic to manage sell items");
        meta.setLore(lore);
        sellItemSettings.setItemMeta(meta);

        GUI.setItem(11, dailyItemSettings);
        GUI.setItem(15, sellItemSettings);

        for (int i=0; i < 27; i++) {
            ItemStack item = GUI.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                item = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
                GUI.setItem(i, item);
            }
        }
        return GUI;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        if(e.getView().getTopInventory().getHolder() != this) {
            return;
        }

        e.setCancelled(true);

        if(e.getSlot() == 11 && e.getSlot() == e.getRawSlot()) {

            e.getWhoClicked().openInventory(main.DailyGuiSettings.getFirstGui());
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
    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getView().getTopInventory().getHolder() == this) {

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }



}
