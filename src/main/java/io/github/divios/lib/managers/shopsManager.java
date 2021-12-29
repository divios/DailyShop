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
import io.github.divios.lib.serialize.serializerApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class shopsManager {

    private final Map<String, dShop> shops = new ConcurrentHashMap<>();
    private final databaseManager dManager;
    private final Set<Task> task = new HashSet<>();

    public shopsManager(databaseManager databaseManager) {
        dManager = databaseManager;
        initialise();
    }

    private void initialise() {
        Log.info("Importing database data...");
        Timer timer = Timer.create();
        dManager.getShops().forEach(shop -> shops.put(shop.getName(), shop));
        timer.stop();
        Log.info("Imported database data in " + timer.getTime() + " ms");

        Schedulers.sync().runRepeating(this::updateShopsAsync, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES);
    }


    private void updateShops() {
        shops.values().forEach(shop -> dManager.updateGui(shop.getName(), shop.getGuis()));
    }

    private void updateShopsAsync() {
        shops.values().forEach(shop -> dManager.updateGuiAsync(shop.getName(), shop.getGuis()));
    }

    /**
     * Gets a list of all the shops
     *
     * @return a list of all the shops. Note that the returned list
     * is a copy of the original.
     */
    public Collection<dShop> getShops() {
        return Collections.unmodifiableCollection(shops.values());
    }

    /**
     * Sets the shops. Private
     *
     * @param shops
     */
    private void setShops(Set<dShop> shops) {
        deleteAllShops();
        shops.forEach(this::createShop);
    }

    /**
     * Sets the shops. Private
     *
     * @param shops
     */
    private CompletableFuture<Void> setShopsAsync(Set<dShop> shops) {
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

    public void createShop(String name) {
        createShop(new WrappedShop(name));
    }

    public void createShop(dShop newShop) {
        dShop newShop_ = WrappedShop.wrap(newShop);

        shops.put(newShop.getName(), newShop_);
        newShop_.reStock();
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop_)));
        dManager.createShop(newShop_);
        newShop_.getItems().forEach(dItem -> {
            dManager.addItem(newShop.getName(), dItem);
        });

    }

    public void createShopAsync(String name) {
        createShopAsync(new WrappedShop(name));
    }

    public void createShopAsync(dShop newShop) {
        dShop newShop_ = WrappedShop.wrap(newShop);

        shops.put(newShop.getName(), newShop_);
        newShop_.reStock();
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop_)));
        dManager.createShopAsync(newShop_).thenAccept(unused -> {
            newShop_.getItems().forEach(dItem -> {
                dManager.addItemAsync(newShop.getName(), dItem);
            });
        });
    }

    /**
     * Gets a shop by name
     *
     * @param name name of the shop
     * @return shop with the name. Null if it does not exist
     */
    public Optional<dShop> getShop(String name) {
        return Optional.of(shops.get(name));
    }

    /**
     * Return the default shop, if any
     * @return Optional with the default shop
     */
    public Optional<dShop> getDefaultShop() {
        return shops.values().stream()
                .filter(dShop::isDefault)
                .findFirst();
    }


    public void deleteShop(dShop shop) {
        deleteShop(shop.getName());
    }

    public void deleteShopAsync(dShop shop) {
        deleteShopAsync(shop.getName());
    }

    /**
     * Deletes a shop by name
     *
     * @param name name of the shop to be deleted
     * @return returns a completableFuture that ends when the shop is deleted
     * from the database
     */
    public void deleteShop(String name) {
        dShop removed = shops.remove(name);
        if (removed == null) return;

        deletedShopEvent event = new deletedShopEvent(removed);
        Events.callEvent(event);     // throw new event

        removed.destroy();
        dManager.deleteShop(name);
    }

    public void deleteShopAsync(String name) {
        dShop removed = shops.remove(name);
        if (removed == null) return;

        deletedShopEvent event = new deletedShopEvent(removed);
        Events.callEvent(event);     // throw new event

        removed.destroy();
        dManager.deleteShopAsync(name);
    }

    public void deleteAllShops() {
        new HashSet<>(shops.keySet()).forEach(this::deleteShop);
    }

    public void deleteAllShopsAsync() {
        new HashSet<>(shops.keySet()).forEach(this::deleteShopAsync);
    }

    public void saveShop(String name) {
        getShop(name).ifPresent(serializerApi::saveShopToFile);
    }

    public void saveAllShops() {
        shops.values().forEach(serializerApi::saveShopToFile);
    }

}
