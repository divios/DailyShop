package io.github.divios.dailyrandomshop.GUIs;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class buyGui {

    private Inventory shop;
    private final DailyRandomShop main;

    private final int[] fillGuiSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 16, 17, 18, 19, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35};

    private final int[] fillGuiSlotsLight = {2, 3, 5, 6, 9, 17, 18, 26, 29, 30, 31, 32, 33};
    private final int[] fillGuiSlotsWhite = {10, 16, 19, 25};
    private final int[] dailyItemsSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24};


    public buyGui(DailyRandomShop main) {
        this.main = main;
        shop = Bukkit.createInventory(null, 36, main.config.BUY_GUI_TITLE);
        inicializeGui(false);
    }

    public void inicializeGui(boolean reload) {

        if (reload) {
            Inventory aux = Bukkit.createInventory(null, 36, main.config.BUY_GUI_TITLE);
            aux.setContents(shop.getContents());
            shop = aux;
        }

        for (Integer i : fillGuiSlots) {

            ItemStack fillItem = null;
            ItemMeta meta = null;
            if (main.utils.IntegerListContains(fillGuiSlotsLight, i)) {
                fillItem = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
            } else if (main.utils.IntegerListContains(fillGuiSlotsWhite, i)) {
                fillItem = XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
            } else if (i == 4) { //Painting de arriba centrado
                fillItem = new ItemStack(Material.PAINTING);
                meta = fillItem.getItemMeta();

                meta.setDisplayName(main.config.BUY_GUI_PAINTING_NAME);
                List<String> lore = new ArrayList<>();
                for (String s : main.config.BUY_GUI_PAINTING_LORE) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                meta.setLore(lore);

                fillItem.setItemMeta(meta);
                shop.setItem(i, fillItem);
                continue;
            } else if (i == 35 && main.getConfig().getBoolean("enable-sell-gui")) {
                fillItem = new ItemStack(Material.ARROW);
                meta = fillItem.getItemMeta();

                meta.setDisplayName(main.config.BUY_GUI_ARROW_NAME);
                List<String> lore = new ArrayList<>();
                for (String s : main.config.BUY_GUI_ARROW_LORE) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                meta.setLore(lore);

                fillItem.setItemMeta(meta);
                shop.setItem(i, fillItem);
                continue;
            } else fillItem = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();

            meta = fillItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");

            fillItem.setItemMeta(meta);

            shop.setItem(i, fillItem);
        }
        if (!reload) createRandomItems();
        else updateRandomItems();
    }

    public void createRandomItems() {
        Map<String, Double[]> listOfMaterials = main.listMaterials;
        ArrayList<Integer> inserted = new ArrayList<>();
        int n = 0;
        while (n < dailyItemsSlots.length) {
            int ran = main.utils.randomValue(0, listOfMaterials.size() - 1);
            if (!inserted.isEmpty() && inserted.contains(ran)) {
                continue;
            }
            inserted.add(ran);

            Material material = main.utils.getEntry(listOfMaterials, ran);
            ItemStack randomItem = new ItemStack(material);

            ItemMeta meta = randomItem.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();

            lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", "" + listOfMaterials.get(material.toString())[0]));
            meta.setLore(lore);

            randomItem.setItemMeta(meta);

            shop.setItem(dailyItemsSlots[n], randomItem);
            n++;
        }
        main.getServer().broadcastMessage(main.config.PREFIX + main.config.MSG_NEW_DAILY_ITEMS);
        main.resetTime();
    }

    public void updateRandomItems() {
        for (int i : dailyItemsSlots) {
            ItemStack item = shop.getItem(i);
            ItemMeta meta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();

            lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", "" + main.listMaterials.get(item.getType().toString())[0]));
            meta.setLore(lore);

            shop.setItem(i, item);

        }
    }

    public Inventory getGui() {
        return shop;
    }

}
