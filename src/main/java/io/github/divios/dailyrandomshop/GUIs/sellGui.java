package io.github.divios.dailyrandomshop.GUIs;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class sellGui {

    private final DailyRandomShop main;
    private final int[] fillSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35};
    private final ArrayList<Integer> dailyItemsSlots = new ArrayList<>();
    private Map<String, Double[]> listMaterials;
    private ArrayList<Inventory> currentInventories= new ArrayList<>();

    public sellGui(DailyRandomShop main) {
        this.main = main;
        listMaterials = main.listMaterials;
        dailyItemsSlots.add(10);
        dailyItemsSlots.add(11);
        dailyItemsSlots.add(12);
        dailyItemsSlots.add(13);
        dailyItemsSlots.add(14);
        dailyItemsSlots.add(15);
        dailyItemsSlots.add(16);
        dailyItemsSlots.add(19);
        dailyItemsSlots.add(20);
        dailyItemsSlots.add(21);
        dailyItemsSlots.add(22);
        dailyItemsSlots.add(23);
        dailyItemsSlots.add(24);
        dailyItemsSlots.add(25);
    }

    public Inventory createSellInv() {
        Inventory sellGui = Bukkit.createInventory(null, 36, main.config.SELL_GUI_TITLE);
        ItemStack item;
        ItemMeta meta;
        for (int i : fillSlots) {
            if (i == 4) {
                item = new ItemStack(Material.PAINTING);
                meta = item.getItemMeta();

                meta.setDisplayName(main.config.SELL_PAINTING_NAME);
                List<String> lore = new ArrayList<>();
                for (String s: main.config.SELL_PAINTING_LORE) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                meta.setLore(lore);

                item.setItemMeta(meta);
                sellGui.setItem(i, item);
                continue;
            }
            if (i == 31) {
                item = XMaterial.GREEN_STAINED_GLASS.parseItem();
                meta = item.getItemMeta();
                meta.setDisplayName(main.config.SELL_ITEM_NAME.replaceAll("\\{price}", "NaN"));
                item.setItemMeta(meta);

                sellGui.setItem(i, item);
                continue;

            } else if( i == 27) {
                item = new ItemStack(Material.ARROW);
                meta = item.getItemMeta();
                meta.setDisplayName(main.config.SELL_ARROW_NAME);
                List<String> lore = new ArrayList<>();
                for (String s: main.config.SELL_ARROW_LORE) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                sellGui.setItem(i, item);
                continue;
            }

            else{
                item = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "");
                item.setItemMeta(meta);
            }

            sellGui.setItem(i, item);
        }

        //currentInventories.add(sellGui);
        return sellGui;
    }

    public double calculatePrice(Inventory inv) {
        double price = 0;

        for( int i : dailyItemsSlots) {
            if (inv.getItem(i) == null) continue;
            String material = inv.getItem(i).getType().toString();
            price += main.listMaterials.get(material)[1] * inv.getItem(i).getAmount();
        }
        price = (double) Math.round(price * 100.0) / 100;
        if (price == 0) {
            ItemStack item = inv.getItem(31);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(main.config.SELL_ITEM_NAME.replaceAll("\\{price}", "NaN"));
            item.setItemMeta(meta);
            inv.setItem(31, item);
        }else{
            ItemStack item = inv.getItem(31);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(main.config.SELL_ITEM_NAME.replaceAll("\\{price}", "" + price));
            item.setItemMeta(meta);
            inv.setItem(31, item);
        }
        return price;
    }

    public ArrayList<Inventory> getInventories() {
        return currentInventories;
    }

    public ArrayList<Integer> getDailyItemsSlots() {
        return dailyItemsSlots;
    }
}
