package io.github.divios.dailyrandomshop.utils;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.xseries.SkullUtils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class utils {

    private static final DRShop main = DRShop.getInstance();

    public static void translateAllItemData(ItemStack recipient, ItemStack receiver) {
        try {
            receiver.setData(recipient.getData());
            receiver.setType(recipient.getType());
            receiver.setItemMeta(recipient.getItemMeta());
            receiver.setAmount(recipient.getAmount());
            receiver.setDurability(recipient.getDurability());
        } catch (IllegalArgumentException ignored) {}
    }

    public static void translateAllItemData(ItemStack recipient,
                                            ItemStack receiver, boolean dailyMetadata) {
        try {
            receiver.setData(recipient.getData());
            receiver.setType(recipient.getType());
            receiver.setItemMeta(recipient.getItemMeta());
            receiver.setAmount(recipient.getAmount());
            receiver.setDurability(recipient.getDurability());
            //if(dailyMetadata) dailyItem.transferDailyMetadata(recipient, receiver);
        } catch (IllegalArgumentException ignored) {}
    }

    public static void setDisplayName(ItemStack item, String name) {
        if (name == null) return;
        ItemMeta meta = getItemMeta(item);
        meta.setDisplayName(formatString(name));
        item.setItemMeta(meta);
    }

    public static void setLore(ItemStack item, List<String> lore) {
        if (lore == null) return;
        ItemMeta meta = getItemMeta(item);
        List<String> coloredLore = getItemLore(meta);
        if (coloredLore == null) coloredLore = new ArrayList<>();
        for (String s : lore) {
            coloredLore.add(formatString(s));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
    }

    public static ItemMeta getItemMeta(ItemStack item) {
        ItemMeta meta;
        if(item.hasItemMeta()) meta = item.getItemMeta();
        else meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        return meta;
    }

    public static List<String> getItemLore(ItemMeta meta) {
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) lore = meta.getLore();
        return lore;
    }

    public static void removeLore(ItemStack item, int n) {
        ItemMeta meta = getItemMeta(item);
        List<String> lore = getItemLore(meta);
        if (isEmpty(lore)) return;
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

    public static boolean isEmpty(String s) { return s == null || s.isEmpty(); }

    public static boolean isEmpty(List<String> s) { return s == null || s.isEmpty(); }

    /**
     * Queue a task to be run asynchronously. <br>
     *
     * @param runnable task to run
     */
    public static BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(main, runnable);
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public static BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(main, runnable);
    }

    public static void runTaskLater(Runnable r, Long ticks) {
        Bukkit.getScheduler().runTaskLater(main, r, ticks);
    }

    public static void sendSound(Player p, Sound s) {
        try {
            p.playSound(p.getLocation(), s, 0.5F, 1);
        } catch (NoSuchFieldError Ignored) {
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


    /**
     *
     * @param inv
     * @return amount of free slots on inventory (excluding armor). If inventory is full returns 0
     */

    public static int inventoryFull (Inventory inv) {

        int freeSlots = 0;
        for (int i = 0; i < 36; i++) {

            if (utils.isEmpty(inv.getItem(i))) {
                freeSlots++;
            }
        }
        return freeSlots;
    }

    public static void noPerms(CommandSender p) {
        p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_PERMS);
    }

    public static void noCmd(CommandSender p) {
        p.sendMessage(conf_msg.PREFIX + utils.formatString("&7Console is no allow to do this command"));
    }


    public static Double getPriceModifier(Player p) {
        AtomicReference<Double> modifier = new AtomicReference<>(1.0);

        p.getEffectivePermissions().forEach(perms -> {
            String perm = perms.getPermission();
            if (perm.startsWith("dailyrandomshop.sellpricemodifier.")) {
                String[] splitStr = perm.split("dailyrandomshop.sellpricemodifier.");
                if(splitStr.length == 1) return;
                double newValue;
                try{
                    newValue = Math.abs(Double.parseDouble(splitStr[1]));
                } catch (NumberFormatException e) { return; }
                if (newValue > modifier.get())
                    modifier.set(newValue);
            }
        });
        return modifier.get();
    }

    public static String getDisplayName(ItemStack item) {
        String name;

        if (item.getItemMeta().hasDisplayName()) name =
                item.getItemMeta().getDisplayName();

        else name = item.getType().toString();

        return name;
    }

    public static boolean isOperative(String pl) {
        return Bukkit.getPluginManager().getPlugin(pl) != null &&
                Bukkit.getPluginManager().getPlugin(pl).isEnabled();
    }

    public static double round(double d, int decimals) {
        return Math.round(d * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static void applyTexture(ItemStack item, String url) {
        item.setItemMeta(SkullUtils.applySkin(item.getItemMeta(), url));
    }


}
