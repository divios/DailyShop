package io.github.divios.lib.managers;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.events.createdShopEvent;
import io.github.divios.dailyShop.events.deletedShopEvent;
import io.github.divios.dailyShop.utils.FutureUtils;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.databaseManager;
import org.bukkit.craftbukkit.v1_17_R1.util.Waitable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class shopsManager {

    private static shopsManager instance = null;
    private Set<dShop> shops = new LinkedHashSet<>();

    private shopsManager() {
        Schedulers.sync().runRepeating(() -> {
            updateShops();
        }, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES);
    }

    public synchronized static shopsManager getInstance() {
        if (instance == null) {
            instance = new shopsManager();
            FutureUtils.waitFor(databaseManager.getInstance().getShops());
        }
        return instance;
    }

    private synchronized void updateShops() {
        shops.forEach(shop -> {
            databaseManager.getInstance().asyncUpdateGui(shop.getName(), shop.getGuis());
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
    private synchronized CompletableFuture<Void> setShops(Set<dShop> shops) {
        return CompletableFuture.runAsync(() -> {
            FutureUtils.waitFor(deleteAllShops());
            shops.forEach(shop -> {
                FutureUtils.waitFor(createShop(shop));
            });
        });
    }

    /**
     * Creates a new shop
     *
     * @param name the name of the shop
     * @return CompletableFuture that ends when the shop is added to the database
     */

    public synchronized CompletableFuture<Void> createShop(String name) {
        return createShop(name, dShop.dShopT.buy);
    }

    public synchronized CompletableFuture<Void> createShop(String name, dShop.dShopT type) {
        return createShop(new WrappedShop(name, type));
    }

    public synchronized CompletableFuture<Void> createShop(dShop newShop) {
        dShop newShop_ = WrappedShop.wrap(newShop);
        shops.add(newShop_);
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop_)));
        return CompletableFuture.runAsync(() -> {
            FutureUtils.waitFor(databaseManager.getInstance().createShop(newShop_));
            newShop_.getItems().forEach(dItem -> {
                FutureUtils.waitFor(databaseManager.getInstance().addItem(newShop.getName(), dItem));
            });
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


    public synchronized CompletableFuture<Void> deleteShop(dShop shop) {
        return deleteShop(shop.getName());
    }

    /**
     * Deletes a shop by name
     *
     * @param name name of the shop to be deleted
     * @return returns a completableFuture that ends when the shop is deleted
     * from the database
     */
    public synchronized CompletableFuture<Void> deleteShop(String name) {

        Optional<dShop> result = getShop(name);
        if (!result.isPresent()) return CompletableFuture.completedFuture(null);

        deletedShopEvent event = new deletedShopEvent(result.get());
        Events.callEvent(event);     // throw new event

        // auto-destroy is handled via event on dShop
        shops.remove(result.get());
        result.get().destroy();
        return databaseManager.getInstance().deleteShop(name);
    }

    public synchronized CompletableFuture<Void> deleteAllShops() {
        return CompletableFuture.runAsync(() -> {
            new HashSet<>(shops).forEach(dShop -> {
                FutureUtils.waitFor(deleteShop(dShop));
            });
            shops.clear();
        });

    }


}
