package io.github.divios.dailyShop.files;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;

public class configManager {

    private static final DailyShop main = DailyShop.getInstance();
    private final langResource langYml;
    private final settingsResource settingsYml;

    public static configManager generate() {
        return new configManager();
    }

    private configManager() {

        FileUtils.createParentDirectory();
        langYml = new langResource();
        settingsYml = new settingsResource();
        FileUtils.createParserFolder();
        FileUtils.createDatabaseFile();
    }

    public synchronized langResource getLangYml() {
        return langYml;
    }

    public synchronized settingsResource getSettingsYml() {
        return settingsYml;
    }

    public synchronized void reload() {
        langYml.reload();
        settingsYml.reload();
    }
}
