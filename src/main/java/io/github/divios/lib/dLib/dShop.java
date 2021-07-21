package io.github.divios.lib.dLib;

import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.core_lib.misc.timeStampUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.deletedShopEvent;
import io.github.divios.dailyShop.events.reStockShopEvent;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.guis.dBuy;
import io.github.divios.lib.storage.dataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.*;

public class dShop {

    private static final DailyShop plugin = DailyShop.getInstance();
    private static final dataManager dManager = dataManager.getInstance();

    private String name;
    private final dShopT type;
    private Set<dItem> items = new LinkedHashSet<>();
    private dGui gui;

    private Timestamp timestamp;
    private int timer;
    private final int[] gui_hash = {0};

    private Task asyncCheck;
    private Task asyncUpdate;
    private EventListener reStock;
    private EventListener updateItem;

    public dShop(String name, dShopT type) {
        this.name = name;
        this.type = type;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.timer = plugin.configM.getSettingsYml().DEFAULT_TIMER;  // seconds representing time to pass until reset
        this.gui = new dBuy(this);

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

                timestamp = new Timestamp(System.currentTimeMillis());
                dManager.updateTimeStamp(this.name, this.timestamp);
                gui.renovate();
                dManager.asyncUpdateGui(this.name, this.gui);
            }
        }, 20L, 20L);

        // get hash based on content an not reference

        asyncUpdate = Task.asyncRepeating(plugin, () -> {       // auto-update gui if any changes where made

            if (gui.getInventory().getContents().length == 0) return;

            int aux = Arrays.stream(gui.getInventory().getContents())
                    .mapToInt(value -> utils.isEmpty(value) ? 0:value.hashCode())
                    .sum();

            if (aux != gui_hash[0]) {
                dManager.asyncUpdateGui(this.name, gui);
                gui_hash[0] = aux;
            }

        }, 18000L, 18000L);

        new EventListener<>(deletedShopEvent.class, EventPriority.LOW, // auto-destroy listener
                (own, e) -> {
                    if (e.getShop().getName().equals(name)) {
                        gui.destroy();
                        asyncCheck.cancel();
                        asyncUpdate.cancel();
                        reStock.unregister();
                        updateItem.unregister();
                        own.unregister();
                    }
                });

        reStock = new EventListener<>(reStockShopEvent.class, EventPriority.LOW,  // reStock due to command by player
                e -> {
                    if (e.getShop() != this) return;

                    gui.renovate();
                    dManager.asyncUpdateGui(this.name, this.gui);
                    timestamp = new Timestamp(System.currentTimeMillis());
                    dManager.updateTimeStamp(this.name, this.timestamp);
                });

        updateItem = new EventListener<>(updateItemEvent.class, EventPriority.HIGHEST,
                e -> {
                    if (!e.getShop().getName().equals(this.getName())) return;

                    this.gui.updateItem(e.getItem(), e.getType());
                });
    }

    /**
     * Gets the name of the shop
     *
     * @return
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets the name of the shop
     *
     * @param name
     */
    public synchronized void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of the shop
     *
     * @return type of the shop (buy,sell)
     */
    public synchronized dShopT getType() {
        return type;
    }

    /**
     * Gets a copy the items in the shop
     *
     * @return returns a List of dItems. Note that this list is a copy of the original,
     * any change made to it won't affect the original one
     */
    public synchronized @NotNull Set<dItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    /**
     * Gets the item by uuid
     *
     * @param uid the UUID to search
     * @return null if it does not exist
     */
    public synchronized Optional<dItem> getItem(UUID uid) {
        return items.stream()
                .filter(dItem -> dItem.getUid().equals(uid))
                .findFirst();
    }

    /**
     * Checks if the shop has a particular item
     *
     * @param uid the UUID to check
     * @return true if exits, false if not
     */
    public synchronized boolean hasItem(UUID uid) {
        return getItem(uid).isPresent();
    }

    /**
     * Updates the item of the shop
     *
     * @param uid
     * @param newItem
     */
    public synchronized void updateItem(UUID uid, dItem newItem) {
        items.iterator().forEachRemaining(dItem -> {
            if (dItem.getUid().equals(uid)) {
                dItem.setItem(newItem.getItem());
                Bukkit.getPluginManager().callEvent(        // Event to update item
                        new updateItemEvent(newItem, updateItemEvent.updatetype.UPDATE_ITEM,
                                this));
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
     *
     * @param item item to be added
     */
    public synchronized void addItem(@NotNull dItem item) {
        items.add(item);
        dManager.addItem(this.name, item);
    }

    /**
     * Removes an item from the shop
     *
     * @param uid UUID of the item to be removed
     * @return true if the item was removed. False if not
     */
    public synchronized boolean removeItem(UUID uid) {

        boolean[] result = {false};
        items.stream()
                .filter(dItem -> dItem.getUid().equals(uid))
                .findFirst()
                .ifPresent(dItem -> {
                    dManager.deleteItem(this.name, uid);
                    items.remove(dItem);
                    Bukkit.getPluginManager().callEvent(
                            new updateItemEvent(dItem,
                                    updateItemEvent.updatetype.DELETE_ITEM, this));
                    result[0] = true;
                });

        return result[0];
    }

    /**
     * Shorcut for {@link dGui#open(Player)}
     * @param p Player to open the gui for
     */
    public synchronized void openGui(Player p) {
        gui.open(p);
    }

    /**
     * Updates the gui with the given inv
     */
    public synchronized void updateGui(dGui gui) {
        this.gui.destroy();
        this.gui = gui;
        gui.renovate();
        dataManager.getInstance().asyncUpdateGui(this.name, this.gui);
    }

    public synchronized void reload() {
        this.gui.reload();
    }

    /**
     * Return the dGui of this shop
     *
     * @return
     */
    public synchronized dGui getGui() {
        return gui;
    }

    public synchronized Timestamp getTimestamp() {
        return this.timestamp;
    }

    public synchronized int getTimer() {
        return timer;
    }

    public synchronized void setTimer(int timer) {
        this.timer = timer;
        dManager.updateTimer(this.name, this.timer);
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
