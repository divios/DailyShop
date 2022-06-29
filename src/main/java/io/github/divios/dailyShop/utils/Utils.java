package io.github.divios.dailyShop.utils;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.jtext.JText;
import io.github.divios.jtext.JTextBuilder;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "deprecation"})
public class Utils {

    private static final DailyShop plugin = DailyShop.get();
    public static final JTextBuilder JTEXT_PARSER;

    static {
        JTEXT_PARSER = JText.builder()
                .withTag("\\{", "\\}")
                .parseChatColors()
                .parseHexColors()
                .parsePlaceholderAPI()
                .parseGradients()
                .parseWithAdventure();
    }

    public static void sendRawMsg(Player p, String s) {
        p.sendMessage(JTEXT_PARSER.parse(Settings.PREFIX.getValue().getAsString() + " " + s, p));
    }

    public static void translateAllItemData(ItemStack recipient, ItemStack receiver) {
        try {
            receiver.setData(recipient.getData());
            receiver.setType(recipient.getType());
            receiver.setItemMeta(recipient.getItemMeta());
            receiver.setAmount(recipient.getAmount());
            receiver.setDurability(recipient.getDurability());
        } catch (IllegalArgumentException ignored) {
        }
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
        } catch (IllegalArgumentException ignored) {
        }
    }

    public static ItemMeta getItemMeta(ItemStack item) {
        ItemMeta meta;
        if (item.hasItemMeta()) meta = item.getItemMeta();
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

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isEmpty(List<String> s) {
        return s == null || s.isEmpty();
    }


    public static void sendSound(Player p, Sound s) {
        try {
            p.playSound(p.getLocation(), s, 0.5F, 1);
        } catch (NoSuchFieldError ignored) {
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
        return ItemBuilder.of(XMaterial.RED_STAINED_GLASS_PANE)
                .setName("&cOut of stock");
    }

    public static void removeFlag(ItemStack i, @NotNull ItemFlag f) {
        ItemMeta meta = i.getItemMeta();
        meta.removeItemFlags(f);
        i.setItemMeta(meta);
    }

    public static boolean isPotion(ItemStack item) {
        return item.getType().equals(XMaterial.POTION.parseMaterial()) ||
                item.getType().equals(XMaterial.SPLASH_POTION.parseMaterial());
    }


    /**
     * @return amount of free slots on inventory (excluding armor). If inventory is full returns 0
     */

    public static int inventoryFull(Inventory inv) {

        int freeSlots = 0;
        for (int i = 0; i < 36; i++) {

            if (Utils.isEmpty(inv.getItem(i))) {
                freeSlots++;
            }
        }
        return freeSlots;
    }

    public static void noPerms(CommandSender p) {
        Messages.MSG_NOT_PERMS.send(p);
    }

    public static void noCmd(CommandSender p) {
        Utils.sendRawMsg((Player) p, "&7Console is no allow to do this command");
    }


    public static Double getPriceModifier(Player p) {
        AtomicReference<Double> modifier = new AtomicReference<>(1.0);

        p.getEffectivePermissions().forEach(perms -> {
            String perm = perms.getPermission();
            if (perm.startsWith("DailyShop.sellpricemodifier.")) {
                String[] splitStr = perm.split("DailyShop.sellpricemodifier.");
                if (splitStr.length == 1) return;
                double newValue;
                try {
                    newValue = Math.abs(Double.parseDouble(splitStr[1]));
                } catch (NumberFormatException e) {
                    return;
                }
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
        return Bukkit.getPluginManager().getPlugin(pl) != null;
    }

    public static double round(double d, int decimals) {
        return Math.round(d * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isShort(String s) {
        try {
            Short.parseShort(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getDiffActualTimer(dShop shop) {
        Duration toCompare = Duration.ofSeconds(shop.getTimer());
        LocalDateTime now = LocalDateTime.now();

        Duration diff = Duration.between(shop.getTimestamp(), now);
        Duration totalDiff = toCompare.minus(diff);

        StringBuilder formattedStr = new StringBuilder();
        if (totalDiff.getSeconds() > 86400) {
            long days = TimeUnit.SECONDS.toDays(totalDiff.getSeconds());
            formattedStr.append(String.format((days == 1) ? "%d Day, " : "%d Days, ", days));
            totalDiff = totalDiff.minusDays(totalDiff.toDays());
        }

        formattedStr.append(String.format("%02d:%02d:%02d",
                totalDiff.toHours(),
                totalDiff.toMinutes() % 60,
                totalDiff.getSeconds() % 60)
        );

        return formattedStr.toString();
    }

    public static boolean playerIsOnline(Player player) {
        return Bukkit.getPlayer(player.getUniqueId()) != null;
    }

    public static boolean testRunnable(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void tryCatchAbstraction(Runnable tryRunnable, Consumer<Exception> catchRunnable) {
        try {
            tryRunnable.run();
        } catch (Exception e) {
            catchRunnable.accept(e);
        }
    }

    private static final Gson gson = new Gson();
    private static final Yaml yaml = new Yaml();

    public static JsonElement getJsonFromFile(File file) {
        Object loadedYaml = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            try (InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                try (BufferedReader br = new BufferedReader(isr)) {
                    loadedYaml = yaml.load(br);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return gson.toJsonTree(loadedYaml, LinkedHashMap.class);
    }

}
