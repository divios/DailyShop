package io.github.divios.dailyShop.files;

import com.cryptomorin.xseries.ReflectionUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.Timer;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.dShopState;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.serialize.serializerApi;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class shopsResource {

    private static final DailyShop plugin = DailyShop.get();
    private static final File shopsFolder = new File(plugin.getDataFolder(), "shops");
    private static final shopsManager sManager = DailyShop.get().getShopsManager();

    //private static final Map<String, Long> cacheCheckSums = new HashMap<>();
    //private static final Set<String> flaggedShops = new HashSet<>();

    public shopsResource() {
        if (!shopsFolder.exists()) {

            shopsFolder.mkdir();
            if (sManager.getShops().isEmpty() && ReflectionUtils.VER >= 12) {
                Stream.of("blocks", "drops", "equipment", "farm", "menu", "ore", "potion", "wood")
                        .forEach(s -> {
                            plugin.saveResource("shops/" + s + ".yml", false);
                        });
            } else {
                Log.warn("Initialization migration to yaml...");
                shopsFolder.mkdir();
                sManager.saveAllShops();        // Migration before 3.6.0
                Log.warn("Migration completed!");
            }
        }

        importYamlShops();
    }

    private void importYamlShops() {
        Log.info("Importing data from shops directory...");
        Timer timer = Timer.create();
        DebugLog.warn("First reading yaml from shops directory");
        Map<String, dShopState> newShops = readYamlShops();


        DebugLog.warn("Applying logic...");


        new HashSet<>(sManager.getShops()).stream()         // Delete removed shops
                //.filter(dShop -> !flaggedShops.contains(dShop.getName()))
                .filter(shop -> !newShops.containsKey(shop.getName()))
                .forEach(shop -> {
                    //cacheCheckSums.remove(shop.getName());
                    sManager.deleteShopAsync(shop);
                    DebugLog.info("removed shop");
                });

        newShops.values().forEach(shopState -> {                          // Process read Shops
            boolean isNew = false;
            dShop currentShop;

            if (!sManager.getShop(shopState.getName()).isPresent()) {        // Create new shops
                isNew = true;
                currentShop = sManager.createShopAsync(shopState.getName());
            } else
                currentShop = sManager.getShop(shopState.getName()).get();         // Update shops

            currentShop.setState(shopState);

            if (isNew) {
                currentShop.reStock();
                Log.info("Registered shop of name " + shopState.getName() + " with " + shopState.getItems().size() + " items");
            }
            else Log.info("Updated shop of name " + shopState.getName() + " with " + shopState.getItems().size() + " items");
        });

        timer.stop();
        Log.info("Data imported successfully in " + timer.getTime() + " ms");

        //flaggedShops.clear();

    }

    protected void reload() {
        importYamlShops();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<String, dShopState> readYamlShops() {

        Set<String> idCaches = new HashSet<>();
        Map<String, dShopState> shops = new HashMap<>();
        for (File shopFile : Objects.requireNonNull(shopsFolder.listFiles((dir, name) -> name.endsWith(".yml")), "The shop directory does not exits")) {

            try {
                dShopState newShop = serializerApi.getShopFromFile(shopFile);

                if (idCaches.contains(newShop.getName())) {
                    Log.severe("There is already a shop registered with the id " + newShop.getName() +
                            " Skipping file " + shopFile.getName());
                    continue;
                }

                shops.put(newShop.getName(), newShop);
                idCaches.add(newShop.getName());

            } catch (Exception e) {
                Log.warn("There was a problem with the shop " + shopFile.getName());
                // e.printStackTrace();
                Log.warn(e.getMessage());
            }
        }
        return shops;
    }

    private String getIdFromFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return Objects.requireNonNull(yaml.getString("id"), "Shop File needs an ID!").toLowerCase();
    }

}
