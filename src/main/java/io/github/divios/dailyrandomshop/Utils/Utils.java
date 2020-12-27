package io.github.divios.dailyrandomshop.Utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {
    private final DailyRandomShop main;

    public Utils ( DailyRandomShop main) {
        this.main = main;
    }

    public boolean IntegerListContains(int[] list, int i) {

        for (int j: list) {
            if (j == i) {
                return true;
            }
        }
        return false;
    }

    public int randomValue(int minValue, int maxValue) {

        return minValue + (int)(Math.random() * ((maxValue - minValue) + 1));
    }

    public boolean inventoryFull(ItemStack[] inventory) {

        for (ItemStack i: inventory) {
            if (i == null) {
                return false;
            }
        }
        return true;
    }

    public Material getEntry(Map<String, Double[]> list, int index) {
        int i = 0;
        for (String material: list.keySet()) {
            if (index == i) return Material.valueOf(material);
            i++;
        }
        return null;
    }

    public int giveItem(Player p, Double price, Inventory bottominv, ItemStack item) {

        int outcome = -1;
        if (main.utils.inventoryFull(bottominv.getContents())) {
            p.sendMessage(main.config.PREFIX + main.config.MSG_INVENTORY_FULL);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            } catch (NoSuchFieldError ignored) {}
            return outcome;
        }
        if (main.econ.getBalance(p) < price) {
            p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_ENOUGHT_MONEY);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            } catch (NoSuchFieldError ignored) {}
            return outcome;
        }
        ItemStack aux = item.clone();
        ItemMeta meta = aux.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        aux.setItemMeta(meta);

        try {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } catch (NoSuchFieldError ignored) {}
        p.getInventory().addItem(aux);
        main.econ.withdrawPlayer(p, price);
        p.sendMessage(main.config.PREFIX + main.config.MSG_BUY_ITEM.replace("{price}", "" + price).replace("{item}", item.getType().toString()));
        p.openInventory(main.BuyGui.getGui());
        outcome = 1;
        return outcome;
    }

    public ItemStack setItemAsFill(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("FillGui", "isfill");
        return nbtItem.getItem();
    }

    public ItemStack setItemAsDaily(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("DailyItem", "isdaily");
        return nbtItem.getItem();
    }

    public boolean isDailyItem(ItemStack item) {
        if (item == null) return false;

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey("DailyItem");
    }

}
