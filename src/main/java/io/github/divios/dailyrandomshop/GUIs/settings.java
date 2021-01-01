package io.github.divios.dailyrandomshop.GUIs;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class settings implements Listener{

    private final DailyRandomShop main;
    private Inventory GUI;

    public settings (DailyRandomShop main) {
        this.main = main;
        createGui();
    }

    private void createGui() {
        GUI = Bukkit.createInventory(null, 27, main.config.SETTINGS_GUI_TITLE + ChatColor.MAGIC);


        ItemMeta meta;
        List<String> lore;

        ItemStack dailyItemSettings = XMaterial.PAINTING.parseItem();
        meta = dailyItemSettings.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Daily items");

        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Clic to manage daily items");
        lore.add(ChatColor.RED + "Not available yet");
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

    }

    public Inventory getGUI() {
        return GUI;
    }

}
