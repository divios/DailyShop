package io.github.divios.lib.dLib;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.misc.timeStampUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.reStockShopEvent;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.guis.settings.shopGui;
import io.github.divios.lib.dLib.synchronizedGui.syncHashMenu;
import io.github.divios.lib.dLib.synchronizedGui.syncMenu;
import io.github.divios.lib.serialize.adapters.dItemAdapter;
import io.github.divios.lib.serialize.adapters.dShopAdapter;
import io.github.divios.lib.serialize.adapters.dStockAdapter;
import io.github.divios.lib.storage.databaseManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.beans.Transient;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class dShop {

    protected static final DailyShop plugin = DailyShop.getInstance();
    protected static final databaseManager dManager = databaseManager.getInstance();

    private transient static final serializeOptions serializer = new serializeOptions();

    protected String name;
    protected final Map<UUID, dItem> items = new LinkedHashMap<>();
    protected final syncMenu guis;

    protected Timestamp timestamp;
    protected int timer;

    protected final Set<Task> tasks = new HashSet<>();
    protected final Set<Subscription> listeners = new HashSet<>();

    public static serializeOptions serializeOptions() {
        return serializer;
    }

    public dShop(String name) {
        this(name, plugin.configM.getSettingsYml().DEFAULT_TIMER);
    }

    public dShop(String name, int timer) {
        this(name, timer, new Timestamp(System.currentTimeMillis()));
    }

    public dShop(String name, int timer, Timestamp timestamp) {
        this.name = name;
        this.timer = timer;
        this.timestamp = timestamp;
        this.guis = syncHashMenu.create(this);

        startTimerTask();
    }

    
    @Deprecated
    public dShop(String name, String base64, Timestamp timestamp, int timer) {
        this.name = name;
        this.timestamp = timestamp;
        this.timer = timer;

        guis = syncHashMenu.fromJson(base64, this);
        startTimerTask();
    }

    @Deprecated
    public dShop(String name, String base64, Timestamp timestamp, int timer, Set<dItem> items) {
        this.name = name;
        this.timestamp = timestamp;
        this.timer = timer;
        items.forEach(dItem -> this.items.put(dItem.getUid(), dItem));

        guis = syncHashMenu.fromJson(base64, this);
        startTimerTask();
    }

    protected void startTimerTask() {
        tasks.add(
                Schedulers.async().runRepeating(() -> {

                    if (timer == -1) return;
                    if (timeStampUtils.diff(timestamp, new Timestamp(System.currentTimeMillis())) >= timer)
                        Schedulers.sync().run(this::reStock);

                }, 1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS)
        );
    }

    /**
     * Opens the actual shop for the player
     *
     * @param p
     */
    public void openShop(Player p) {
        guis.generate(p);
    }

    /**
     * Opens the gui to manage the items of this shop
     *
     * @param p
     */
    public void manageItems(Player p) {
        shopGui.open(p, this);
    }

    /**
     * Opens the gui to customize the display of this shop
     *
     * @param p
     */
    public void openCustomizeGui(Player p) {
        guis.customizeGui(p);
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
    public synchronized void rename(String name) {
        this.name = name;
    }

    /**
     * Gets a copy the items in the shop
     *
     * @return returns a List of dItems. Note that this list is a copy of the original,
     * any change made to it won't affect the original one
     */
    public synchronized @NotNull
    Set<dItem> getItems() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(items.values()));
    }

    /**
     * Gets the item by uuid
     *
     * @param uid the UUID to search
     * @return null if it does not exist
     */
    public synchronized Optional<dItem> getItem(UUID uid) {
        return Optional.ofNullable(items.get(uid));
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
     * Restocks the items of this shop.
     */
    public synchronized void reStock() {
        timestamp = new Timestamp(System.currentTimeMillis());
        Events.callEvent(new reStockShopEvent(this));
        guis.reStock();
    }

    /**
     * Updates the item of the shop
     *
     * @param uid
     * @param newItem
     */
    public synchronized void updateItem(UUID uid, dItem newItem) {
        if (!items.containsKey(uid))
            return; // Throw error

        items.put(uid, newItem);
        Events.callEvent(new updateItemEvent(newItem.getUid(), updateItemEvent.type.UPDATE_ITEM, this));    // Event to update item
    }


    /**
     * Sets the items of this shop
     */
    public synchronized void setItems(@NotNull Set<dItem> items) {
        Map<UUID, dItem> newItems = new HashMap<>();
        items.forEach(dItem -> newItems.put(dItem.getUid(), dItem));            // Cache values for a O(1) search

        for (Iterator<Map.Entry<UUID, dItem>> it = new HashMap<>(this.items).entrySet().iterator(); it.hasNext(); ) {          // Remove items that are not on the newItems list

            Map.Entry<UUID, dItem> entry = it.next();
            if (newItems.containsKey(entry.getKey())) {     // Update items if changed
                dItem toUpdateItem = newItems.get(entry.getKey());
                if (toUpdateItem != null && !toUpdateItem.getItem().isSimilar(entry.getValue().getItem())) {
                    updateItem(entry.getKey(), toUpdateItem);
                }
                continue;
            }
            removeItem(entry.getKey());
        }

        items.forEach(this::addItem);       // Replace the old values for the new ones
    }

    /**
     * Adds an item to this shop
     *
     * @param item item to be added
     */
    public synchronized void addItem(@NotNull dItem item) {
        items.put(item.getUid(), item);
    }

    /**
     * Removes an item from the shop
     *
     * @param uid UUID of the item to be removed
     * @return true if the item was removed. False if not
     */
    public synchronized boolean removeItem(UUID uid) {
        dItem removed = items.remove(uid);
        if (removed == null) return false;
        Events.callEvent(new updateItemEvent(removed.getUid(), updateItemEvent.type.DELETE_ITEM, this));
        return true;
    }


    /**
     * Return the dGui of this shop
     *
     * @return
     */
    public synchronized syncMenu getGuis() {
        return guis;
    }

    public synchronized void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public synchronized Timestamp getTimestamp() {
        return this.timestamp;
    }

    public synchronized int getTimer() {
        return timer;
    }

    public synchronized void setTimer(int timer) {
        this.timer = timer;
    }

    public synchronized void destroy() {
        guis.destroy();
        tasks.forEach(Task::stop);
        tasks.clear();
        listeners.forEach(Subscription::unregister);
        listeners.clear();
    }

    @Override
    public String toString() {
        return "dShop{" +
                "name='" + name + '\'' +
                ", items=" + items +
                ", guis=" + guis +
                ", timestamp=" + timestamp +
                ", timer=" + timer +
                ", tasks=" + tasks +
                ", listeners=" + listeners +
                '}';
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


    /**
     * Serializers
     **/

    public static class serializeOptions {

        private transient final jsonSerializer JSON = new jsonSerializer();

        private serializeOptions() {
        }

        public jsonSerializer json() {
            return JSON;
        }
    }

    public static final class jsonSerializer {

        private transient static final Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(dShop.class, new dShopAdapter())
                .create();

        private jsonSerializer() {
        }

        public JsonObject toJson(dShop shop) {
            return gson.toJsonTree(shop).getAsJsonObject();
        }

        public dShop fromJson(JsonElement element) {
            return gson.fromJson(element, dShop.class);
        }

    }

}
