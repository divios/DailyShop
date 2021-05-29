package io.github.divios.lib.itemHolder;

import io.github.divios.lib.storage.dataManager;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class dShop {

    private String name;
    private final dShopT type;
    private Set<dItem> items = new LinkedHashSet<>();
    private final dGui gui = new dGui();

    public dShop(String name, dShopT type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of the shop
     * @return
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets the name of the shop
     * @param name
     */
    public synchronized void setName(String name) { this.name = name; }

    /**
     * Gets the type of the shop
     * @return type of the shop (buy,sell)
     */
    public synchronized dShopT getType() {
        return type;
    }

    /**
     * Gets a copy the items in the shop
     * @return returns a List of dItems. Note that this list is a copy of the original,
     * any change made to it won't affect the original one
     */
    public synchronized @NotNull Set<dItem> getItems() {
        Set<dItem> copy = new LinkedHashSet<>();
        items.forEach(dItem -> copy.add(dItem.clone()));
        return copy;
    }

    /**
     * Gets the item by uuid
     * @param uid the UUID to search
     * @return null if it does not exist
     */
    public synchronized dItem getItem(UUID uid) {
        for (dItem item : items)
            if (item.getUid().equals(uid))
                return item;
        return null;
    }

    /**
     * Checks if the shop has a particular item
     * @param uid the UUID to check
     * @return true if exits, false if not
     */
    public synchronized boolean hasItem(UUID uid) {
        return getItem(uid) != null;
    }

    /**
     * Updates the item of the shop
     * @param uid
     * @param newItem
     */
    public synchronized void updateItem(UUID uid, dItem newItem) {
        items.iterator().forEachRemaining(dItem -> {
            if (dItem.getUid().equals(uid)) {
                dItem.setItem(newItem.getItem());
                dataManager.getInstance().updateItem(getName(), dItem);
            }
        });
    }

    /**
     * Sets the items of this shop
     */
    public synchronized void setItems(@NotNull HashSet<dItem> items) {
        this.items = items;
    }

    /**
     * Adds an item to this shop
     * @param item item to be added
     */
    public synchronized void addItem(@NotNull dItem item) {
        items.add(item);
        dataManager.getInstance().addItem(this.name, item);
    }

    /**
     * Removes an item from the shop
     * @param uid UUID of the item to be removed
     * @return true if the item was removed. False if not
     */
    public synchronized boolean removeItem(UUID uid) {
        boolean result = items.removeIf(dItem -> dItem.getUid().equals(uid));
        if (result)
            dataManager.getInstance().deleteItem(this.name, uid);
        return result;
    }

    /**
     * Updates the gui from a base 64
     * @param base64
     */
    public synchronized void updateGui(String base64) {
        gui.updateInventory(base64);
    }

    /**
     * Updates the gui with the given inv
     * @param inv
     */
    public synchronized void updateGui(Inventory inv) {
        gui.updateInventory(inv);
    }

    /**
     * Return the dGui of this shop
     * @return
     */
    public synchronized dGui getGui() {
        return gui;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof dShop &&
                this.getName().equals(((dShop) o).getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }


    public enum dShopT {
        buy,
        sell
    }

}
