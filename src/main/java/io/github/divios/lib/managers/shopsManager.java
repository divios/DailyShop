package io.github.divios.lib.managers;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.events.createdShopEvent;
import io.github.divios.dailyShop.events.deletedShopEvent;
import io.github.divios.dailyShop.utils.FutureUtils;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.dShopI;
import io.github.divios.lib.storage.databaseManager;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class shopsManager {

    private static shopsManager instance = null;
    private Set<dShopI> shops = new LinkedHashSet<>();

    private shopsManager() {}

    public synchronized static shopsManager getInstance() {
        if (instance == null) {
            instance = new shopsManager();
            databaseManager.getInstance().getShops().thenAccept(instance::setShops);
        }
        return instance;
    }

    /**
     * Gets a list of all the shops
     *
     * @return a list of all the shops. Note that the returned list
     * is a copy of the original.
     */
    public synchronized Set<dShopI> getShops() {
        return Collections.unmodifiableSet(shops);
    }

    /**
     * Sets the shops. Private
     * @param shops
     */
    private synchronized void setShops(Set<dShopI> shops) {
        this.shops = shops;
    }

    /**
     * Creates a new shop
     *
     * @param name the name of the shop
     * @return CompletableFuture that ends when the shop is added to the database
     *
     */

    public synchronized CompletableFuture<Void> createShop(String name) {
        return createShop(name, dShop.dShopT.buy);
    }

    public synchronized CompletableFuture<Void> createShop(String name, dShop.dShopT type) {
        dShopSync newShop = new dShopSync(name, type);
        shops.add(newShop);
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop)));
        return databaseManager.getInstance().createShop(newShop);
    }

    public synchronized CompletableFuture<Void> createShop(dShop newShop) {
        dShopSync newShop_ = new dShopSync(newShop);
        shops.add(newShop_);
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop_)));
        return databaseManager.getInstance().createShop(newShop_);
    }

    /**
     * Gets a shop by name
     *
     * @param name name of the shop
     * @return shop with the name. Null if it does not exist
     */
    public synchronized Optional<dShopI> getShop(String name) {
        return shops.stream()
                .filter(shop -> shop.getName().equalsIgnoreCase(name))
                .findFirst();
    }


    public synchronized CompletableFuture<Void> deleteShop(dShop shop) {
        return deleteShop(shop.getName());
    }

    /**
     * Deletes a shop by name
     * @param name name of the shop to be deleted
     * @return returns a completableFuture that ends when the shop is deleted
     * from the database
     */
    public synchronized CompletableFuture<Void> deleteShop(String name) {

        Optional<dShopI> result = getShop(name);
        if (!result.isPresent()) return CompletableFuture.completedFuture(null);

        deletedShopEvent event = new deletedShopEvent(result.get());
        Events.callEvent(event);     // throw new event

        // auto-destroy is handled via event on dShop
        shops.remove(result.get());
        return databaseManager.getInstance().deleteShop(name);
    }



}
