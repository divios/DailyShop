package io.github.divios.dailyrandomshop.utils;

import io.github.divios.dailyrandomshop.builders.factory.itemsFactory;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class utils {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();

    public static void translateAllItemData(ItemStack recipient, ItemStack receiver) {
        receiver.setData(recipient.getData());
        receiver.setType(recipient.getType());
        receiver.setItemMeta(recipient.getItemMeta());
        receiver.setAmount(recipient.getAmount());
        receiver.setDurability(recipient.getDurability());
    }

    public static void setDisplayName(ItemStack item, String name) {
        if (name == null) return;
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(formatString(name));
        item.setItemMeta(meta);
    }

    public static void setLore(ItemStack item, List<String> lore) {
        if (lore == null) return;
        ItemMeta meta = item.getItemMeta();
        List<String> coloredLore = meta.getLore();
        if (coloredLore == null) coloredLore = new ArrayList<>();
        for (String s : lore) {
            coloredLore.add(formatString(s));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
    }

    public static void removeLore(ItemStack item, int n) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore.isEmpty() || lore == null) return;
        if (n == -1) lore.clear();
        else
            for (int i = 0; i < n; i++) {
                lore.remove(lore.size() - 1);
            }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static String formatString(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String trimString(String str) {
        return ChatColor.stripColor(str);
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Queue a task to be run asynchronously. <br>
     *
     * @param runnable task to run
     */
    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(main, runnable);
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(main, runnable);
    }

    public static void runTaskLater(Runnable r, Long ticks) {
        Bukkit.getScheduler().runTaskLater(main, r, ticks);
    }

    public static void sendSound(Player p, Sound s) {
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
        } catch (NoSuchFieldError Ignored) {
        }
    }

    public static ItemStack getItemByUuid(String uuid, Map<ItemStack, Double> list) {
        for (Map.Entry<ItemStack, Double> entry : list.entrySet()) {
            if (new itemsFactory.Builder(entry.getKey(), false).getUUID().equals(uuid)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void removeItemByUuid(String uuid, Map<ItemStack, Double> list) {
        list.entrySet().removeIf(e ->
                new itemsFactory.Builder(e.getKey()).getUUID().equals(uuid));
    }

    public static void changePriceByUuid(String uuid, Double price, Map<ItemStack, Double> list) {
        for (Map.Entry<ItemStack, Double> e : list.entrySet()) {
            if (new itemsFactory.Builder(e.getKey()).getUUID().equals(uuid)) e.setValue(price);
        }
    }

    public static int randomValue(int minValue, int maxValue) {

        return minValue + (int) (Math.random() * ((maxValue - minValue) + 1));
    }

    public static ItemStack getEntry(Map<ItemStack, Double> list, int index) {
        int i = 0;
        for (ItemStack item : list.keySet()) {
            if (index == i) return item;
            i++;
        }
        return null;
    }

    public static ItemStack getRedPane() {
        ItemStack redPane = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        utils.setDisplayName(redPane, "&cOut of stock");
        return redPane;
    }

    public static void addFlag(ItemStack i, ItemFlag f) {
        ItemMeta meta = i.getItemMeta();
        meta.addItemFlags(f);
        i.setItemMeta(meta);
    }

    public static void removeFlag(ItemStack i, ItemFlag f) {
        ItemMeta meta = i.getItemMeta();
        meta.removeItemFlags(f);
        i.setItemMeta(meta);
    }

    public static boolean hasFlag(ItemStack item, ItemFlag f) {
        return item.getItemMeta().hasItemFlag(f);
    }

    public static List<String> replaceOnLore(List<String> lore, String pattern, String replace) {
        List<String> loreX = new ArrayList<>();
        loreX.addAll(lore);
        loreX.replaceAll(s -> s.replaceAll(pattern, replace));
        return loreX;
    }

    public static boolean isPotion(ItemStack item) {
        return item.getType().equals(XMaterial.POTION.parseMaterial()) ||
                item.getType().equals(XMaterial.SPLASH_POTION.parseMaterial());
    }

    //common (100), uncommon (80), rare (60), epic (40), ancient (20), legendary (10), mythic (5)
    public static ItemStack getItemRarity(int s) {
        ItemStack changeRarity = null;
        switch (s) {
            case 0:
                changeRarity = XMaterial.GRAY_DYE.parseItem();
                utils.setDisplayName(changeRarity, "&7Common");
                break;
            case 80:
                changeRarity = XMaterial.PINK_DYE.parseItem();
                utils.setDisplayName(changeRarity, "&dUncommon");
                break;
            case 60:
                changeRarity = XMaterial.MAGENTA_DYE.parseItem();
                utils.setDisplayName(changeRarity, "&5Rare");
                break;
            case 40:
                changeRarity = XMaterial.PURPLE_DYE.parseItem();
                utils.setDisplayName(changeRarity, "&5Epic");
                break;
            case 20:
                changeRarity = XMaterial.CYAN_DYE.parseItem();
                utils.setDisplayName(changeRarity, "&9Ancient");
                break;
            case 10:
                changeRarity = XMaterial.ORANGE_DYE.parseItem();
                utils.setDisplayName(changeRarity, "&6Legendary");
                break;
            default:
                changeRarity = XMaterial.YELLOW_DYE.parseItem();
                utils.setDisplayName(changeRarity, "&eMythic");
                break;
        }
        return changeRarity;
    }

    public static String getRarityLore(int rarity) {

        switch (rarity) {
            case 0:
                return "Common";

            case 80:
                return "UnCommon";

            case 60:
                return "Rare";

            case 40:
                return "Epic";

            case 20:
                return "Ancient";

            case 10:
                return "Legendary";

            default:
                return "Mythic";

        }
    }

}
