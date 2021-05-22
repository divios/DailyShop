package io.github.divios.lib.managers;

import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.storage.dataManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class shopsManager {

    private static shopsManager instance = null;
    private final HashSet<dShop> shops;

    private shopsManager(HashSet<dShop> shops) {
        this.shops = shops;
    }

    public static shopsManager getInstance() {
        if (instance == null) {
            try {
                instance = new shopsManager(dataManager.getInstance().getShops().get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return instance;
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
     * Creates a new shop
     *
     * @param name the name of the shop
     * @param type the type of the shop
     */

    public void createShop(String name, dShop.dShopT type) {
        shops.add(new dShop(name, type));
        // TODO: llamar a sqlManager
    }

    /**
     * Gets a shop by name
     *
     * @param name name of the shop
     * @return shop with the name. Null if it does not exist
     */
    public @Nullable dShop getShop(String name) {
        for (dShop dS : shops) {
            if (dS.getName().equals(name))
                return dS;
        }
        return null;
    }

    /**
     * Deletes a shop by name
     * @param name name of the shop to be deleted
     * @return true if succeeded. False if it does not exist
     */
    public boolean deleteShop(String name) {
        boolean result = shops.removeIf(dShop -> dShop.getName().equals(name));
        if (result)
            System.out.println("oke"); //quitar xd
            //TODO: llamar a sqlManager y eliminar tienda
        return result;
    }



}
