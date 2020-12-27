package io.github.divios.dailyrandomshop.Utils;

import io.github.divios.dailyrandomshop.Config;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.Database.DataManager;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import io.github.divios.dailyrandomshop.Tasks.UpdateTimer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;

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

        main.listMaterials = new HashMap<String, Double[]>();

        file = YamlConfiguration.loadConfiguration(customFile);
        for (String key : file.getKeys(false)) {

            try {
                Material.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                main.getLogger().warning("The material " + key.toUpperCase(Locale.ROOT) + " doesnt exist on this version of minecraft, skipping material");
                continue;
            }

            Double buyPrice = Double.parseDouble(file.getString(key + ".buyPrice"));
            Double sellPrice = Double.parseDouble(file.getString(key + ".sellPrice"));

            if (buyPrice < 0 || sellPrice < 0) {
                main.getLogger().warning("Negative values on " + key + " , skipping item");
                continue;
            }

            Double[] prices = {buyPrice, sellPrice};

            main.listMaterials.put(key.toUpperCase(Locale.ROOT), prices);

            //file.set(material + ".buyPrice", buyPrice);
            //file.set(material + ".sellPrice", sellPrice);

        }

        if (main.listMaterials.isEmpty()) {
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