package io.github.divios.lib.managers;

import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.createdShopEvent;
import io.github.divios.dailyShop.events.deletedShopEvent;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.dataManager;
import org.bukkit.Bukkit;

import java.util.*;

public class shopsManager {

    private static shopsManager instance = null;
    private HashSet<dShop> shops = new LinkedHashSet<>();

    private shopsManager() {}

    public static shopsManager getInstance() {
        if (instance == null) {
            instance = new shopsManager();
            dataManager.getInstance()
                    .getShops().thenAccept(instance::setShops);
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
    private synchronized void setShops(HashSet<dShop> shops) {
        this.shops = shops;
    }

    /**
     * Creates a new shop
     *
     * @param name the name of the shop
     * @param type the type of the shop
     */

    public synchronized void createShop(String name, dShop.dShopT type) {
        dShop newShop = new dShop(name, type);
        shops.add(newShop);
        Task.syncDelayed(DailyShop.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new createdShopEvent(newShop)));
        dataManager.getInstance().createShop(newShop);
    }

    /**
     * Gets a shop by name
     *
     * @param name name of the shop
     * @return shop with the name. Null if it does not exist
     */
    public synchronized Optional<dShop> getShop(String name) {
        return shops.stream()
                .filter(shop -> shop.getName().equals(name))
                .findFirst();
    }

    /**
     * Deletes a shop by name
     * @param name name of the shop to be deleted
     * @return true if succeeded. False if it does not exist
     */
    public synchronized boolean deleteShop(String name) {

        Optional<dShop> result = getShop(name);
        if (!result.isPresent()) return false;

        deletedShopEvent event = new deletedShopEvent(result.get());
        Bukkit.getPluginManager().callEvent(event);     // throw new event

        // auto-destroy is handled via event on dShop
        shops.removeIf(shop -> shop.getName().equals(name));
        dataManager.getInstance().deleteShop(name);

        return true;
    }

}
