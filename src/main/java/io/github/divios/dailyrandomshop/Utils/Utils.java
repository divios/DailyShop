package io.github.divios.dailyrandomshop.Utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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

    public ItemStack getEntry(HashMap<ItemStack, Double> list, int index) {
        int i = 0;
        for (ItemStack item: list.keySet()) {
            if (index == i) return item;
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
            p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_ENOUGH_MONEY);
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

    public String getNBT(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        String nbtValues = "";

        if(nbtItem.getKeys() == null || nbtItem.getKeys().isEmpty()) return nbtValues;

        main.getLogger().warning(nbtItem.getKeys().toString());

        for (String key: nbtItem.getKeys()) {
            if(key.equals("Command") || key.equals("DailyItem")) {
                continue;
            }
            String value = nbtItem.getString(key);
            main.getLogger().warning(value);
            if(value.isEmpty()) value = "null";

            nbtValues = nbtValues.concat(";" + key);
            nbtValues = nbtValues.concat(":" + value);

        }
        if(!nbtValues.isEmpty()) nbtValues = nbtValues.substring(1);
        return nbtValues;
    }

    public ItemStack setItemAsCommand(ItemStack item, List<String> command) {
        NBTItem nbtItem = new NBTItem(item);
        String commands = "";

        for(String s: command) {
            commands = commands.concat(";" + s);
        }
        commands = commands.substring(1);
        nbtItem.setString("Command", commands);
        return nbtItem.getItem();
    }

    public boolean isCommandItem(ItemStack item) {
        if (item == null) return false;

        NBTItem NBTitem = new NBTItem(item);
        return NBTitem.hasKey("Command");
    }

    public List<String> getItemCommand(ItemStack item) {

        NBTItem NBTitem = new NBTItem(item);
        List<String> commands = new ArrayList<>();
        String rawCommands = NBTitem.getString("Command");

        for (String s: rawCommands.split(";")) {
            main.getLogger().severe(s);
            commands.add(s);
        }

        return commands;
    }

    public Double getItemPrice(HashMap<ItemStack, Double> items, ItemStack toCompare) {
        Double price = null;
        ItemStack toCompare2 = removePriceLore(toCompare);

        for (Map.Entry<ItemStack, Double> item: items.entrySet()) {
            ItemStack item2 = removePriceLore(item.getKey());
            if (item2.isSimilar(toCompare2)) return item.getValue();
        }

        return price;
    }

    public ItemStack removePriceLore ( ItemStack item) {
        ItemStack item2 = item.clone();
        ItemMeta meta = null;
        meta = item2.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        item2.setItemMeta(meta);

        return item2;
    }

}
