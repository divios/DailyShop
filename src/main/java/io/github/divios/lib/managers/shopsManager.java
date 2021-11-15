package io.github.divios.lib.managers;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.events.createdShopEvent;
import io.github.divios.dailyShop.events.deletedShopEvent;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.dataManager;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class shopsManager {

    private static shopsManager instance = null;
    private Set<dShop> shops = new LinkedHashSet<>();

    private shopsManager() {}

    public static shopsManager getInstance() {
        if (instance == null) {
            instance = new shopsManager();
            dataManager.getInstance().getShops().thenAccept(instance::setShops);
        }
        return instance;
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
     * @param shops
     */
    private synchronized void setShops(Set<dShop> shops) {
        this.shops = shops;
    }

    /**
     * Creates a new shop
     *
     * @param name the name of the shop
     * @param type the type of the shop
     * @return CompletableFuture that ends when the shop is added to the database
     *
     */

    public synchronized CompletableFuture<Void> createShop(String name, dShop.dShopT type) {
        dShop newShop = new dShop(name, type);
        shops.add(newShop);
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop)));
        return dataManager.getInstance().createShop(newShop);
    }

    public synchronized CompletableFuture<Void> createShop(dShop newShop) {
        shops.add(newShop);
        Schedulers.sync().run(() -> Events.callEvent(new createdShopEvent(newShop)));
        return dataManager.getInstance().createShop(newShop);
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

    /**
     * Deletes a shop by name
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
        shops.removeIf(shop -> shop.getName().equals(name));
        return dataManager.getInstance().deleteShop(name);
    }

}
