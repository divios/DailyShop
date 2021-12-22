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
import org.checkerframework.checker.nullness.Opt;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class shopsManager {

    private final Set<dShop> shops = ConcurrentHashMap.newKeySet();
    private final databaseManager dManager;
    private final Set<Task> task = new HashSet<>();

    public shopsManager(databaseManager databaseManager) {
        dManager = databaseManager;
        Log.info("Importing database data...");
        Timer timer = Timer.create();
        shops.addAll(databaseManager.getShops());
        timer.stop();
        Log.info("Imported database data in " + timer.getTime() + " ms");

        Schedulers.sync().runRepeating(this::updateShopsAsync, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES);
    }

    private void updateShops() {
        shops.forEach(shop -> {
            dManager.updateGui(shop.getName(), shop.getGuis());
        });
    }

    private void updateShopsAsync() {
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
    public Set<dShop> getShops() {
        return Collections.unmodifiableSet(shops);
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

        shops.add(newShop_);
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

        shops.add(newShop_);
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
        return shops.stream()
                .filter(shop -> shop.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Return the default shop, if any
     * @return Optional with the default shop
     */
    public Optional<dShop> getDefaultShop() {
        Log.info("oke2");
        return shops.stream()
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

        Optional<dShop> result = getShop(name);
        if (!result.isPresent()) return;

        deletedShopEvent event = new deletedShopEvent(result.get());
        Events.callEvent(event);     // throw new event

        // auto-destroy is handled via event on dShop
        shops.remove(result.get());
        result.get().destroy();
        dManager.deleteShop(name);
    }

    public void deleteShopAsync(String name) {

        Optional<dShop> result = getShop(name);
        if (!result.isPresent()) return;

        deletedShopEvent event = new deletedShopEvent(result.get());
        Events.callEvent(event);     // throw new event

        // auto-destroy is handled via event on dShop
        shops.remove(result.get());
        result.get().destroy();
        dManager.deleteShopAsync(name);
    }

    public void deleteAllShops() {
        new HashSet<>(shops).forEach(this::deleteShop);
    }

    public void deleteAllShopsAsync() {
        new HashSet<>(shops).forEach(this::deleteShopAsync);
    }

    public void saveShop(String name) {
        getShop(name).ifPresent(serializerApi::saveShopToFile);
    }

    public void saveAllShops() {
        shops.forEach(serializerApi::saveShopToFile);
    }

}
