package io.github.divios.dailyrandomshop.Utils;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
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

    public boolean inventoryFull(Player p) {

        for (int i = 0; i < 36; i++) {

            if (p.getInventory().getItem(i) == null ||
                    p.getInventory().getItem(i).getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }

    public ItemStack getEntry(HashMap<ItemStack, Double> list, int index) {
        int i = 0;
        for (ItemStack item: list.keySet()) {
            if (index == i) return item.clone();
            i++;
        }
        return null;
    }

    public int giveItem(Player p, Double price, Inventory bottominv, ItemStack item) {

        int outcome = -1;

        if (main.utils.inventoryFull(p)) {
            p.sendMessage(main.config.PREFIX + main.config.MSG_INVENTORY_FULL);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);

            } catch (NoSuchFieldError ignored) {}
            finally { return outcome; }

        }

        if (main.econ.getBalance(p) < price) {
            p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_ENOUGH_MONEY);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return outcome;
            } catch (NoSuchFieldError ignored) {}
            finally { return outcome; }
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

    public ItemStack setItemAsAmount(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("setAmount", "amnt");
        return nbtItem.getItem();
    }

    public boolean isItemAmount(ItemStack item) {
        if (item == null) return false;

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey("setAmount");
    }

    public void processItemAmount(ItemStack item, int slot) {
        if(item.getAmount() == 1) {
            item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();

        } else item.setAmount(item.getAmount() - 1);

        main.BuyGui.getGui().setItem(slot, item);

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

    public List<String> getNBT(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        List<String> nbtValues = new ArrayList<>();

        if(nbtItem.getKeys() == null || nbtItem.getKeys().isEmpty()) return nbtValues;

        for (String key: nbtItem.getKeys()) {
            if(key.equals("Command") || key.equals("DailyItem")) {
                continue;
            }
            String value = nbtItem.getString(key);
            if(value.isEmpty()) continue;

            nbtValues.add(key.concat(":" + value));

        }

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
        String rawCommands = NBTitem.getString("Command");

        return new ArrayList<>(Arrays.asList(rawCommands.split(";")));
    }

    public Double getItemPrice(HashMap<ItemStack, Double> items, ItemStack toCompare, boolean lore) {
        Double price = 0.0;
        ItemStack toCompare2 = null;
        if (lore) toCompare2 =  removePriceLore(toCompare);
        else toCompare2 = toCompare.clone();

        for (Map.Entry<ItemStack, Double> item: items.entrySet()) {
            //ItemStack item2 = removePriceLore(item.getKey());
            if (item.getKey().isSimilar(toCompare2)) return item.getValue();
        }

        return price;
    }

    public ItemStack removePriceLore ( ItemStack item) {
        ItemStack item2 = item.clone();
        ItemMeta meta = null;
        meta = item2.getItemMeta();

        if(meta.hasLore()) {
            List<String> lore = meta.getLore();
            lore.remove(lore.size() - 1);
            meta.setLore(lore);
        }
        item2.setItemMeta(meta);

        return item2;
    }

    public ItemStack getBuyItem(ItemStack item) {
        for( Map.Entry<ItemStack, Double> s: main.listDailyItems.entrySet()) {
            if (s.getKey().isSimilar(item)) return s.getKey().clone();
        }
        return null;
    }




}
