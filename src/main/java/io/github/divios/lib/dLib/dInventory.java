package io.github.divios.lib.dLib;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.transaction.transaction;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@SuppressWarnings({"ConstantConditions", "deprecation", "unchecked", "unused"})
public class dInventory {

    protected static final DailyShop plugin = DailyShop.getInstance();

    protected String title;       // for some reason is throwing noSuchMethod
    protected Inventory inv;
    protected final dShop shop;

    protected final TreeSet<Integer> dailyItemsSlots = new TreeSet<>();
    protected final ConcurrentHashMap<UUID, dItem> buttons = new ConcurrentHashMap<>();

    private final Set<Subscription> listeners = new HashSet<>();
    protected final loreStrategy strategy;

    public dInventory(String title, int size, dShop shop) {
        this(title, Bukkit.createInventory(null, size, title), shop);
    }

    public dInventory(dShop shop) {
        this(shop.getName(), 27, shop);
    }

    public dInventory(String base64, dShop shop) {
        this.shop = shop;
        this.strategy = new shopItemsLore();
        _deserialize(base64);

        createListeners();
    }

    public dInventory(String title, Inventory inv, dShop shop) {
        this.title = title;
        this.inv = inv;
        this.shop = shop;

        this.strategy = new shopItemsLore();

        IntStream.range(0, inv.getSize()).forEach(dailyItemsSlots::add);
        createListeners();
    }

    public void openInventory(Player p) {
        p.openInventory(inv);
    }

    public String getInventoryTitle() {
        return title;
    }

    public void setInventoryTitle(String title) {
        this.title = title;
        Inventory temp = Bukkit.createInventory(null, inv.getSize(), title);
        inventoryUtils.translateContents(inv, temp);
        inv = temp;
    }

    public Inventory getInventory() {
        return inv;
    }

    public int getInventorySize() {
        return inv.getSize();
    }

    public boolean addInventoryRow() {
        if (inv.getSize() == 54) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() + 9, title);
        inventoryUtils.translateContents(inv, aux);
        IntStream.range(inv.getSize(), inv.getSize() + 9).forEach(dailyItemsSlots::add);
        inv = aux;

        return true;
    }

    public boolean removeInventoryRow() {
        if (inv.getSize() == 9) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() - 9, title);
        inventoryUtils.translateContents(inv, aux);
        buttons.entrySet().removeIf(entry -> entry.getValue().getSlot() >= aux.getSize());
        IntStream.range(inv.getSize() - 9, inv.getSize()).forEach(dailyItemsSlots::remove);
        inv = aux;

        return true;
    }

    public void addButton(dItem item, int slot) {
        dItem cloned = item.clone();
        cloned.generateNewBuyPrice();
        cloned.generateNewSellPrice();
        cloned.setSlot(slot);
        buttons.put(cloned.getUid(), cloned);
        inv.setItem(slot, cloned.getItem());
    }

    public dItem removeButton(dItem item) {
        return removeButton(item.getUid());
    }

    public dItem removeButton(UUID uuid) {
        dItem removedItem = buttons.remove(uuid);
        if (removedItem != null) {
            dailyItemsSlots.add(removedItem.getSlot());
            inv.clear(removedItem.getSlot());
        }
        return removedItem;
    }

    public Map<UUID, dItem> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    public Set<Integer> getDailyItemsSlots() {
        return Collections.unmodifiableSet(dailyItemsSlots);
    }

    public void restock(Player p) {
        restock(p, shop.getItems());
    }

    public void restock(Player p, Set<dItem> itemsToRoll) {
        removeAirItems();     // Just in case
        removeDailyItems();
        Set<dItem> newRolledItems = dRandomItemsSelector.fromItems(itemsToRoll).roll(dailyItemsSlots.size());
        for (dItem item : newRolledItems)
            addButton(item, dailyItemsSlots.pollFirst());
    }

    public void updateItem(Player own, updateItemEvent o) {
        dItem toUpdateItem = buttons.get(o.getItem().getUid());
        if (toUpdateItem == null) return;

        updateItemEvent.updatetype type = o.getType();

        if (type == updateItemEvent.updatetype.UPDATE_ITEM)
            addButton(o.getItem(), toUpdateItem.getSlot());

        else if (type == updateItemEvent.updatetype.NEXT_AMOUNT) {
            dStock stock = toUpdateItem.getStock();
            stock.decrement(o.getPlayer(), o.getAmount());
            if (stock.get(o.getPlayer()) <= 0) stock.set(o.getPlayer(), -1);
            Events.callEvent(new updateItemEvent(toUpdateItem, o.getAmount(), updateItemEvent.updatetype.UPDATE_ITEM, o.getShop()));
        }

        else if (type == updateItemEvent.updatetype.DELETE_ITEM) {
            removeButton(toUpdateItem);
            inv.setItem(toUpdateItem.getSlot(), Utils.getRedPane());
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

                            dItem itemClicked = buttons.get(dItem.getUid(e.getCurrentItem()));
                            if (itemClicked == null) return;

                            if (dailyItemsSlots.contains(itemClicked.getSlot())) {
                                if (e.isLeftClick()) transaction.init((Player) e.getWhoClicked(), buttons.get(itemClicked.getUid()), shop);
                                if (e.isRightClick()) transaction.init((Player) e.getWhoClicked(), buttons.get(itemClicked.getUid()), shop);
                            } else
                                itemClicked.getAction().stream((dAction, s) -> dAction.run((Player) e.getWhoClicked(), s));
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
                inv.clear(entry.getValue().getSlot());
            }
        }
    }

    public void destroy() {
        listeners.forEach(Subscription::unregister);
        listeners.clear();
    }

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

    private void _deserialize(String base64) {
        try (ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64))) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream)) {

                inventoryUtils.deserialize((String) dataInput.readObject()).stream((s1, itemStacks) -> {
                    title = s1;
                    inv = itemStacks;
                });

                dailyItemsSlots.addAll((Set<Integer>) dataInput.readObject());

                Object o = dataInput.readObject();
                if (o instanceof Set) ((Set<dItem>) o).forEach(dItem -> buttons.put(dItem.getUid(), dItem));
                else buttons.putAll((Map<? extends UUID, ? extends dItem>) o);
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Unable to deserialize gui of shop "
                    + shop.getName() + ", setting it to default");

            e.printStackTrace();
            buttons.clear();
            dailyItemsSlots.clear();
            this.title = shop.getName();
            this.inv = Bukkit.createInventory(null, 27, title);
            IntStream.range(0, inv.getSize()).forEach(dailyItemsSlots::add);

        }
    }

    public static dInventory fromBase64(String base64, dShop shop) {
        return new dInventory(base64, shop);
    }

    // Returns a clone of this gui without the daily items
    public dInventory skeleton() {
        dInventory cloned = fromBase64(this.toBase64(), shop);
        cloned.removeDailyItems();
        cloned.destroy();

        cloned.buttons.entrySet().stream()   // gets the AIR buttons back
                .filter(entry -> entry.getValue().isAIR())
                .forEach(entry -> cloned.inv.setItem(entry.getValue().getSlot(), entry.getValue().getItem()));

        return cloned;
    }

    public dInventory copy() {
        dInventory newInv = new dInventory(this.title, this.inv.getSize(), this.shop);
        newInv.inv.setContents(this.inv.getContents());
        newInv.buttons.putAll(this.buttons);
        newInv.dailyItemsSlots.clear();
        newInv.dailyItemsSlots.addAll(dailyItemsSlots);

        return newInv;
    }

}
