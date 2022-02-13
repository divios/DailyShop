package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.Timer;
import io.github.divios.lib.dLib.shop.dShop;
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
        DebugLog.warn("First reading yaml from shops directory");
        Set<dShop> newShops = readYamlShops();


        DebugLog.warn("Applying logic...");


        new HashSet<>(sManager.getShops()).stream()         // Delete removed shops
                .filter(shop -> !newShops.contains(shop))
                .forEach(shop -> {
                    cacheCheckSums.remove(shop.getName());
                    sManager.deleteShop(shop.getName());
                    DebugLog.info("removed shop");
                });

        newShops.forEach(shop -> {                          // Process read Shops
            boolean isNew = false;
            if (flaggedShops.contains(shop)) {              // If flagged, skip since no changes were made
                Log.info("No changes in shop " + shop.getName() + ", skipping...");
                return;
            }

            if (!sManager.getShop(shop.getName()).isPresent()) {        // Create new shops
                isNew = true;
                sManager.createShopAsync(shop.getName());
            }

            dShop currentShop = sManager.getShop(shop.getName()).get();         // Update shops

            currentShop.setTimer(shop.getTimer());
            currentShop.set_announce(shop.get_announce());
            currentShop.setDefault(shop.isDefault());
            currentShop.updateShopGui(shop.getGui());
            currentShop.setItems(shop.getItems());

            if (shop.getAccount() == null)
                currentShop.setAccount(null);
            else if (!shop.getAccount().isSimilar(currentShop.getAccount()))
                currentShop.setAccount(shop.getAccount());

            if (isNew) currentShop.reStock();

            if (isNew)
                Log.info("Registered shop of name " + shop.getName() + " with " + shop.getItems().size() + " items");
            else Log.info("Updated shop of name " + shop.getName() + " with " + shop.getItems().size() + " items");
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
            if ((checkSum = cacheCheckSums.get(getIdFromFile(shopFile))) != null)
                if (checkSum == FileUtils.getFileCheckSum(shopFile)) {
                    sManager.getShop(getIdFromFile(shopFile)).ifPresent(sameShop -> {
                        shops.add(sameShop);
                        flaggedShops.add(sameShop);
                    });  // get only the id of the shop
                    continue;
                }

            try {
                dShop newShop = serializerApi.getShopFromFile(shopFile);
                newShop.destroy();
                shops.add(newShop);
                cacheCheckSums.put(newShop.getName(), FileUtils.getFileCheckSum(shopFile));
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
