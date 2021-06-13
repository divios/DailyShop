package io.github.divios.lib.itemHolder;

import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.core_lib.misc.WeightedRandom;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.lorestategy.loreStrategy;
import io.github.divios.dailyrandomshop.lorestategy.shopItemsLore;
import io.github.divios.dailyrandomshop.transaction.transaction;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class dGui {

    private static final DRShop plugin = DRShop.getInstance();

    private String title;       // for some reason is throwing noSuchMethod
    private Inventory inv;
    private final dShop shop;

    private boolean available = true;
    private final Set<Integer> openSlots = new HashSet<>();
    private final Set<dItem> buttons = new HashSet<>();

    private transient EventListener<InventoryClickEvent> clickEvent;
    private transient EventListener<InventoryDragEvent> dragEvent;
    private transient EventListener<InventoryOpenEvent> openEvent;

    private final loreStrategy strategy;

    protected dGui(String title, Inventory inv, dShop shop) {
        this.title = title;
        this.inv = inv;
        this.shop = shop;

        this.strategy = new shopItemsLore(shop.getType());

        IntStream.range(0, inv.getSize()).forEach(openSlots::add);
        renovate();   // not really necessary but why not

        initListeners();
    }

    protected dGui(String title, int size, dShop shop) {
        this(title,
                Bukkit.createInventory(null, size, title), shop);
    }

    private dGui(String base64, dShop shop) {
        _deserialize(base64);
        this.shop = shop;
        this.strategy = new shopItemsLore(shop.getType());

        initListeners();
    }

    protected dGui(dShop shop) {
        this(shop.getName(), 27, shop);
    }

    /**
     * Opens the inventory for a player
     * @param p The player to open the inventory
     */
    public void open(Player p) { p.openInventory(inv); }

    /**
     * Closes the inventory for all the current viewers
     */
    public void closeAll() {
        try {
            inv.getViewers().forEach(HumanEntity::closeInventory);
        } catch (Exception ignored) {}
    }

    /**
     * Returns the title of this gui
     * @return The String representing the title
     */
    public String getTitle() { return title; }

    /**
     * Sets the inventory title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        Inventory temp = Bukkit.createInventory(null, inv.getSize(), title);
        inventoryUtils.translateContents(inv, temp);
        inv = temp;
    }

    /**
     * Returns a copy of this instance inventory
     * @return The Copied inventory
     */
    public Inventory getInventory() { return inventoryUtils.cloneInventory(inv, title); }

    public boolean addRow() {
        if (inv.getSize() == 54) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() + 9, title);
        inventoryUtils.translateContents(inv, aux);

        IntStream.range(inv.getSize(), inv.getSize() + 9).forEach(openSlots::add);
        inv = aux;

        return true;
    }

    public boolean removeRow() {
        if (inv.getSize() == 9) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() - 9, title);
        inventoryUtils.translateContents(inv, aux);

        IntStream.range(inv.getSize() - 9, inv.getSize()).forEach(openSlots::remove);
        inv = aux;

        return true;
    }

    /**
     * Checks if the inventory is available
     * @return true if it is, false if is being edited
     */
    public boolean getAvailable() { return available; }

    /**
     * Sets the availability of the inventory
     * @param value Boolean
     */
    public void setAvailable(boolean value) {
        available = value;
        if (!value) closeAll();
    }

    /**
     * Adds a button to the inventory
     * @param item The item to add
     * @param slot The slot where the item 'll be added
     */
    public void addButton(dItem item, int slot) {
        dItem cloned = item.clone();
        cloned.setSlot(slot);

        if (buttons.contains(cloned))
            buttons.removeIf(dItem -> dItem.getSlot() == slot);     // remove to overwrite
        buttons.add(cloned);

        openSlots.remove(slot);
        inv.setItem(slot, cloned.getItem());
    }

    /**
     * Removes a slot from the inventory
     * @param slot The slot which wants to be clear
     * @return true if an item was successfully removed
     */
    public boolean removeButton(int slot) {
        boolean result = buttons.removeIf(item -> item.getSlot() == slot);
        if (result) {
            openSlots.add(slot);
            inv.clear(slot);
        }
        return result;
    }

    /**
     * Gets the button in this object
     * @return An unmodifiable view of the buttons set
     */
    public Set<dItem> getButtons() {
        return Collections.unmodifiableSet(buttons);
    }

    /**
     * Gets the openSlots in this object
     * @return An unmodifiable view of the open Slots
     */
    public Set<Integer> getOpenSlots() {
        return Collections.unmodifiableSet(openSlots);
    }

    /**
     * Renovates daily items on the openSlots
     */
    protected void renovate() {

        IntStream.range(0, inv.getSize()).forEach(i ->   // Removes all the AIR items
                buttons.stream()
                .filter(dItem -> dItem.getSlot() == i)
                .findFirst()
                .ifPresent(dItem -> {
                    if (dItem.isAIR()) inv.clear(i);
                }));

        List<dItem> added = new ArrayList<>();
        WeightedRandom<dItem> RRM = WeightedRandom.fromCollection(
                shop.getItems().stream().filter(dItem -> dItem.getRarity().getWeight() != 0)
                        .collect(Collectors.toList()),  // remove unAvailable
                item -> {
                    dItem cloned = item.clone();
                    cloned.applyLoreStrategy(strategy);
                    return cloned;
                },
                value -> value.getRarity().getWeight()
        );

        clearDailyItems();

        openSlots.forEach(i -> {
            inv.clear(i);

            if (added.size() >= shop.getItems().size()) return;

            ItemStack toAdd;

            while(true) {
                dItem aux = RRM.roll();
                if (added.contains(aux))
                    continue;
                added.add(aux);
                toAdd = aux.getItem();
                break;
            }

            inv.setItem(i, toAdd);
        });

        Bukkit.broadcastMessage("Renovated items of shop " + shop.getName());
    }

    /**
     * Clears all the slots corresponding to daily Items
     */
    private void clearDailyItems() {
        openSlots.forEach(i -> inv.clear(i));
    }


    private void initListeners() {

        this.clickEvent = new EventListener<>(plugin, InventoryClickEvent.class,
                EventPriority.HIGHEST, e -> {
            if (e.getInventory() != inv) return;

            e.setCancelled(true);

            if (openSlots.contains(e.getSlot()) &&
                    shop.getType().equals(dShop.dShopT.buy)) {}
            //transaction.initTransaction(e.getWhoClicked(), );
            else {
                if (utils.isEmpty(e.getCurrentItem())) return;

                buttons.stream().filter(dItem -> dItem.getSlot() == e.getSlot())
                        .findFirst().ifPresent(dItem -> dItem.getAction()
                        .stream((dAction, s) -> dAction.run((Player) e.getWhoClicked(), s)));
            }
        });

        this.dragEvent = new EventListener<>(plugin, InventoryDragEvent.class,
                e -> {
            if (e.getInventory() != inv) return;

            e.setCancelled(true);

        });

        this.openEvent = new EventListener<>(plugin, InventoryOpenEvent.class,
                EventPriority.HIGHEST, e -> {
            if (e.getInventory() != inv) return;

            if (!available) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("The shop is closed... come again in some minutes");
            }
        });

    }

    /**
     * Destroys the inventory. In summary, unregisters all the listeners
     */
    protected void destroy() {
        clickEvent.unregister();
        dragEvent.unregister();
        openEvent.unregister();
    }

    public String serialize() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Serialize inventory
            dataOutput.writeObject(inventoryUtils.serialize(inv, title));

            // Serialize openSlots
            dataOutput.writeObject(openSlots);

            // Serialize buttons
            dataOutput.writeObject(buttons);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize inventory.", e);
        }
    }

    private void _deserialize(String base64) {
        try {
            ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream);

            Pair<String, Inventory> s = inventoryUtils.deserialize((String) dataInput.readObject());
            title = s.get1();
            inv = s.get2();

            openSlots.addAll((Set<Integer>) dataInput.readObject());

            buttons.addAll((Set<dItem>) dataInput.readObject());
            dataInput.close();


        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize inventory.", e);
        }
    }

    public static dGui deserialize(String base64, dShop shop) { return new dGui(base64, shop); }

    // Returns a clone of this gui without the daily items
    public dGui clone() {
        dGui cloned = deserialize(this.serialize(), shop);
        cloned.clearDailyItems();

        cloned.getButtons().stream()   // gets the AIR buttons back
                .filter(dItem::isAIR)
                .forEach(dItem -> cloned.inv
                        .setItem(dItem.getSlot(), dItem.getItem()));

        return  cloned; }


}
