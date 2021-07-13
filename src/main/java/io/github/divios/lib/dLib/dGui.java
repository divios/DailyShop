package io.github.divios.lib.dLib;

import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.core_lib.misc.WeightedRandom;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.lib.dLib.guis.dBuy;
import io.github.divios.lib.dLib.guis.dSell;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntSets;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
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

public abstract class dGui {

    protected static final DRShop plugin = DRShop.getInstance();

    protected String title;       // for some reason is throwing noSuchMethod
    protected Inventory inv;
    protected final dShop shop;

    protected boolean available = true;
    protected final Set<Integer> openSlots = Collections.synchronizedSet(new HashSet<>());
    protected final Set<dItem> buttons = Collections.synchronizedSet(new HashSet<>());

    protected transient EventListener<InventoryClickEvent> clickEvent;
    protected transient EventListener<InventoryDragEvent> dragEvent;
    protected transient EventListener<InventoryOpenEvent> openEvent;

    protected final loreStrategy strategy;

    protected dGui(String title, Inventory inv, dShop shop) {
        this.title = title;
        this.inv = inv;
        this.shop = shop;

        this.strategy = new shopItemsLore(shop.getType());

        IntStream.range(0, inv.getSize()).forEach(openSlots::add);  //initializes slots
        renovate();   // not really necessary but why not

        initListeners();
    }

    protected dGui(String title, int size, dShop shop) {
        this(title,
                Bukkit.createInventory(null, size, title), shop);
    }

    protected dGui(String base64, dShop shop) {
        this.shop = shop;
        this.strategy = new shopItemsLore(shop.getType());
        _deserialize(base64);

        initListeners();
    }

    protected dGui(dShop shop) {
        this(shop.getName(), 27, shop);
    }

    /**
     * Opens the inventory for a player
     * @param p The player to open the inventory
     */
    public void open(Player p) {
        p.openInventory(inv);
    }

    /**
     * Closes the inventory for all the current viewers
     */
    public abstract void closeAll();

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

        buttons.stream()            // Clear AIR items
                .filter(dItem::isAIR)
                .forEach(dItem -> inv.clear(dItem.getSlot()));

        WeightedRandom<dItem> RRM = WeightedRandom.fromCollection(      // create weighted random
                shop.getItems().stream().filter(dItem -> dItem.getRarity().getWeight() != 0)
                        .filter( dItem -> !(dItem.getBuyPrice().get().getPrice() <= 0 &&
                                dItem.getSellPrice().get().getPrice() <= 0))
                        .collect(Collectors.toList()),  // remove unAvailable
                dItem::clone,
                value -> DRShop.getInstance().getConfig().getBoolean("enable-rarity", true) ?
                value.getRarity().getWeight():1     // Get weights depending if rarity enable
        );

        clearDailyItems();

        int addedButtons = 0;
        for (int i : openSlots.stream().sorted().collect(Collectors.toList())) {
            if (i >= inv.getSize()) break;
            inv.clear(i);

            if (addedButtons >= shop.getItems().size()) break;

            dItem rolled = RRM.roll();
            _renovate(rolled, i);
            RRM.remove(rolled);
            addedButtons++;
        }

        Bukkit.broadcastMessage(conf_msg.PREFIX +       // broadcast msg
                Msg.singletonMsg(conf_msg.MSG_NEW_DAILY_ITEMS)
                .add("\\{shop}", shop.getName()).build());
    }

    protected abstract void _renovate(dItem newItem, int slot);

    protected abstract void updateItem(dItem item, updateItemEvent.updatetype type);

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


    protected abstract void initListeners();

    public void reload() {
        buttons.stream()
                .filter(dItem -> !dItem.isAIR())
                .forEach(dItem -> updateItem(dItem, updateItemEvent.updatetype.UPDATE_ITEM));
    }

    /**
     * Destroys the inventory. In summary, unregisters all the listeners
     */
    protected abstract void destroy();

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
            plugin.getLogger().severe("Unable to deserialize gui of shop "
                    + shop.getName() + ", setting it to default");

            buttons.clear();
            openSlots.clear();
            IntStream.range(0, inv.getSize()).forEach(openSlots::add);
            this.title = shop.getName();
            this.inv = Bukkit.createInventory(null, 27, title);
        }
    }

    public static dGui deserialize(String base64, dShop shop) {
        if (shop.getType().equals(dShop.dShopT.buy))
            return new dBuy(base64, shop);
        else
            return new dSell(base64, shop);
    }

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
