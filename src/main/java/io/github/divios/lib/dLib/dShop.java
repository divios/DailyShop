package io.github.divios.lib.dLib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.misc.timeStampUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.reStockShopEvent;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.guis.settings.shopGui;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.dLib.synchronizedGui.syncHashMenu;
import io.github.divios.lib.dLib.synchronizedGui.syncMenu;
import io.github.divios.lib.serialize.adapters.dShopAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class dShop {

    protected static final DailyShop plugin = DailyShop.get();

    private transient static final encodeOptions serializer = new encodeOptions();

    protected String name;
    protected final Map<UUID, dItem> items = Collections.synchronizedMap(new LinkedHashMap<>());
    protected final syncMenu guis;

    protected Timestamp timestamp;
    protected int timer;

    protected boolean announce_restock = true;
    protected boolean isDefault = false;

    protected final Set<Task> tasks = new HashSet<>();
    protected final Set<Subscription> listeners = new HashSet<>();

    public dShop(String name) {
        this(name, Settings.DEFAULT_TIMER.getValue().getAsInt());
    }

    public dShop(String name, int timer) {
        this(name, timer, new Timestamp(System.currentTimeMillis()));
    }

    public dShop(String name, int timer, Timestamp timestamp) {
        this.name = name.toLowerCase();
        this.timer = timer;
        this.timestamp = timestamp;
        this.guis = syncHashMenu.create(this);

        startTimerTask();
        startListeners();
    }

    @Deprecated
    public dShop(String name, String base64, Timestamp timestamp, int timer) {
        this(name, base64, timestamp, timer, Collections.EMPTY_SET);
    }

    @Deprecated
    public dShop(String name, String base64, Timestamp timestamp, int timer, Set<dItem> items) {
        this.name = name.toLowerCase();
        this.timestamp = timestamp;
        this.timer = timer;
        items.forEach(dItem -> this.items.put(dItem.getUid(), dItem));

        guis = syncHashMenu.fromJson(base64, this);
        startTimerTask();
        startListeners();
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

    protected void startListeners() {
        listeners.add(
                Events.subscribe(updateItemEvent.class)
                        .filter(o -> o.getShop().equals(this))
                        .handler(guis::updateItem)
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
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the shop
     *
     * @param name
     */
    public void rename(String name) {
        this.name = name;
    }

    /**
     * Gets a copy the items in the shop
     *
     * @return returns a List of dItems. Note that this list is a copy of the original,
     * any change made to it won't affect the original one
     */
    public @NotNull
    Collection<dItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    /**
     * Gets the item by uuid
     *
     * @param uid the UUID to search
     * @return null if it does not exist
     */
    public Optional<dItem> getItem(UUID uid) {
        return Optional.ofNullable(items.get(uid));
    }

    /**
     * Checks if the shop has a particular item
     *
     * @param uid the UUID to check
     * @return true if exits, false if not
     */
    public boolean hasItem(UUID uid) {
        return getItem(uid).isPresent();
    }

    /**
     * Restocks the items of this shop.
     */
    public void reStock() {
        timestamp = new Timestamp(System.currentTimeMillis());
        Events.callEvent(new reStockShopEvent(this));
        guis.reStock(!announce_restock);
    }

    /**
     * Updates the item of the shop
     *
     * @param newItem
     */
    public void updateItem(dItem newItem) {
        UUID uid = newItem.getUid();
        if (uid == null) return;

        if (!items.containsKey(uid)) {
            addItem(newItem);
            return;
        }

        items.put(uid, newItem);
        guis.updateItem(new updateItemEvent(uid, updateItemEvent.type.UPDATE_ITEM, this));    // Event to update item
    }


    /**
     * Sets the items of this shop
     */
    public void setItems(@NotNull Collection<dItem> items) {
        Map<UUID, dItem> newItems = new HashMap<>();
        items.forEach(dItem -> newItems.put(dItem.getUid(), dItem));            // Cache values for a O(1) search

        for (Iterator<Map.Entry<UUID, dItem>> it = new HashMap<>(this.items).entrySet().iterator(); it.hasNext(); ) {          // Remove or update
            Map.Entry<UUID, dItem> entry = it.next();

            if (newItems.containsKey(entry.getKey())) {     // Update items if changed
                dItem toUpdateItem = newItems.remove(entry.getKey());

                if (toUpdateItem != null && !toUpdateItem.isSimilar(entry.getValue())) {
                    updateItem(toUpdateItem);
                }
            } else
                removeItem(entry.getKey());

        }

        newItems.values().forEach(this::addItem);       // Replace the old values for the new ones
    }

    /**
     * Adds an item to this shop
     *
     * @param item item to be added
     */
    public void addItem(@NotNull dItem item) {
        items.put(item.getUid(), item);
    }

    /**
     * Removes an item from the shop
     *
     * @param uid UUID of the item to be removed
     * @return true if the item was removed. False if not
     */
    public boolean removeItem(UUID uid) {
        dItem removed = items.remove(uid);
        if (removed == null) return false;
        Events.callEvent(new updateItemEvent(uid, updateItemEvent.type.DELETE_ITEM, this));
        return true;
    }

    public void updateShopGui(dInventory inv) {
        guis.updateBase(inv);
    }

    public void updateShopGui(dInventory inv, boolean isSilent) {
        guis.updateBase(inv, isSilent);
    }

    /**
     * Return the dGui of this shop
     *
     * @return
     */
    public syncMenu getGuis() {
        return guis;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public int getTimer() {
        return timer;
    }

    public boolean get_announce() {
        return announce_restock;
    }

    public void set_announce(boolean announce_restock) {
        this.announce_restock = announce_restock;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void destroy() {
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
        return o instanceof dShop && this.getName().equalsIgnoreCase(((dShop) o).getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }


    /**
     * Serializers
     **/

    public static class encodeOptions {

        public static transient final jsonSerializer JSON = new jsonSerializer();

        private encodeOptions() {
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
