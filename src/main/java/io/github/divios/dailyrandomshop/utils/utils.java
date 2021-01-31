package io.github.divios.dailyrandomshop.utils;

import io.github.divios.dailyrandomshop.builders.itemsFactory;
import io.github.divios.dailyrandomshop.database.dataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class utils {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();

    public static void translateAllItemData(ItemStack recipient, ItemStack  receiver) {
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
        if(lore == null) return;
        ItemMeta meta = item.getItemMeta();
        List<String> coloredLore = meta.getLore();
        if( coloredLore == null) coloredLore = new ArrayList<>();
        for (String s : lore) {
            coloredLore.add(formatString(s));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
    }

    public static void removeLore(ItemStack item, int n) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if(lore.isEmpty() || lore == null) return;
        if(n == -1) lore.clear();
        else
            for(int i = 0; i < n; i++) {
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
}
