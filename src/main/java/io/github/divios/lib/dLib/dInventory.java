package io.github.divios.lib.dLib;

import io.github.divios.core_lib.Events;
import io.github.divios.core_lib.event.SingleSubscription;
import io.github.divios.core_lib.event.Subscription;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.core_lib.misc.WeightedRandom;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.transaction.sellTransaction;
import io.github.divios.dailyShop.transaction.transaction;
import io.github.divios.dailyShop.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class dInventory {

    protected static final DailyShop plugin = DailyShop.getInstance();

    protected String title;       // for some reason is throwing noSuchMethod
    protected Inventory inv;
    protected final dShop shop;

    protected final Set<Integer> openSlots = Collections.synchronizedSet(new HashSet<>());
    protected final Set<dItem> buttons = Collections.synchronizedSet(new HashSet<>());

    private final Set<SingleSubscription> listeners = new HashSet<>();
    protected final loreStrategy strategy;

    public dInventory(String title, Inventory inv, dShop shop) {
        this.title = title;
        this.inv = inv;
        this.shop = shop;

        this.strategy = new shopItemsLore(shop.getType());

        IntStream.range(0, inv.getSize()).forEach(openSlots::add);  //initializes slots
        renovate();   // not really necessary but why not

        ready();
    }

    public dInventory(String title, int size, dShop shop) {
        this(title,
                Bukkit.createInventory(null, size, title), shop);
    }

    public dInventory(String base64, dShop shop) {
        this.shop = shop;
        this.strategy = new shopItemsLore(shop.getType());
        _deserialize(base64);

        ready();
    }

    public dInventory(dShop shop) {
        this(shop.getName(), 27, shop);
    }

    /**
     * Opens the inventory for a player
     *
     * @param p The player to open the inventory
     */
    public void open(Player p) {
        p.openInventory(inv);
    }

    /**
     * Returns the title of this gui
     *
     * @return The String representing the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the inventory title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        Inventory temp = Bukkit.createInventory(null, inv.getSize(), title);
        inventoryUtils.translateContents(inv, temp);
        inv = temp;
    }

    /**
     * Returns the inventory
     *
     * @return The inventory this object holds
     */
    public Inventory getInventory() { return inv; }

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

        buttons.removeIf(dItem -> dItem.getSlot() >= aux.getSize());
        IntStream.range(inv.getSize() - 9, inv.getSize()).forEach(openSlots::remove);
        inv = aux;

        return true;
    }

    /**
     * Adds a button to the inventory
     *
     * @param item The item to add
     * @param slot The slot where the item 'll be added
     */
    public void addButton(dItem item, int slot) {
        dItem cloned = item.clone();
        cloned.generateNewBuyPrice();   // Generate new prices
        cloned.generateNewSellPrice();
        cloned.setSlot(slot);

        if (buttons.contains(cloned))
            buttons.removeIf(dItem -> dItem.getSlot() == slot);     // remove to overwrite
        buttons.add(cloned);

        openSlots.remove(slot);
        inv.setItem(slot, cloned.getItem());
    }

    /**
     * Removes a slot from the inventory
     *
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
     *
     * @return An unmodifiable view of the buttons set
     */
    public Set<dItem> getButtons() {
        return Collections.unmodifiableSet(buttons);
    }

    /**
     * Gets the openSlots in this object
     *
     * @return An unmodifiable view of the open Slots
     */
    public Set<Integer> getOpenSlots() {
        return Collections.unmodifiableSet(openSlots);
    }

    /**
     * Renovates daily items on the openSlots
     */
    public void renovate() {

        buttons.stream()            // Clear AIR items
                .filter(dItem::isAIR)
                .forEach(dItem -> inv.clear(dItem.getSlot()));

        WeightedRandom<dItem> RRM = WeightedRandom.fromCollection(      // create weighted random
                shop.getItems().stream().filter(dItem -> dItem.getRarity().getWeight() != 0)
                        .filter(dItem -> !(dItem.getBuyPrice().get().getPrice() <= 0 &&
                                dItem.getSellPrice().get().getPrice() <= 0))
                        .collect(Collectors.toList()),  // remove unAvailable
                dItem::clone,
                value -> DailyShop.getInstance().getConfig().getBoolean("enable-rarity", true) ?
                        value.getRarity().getWeight() : 1     // Get weights depending if rarity enable
        );

        clearDailyItems();

        int addedButtons = 0;
        for (int i : openSlots.stream().sorted().collect(Collectors.toList())) {

            if (i >= inv.getSize()) continue;
            inv.clear(i);

            if (addedButtons >= shop.getItems().size()) break;

            dItem rolled = RRM.roll();
            if (rolled == null) break;

            rolled.generateNewBuyPrice();
            rolled.generateNewSellPrice();
            _renovate(rolled, i);
            RRM.remove(rolled);
            addedButtons++;
        }

        Bukkit.broadcastMessage(plugin.configM.getSettingsYml().PREFIX +       // broadcast msg
                FormatUtils.color(Msg.singletonMsg(plugin.configM.getLangYml().MSG_RESTOCK)
                        .add("\\{shop}", shop.getName()).build()));
    }

    protected void _renovate(dItem newItem, int slot) {
        newItem.setSlot(slot);
        buttons.add(newItem);

        ItemStack itemToAdd = newItem.getItem().clone();
        new shopItemsLore(shop.getType()).setLore(itemToAdd);
        inv.setItem(slot, itemToAdd);
    }

    protected void updateItem(dItem item, updateItemEvent.updatetype type) {

        if (buttons.stream().noneMatch(dItem -> dItem.getUid().equals(item.getUid()))) return;

        buttons.stream().filter(dItem -> dItem.getUid().equals(item.getUid()))
                .findFirst()
                .ifPresent(dItem -> {

                    if (type.equals(updateItemEvent.updatetype.UPDATE_ITEM)) {

                        item.setSlot(dItem.getSlot());
                        buttons.remove(dItem);
                        buttons.add(item);
                        ItemStack itemWithLore = item.getItem().clone();
                        strategy.setLore(itemWithLore);
                        inv.setItem(dItem.getSlot(), itemWithLore);

                    } else if (type.equals(updateItemEvent.updatetype.NEXT_AMOUNT)) {

                        dItem.setStock(dItem.getStock().orElse(0) - 1);

                        if (dItem.getStock().orElse(0) <= 0) {
                            dItem.setStock(-1);
                        }

                        updateItem(dItem, updateItemEvent.updatetype.UPDATE_ITEM);

                    } else if (type.equals(updateItemEvent.updatetype.DELETE_ITEM)) {

                        buttons.stream()
                                .filter(dItem1 -> dItem1.getUid().equals(item.getUid()))
                                .findFirst()
                                .ifPresent(dItem1 -> {
                                    buttons.remove(dItem1);
                                    inv.setItem(dItem1.getSlot(), utils.getRedPane());
                                });
                    }
                });
    }

    /**
     * Clears all the slots corresponding to daily Items
     */

    private void clearDailyItems() {

        for (Iterator<dItem> it = buttons.iterator(); it.hasNext(); ) {
            dItem ditem = it.next();

            if (openSlots.contains(ditem.getSlot())) {
                it.remove();
                inv.clear(ditem.getSlot());
            }

        }
    }


    protected void ready() {

        listeners.add(
                Events.subscribe(InventoryClickEvent.class, EventPriority.HIGHEST)
                        .filter(e -> e.getInventory().equals(inv))
                        .handler(e -> {

                            e.setCancelled(true);

                            if (utils.isEmpty(e.getCurrentItem())) return;

                            if (openSlots.contains(e.getSlot()))
                                buttons.stream()
                                        .filter(ditem -> ditem.getUid().equals(dItem.getUid(e.getCurrentItem())))
                                        .findFirst()
                                        .ifPresent(dItem -> {
                                            if (e.isLeftClick())
                                                transaction.init(
                                                        (Player) e.getWhoClicked(), dItem, shop);
                                            else {
                                                sellTransaction.init(
                                                        (Player) e.getWhoClicked(), dItem, shop);
                                            }
                                        });

                            else {
                                buttons.stream().filter(dItem -> dItem.getSlot() == e.getSlot())
                                        .findFirst().ifPresent(dItem -> dItem.getAction()
                                        .stream((dAction, s) -> dAction.run((Player) e.getWhoClicked(), s)));
                            }

                        })
        );

        listeners.add(
                Events.subscribe(InventoryDragEvent.class)
                        .filter(o -> o.getInventory().equals(inv))
                        .handler(e -> e.setCancelled(true))
        );

        if (!utils.isOperative("PlaceholderAPI")) return;       // If placeholderApi is not available, break

    }

    public void reload() {
        buttons.stream()
                .filter(dItem -> !dItem.isAIR())
                .forEach(dItem -> updateItem(dItem, updateItemEvent.updatetype.UPDATE_ITEM));
    }

    /**
     * Destroys the inventory. In summary, unregisters all the listeners
     */
    public void destroy() {
        listeners.forEach(Subscription::unregister);
        listeners.clear();
    }

    public String toJson() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                // Serialize inventory
                dataOutput.writeObject(inventoryUtils.serialize(inv, title));
                // Serialize openSlots
                dataOutput.writeObject(openSlots);
                // Serialize buttons
                dataOutput.writeObject(buttons);
                return Base64Coder.encodeLines(outputStream.toByteArray());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize inventory.", e);
        }
    }

    private void _deserialize(String base64) {
        try (ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64))) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream)) {

                Pair<String, Inventory> s = inventoryUtils.deserialize((String) dataInput.readObject());
                title = s.get1();
                inv = s.get2();

                openSlots.addAll((Set<Integer>) dataInput.readObject());
                buttons.addAll((Set<dItem>) dataInput.readObject());
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Unable to deserialize gui of shop "
                    + shop.getName() + ", setting it to default");

            buttons.clear();
            openSlots.clear();
            this.title = shop.getName();
            this.inv = Bukkit.createInventory(null, 27, title);
            IntStream.range(0, inv.getSize()).forEach(openSlots::add);


        }
    }

    public static dInventory fromJson(String base64, dShop shop) {
        return new dInventory(base64, shop);
    }

    // Returns a clone of this gui without the daily items
    public dInventory skeleton() {
        dInventory cloned = fromJson(this.toJson(), shop);
        cloned.clearDailyItems();

        cloned.getButtons().stream()   // gets the AIR buttons back
                .filter(dItem::isAIR)
                .forEach(dItem -> cloned.inv
                        .setItem(dItem.getSlot(), dItem.getItem()));

        return cloned;
    }

    public dInventory clone() {
        return fromJson(toJson(), shop);
    }

}
