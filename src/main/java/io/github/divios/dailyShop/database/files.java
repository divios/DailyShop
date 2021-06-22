package io.github.divios.dailyShop.database;

import io.github.divios.dailyShop.DRShop;

import java.io.File;
import java.io.IOException;

public class files {

    private static final DRShop main = DRShop.getInstance();

    public static void createdb() throws IOException {
        File localeDirectory = new File(main.getDataFolder() + File.separator + "locales");

        if (!localeDirectory.exists() && !localeDirectory.isDirectory()) {
            localeDirectory.mkdir();
        }

        File file = new File(main.getDataFolder(), main.getDescription().getName().toLowerCase() + ".db");

        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
