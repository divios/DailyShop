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
        Set<dShop> newShops = getAllShopsFromFiles();

        deleteRemovedShops(newShops);
        newShopsAction(newShops);

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

    private void deleteRemovedShops(Set<dShop> newShops) {
        new HashSet<>(sManager.getShops()).stream()
                .filter(shop -> !newShops.contains(shop))
                .forEach(shop -> sManager.deleteShop(shop.getName()));
    }

    private void newShopsAction(Set<dShop> newShops) {
        newShops.forEach(shop -> {
            if (!sManager.getShop(shop.getName()).isPresent()) {
                shop.reStock();
                sManager.createShopAsync(shop);
                shop.destroy();

            } else {
                dShop currentShop = sManager.getShop(shop.getName()).get();
                currentShop.setItems(shop.getItems());
            }
            Log.info("Registered shop of name " + shop.getName() + " with " + shop.getItems().size() + " items");
        });
    }

}
