package io.github.divios.dailyrandomshop.database;

import java.io.File;
import java.io.IOException;

public class files {

    private static io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

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
