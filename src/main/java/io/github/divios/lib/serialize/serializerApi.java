package io.github.divios.lib.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.divios.core_lib.cache.Lazy;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.serialize.adapters.dShopAdapter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class serializerApi {

    private static final DailyShop plugin = DailyShop.get();
    private static final Lazy<File> shopsFolder = Lazy.suppliedBy(() -> new File(plugin.getDataFolder(), "shops"));

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(dShop.class, new dShopAdapter())
            .create();

    public static void saveShopToFile(dShop shop) {
        try {
            File data = new File(shopsFolder.get(), shop.getName() + ".yml");
            FileUtils.dumpToYaml(gson.toJsonTree(shop), data);
        } catch (Exception e) {
            Log.info("There was a problem saving the shop " + shop.getName());
            e.printStackTrace();
        }
        //Log.info("Converted all items correctly of shop " + shop.getName());
    }

    public static dShop getShopFromFile(File data) {
        Objects.requireNonNull(data, "data cannot be null");
        Preconditions.checkArgument(data.exists(), "The file does not exist");
        return gson.fromJson(Utils.getJsonFromFile(data), dShop.class);
    }

    public static void saveShopToFileAsync(dShop shop) {
        Schedulers.async().run(() -> saveShopToFile(shop));
    }

    public static CompletableFuture<dShop> getShopFromFileAsync(File data) {
        return CompletableFuture.supplyAsync(() -> getShopFromFile(data));
    }

    public static void deleteShop(String name) {
        File[] files = shopsFolder.get().listFiles((dir, name1) -> name1.endsWith(".yml"));
        if (files == null) throw new RuntimeException("shops directory does not exits");
        for (int i = 0; i < files.length; i++) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(files[i]);
            if (yaml.get("id").equals(name)) {
                files[i].delete();
                break;
            }
        }
    }

}
