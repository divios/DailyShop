package io.github.divios.dailyrandomshop.Utils;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        aux.setItemMeta(meta);

        try {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } catch (NoSuchFieldError ignored) {}
        p.getInventory().addItem(aux);
        main.econ.withdrawPlayer(p, price);
        p.sendMessage(main.config.PREFIX + main.config.MSG_BUY_ITEM.replace("{price}", String.format("%,.2f", price)).replace("{item}", item.getType().toString()));
        p.openInventory(main.BuyGui.getInventory());
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

    public ItemStack removeItemAmount(ItemStack item) {

        item.setAmount(1);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey("setAmount");
        return nbtItem.getItem();
    }

    public void processItemAmount(ItemStack item, int slot) {
        if(item.getAmount() == 1) {
            item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();

        } else item.setAmount(item.getAmount() - 1);

        main.BuyGui.getInventory().setItem(slot, item);

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

    public ItemStack removeItemAsDaily(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey("DailyItem");
        return nbtItem.getItem();
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

    public boolean isMMOItem(ItemStack item) {
        try {
            net.mmogroup.mmolib.api.item.NBTItem NBTItem = net.mmogroup.mmolib.api.item.NBTItem.get(item);
            return NBTItem.hasType();
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            return false;
        }
    }

    public String[] getMMOItemConstruct(ItemStack item) {

        net.mmogroup.mmolib.api.item.NBTItem NBTItem = net.mmogroup.mmolib.api.item.NBTItem.get(item);
        String type = NBTItem.getType();
        String id = NBTItem.getString("MMOITEMS_ITEM_ID");

        return new String[]{type, id};
    }

    public ItemStack setItemAsScracth(ItemStack item) {
        NBTItem NBTItem = new NBTItem(item);
        NBTItem.setString("Scratch", "true");

        return NBTItem.getItem();
    }

    public ItemStack removeItemScracth(ItemStack item) {
        NBTItem NBTItem = new NBTItem(item);
        NBTItem.removeKey("Scratch");

        return NBTItem.getItem();
    }

    public boolean isItemScracth (ItemStack item) {
        NBTItem NBTItem = new NBTItem(item);

        return NBTItem.hasKey("Scratch");
    }

    public ItemStack setItemAsCommand(ItemStack item, List<String> command) {
        NBTItem nbtItem = new NBTItem(item);
        String commands = "";

        for(String s: command) {
            commands = commands.concat(";" + s);
        }

        if(!commands.isEmpty()) commands = commands.substring(1);

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

    public ItemStack removeItemCommand(ItemStack item) {
        NBTItem NBTitem = new NBTItem(item);
        NBTitem.removeKey("Command");

        return NBTitem.getItem();
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

    public void removeItemOnList(LinkedHashMap<ItemStack, Double> list, ItemStack item) {

        for (ItemStack entryItem: list.keySet()) {
            if(!entryItem.isSimilar(item)) continue;

            list.remove(entryItem);
            return;
        }

    }

    public void replacePriceOnList(LinkedHashMap<ItemStack, Double> list, ItemStack item, Double price) {

        for (ItemStack entryItem: list.keySet()) {
            if(!entryItem.isSimilar(item)) continue;

            list.replace(entryItem, price);
            return;
        }
    }

    public boolean listContaisItem(LinkedHashMap<ItemStack, Double> list, ItemStack item) {
        for (ItemStack entryItem: list.keySet()) {
            if(entryItem.isSimilar(item)) return true;
        }
        return false;
    }

    public void waitXticks(long ticks) {
        Bukkit.getScheduler().runTaskLater(main, () -> {

        }, ticks);
    }

    public int getRarity(ItemStack item) {
        int rarity = 100;
        NBTItem nbtItem = new NBTItem(item);
        if(nbtItem.hasKey("rarityRdshop")) {
            rarity = nbtItem.getInteger("rarityRdshop");
        }

        return rarity;
    }

    public String getRarityString(ItemStack item) {
        String rarity = "";
        NBTItem nbtItem = new NBTItem(item);
        switch (getRarity(item)) {
            case 100:
                rarity = "Common";
                break;
            case 80:
                rarity = "unCommon";
                break;
            case 60:
                rarity = "Rare";
                break;
            case 40:
                rarity = "Epic";
                break;
            case 20:
                rarity = "Ancient";
                break;
            case 10:
                rarity = "Legendary";
                break;
            case 5:
                rarity = "Mythic";
                break;
        }
        return rarity;
    }

    //common (100), uncommon (80), rare (60), epic (40), ancient (20), legendary (10), mythic (5)
    public ItemStack processNextRarity(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if(!nbtItem.hasKey("rarityRdshop")) {
            nbtItem.setInteger("rarityRdshop", 80);
        }
        else {
            switch (nbtItem.getInteger("rarityRdshop")) {
                case 80: nbtItem.setInteger("rarityRdshop", 60); break;
                case 60: nbtItem.setInteger("rarityRdshop", 40); break;
                case 40: nbtItem.setInteger("rarityRdshop", 20); break;
                case 20: nbtItem.setInteger("rarityRdshop", 10); break;
                case 10: nbtItem.setInteger("rarityRdshop", 5); break;
                case 5: nbtItem.removeKey("rarityRdshop"); break;
            }
        }
        return nbtItem.getItem();
    }

    public void setRarityLore(ItemStack item, int rarity) {

        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if (meta.hasLore()) lore = meta.getLore();
        else lore = new ArrayList<>();
        switch (rarity) {
            case 100:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Common"));
                break;
            case 80:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "UnCommon"));
                break;
            case 60:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Rare"));
                break;
            case 40:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Epic"));
                break;
            case 20:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Ancient"));
                break;
            case 10:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Legendary"));
                break;
            case 5:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Mythic"));
                break;
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

}
