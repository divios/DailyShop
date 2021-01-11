package io.github.divios.dailyrandomshop.Utils;

import io.github.divios.dailyrandomshop.Config;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.settings.dailyGuiSettings;
import io.github.divios.dailyrandomshop.GUIs.settings.sellGuiSettings;
import io.github.divios.dailyrandomshop.Tasks.updateLists;
import io.github.divios.dailyrandomshop.Tasks.updateTimer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    public static void reloadConfig(DailyRandomShop main, boolean reload) throws IOException {

        main.saveDefaultConfig();

        createLocale(main);
        main.config = new Config(main);

        createDB(main, reload);
        main.listDailyItems = main.dbManager.getDailyItems();
        main.listSellItems = main.dbManager.getSellItems();
        //readItems(main);
        HandlerList.unregisterAll(main.DailyGuiSettings);
        main.DailyGuiSettings = new dailyGuiSettings(main);
        HandlerList.unregisterAll(main.SellGuiSettings);
        main.SellGuiSettings = new sellGuiSettings(main);

        updateTimer.initTimer(main, reload);
        updateLists.initTask(main, reload);

        main.getLogger().info("Loaded " + main.listDailyItems.size() + " daily items");
        main.getLogger().info("Loaded " + main.listSellItems.size() + " sell items");
        //readTimer(main); antiguo con yaml
        if (reload) {
            main.BuyGui.inicializeGui(false);
        }
    }

    public static void createLocale(DailyRandomShop main) {
        File localeDirectory = new File(main.getDataFolder() + File.separator + "locales");

        if (!localeDirectory.exists() && !localeDirectory.isDirectory()) {
            localeDirectory.mkdir();
        }

        List<String> locales = new ArrayList<>();
        locales.add("en_US.yml");
        locales.add("es_ES.yml");
        locales.add("rus_RU.yml");

        for(String s: locales) {
            File locale = new File(main.getDataFolder() + File.separator + "locales" + File.separator + s);
            if (locale.exists()) continue;
            try{
                locale.createNewFile();
                InputStream in = main.getResource("locales/" + s);
                OutputStream out = new FileOutputStream(locale);
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
    }

    public static void resetTime(DailyRandomShop main) {
        main.time = main.getConfig().getInt("timer-duration");
    }

    public static void createDB(DailyRandomShop main, boolean reload) throws IOException {
        File file = new File(main.getDataFolder(), main.getDescription().getName().toLowerCase() + ".db");

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

        if (!reload) {
            int time = main.dbManager.getTimer();

            if (time == -1) ConfigUtils.resetTime(main);
            else main.time = time;
        }
    }

    public static void CloseAllInventories(DailyRandomShop main) {
        for (HumanEntity h : main.BuyGui.getInventory().getViewers()) {
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

        for (String key : file.getKeys(false)) {

        }

        return slot;
    }

}