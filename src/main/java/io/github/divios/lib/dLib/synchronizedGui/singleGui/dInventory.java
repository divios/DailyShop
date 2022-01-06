package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.dTransaction.SingleTransaction;
import io.th0rgal.oraxen.utils.message.MessageAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.IntStream;

@SuppressWarnings({"ConstantConditions", "unchecked", "unused", "UnusedReturnValue", "UnstableApiUsage"})
public class dInventory implements Cloneable {

    protected static final DailyShop plugin = DailyShop.get();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, (JsonSerializer<dItem>) (dItem, type, jsonSerializationContext) -> dItem.toJson())
            .registerTypeAdapter(dItem.class, (JsonDeserializer<dItem>) (jsonElement, type, jsonDeserializationContext) -> dItem.fromJson(jsonElement))
            .create();

    private static final TypeToken<ConcurrentHashMap<UUID, dItem>> buttonsToken = new TypeToken<ConcurrentHashMap<UUID, dItem>>() {
    };
    private static final TypeToken<ConcurrentSkipListSet<Integer>> dailySlotsToken = new TypeToken<ConcurrentSkipListSet<Integer>>() {
    };

    private static final Function<Integer, ConcurrentSkipListSet<Integer>> getDefaultSlots = input -> {
        ConcurrentSkipListSet<Integer> defaultList = new ConcurrentSkipListSet<>();
        IntStream.range(0, input).forEach(defaultList::add);

        return defaultList;
    };

    @Deprecated
    public static dInventory fromBase64(String base64, dShop shop) {
        return deserialize(base64, shop);
    }

    public static dInventory fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(!object.get("title").isJsonNull());
        Preconditions.checkArgument(!object.get("inventory").isJsonNull());

        String title = object.get("title").getAsString();

        Inventory inv = inventoryUtils.fromJson(object.get("inventory"));
        ConcurrentSkipListSet<Integer> dailyItemsSlots = gson.fromJson(object.get("dailySlots"), dailySlotsToken.getType());
        ConcurrentHashMap<UUID, dItem> buttons = gson.fromJson(object.get("buttons"), buttonsToken.getType());
        ConcurrentHashMap<Integer, dItem> buttonsSlots = new ConcurrentHashMap<>();

        buttons.forEach((uuid, dItem) -> buttonsSlots.put(dItem.getSlot(), dItem));

        return new dInventory(title, inv, dailyItemsSlots, buttons, buttonsSlots);

    }

    protected String title;       // for some reason is throwing noSuchMethod
    protected Inventory inv;

    protected ConcurrentSkipListSet<Integer> dailyItemsSlots;
    protected ConcurrentHashMap<UUID, dItem> buttons;
    protected ConcurrentHashMap<Integer, dItem> buttonsSlot;

    private Set<Subscription> listeners = new HashSet<>();

    public dInventory(String title, int size) {
        this(title, Bukkit.createInventory(null, size, Utils.JTEXT_PARSER.parse(title)));
    }

    protected dInventory(String title, Inventory inv) {
        this(title, inv,
                getDefaultSlots.apply(inv.getSize()),
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>());
    }

    private dInventory(String title,
                       Inventory inv,
                       ConcurrentSkipListSet<Integer> dailyItemsSlots,
                       ConcurrentHashMap<UUID, dItem> buttons,
                       ConcurrentHashMap<Integer, dItem> buttonsSlot) {
        this.title = title;
        this.inv = inv;
        this.dailyItemsSlots = dailyItemsSlots;
        this.buttons = buttons;
        this.buttonsSlot = buttonsSlot;

        createListeners();
    }

    /**
     * Opens the inventory for a player
     *
     * @param p The player to open the inventory to.
     */
    public void openInventory(@NotNull Player p) {
        p.openInventory(inv);
    }

    /**
     * Gets the title of this inventory
     *
     * @return String representing the title of this inventory
     */
    public String getInventoryTitle() {
        return title;
    }

    /**
     * Sets the title for this inventory
     *
     * @param title String representing the new title of the inventory.
     */
    public void setInventoryTitle(@NotNull String title) {
        this.title = title;
        Inventory temp = Bukkit.createInventory(null, inv.getSize(), Utils.JTEXT_PARSER.parse(title));
        temp.setContents(inv.getContents());
        inv = temp;
    }

    /**
     * Get the inventory that holds this object
     *
     * @return The inventory in question
     */
    public Inventory getInventory() {
        return inv;
    }

    /**
     * Gets this inventory size
     *
     * @return Int representing the inventory size
     */
    public int getInventorySize() {
        return inv.getSize();
    }

    /**
     * Adds a new row to the inventory, in other words, increases the size
     * of the inventory by 9.
     *
     * @return Returns a boolean representing if the inventory
     * has been altered due to this function.
     */
    public boolean addInventoryRow() {
        if (inv.getSize() == 54) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() + 9, Utils.JTEXT_PARSER.parse(title));
        inventoryUtils.translateContents(inv, aux);
        IntStream.range(inv.getSize(), inv.getSize() + 9).forEach(dailyItemsSlots::add);
        inv = aux;

        return true;
    }

    /**
     * Removes a row of the inventory, in other words, decreases the inventory
     * size by 9
     *
     * @return Returns a boolean representing if the inventory
     * has been altered due to this function.
     */
    public boolean removeInventoryRow() {
        if (inv.getSize() == 9) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() - 9, Utils.JTEXT_PARSER.parse(title));
        inventoryUtils.translateContents(inv, aux);
        IntStream.range(inv.getSize() - 9, inv.getSize()).forEach(value -> {
            removeButton(value);
            dailyItemsSlots.remove(value);
        });
        inv = aux;

        return true;
    }

    /**
     * Adds an item to the inventory. When this item is clicked, the action
     * attacked to it will be executed
     *
     * @param item ItemStack to be added
     * @param slot Integer representing the position of the new item on the inventory.
     */
    public void addButton(@NotNull dItem item, int slot) {
        DebugLog.info("Trying to add item of ID " + item.getID() + " in slot " + slot);
        if (slot >= inv.getSize()) {
            DebugLog.info("Tried to add item but is out of bounds");
            return;
        }

        dItem crashItem;
        if ((crashItem = buttonsSlot.get(slot)) != null) {        // If there is a previous item with that slot, remove first
            DebugLog.info("Crashed with another item, removing firsts");
            removeButton(crashItem.getUUID());
        }

        dItem cloned = item.clone();
        cloned.setSlot(slot);

        buttons.put(cloned.getUUID(), cloned);
        buttonsSlot.put(slot, cloned);
        dailyItemsSlots.remove(slot);

        if (!item.isAir()) {
            DebugLog.info("Added item to inventory");
            inv.setItem(slot, cloned.getItemWithId());
        } else {
            DebugLog.info("Not adding since is air");
            inv.clear(slot);
        }
    }

    /**
     * Removes a button from the inventory by its slot
     *
     * @param slot The Integer representing the slot to be removed.
     * @return The item that was removed if any.
     */
    public dItem removeButton(int slot) {
        dItem item;
        return removeButton((item = buttonsSlot.get(slot)) == null ? null : item.getUUID());
    }

    /**
     * Removes a button from the inventory by its UUID
     *
     * @param uuid The UUID of the item to be removed.
     * @return The item that was removed if any.
     */
    public dItem removeButton(@NotNull UUID uuid) {
        if (uuid == null) return null;      // ConcurrentHashMap throws error on null keys
        dItem removedItem = buttons.remove(uuid);
        if (removedItem != null) {
            DebugLog.info("Removed item of ID: " + removedItem.getID() + " on slot: " + removedItem.getSlot());
            buttonsSlot.remove(removedItem.getSlot());
            dailyItemsSlots.add(removedItem.getSlot());
            inv.clear(removedItem.getSlot());
        }
        return removedItem;
    }

    /**
     * Gets an unmodifiable view of the buttons of the inventory.
     *
     * @return A map representing the buttons of this inventory.
     */
    public Map<UUID, dItem> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    /**
     * Gets an unmodifiable view of the buttons of the inventory.
     *
     * @return A map representing the buttons of this inventory.
     */
    public Map<Integer, dItem> getButtonsSlots() {
        return Collections.unmodifiableMap(buttonsSlot);
    }

    /**
     * Gets the slots where the daily random items can appear.
     *
     * @return An unmodifiable set of the slots.
     */
    public Set<Integer> getDailyItemsSlots() {
        return Collections.unmodifiableSet(dailyItemsSlots);
    }

    /**
     * Triggers the generation of new random items based on the
     * items passed as parameter.
     *
     * @param itemsToRoll A collection of items that will be chosen as daily items.
     */
    protected void restock(@NotNull Set<dItem> itemsToRoll) {
        if (dailyItemsSlots.isEmpty()) return;

        removeAirItems();     // Just in case
        removeDailyItems();

        int index = dailyItemsSlots.first();
        for (dItem item : itemsToRoll) {
            addButton(item, (index = dailyItemsSlots.ceiling(index)));
            dailyItemsSlots.add(index++);     // Restore slot
        }
    }

    /**
     * Updates a dailyItem of the inventory.
     * This function should only be called from protected sources.
     *
     * @param item The item to update
     */
    protected void updateDailyItem(@NotNull dItem item) {
        dItem toUpdateItem = buttons.get(item.getUUID());
        if (toUpdateItem == null) return;

        int slot = toUpdateItem.getSlot();
        DebugLog.info("Update item on dInventory of ID: " + item.getID() + " Slot: " + slot);
        addButton(item, slot);
        dailyItemsSlots.add(slot);
    }

    /**
     * Returns a copy of this inventory without the daily Items,
     * only the buttons added manually.
     *
     * @return The inventory in question.
     */
    public dInventory skeleton() {
        dInventory cloned = this.deepClone();
        cloned.removeDailyItems();
        cloned.listeners.forEach(Subscription::unregister);

        cloned.buttons.entrySet().stream()   // gets the AIR buttons back
                .filter(entry -> entry.getValue().isAir())
                .forEach(entry -> cloned.inv.setItem(entry.getValue().getSlot(), entry.getValue().getItemWithId()));

        return cloned;
    }

    /**
     * Returns a full copy of this inventory. This is
     * the same as calling clone()
     *
     * @return The copy of this inventory.
     */
    public dInventory copy() {
        return clone();
    }

    /**
     * Destroys this inventory, in other words, unregisters all the listeners
     * and leaves all the variables for the garbage collector
     */
    public void destroy() {
        listeners.forEach(Subscription::unregister);
        listeners.clear();
    }

    @Override
    public String toString() {
        return "dInventory{" +
                "title='" + title + '\'' +
                ", inv=" + inv +
                ", dailyItemsSlots=" + dailyItemsSlots +
                ", buttons=" + buttons +
                ", buttonsSlots=" + buttonsSlot +
                ", listeners=" + listeners +
                '}';
    }

    private boolean isSimilar(Inventory inv1, Inventory inv2) {
        ItemStack[] contents1 = inv1.getContents();
        ItemStack[] contents2 = inv2.getContents();

        if (contents1.length != contents2.length) return false;

        for (int i = 0; i < contents1.length; i++) {

            if ((contents1[i] != null && contents2[i] == null)
                    || (contents1[i] == null && contents2[i] != null)) return false;

            if (contents1[i] == null && contents2[i] == null) continue;

            if (!contents1[i].isSimilar(contents2[i]))
                return false;

        }
        return true;
    }

    @Override
    public dInventory clone() {
        try {
            dInventory clone = (dInventory) super.clone();

            clone.inv = inventoryUtils.cloneInventory(inv, title);

            //clone.dailyItemsSlots = (TreeSet<Integer>) dailyItemsSlots.clone();       // Shouldn't be necessary to
            // deep copy this, can be shared
            //clone.buttons = new ConcurrentHashMap<>();
            //buttons.forEach((uuid, dItem) -> clone.buttons.put(uuid, dItem.clone()));

            //clone.buttonsSlot = new ConcurrentHashMap<>();
            //buttonsSlot.forEach((integer, dItem) -> clone.buttonsSlot.put(integer, dItem.clone()));

            clone.listeners = new HashSet<>();
            clone.createListeners();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public dInventory deepClone() {
        dInventory clone = clone();

        clone.dailyItemsSlots = dailyItemsSlots.clone();

        clone.buttons = new ConcurrentHashMap<>();
        clone.buttonsSlot = new ConcurrentHashMap<>();
        buttons.forEach((uuid, dItem) -> {
            dItem cloned = dItem.clone();
            clone.buttons.put(uuid, cloned);
            clone.buttonsSlot.put(dItem.getSlot(), cloned);
        });

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dInventory that = (dInventory) o;
        return Objects.equals(title, that.title)
                && Objects.equals(inv, that.inv)
                && Objects.equals(dailyItemsSlots, that.dailyItemsSlots)
                && Objects.equals(buttons, that.buttons)
                && Objects.equals(buttonsSlot, that.buttonsSlot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title,
                inv,
                dailyItemsSlots,
                buttons,
                buttonsSlot);
    }

    public JsonElement toJson() {
        return JsonBuilder.object()
                .add("title", title)
                .add("inventory", inventoryUtils.toJson(title, inv))
                .add("dailySlots", gson.toJsonTree(this.dailyItemsSlots, dailySlotsToken.getType()))
                .add("buttons", gson.toJsonTree(buttons, buttonsToken.getType()))
                .build();
    }

    /**
     * Serializes this inventory into base64.
     *
     * @return The String representing this inventory as base64.
     */
    @Deprecated
    public String toBase64() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                // Serialize inventory
                dataOutput.writeObject(inventoryUtils.serialize(inv, title));
                // Serialize openSlots
                dataOutput.writeObject(dailyItemsSlots);
                // Serialize buttons
                dataOutput.writeObject(buttons);
                return Base64Coder.encodeLines(outputStream.toByteArray());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize inventory.", e);
        }
    }

    /**********  Utils  **********/

    protected void createListeners() {
        createClickInventoryListener();
        createDragEventListener();
    }

    private void createClickInventoryListener() {
        listeners.add(
                Events.subscribe(InventoryClickEvent.class, EventPriority.HIGHEST)
                        .filter(e -> e.getInventory().equals(inv))
                        .filter(e -> !ItemUtils.isEmpty(e.getCurrentItem()))
                        .handler(e -> {
                            e.setCancelled(true);
                            if (e.getSlot() != e.getRawSlot()) return;  // is not upper inventory

                            dItem itemClicked = buttonsSlot.get(e.getSlot());
                            if (itemClicked == null) return;
                            itemClicked = itemClicked.clone();

                            Player player = (Player) e.getWhoClicked();
                            if (dailyItemsSlots.contains(e.getSlot())) {
                                if (e.isLeftClick()) {

                                    DebugLog.info("ItemClicked: " + itemClicked.toJson());

                                    if (!meetsPermissions(player, itemClicked.getBuyPerms())) {
                                        Messages.MSG_NOT_PERMS.send(player);
                                        return;
                                    }

                                    if (itemClicked.getDStock() != null && itemClicked.getPlayerStock(player) <= 0) {
                                        Messages.MSG_NOT_STOCK.send(player);
                                        return;
                                    }

                                    if (itemClicked.getBuyPrice() <= 0) {
                                        Messages.MSG_INVALID_BUY.send(player);
                                        return;
                                    }

                                    if (Arrays.stream(Arrays.copyOf(player.getInventory().getContents(), 36))
                                            .noneMatch(Objects::isNull)) {
                                        Messages.MSG_INV_FULL.send(player);
                                        return;
                                    }

                                    Events.callEvent(new TransactionEvent(this,
                                            SingleTransaction.Type.BUY,
                                            player,
                                            itemClicked
                                    ));

                                } else if (e.isRightClick()) {
                                    if (!meetsPermissions(player, itemClicked.getSellPerms())) {
                                        Messages.MSG_NOT_PERMS.send(player);
                                        return;
                                    }

                                    if (itemClicked.getSellPrice() <= 0) {
                                        Messages.MSG_INVALID_SELL.send(player);
                                        return;
                                    }

                                    if (ItemUtils.count(player.getInventory(), itemClicked.getItem()) <= 0) {
                                        Messages.MSG_NOT_ITEMS.send(player);
                                        return;
                                    }

                                    Events.callEvent(new TransactionEvent(this,
                                            SingleTransaction.Type.SELL,
                                            player,
                                            itemClicked
                                    ));

                                }
                            } else {
                                itemClicked.getAction().execute(player);
                            }
                        })
        );
    }

    private boolean meetsPermissions(@NotNull Player player, @Nullable List<String> perms) {
        if (perms == null) return true;
        for (String perm : perms) {
            if (!player.hasPermission(perm)) return false;
        }
        return true;
    }

    private void createDragEventListener() {
        listeners.add(
                Events.subscribe(InventoryDragEvent.class)
                        .filter(o -> o.getInventory().equals(inv))
                        .handler(e -> e.setCancelled(true))
        );
    }

    private void removeAirItems() {
        buttons.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isAir())
                .forEach(entry -> inv.clear(entry.getValue().getSlot()));  // Clear AIR items
    }

    private void removeDailyItems() {
        for (Iterator<Map.Entry<UUID, dItem>> it = buttons.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, dItem> entry = it.next();
            if (dailyItemsSlots.contains(entry.getValue().getSlot())) {
                it.remove();
                buttonsSlot.remove(entry.getValue().getSlot());
                inv.clear(entry.getValue().getSlot());
            }
        }
    }

    private static dInventory deserialize(String base64, dShop shop) {
        dInventory[] newInv = {null};
        try (ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64))) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream)) {

                inventoryUtils.deserialize((String) dataInput.readObject()).stream((s1, inv) ->
                        newInv[0] = new dInventory(s1, inv));

                newInv[0].dailyItemsSlots.clear();
                newInv[0].dailyItemsSlots.addAll((Set<Integer>) dataInput.readObject());

                Object o = dataInput.readObject();
                if (o instanceof Set)
                    ((Set<dItem>) o).forEach(dItem -> {
                        newInv[0].buttons.put(dItem.getUUID(), dItem);
                        newInv[0].buttonsSlot.put(dItem.getSlot(), dItem);
                    });
                else            // Remember that for some reason java cannot serialize/deserialize UUIDs
                    ((Map<UUID, dItem>) o).values().forEach(dItem -> {
                        newInv[0].buttons.put(dItem.getUUID(), dItem);
                        newInv[0].buttonsSlot.put(dItem.getSlot(), dItem);
                    });

                return newInv[0];
            }

        } catch (Exception | Error e) {
            Log.severe("Unable to deserialize gui of shop "
                    + shop.getName() + ", . Probably is caused by a server downgrade? Setting it to default");
            //e.printStackTrace();
            return new dInventory(shop.getName(), 27);
        }

    }

}
