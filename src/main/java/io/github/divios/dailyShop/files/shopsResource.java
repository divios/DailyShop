package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.Timer;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.serialize.serializerApi;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class shopsResource {

    private static final DailyShop plugin = DailyShop.getInstance();
    private static final File shopsFolder = new File(plugin.getDataFolder(), "shops");
    private static final shopsManager sManager = shopsManager.getInstance();

    private static final Map<String, Long> cacheCheckSums = new HashMap<>();
    private static final Set<dShop> flaggedShops = new HashSet<>();

    public shopsResource() {

        if (!shopsFolder.exists()) {

            if (sManager.getShops().isEmpty()) {
                shopsFolder.mkdir();
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
        Set<dShop> newShops = readYamlShops();

        new HashSet<>(sManager.getShops()).stream()         // Delete removed shops
                .filter(shop -> !newShops.contains(shop))
                .forEach(shop -> sManager.deleteShop(shop.getName()));

        newShops.forEach(shop -> {                          // Process read Shops
            if (flaggedShops.contains(shop)) {              // If flagged, skip since no changes were made
                Log.info("No changes in shop " + shop.getName() + ", skipping...");
                return;
            }

            if (!sManager.getShop(shop.getName()).isPresent()) {        // Create new shops
                sManager.createShopAsync(shop);
            } else {                                                    // Update shops if exist
                dShop currentShop = sManager.getShop(shop.getName()).get();

                currentShop.setTimer(shop.getTimer());
                currentShop.updateShopGui(shop.getGuis().getDefault().skeleton());
                currentShop.setItems(shop.getItems());
            }
            Log.info("Registered shop of name " + shop.getName() + " with " + shop.getItems().size() + " items");
        });

        timer.stop();
        Log.info("Data imported successfully in " + timer.getTime() + " ms");

        flaggedShops.clear();

    }

    protected void reload() {
        importYamlShops();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    private Set<dShop> readYamlShops() {
        Set<dShop> shops = new HashSet<>();
        for (File shopFile : Objects.requireNonNull(shopsFolder.listFiles((dir, name) -> name.endsWith(".yml")), "The shop directory does not exits")) {

            Long checkSum;      // Check if same checkSum
            if ((checkSum = cacheCheckSums.get(shopFile.getName())) != null)
                if (checkSum == FileUtils.getFileCheckSum(shopFile)) {
                    dShop sameShop = sManager.getShop(getIdFromFile(shopFile)).get();  // get only the id of the shop
                    shops.add(sameShop);
                    flaggedShops.add(sameShop);
                    continue;
                }

            try {
                dShop newShop = serializerApi.getShopFromFile(shopFile);
                newShop.destroy();
                shops.add(newShop);
                cacheCheckSums.put(shopFile.getName(), FileUtils.getFileCheckSum(shopFile));

            } catch (Exception e) {
                Log.warn("There was a problem with the shop " + shopFile.getName());
                Log.warn(e.getMessage());
            }
        }
        return shops;
    }

    private String getIdFromFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return yaml.getString("id");
    }

}
