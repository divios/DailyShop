package io.github.divios.dailyrandomshop.Utils;

import com.cryptomorin.xseries.XEnchantment;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.Config;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import io.github.divios.dailyrandomshop.Tasks.UpdateTimer;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class ConfigUtils {

    public static void reloadConfig(DailyRandomShop main, boolean reload) throws IOException {

        File customFile;
        FileConfiguration file;

        main.saveDefaultConfig();
        main.config = new Config(main);

        customFile = new File(main.getDataFolder(), "items.yml");

        if (!customFile.exists()) { // si no existe items.yml lo creamos
            customFile.createNewFile();

            try (InputStream in = main.getResource("items.yml")) {
                OutputStream out = new FileOutputStream(customFile);
                byte[] buffer = new byte[1024];
                int lenght = in.read(buffer);
                while (lenght != -1) {
                    out.write(buffer, 0, lenght);
                    lenght = in.read(buffer);
                }
                //ByteStreams.copy(in, out); BETA method, data losses ahead
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        readItems(main);
        createDB(main, reload);
        new BukkitRunnable() {
            @Override
            public void run () {
                // do stuff
            }
        }.runTaskLater(main, 5);

        UpdateTimer.initTimer(main, reload);
        main.getLogger().info("Loaded " + main.listDailyItems.size() + " daily items");
        main.getLogger().info("Loaded " + main.listSellItems.size() + " sell items");
        //readTimer(main); antiguo con yaml
        if(reload) {
            main.BuyGui.inicializeGui(false);
            main.ConfirmGui = new confirmGui(main);
        }
    }

    static void readItems(DailyRandomShop main) {

        File customFile = new File(main.getDataFolder(), "items.yml");
        FileConfiguration file;

        main.listDailyItems = new HashMap<>();
        try {
            main.listSellItems = main.dbManager.getSellItems();
        } catch (Exception e) {
            main.listSellItems = new HashMap<>();
        }

        file = YamlConfiguration.loadConfiguration(customFile);
        for (String key : file.getKeys(false)) {
            ItemStack item;
            types type;
            Material material;
            Map<Enchantment, Integer> enchants = new HashMap<>();


            try {
                type = types.valueOf(file.getString(key + ".type").toUpperCase());
            } catch (NoSuchFieldError e){
                main.getLogger().warning("The entry " + key + " has an error on the value field, skipping");
                continue;
            }

            try {
                material = Material.valueOf(file.getString(key + ".material").toUpperCase());
            } catch (IllegalArgumentException e){
                main.getLogger().warning("The entry " + key + " has an error on the value material, skipping");
                continue;
            }

            item = new ItemStack(material);

            if (!file.getStringList(key + ".nbtValues").isEmpty()) {

                NBTItem nbtItem = new NBTItem(item);
                List<String> nbtValues = file.getStringList(key + ".nbtValues");

                for(String s: nbtValues) {
                    String nbtKey = s.split(":")[0];
                    String nbtValue = s.split(":")[1];
                    if(nbtValue.equals("null")) nbtValue = "";
                    nbtItem.setString(nbtKey, nbtValue);
                }
                item = nbtItem.getItem();
            }
            item = main.utils.setItemAsDaily(item);

            if (file.getInt(key + ".amount") != 0) {
                int amount = file.getInt(key + ".amount");
                if (amount <= 0) {
                    main.getLogger().warning("The entry " + key + " has an error on the value amount, skipping");
                } else {
                    item = main.utils.setItemAsAmount(item);
                    item.setAmount(amount);
                }
            }

            if (!file.getStringList(key + ".flags").isEmpty()) {
                ItemMeta meta = item.getItemMeta();
                try {
                    for (String s : file.getStringList(key + ".flags")) {
                        meta.addItemFlags(ItemFlag.valueOf(s));
                    }
                    item.setItemMeta(meta);
                } catch (IllegalArgumentException e) {
                    main.getLogger().warning("The entry " + key + " has an error on the flag, skipping");
                    continue;
                }
            }

            List<String> enchantss = file.getStringList(key + ".enchantments");
            try {
                for (String s : enchantss) {
                    if (!enchantss.isEmpty() || Enchantment.getByName(s.split(":")[0]) != null) {

                        enchants.put(Enchantment.getByName(s.split(":")[0]), Integer.parseInt(s.split(":")[1]));
                    }
                }
            } catch (IllegalArgumentException e) {
                main.getLogger().warning("Item on key " + key + " has an error on the value enchants, skipping");
                continue;
            }

            if(!enchants.isEmpty()) {
                try {
                    item.addUnsafeEnchantments(enchants);
                } catch (Exception e){
                    main.getLogger().warning("The entry " + key + " has an error on the value enchants, skipping");
                    continue;
                }
            }

            List<String> lore = file.getStringList(key + ".lore");
            ItemMeta meta = item.getItemMeta();
            if (!lore.isEmpty()) {
                List<String> loreColor = new ArrayList<>();
                for (String s: lore) {
                    loreColor.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                meta.setLore(loreColor);

            }

            String name = file.getString(key + ".name");
            if (name != null && !name.isEmpty()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);

            if (type == types.COMMAND) {
                if (file.getStringList(key + ".commands") == null ||
                        file.getStringList(key + ".commands").isEmpty()) {
                    main.getLogger().warning("Item on key " + key + " has to specify a command");
                    continue;
                }
                item = main.utils.setItemAsCommand(item, file.getStringList(key + ".commands"));
            }

            if (file.getDouble(key + ".price") <= 0 ) {
                main.getLogger().warning("Item on key " + key + " has to specify price and cannot be <= 0");
                continue;
            }

            if (item.getType().toString().equalsIgnoreCase("POTION") ||
                    item.getType().toString().equalsIgnoreCase("SPLASH_POTION") ) {
                PotionMeta pmetaitem = (PotionMeta) item.getItemMeta();
                String[] effect;
                String[] color = file.getString(key + ".color").split(",");
                try {
                    for (String s : file.getStringList(key + ".effects")) {

                        effect = s.split(",");

                        pmetaitem.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect[0]), Integer.parseInt(effect[1]), Integer.parseInt(effect[2])), true);
                        try {
                            pmetaitem.setColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                        } catch (NoSuchMethodError Ignored) {}
                    }
                } catch (Exception e) {
                    main.getLogger().warning("Item on key " + key + " has invalid effects");
                    continue;
                }
                item.setItemMeta(pmetaitem);
            }


            main.listDailyItems.put(item, file.getDouble(key + ".price"));

        }

        if (main.listDailyItems.isEmpty()) {
            main.getLogger().severe("items.yml is either empty, with negative values or materials not supported in this version, please check it");
            main.getServer().getPluginManager().disablePlugin(main);
        }

    }

    public static void migrateItemToConfig(DailyRandomShop main, ItemStack item, Double price) throws IOException {
        File customFile = new File(main.getDataFolder(), "items.yml");
        FileConfiguration file;

        file = YamlConfiguration.loadConfiguration(customFile);
        String key = "" + main.listDailyItems.size();

        file.set(key + ".type", "item");

        file.set(key + ".material", item.getType().toString());
        if(item.getItemMeta().getDisplayName() != null) {
            file.set(key + ".name", item.getItemMeta().getDisplayName().replaceAll("§", "&"));
        }
        if(item.getItemMeta().hasLore()) {
            List<String> lore = new ArrayList<>();
            for (String s: item.getItemMeta().getLore()) {
                lore.add(s.replaceAll("§", "&"));
            }
            file.set(key + ".lore", lore);
        }
        List<String> enchants = new ArrayList<>();
        if(!item.getEnchantments().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> e: item.getEnchantments().entrySet()) {
                enchants.add(e.getKey().getKey().getKey().toUpperCase().concat(":" + e.getValue()));
            }
            file.set(key + ".enchantments", enchants.toArray(new String[0]));
        }
        file.set(key + ".price", price);

        List<String> flags = new ArrayList<>();
        if(!item.getItemMeta().getItemFlags().isEmpty()) {
            for (ItemFlag e: item.getItemMeta().getItemFlags()) {
                flags.add(e.toString());
            }
            file.set(key + ".flags", flags);
        }

        List<String> nbtValues = main.utils.getNBT(item);
        if(!nbtValues.isEmpty()) {
            file.set(key + ".nbtValues", nbtValues);
        }
        file.save(customFile);
    }

    public static void resetTime(DailyRandomShop main) {
        main.time = main.getConfig().getInt("timer-duration");
    }

    public static void createDB(DailyRandomShop main, boolean reload) throws IOException {
        File file = new File(main.getDataFolder() + File.separator + main.getDescription().getName().toLowerCase() + ".db");

        if (!file.exists()) {
            file.createNewFile();
            try {
                main.dbManager.createTables();
                ConfigUtils.resetTime(main);
            } catch (SQLException throwables) {
                main.getLogger().severe("Couldn't create db tables");
                main.getServer().getPluginManager().disablePlugin(main);
            }
        }
        else if (!reload){
            try {
                main.time = main.dbManager.getTimer();
            } catch (SQLException throwables) {
                //throwables.printStackTrace();
                main.getLogger().warning("Couldn't read timer value from database, setting it to value on config");
                ConfigUtils.resetTime(main);
            }
        }
    }

    public static void CloseAllInventories(DailyRandomShop main) {
        for(HumanEntity h: main.BuyGui.getGui().getViewers()) {
            Player p = (Player) h;
            p.closeInventory();
        }
        /*for(Player p: main.getServer().getOnlinePlayers()) {
            if(p.getOpenInventory().getTopInventory().getTitle().equals(main.config.CONFIRM_GUI_NAME)){
                p.closeInventory();
            }
        }*/

    }

    public static int getAvariableSlot(DailyRandomShop main) {
        File customFile = new File(main.getDataFolder(), "items.yml");
        FileConfiguration file;
        int slot = -1;
        file = YamlConfiguration.loadConfiguration(customFile);

        List<String> keysOrdened = new ArrayList<>();
        file = YamlConfiguration.loadConfiguration(customFile);

        for(String key: file.getKeys(false)) {

        }

        return slot;
    }

}