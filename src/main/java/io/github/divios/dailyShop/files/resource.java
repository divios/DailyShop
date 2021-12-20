package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.Timer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public abstract class resource {

    private static final DailyShop plugin = DailyShop.get();

    protected final String name;
    protected boolean firstTime = true;

    private File file;
    protected YamlConfiguration yaml;
    private long checkSum;

    protected resource(String name) {
        this.name = name;
        file = new File(plugin.getDataFolder(), name);
        create();
        firstTime = false;
    }

    public void create() {

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        Timer timer = Timer.create();
        Log.info(getStartMessage());
        long checkSumAux;
        if ((checkSumAux = FileUtils.getFileCheckSum(file)) == (checkSum)) { // If same checkSum -> no changes
            timer = null;
            Log.info(getCanceledMessage());
            return;
        }
        checkSum = checkSumAux;

        yaml = YamlConfiguration.loadConfiguration(file);
        copyDefaults();

        init();

        timer.stop();
        Log.info(getFinishedMessage(timer.getTime()));
    }

    public void reload() {
        create();
    }

    protected abstract String getStartMessage();

    protected abstract String getCanceledMessage();

    protected abstract String getFinishedMessage(long time);

    protected abstract void init();

    private void copyDefaults() {
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(plugin.getResource(name), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        YamlConfiguration defConfig = null;
        if (defConfigStream != null) {
            defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            yaml.setDefaults(defConfig);
            yaml.options().copyDefaults(true);
        }

        try { yaml.save(file); }
        catch (IOException e) { e.printStackTrace(); }

        if (defConfig != null) yaml.setDefaults(defConfig);
    }

}