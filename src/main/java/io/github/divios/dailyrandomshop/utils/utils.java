package io.github.divios.dailyrandomshop.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class utils {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

    public static void setDisplayName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(formatString(name));
        item.setItemMeta(meta);
    }

    public static void setLore(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> coloredLore = new ArrayList<>();
        for (String s : lore) {
            coloredLore.add(formatString(s));
        }
        meta.setLore(coloredLore);
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
}
