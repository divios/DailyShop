package io.github.divios.lib.managers;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.events.createdShopEvent;
import io.github.divios.dailyShop.events.deletedShopEvent;
import io.github.divios.dailyShop.utils.Timer;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.databaseManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class shopsManager {

    private static shopsManager instance = null;
    private final Set<dShop> shops = new LinkedHashSet<>();
    private static final databaseManager dManager = databaseManager.getInstance();
    private final Set<Task> task = new HashSet<>();

    private shopsManager() {
        Schedulers.sync().runRepeating(this::updateShopsAsync, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES);
    }

    public synchronized static shopsManager getInstance() {
        if (instance == null) {
            instance = new shopsManager();
            Log.info("Importing database data...");
            Timer timer = Timer.create();
            instance.shops.addAll(databaseManager.getInstance().getShops());
            timer.stop();
            Log.info("Imported database data in " + timer.getTime() + " ms");
        }
        return instance;
    }

    private synchronized void updateShops() {
        shops.forEach(shop -> {
            dManager.updateGui(shop.getName(), shop.getGuis());
        });
    }

    private synchronized void updateShopsAsync() {
        shops.forEach(shop -> {
            dManager.updateGuiAsync(shop.getName(), shop.getGuis());
        });
    }

    /**
     * Gets a list of all the shops
     *
     * @return a list of all the shops. Note that the returned list
     * is a copy of the original.
     */
    public synchronized Set<dShop> getShops() {
        return Collections.unmodifiableSet(shops);
    }

    /**
     * Sets the shops. Private
     *
     * @param shops
     */
    private synchronized void setShops(Set<dShop> shops) {
        deleteAllShops();
        shops.forEach(this::createShop);
    }

    /**
     * Sets the shops. Private
     *
     * @param shops
     */
    private synchronized CompletableFuture<Void> setShopsAsync(Set<dShop> shops) {
        return CompletableFuture.runAsync(() -> {
            deleteAllShops();
            shops.forEach(this::createShop);
        });
    }

    /**
     * Creates a new shop
     *
     * @param name the name of the shop
     * @return CompletableFuture that ends when the shop is added to the database
     */

    public synchronized void createShop(String name) {
        createShop(name, dShop.dShopT.buy);
    }

    public synchronized void createShop(String name, dShop.dShopT type) {
        createShop(new WrappedShop(name, type));
    }

    public synchronized void createShop(dShop newShop) {
        dShop newShop_ = WrappedShop.wrap(newShop);

        shops.add(newShop_);
        newShop_.reStock();
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop_)));
        dManager.createShop(newShop_);
        newShop_.getItems().forEach(dItem -> {
            dManager.addItem(newShop.getName(), dItem);
        });
    }

    public synchronized void createShopAsync(String name) {
        createShopAsync(name, dShop.dShopT.buy);
    }

    public synchronized void createShopAsync(String name, dShop.dShopT type) {
        createShopAsync(new WrappedShop(name, type));
    }

    public synchronized void createShopAsync(dShop newShop) {
        dShop newShop_ = WrappedShop.wrap(newShop);

        shops.add(newShop_);
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop_)));
        dManager.createShopAsync(newShop_);
        newShop_.getItems().forEach(dItem -> {
            dManager.addItemAsync(newShop.getName(), dItem);
        });
    }

    /**
     * Gets a shop by name
     *
     * @param name name of the shop
     * @return shop with the name. Null if it does not exist
     */
    public synchronized Optional<dShop> getShop(String name) {
        return shops.stream()
                .filter(shop -> shop.getName().equalsIgnoreCase(name))
                .findFirst();
    }


    public synchronized void deleteShop(dShop shop) {
        deleteShop(shop.getName());
    }

    public synchronized void deleteShopAsync(dShop shop) {
        deleteShopAsync(shop.getName());
    }

    /**
     * Deletes a shop by name
     *
     * @param name name of the shop to be deleted
     * @return returns a completableFuture that ends when the shop is deleted
     * from the database
     */
    public synchronized void deleteShop(String name) {

        Optional<dShop> result = getShop(name);
        if (!result.isPresent()) return;

        deletedShopEvent event = new deletedShopEvent(result.get());
        Events.callEvent(event);     // throw new event

        // auto-destroy is handled via event on dShop
        shops.remove(result.get());
        result.get().destroy();
        dManager.deleteShop(name);
    }

    public synchronized void deleteShopAsync(String name) {

        Optional<dShop> result = getShop(name);
        if (!result.isPresent()) return;

        deletedShopEvent event = new deletedShopEvent(result.get());
        Events.callEvent(event);     // throw new event

        // auto-destroy is handled via event on dShop
        shops.remove(result.get());
        result.get().destroy();
        dManager.deleteShopAsync(name);
    }

    public synchronized void deleteAllShops() {
            new HashSet<>(shops).forEach(this::deleteShop);
    }

    public synchronized void deleteAllShopsAsync() {
        new HashSet<>(shops).forEach(this::deleteShopAsync);
    }


}
