package io.github.divios.dailyrandomshop.Utils;

import io.github.divios.dailyrandomshop.Config;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
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
        if(reload) {
            readTimer(main);
            main.BuyGui.inicializeGui(true);
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
                Material.valueOf(key.toUpperCase(Locale.ROOT));
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

    static void readTimer(DailyRandomShop main) {
        File customFile;
        FileConfiguration file;

        customFile = new File(main.getDataFolder(), "time.yml");
        file =YamlConfiguration.loadConfiguration(customFile);

        if(customFile.exists()) main.time = Integer.parseInt(file.getString("currentime.time"));
        else main.time = main.getConfig().getInt("timer-duration");
        initTimer(main);
    }

    static void initTimer(DailyRandomShop main) {

        final File customFile = new File(main.getDataFolder(), "time.yml");
        final FileConfiguration file = YamlConfiguration.loadConfiguration(customFile);

        if (customFile.exists()) {
            main.time = file.getInt("currentime.time");
        } else resetTime(main);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                if (main.time == 0) {
                    main.BuyGui.createRandomItems();
                    resetTime(main);
                    return;
                }
                main.time--;
                if (main.time % 60 == 0) {
                    file.set("currentime.time", main.time);
                    try {
                        file.save(customFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 20L, 20L);
    }

    public static void resetTime(DailyRandomShop main) {
        main.time = main.getConfig().getInt("timer-duration");
    }

}