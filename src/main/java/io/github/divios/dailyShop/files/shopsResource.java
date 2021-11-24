package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.Timer;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.parser.ParserApi;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class shopsResource {

    private static final DailyShop plugin = DailyShop.getInstance();
    private static final File shopsFolder = new File(plugin.getDataFolder(), "shops");
    private static final shopsManager sManager = shopsManager.getInstance();

    public shopsResource() {
        FileUtils.createShopsFolder();
        processNewShops();
    }

    private void processNewShops() {
        Log.info("Importing data from shops directory...");
        Timer timer = Timer.create();
        Set<dShop> currentShops = new HashSet<>(sManager.getShops());
        currentShops.forEach(shop -> Log.warn(shop.getName()));
        Set<dShop> newShops = getAllShopsFromFiles();

        deleteRemovedShops(currentShops, newShops);
        createNewlyAddedShops(currentShops, newShops);
        updateNonRemovedShops(currentShops, newShops);
        timer.stop();
        Log.info("Data imported successfully in " + timer.getTime() + " ms");
    }

    private Set<dShop> getAllShopsFromFiles() {
        Set<dShop> shops = new HashSet<>();
        for (File shopFile : Objects.requireNonNull(shopsFolder.listFiles(), "The shop directory does not exits")) {
            dShop newShop = ParserApi.getShopFromFile(shopFile);
            shops.add(newShop);
        }
        return shops;
    }

    protected void reload() {
        processNewShops();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void deleteRemovedShops(Set<dShop> currentShops, Set<dShop> newShops) {
        currentShops.stream()
                .filter(shop -> !newShops.contains(shop))
                .forEach(shop -> sManager.deleteShop(shop.getName()));
    }

    private void createNewlyAddedShops(Set<dShop> currentShops, Set<dShop> newShops) {
        newShops.stream()
                .filter(shop -> !currentShops.contains(shop))
                .forEach(dShop -> sManager.createShop(dShop));
    }

    private void updateNonRemovedShops(Set<dShop> currentShops, Set<dShop> newShops) {
        currentShops.stream()
                .filter(newShops::contains)
                .forEach(shop -> {
                    dShop newShop = newShops.stream()
                            .filter(shop1 -> shop1.equals(shop))
                            .findFirst().get();

                    shop.setItems(newShop.getItems());
                });
    }
}
