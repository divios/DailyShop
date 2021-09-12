package io.github.divios.dailyShop.utils;

import io.github.divios.core_lib.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.lib.managers.shopsManager;
import me.xanium.gemseconomy.file.F;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    private static final DailyShop plugin = DailyShop.getInstance();

    public static void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createParentDirectory() {
        File localeDirectory = plugin.getDataFolder();

        if (!localeDirectory.exists())
            localeDirectory.mkdir();

    }

    public static void createParserFolder() {
        File parser = new File(plugin.getDataFolder(), "parser");
        parser.mkdir();
    }

    public static void createDatabaseFile() {
        File db = new File(plugin.getDataFolder(), "dailyshop.db");
        if (!db.exists()) {
            plugin.saveResource("dailyshop.db", false);
            Schedulers.sync().runLater(() -> {
                shopsManager.getInstance().getShops().forEach(shop -> {
                    shop.getGuis().reStock(true);
                });
            }, 60);
        }

    }

}
