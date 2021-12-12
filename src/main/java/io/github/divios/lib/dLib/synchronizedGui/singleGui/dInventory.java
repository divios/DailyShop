package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.transaction.sellTransaction;
import io.github.divios.dailyShop.transaction.transaction;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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
import java.util.stream.IntStream;

@SuppressWarnings({"ConstantConditions", "deprecation", "unchecked", "unused"})
public class dInventory {

    protected static final DailyShop plugin = DailyShop.getInstance();

    protected String title;       // for some reason is throwing noSuchMethod
    protected Inventory inv;
    protected final dShop shop;

    protected final ConcurrentSkipListSet<Integer> dailyItemsSlots = new ConcurrentSkipListSet<>();
    protected final ConcurrentHashMap<UUID, dItem> buttons = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<Integer, dItem> buttonsSlot = new ConcurrentHashMap<>();

    private final Set<Subscription> listeners = new HashSet<>();

    public static dInventory fromBase64(String base64, dShop shop) {
        return deserialize(base64, shop);
    }

    public dInventory(String title, int size, dShop shop) {
        this(title, Bukkit.createInventory(null, size, title), shop);
    }

    public dInventory(dShop shop) {
        this(shop.getName(), 27, shop);
    }

    protected dInventory(String title, Inventory inv, dShop shop) {
        this.title = title;
        this.inv = inv;
        this.shop = shop;

        IntStream.range(0, inv.getSize()).forEach(dailyItemsSlots::add);
        createListeners();
    }

    /**
     * Opens the inventory for a player
     *
     * @param p The player to open the inventory to.
     */
    public void openInventory(Player p) {
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
    public void setInventoryTitle(String title) {
        this.title = title;
        Inventory temp = Bukkit.createInventory(null, inv.getSize(), title);
        inventoryUtils.translateContents(inv, temp);
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

        Inventory aux = Bukkit.createInventory(null, inv.getSize() + 9, title);
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

        Inventory aux = Bukkit.createInventory(null, inv.getSize() - 9, title);
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
     * attached to it will be executed
     *
     * @param item ItemStack to be added
     * @param slot Integer representing the position of the new item on the inventory.
     */
    public void addButton(ItemStack item, int slot) {
        addButton(dItem.of(item), slot);
    }

    /**
     * Adds an item to the inventory. When this item is clicked, the action
     * attacked to it will be executed
     *
     * @param item ItemStack to be added
     * @param slot Integer representing the position of the new item on the inventory.
     */
    public void addButton(dItem item, int slot) {
        if (slot >= inv.getSize()) return;

        dItem crashItem;
        if ((crashItem = buttonsSlot.get(slot)) != null)        // If there is a previous item with that slot, remove first
            removeButton(crashItem.getUid());

        dItem cloned = item.clone();
        cloned.generateNewBuyPrice();
        cloned.generateNewSellPrice();
        cloned.setSlot(slot);
        buttons.put(cloned.getUid(), cloned);
        buttonsSlot.put(slot, cloned);
        dailyItemsSlots.remove(slot);
        if (!item.isAIR())
            inv.setItem(slot, cloned.getItem());
        else
            inv.clear(slot);
    }

    /**
     * Removes a button from the inventory by its slot
     *
     * @param slot The Integer representing the slot to be removed.
     * @return The item that was removed if any.
     */
    public dItem removeButton(int slot) {
        dItem item;
        return removeButton((item = buttonsSlot.get(slot)) == null ? null : item.getUid());
    }

    /**
     * Removes a button from the inventory by its UUID
     *
     * @param uuid The UUID of the item to be removed.
     * @return The item that was removed if any.
     */
    public dItem removeButton(UUID uuid) {
        if (uuid == null) return null;      // ConcurrentHashMap throws error on null keys
        dItem removedItem = buttons.remove(uuid);
        if (removedItem != null) {
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
    protected void restock(Set<dItem> itemsToRoll) {
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
     * @param item   The item to update
     * @param delete If the item should be deleted or updated
     */
    protected void updateItem(dItem item, boolean delete) {
        dItem toUpdateItem = buttons.get(item.getUid());
        if (toUpdateItem == null) return;

        int slot = toUpdateItem.getSlot();
        if (delete) {
            removeButton(item.getUid());
            inv.setItem(slot, Utils.getRedPane());
        } else {
            addButton(item, slot);
            dailyItemsSlots.add(slot);
        }
    }

    /**
     * Returns a copy of this inventory without the daily Items,
     * only the buttons added manually.
     *
     * @return The inventory in question.
     */
    public dInventory skeleton() {
        dInventory cloned = this.copy();
        cloned.removeDailyItems();
        cloned.listeners.forEach(Subscription::unregister);

        cloned.buttons.entrySet().stream()   // gets the AIR buttons back
                .filter(entry -> entry.getValue().isAIR())
                .forEach(entry -> cloned.inv.setItem(entry.getValue().getSlot(), entry.getValue().getItem()));

        return cloned;
    }

    /**
     * Returns a full copy of this inventory
     *
     * @return The copy of this inventory.
     */
    public dInventory copy() {
        return fromBase64(toBase64(), shop);
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
                ", shop=" + shop +
                ", dailyItemsSlots=" + dailyItemsSlots +
                ", buttons=" + buttons +
                ", buttonsSlots=" + buttonsSlot +
                ", listeners=" + listeners +
                '}';
    }

    /**
     * Serializes this inventory into base64.
     *
     * @return The String representing this inventory as base64.
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dInventory that = (dInventory) o;
        /*Log.info("Titles: " + Objects.equals(title, that.title));
        Log.info("Invs: " + compareInvs(that.inv, inv));
        Log.info("Shop: " + Objects.equals(shop, that.shop));
        Log.info("DailySlots: " + Objects.equals(dailyItemsSlots, that.dailyItemsSlots));*/
        return Objects.equals(title, that.title) && compareInvs(that.inv, inv) && Objects.equals(shop, that.shop) && Objects.equals(dailyItemsSlots, that.dailyItemsSlots);
    }

    @Override
    public int hashCode() {
        int invHash = Arrays.stream(inv.getContents())
                .map(itemStack -> new NBTItem(itemStack).toString())
                .mapToInt(String::hashCode)
                .sum();

        return Objects.hash(title, invHash, shop, dailyItemsSlots);
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

                            dItem itemClicked = buttons.get(dItem.getUid(e.getCurrentItem()));
                            if (itemClicked == null) return;

                            if (dailyItemsSlots.contains(itemClicked.getSlot())) {
                                if (e.isLeftClick())
                                    transaction.init((Player) e.getWhoClicked(), buttons.get(itemClicked.getUid()), shop);
                                else if (e.isRightClick())
                                    sellTransaction.create((Player) e.getWhoClicked(), buttons.get(itemClicked.getUid()), shop);
                            } else {
                                itemClicked.getAction().stream((dAction, s) -> dAction.run((Player) e.getWhoClicked(), s));
                            }
                        })
        );
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
                .filter(entry -> entry.getValue().isAIR())
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
        dInventory newInv[] = {null};
        try (ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64))) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream)) {

                inventoryUtils.deserialize((String) dataInput.readObject()).stream((s1, inv) -> {
                    newInv[0] = new dInventory(s1, inv, shop);
                });

                newInv[0].dailyItemsSlots.clear();
                newInv[0].dailyItemsSlots.addAll((Set<Integer>) dataInput.readObject());

                Object o = dataInput.readObject();
                if (o instanceof Set)
                    ((Set<dItem>) o).forEach(dItem -> {
                        newInv[0].buttons.put(dItem.getUid(), dItem);
                        newInv[0].buttonsSlot.put(dItem.getSlot(), dItem);
                    });
                else            // Remember that for some reason java cannot serialize/deserialize UUIDs
                    ((Map<UUID, dItem>) o).values().forEach(dItem -> {
                        newInv[0].buttons.put(dItem.getUid(), dItem);
                        newInv[0].buttonsSlot.put(dItem.getSlot(), dItem);
                    });

                return newInv[0];
            }

        } catch (Exception e) {
            Log.severe("Unable to deserialize gui of shop "
                    + shop.getName() + ", setting it to default");
            e.printStackTrace();
            return new dInventory(shop);
        }

    }

    private boolean compareInvs(Inventory inv1, Inventory inv2) {
        ItemStack contents1[] = inv1.getContents();
        ItemStack contents2[] = inv2.getContents();

        if (contents1.length != contents2.length) return false;

        for (int i = 0; i < contents1.length; i++) {

            //Log.info((ItemUtils.isEmpty(contents1[i]) ? "" : contents1[i].getType().name()) + " -----> " + (ItemUtils.isEmpty(contents2[i]) ? "" : contents2[i].getType().name()));

            if ((contents1[i] != null && contents2[i] == null)
                    || (contents1[i] == null && contents2[i] != null)) return false;

            if (contents1[i] == null && contents2[i] == null) continue;

            if (!contents1[i].isSimilar(contents2[i]))
                return false;

        }
        return true;
    }

}
