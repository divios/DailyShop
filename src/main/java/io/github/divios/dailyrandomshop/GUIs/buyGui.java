package io.github.divios.dailyrandomshop.GUIs;

import io.github.divios.dailyrandomshop.Utils.ConfigUtils;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.cryptomorin.xseries.XMaterial;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class buyGui {

    private Inventory shop;
    private final DailyRandomShop main;

    public buyGui(DailyRandomShop main) {
        this.main = main;
        inicializeGui(false);
    }

    public void firstRow() {
        ItemStack item;
        ItemMeta meta;

        item = main.utils.setItemAsFill(new ItemStack(Material.PAINTING));
        meta = item.getItemMeta();
        meta.setDisplayName(main.config.BUY_GUI_PAINTING_NAME);
        List<String> lore = new ArrayList<>();
        for (String s : main.config.BUY_GUI_PAINTING_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        shop.setItem(4, item);

        if(main.getConfig().getBoolean("enable-sell-gui")) {
            ItemStack item2 = XMaterial.OAK_FENCE_GATE.parseItem();
            meta = item2.getItemMeta();
            meta.setDisplayName(main.config.BUY_GUI_ARROW_NAME);
            lore = new ArrayList<>();
            for (String s : main.config.BUY_GUI_ARROW_LORE) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(lore);
            item2.setItemMeta(meta);
            shop.setItem(8, item2);
        }
    }

    public void secondRow() {
        ItemStack item;
        ItemMeta meta;
        for (int j = 0; j < 9; j++) {

            item = main.utils.setItemAsFill(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            item.setItemMeta(meta);
            shop.setItem(9 + j, item);
        }
    }

    public void inicializeGui(boolean timer) {

        double dailyRows = main.config.N_DAILY_ITEMS / 9F;
        int rows = (int) Math.ceil(dailyRows + 2);
        if (rows <= 2) rows = 3;

        shop = Bukkit.createInventory(null, (rows * 9), main.config.BUY_GUI_TITLE + ChatColor.GOLD);

        firstRow();
        secondRow();

        if (timer) createRandomItems();
        else {
            getDailyItems();
        }
    }

    public void createRandomItems() {
        HashMap<ItemStack, Double> listOfMaterials = main.listDailyItems;
        ArrayList<Integer> inserted = new ArrayList<>();

        int j=18;
        while(true) {

            if(shop.firstEmpty() == -1) break;

            if(j >= (18 + main.config.N_DAILY_ITEMS - 1)) {
                break;
            }

            if (listOfMaterials.size() == inserted.size()) {
                break; //make sure to break infinite loop if happens
            }

            int ran = main.utils.randomValue(0, listOfMaterials.size() - 1);

            if (!inserted.isEmpty() && inserted.contains(ran)) {
                continue;
            }

            inserted.add(ran);

            ItemStack randomItem = main.utils.getEntry(main.listDailyItems, ran);

            ItemMeta meta = randomItem.getItemMeta();
            List<String> lore;
            if(meta.hasLore()) lore = meta.getLore();
            else lore = new ArrayList<>();

            lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", String.format("%,.2f", main.listDailyItems.get(randomItem))));
            meta.setLore(lore);

            randomItem.setItemMeta(meta);

            shop.setItem(j, randomItem);
            j++;
        }
        saveDailyItems();

        main.getServer().broadcastMessage(main.config.PREFIX + main.config.MSG_NEW_DAILY_ITEMS);
        ConfigUtils.resetTime(main);

    }

    public void saveDailyItems() {
        ArrayList<ItemStack> dailyItems = new ArrayList<>();

        for (ItemStack item : shop.getContents()) {

            if (item == null || !main.utils.isDailyItem(item)) {
                continue;
            }
            ItemStack itemCloned = item.clone();
            ItemMeta meta = itemCloned.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(lore.size() - 1);
            meta.setLore(lore);
            itemCloned.setItemMeta(meta);

            dailyItems.add(itemCloned);
        }
        if (!dailyItems.isEmpty()) {
            main.dbManager.updateCurrentItems(dailyItems);
        }
    }

    public void getDailyItems() {
        try {
            ArrayList<ItemStack> dailyItem = main.dbManager.getCurrentItems();
            if (dailyItem.isEmpty()) {
                createRandomItems();
                //main.getLogger().severe("Hubo un error al recuperar los items diarios, generando items aleatorios");
                return;
            }

            int n = 18;
            for (ItemStack item : dailyItem) {
                if(n >= (18 + main.config.N_DAILY_ITEMS -1)) break;
                if (shop.firstEmpty() == -1) break;
                ItemMeta meta = item.getItemMeta();
                List<String> lore;
                if(meta.hasLore()) lore = meta.getLore();
                else lore = new ArrayList<>();

                lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", String.format("%,.2f", main.listDailyItems.get(item))));
                meta.setLore(lore);

                item.setItemMeta(meta);
                shop.setItem(n, item);
                n++;
            }

        } catch (Exception e) {
            main.getLogger().severe("Hubo un error al recuperar los items diarios, generando items aleatorios");
            createRandomItems();
            e.printStackTrace();
        }

    }


    public Inventory getGui() {
        return shop;
    }

}
