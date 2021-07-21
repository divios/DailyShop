package io.github.divios.dailyShop.files;

public class configManager {

    private final langResource langYml;
    private final settingsResource settingsYml;

    public static configManager generate() {
        return new configManager();
    }

    private configManager() {
        langYml = new langResource();
        settingsYml = new settingsResource();
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
