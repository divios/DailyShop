package io.github.divios.dailyrandomshop.Utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.Config;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import io.github.divios.dailyrandomshop.Tasks.UpdateTimer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        UpdateTimer.initTimer(main, reload);
        //readTimer(main); antiguo con yaml
        if(reload) {
            main.BuyGui.inicializeGui(false);
            main.ConfirmGui = new confirmGui(main);
        }
    }

    static void readItems(DailyRandomShop main) {

        File customFile = new File(main.getDataFolder(), "items.yml");
        FileConfiguration file;

        main.listItem = new HashMap<>();

        file = YamlConfiguration.loadConfiguration(customFile);
        for (String key : file.getKeys(false)) {
            ItemStack item;
            types type;
            Material material;
            Map<Enchantment, Integer> enchants = new HashMap<>();

            try {
                type = types.valueOf(file.getString(key + ".type").toUpperCase());
            } catch (NoSuchFieldError e){
                main.getLogger().warning("The entry " + key + " has an error on the type field, skipping");
                continue;
            }

            try {
                material = Material.valueOf(file.getString(key + ".material").toUpperCase());
            } catch (NoSuchFieldError e){
                main.getLogger().warning("The entry " + key + " has an error on the type material, skipping");
                continue;
            }

            item = new ItemStack(material);

            List<String> enchantss = file.getStringList(key+".enchantments");

            for (String s: enchantss) {
                main.getLogger().warning(s.split(":")[0]);
                if (!enchantss.isEmpty() &&
                        EnchantmentWrapper.getByKey(NamespacedKey.minecraft(s.split(":")[0].toLowerCase())) != null)  {
                    enchants.put(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(s.split(":")[0].toLowerCase())), Integer.parseInt(s.split(":")[1]));
                }
            }

            if(!enchants.isEmpty()) {
                try {
                    item.addEnchantments(enchants);
                } catch (IllegalArgumentException e){
                    main.getLogger().warning("The entry " + key + " has an error on the type enchants, skipping");
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
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);

            if (type == types.COMMAND) {
                NBTItem nbti = new NBTItem(item);
                nbti.setString("Command", file.getString(key + ".command"));
                item = nbti.getItem();
            }

            if (file.getDouble(key + ".price") <= 0 ) {
                main.getLogger().warning("Item on key " + key + " has to specify price and cannot be <= 0");
                continue;
            }

            item = main.utils.setItemAsDaily(item);
            main.listItem.put(item, file.getDouble(key + ".price"));

        }

        if (main.listItem.isEmpty()) {
            main.getLogger().severe("items.yml is either empty, with negative values or materials not supported in this version, please check it");
            main.getServer().getPluginManager().disablePlugin(main);
        }

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
                throwables.printStackTrace();
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

}