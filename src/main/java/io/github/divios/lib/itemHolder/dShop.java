package io.github.divios.lib.itemHolder;

import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.core_lib.misc.timeStampUtils;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.events.deletedShopEvent;
import io.github.divios.dailyrandomshop.events.reStockShopEvent;
import io.github.divios.lib.storage.dataManager;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.*;

public class dShop {

    private static final DRShop plugin = DRShop.getInstance();
    private static final dataManager dManager = dataManager.getInstance();

    private String name;
    private final dShopT type;
    private Set<dItem> items = new LinkedHashSet<>();
    private dGui gui;

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private int timer = 24 * 60 * 60; // seconds representing time to pass until reset

    private Task asyncCheck;
    private Task asyncUpdate;
    private EventListener reStock;

    //TODO: add timeStamp and integer representing the minutes that have to pass until
    // generates new items

    public dShop(String name, dShopT type) {
        this.name = name;
        this.type = type;

        gui = new dGui(this);
        initialize();
    }

    public dShop(String name, dShopT type, String base64, Timestamp timestamp, int timer) {
        this.name = name;
        this.type = type;
        this.timestamp = timestamp;
        this.timer = timer;

        gui = dGui.deserialize(base64, this);
        initialize();
    }

    private void initialize() {

        asyncCheck = Task.asyncRepeating(plugin, () -> {        // forced reStock due to timer
            if (timeStampUtils.diff(timestamp,
                    new Timestamp(System.currentTimeMillis())) > timer) {

                gui.renovate();
                dManager.updateGui(this.name, this.gui);
                timestamp = new Timestamp(System.currentTimeMillis());
                dManager.updateTimeStamp(this.name, this.timestamp);
            }
        }, 20L, 20L);

        final int[] gui_hash = {Arrays.stream(gui.getInventory().getContents()).map(ItemStack::hashCode)
                .reduce((acum, hash) -> acum += hash).orElse(0)};        // get hash based on content an not reference

        asyncUpdate = Task.asyncRepeating(plugin, () -> {       // auto-update gui if any changes where made
            int aux = Arrays.stream(gui.getInventory().getContents()).map(ItemStack::hashCode)
                    .reduce((acum, hash) -> acum += hash).orElse(0);

            if (aux != gui_hash[0]) {
                dManager.updateGui(this.name, gui);
                gui_hash[0] = aux;
            }

        }, 18000L, 18000L);

        new EventListener<>(plugin, deletedShopEvent.class, EventPriority.LOW, // auto-destroy listener
                (own, e) -> {
                    if (e.getShop().getName().equals(name)) {
                        gui.destroy();
                        asyncCheck.cancel();
                        asyncUpdate.cancel();
                        reStock.unregister();
                        own.unregister();
                    }
                });

        reStock = new EventListener<>(plugin, reStockShopEvent.class, EventPriority.LOW,  // reStock due to command by player
                e -> {
                    if (e.getShop() != this) return;

                    gui.renovate();
                    dManager.updateGui(this.name, this.gui);
                    timestamp = new Timestamp(System.currentTimeMillis());
                    dManager.updateTimeStamp(this.name, this.timestamp);
                });
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
    public synchronized Optional<dItem> getItem(UUID uid) {
        for (dItem item : items)
            if (item.getUid().equals(uid))
                return Optional.of(item);
        return Optional.empty();
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
                dManager.updateItem(getName(), dItem);
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
        dManager.addItem(this.name, item);
    }

    /**
     * Removes an item from the shop
     * @param uid UUID of the item to be removed
     * @return true if the item was removed. False if not
     */
    public synchronized boolean removeItem(UUID uid) {
        boolean result = items.removeIf(dItem -> dItem.getUid().equals(uid));
        if (result)
            dManager.deleteItem(this.name, uid);
        return result;
    }

    /**
     * Updates the gui with the given inv
     */
    public synchronized void updateGui(dGui gui) {
        this.gui.destroy();
        this.gui = gui;
        gui.renovate();
        dataManager.getInstance().updateGui(this.name, this.gui);
    }

    /**
     * Return the dGui of this shop
     * @return
     */
    public synchronized dGui getGui() {
        return gui;
    }

    public synchronized Timestamp getTimestamp() { return this.timestamp; }

    public synchronized int getTimer() { return timer; }

    public synchronized void setTimer(int timer) { this.timer = timer; }

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
