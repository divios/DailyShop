package io.github.divios.dailyShop.files;

import com.google.common.collect.Lists;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.List;

public abstract class resource {

    private static final DailyShop plugin = DailyShop.getInstance();

    private final String name;

    private File file;
    protected YamlConfiguration yaml;
    private Long checkSum;

    protected resource(String name) {
        this.name = name;
        create();
    }

    public void create() {

        file = new File(plugin.getDataFolder(), name);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        Long checkSumAux;
        if ( (checkSumAux = FileUtils.getFileCheckSum(file)) == checkSum )  // If same checkSum -> no changes
            return;
        checkSum = checkSumAux;

        yaml = YamlConfiguration.loadConfiguration(file);
        copyDefaults();


        init();
    }

    protected abstract void init();

    protected List<String> getSetLines() {
        return Lists.newArrayList(yaml.getKeys(true));
    }

    public void reload() {
        create();
    }

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