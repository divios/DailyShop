package io.github.divios.dailyrandomshop.GUIs;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.Utils.ConfigUtils;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.cryptomorin.xseries.XMaterial;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class buyGui {

    private Inventory shop;
    private final DailyRandomShop main;


    public buyGui(DailyRandomShop main) {
        this.main = main;
        inicializeGui(false);
    }

    public void firstRow(int i) {
        int row = 9*i;
        ItemStack item;
        ItemMeta meta;
        for (int j=0; j < 9; j++) {

            if(j==0 || j==1 || j==7 || j==8) {
                item = main.utils.setItemAsFill(XMaterial.valueOf(main.config.BUY_GUI_PANE1).parseItem());
            }
            else if(j==4){
                item = main.utils.setItemAsFill(new ItemStack(Material.PAINTING));
                meta = item.getItemMeta();

                meta.setDisplayName(main.config.BUY_GUI_PAINTING_NAME);
                List<String> lore = new ArrayList<>();
                for (String s : main.config.BUY_GUI_PAINTING_LORE) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                meta.setLore(lore);

                item.setItemMeta(meta);
                shop.setItem(row+j, item);
                continue;
            }
            else item = main.utils.setItemAsFill(XMaterial.valueOf(main.config.BUY_GUI_PANE2).parseItem());

            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            shop.setItem(row+j, item);
        }
    }

    public void betweenrows(int i) {
        int row = 9*i;
        ItemStack item;
        ItemMeta meta;
        for (int j=0; j < 9; j++) {
            if (j==0 || j==8) {
                item = main.utils.setItemAsFill(XMaterial.valueOf(main.config.BUY_GUI_PANE2).parseItem());
            } else continue;
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            shop.setItem(row+j, item);
        }
    }

    public void finalRow(int i) {
        int row = 9*i;
        ItemStack item;
        ItemMeta meta;
        for (int j=0; j < 9; j++) {
            if (j==0 || j==1 || j==7) {
                item = main.utils.setItemAsFill(XMaterial.valueOf(main.config.BUY_GUI_PANE1).parseItem());
            }
            else if (j==2 || j==3 || j==4 || j==5 || j==6) {
                item = main.utils.setItemAsFill(XMaterial.valueOf(main.config.BUY_GUI_PANE2).parseItem());
            } else {
                if (main.getConfig().getBoolean("enable-sell-gui")) {
                    item = main.utils.setItemAsFill(new ItemStack(Material.ARROW));
                    meta = item.getItemMeta();

                    meta.setDisplayName(main.config.BUY_GUI_ARROW_NAME);
                    List<String> lore = new ArrayList<>();
                    for (String s : main.config.BUY_GUI_ARROW_LORE) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                    }
                    meta.setLore(lore);

                    item.setItemMeta(meta);
                    shop.setItem(row + j, item);
                    continue;
                }
                item = main.utils.setItemAsFill(XMaterial.valueOf(main.config.BUY_GUI_PANE1).parseItem());
            }
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            shop.setItem(row+j, item);
        }
    }

    public void inicializeGui(boolean timer) {

        double dailyRows = main.config.N_DAILY_ITEMS / 7;
        int rows = (int) Math.ceil(dailyRows + 2);
        if (rows == 2) rows = 3;

        shop = Bukkit.createInventory(null, (rows * 9) , main.config.BUY_GUI_TITLE);

        for ( int i=0; i < rows; i++) {
            if (i == 0) firstRow(i);
            else if(i==rows-1) finalRow(i);
            else betweenrows(i);
        }

        if (timer) createRandomItems();
        else getDailyItems();
    }

    public void createRandomItems() {
        Map<String, Double[]> listOfMaterials = main.listMaterials;
        ArrayList<Integer> inserted = new ArrayList<>();
        int n = 0;
        while (n < main.getConfig().getInt("number-of-daily-items")) {

            if (shop.firstEmpty() == -1) break;

            if (listOfMaterials.size() == inserted.size()) break; //make sure to break infinite loop if happens

            int ran = main.utils.randomValue(0, listOfMaterials.size() - 1);
            if (!inserted.isEmpty() && inserted.contains(ran)) {
                continue;
            }
            inserted.add(ran);

            Material material = main.utils.getEntry(listOfMaterials, ran);
            ItemStack randomItem = main.utils.setItemAsDaily(new ItemStack(material));

            ItemMeta meta = randomItem.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();

            lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", "" + listOfMaterials.get(material.toString())[0]));
            meta.setLore(lore);

            randomItem.setItemMeta(meta);

            shop.setItem(shop.firstEmpty(), randomItem);
            n++;
        }
        saveDailyItems();

        main.getServer().broadcastMessage(main.config.PREFIX + main.config.MSG_NEW_DAILY_ITEMS);
        ConfigUtils.resetTime(main);

    }

    public void saveDailyItems() {
        ArrayList<ItemStack> dailyItems = new ArrayList<>();
        ItemStack item;
        for(int i=0; i < (shop.getSize()) ; i++) {
            item = shop.getItem(i);
            if(item == null || !main.utils.isDailyItem(item)) continue;
            dailyItems.add(item);
        }
        if(!dailyItems.isEmpty()) {
            try {
                main.dbManager.updateCurrentItems(dailyItems);
            } catch (Exception throwables) {
                main.getLogger().severe("Couldn't save current daily items on database");
                throwables.printStackTrace();
            }
        }
    }

    public void getDailyItems() {
        try {
            ArrayList<ItemStack> dailyItem = main.dbManager.getCurrentItems();
            if(dailyItem.isEmpty()) {
                createRandomItems();
                //main.getLogger().severe("Hubo un error al recuperar los items diarios, generando items aleatorios");
                return;
            }

            for(ItemStack item: dailyItem) {
                if(shop.firstEmpty() == -1) break;
                ItemMeta meta = item.getItemMeta();
                ArrayList<String> lore = new ArrayList<String>();

                lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", "" + main.listMaterials.get(item.getType().toString())[0]));
                meta.setLore(lore);

                item.setItemMeta(meta);
                shop.setItem(shop.firstEmpty(), item);
            }

            saveDailyItems();

        } catch (Exception e) {
            //main.getLogger().severe("Hubo un error al recuperar los items diarios, generando items aleatorios");
            createRandomItems();
            e.printStackTrace();
        }

    }


    public Inventory getGui() {
        return shop;
    }

}
