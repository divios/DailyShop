package io.github.divios.lib.dLib;

import com.google.common.collect.Sets;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.core_lib.misc.WeightedRandom;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.transaction.sellTransaction;
import io.github.divios.dailyShop.transaction.transaction;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"ConstantConditions", "deprecation", "unchecked", "unused"})
public class dInventory {

    protected static final DailyShop plugin = DailyShop.getInstance();

    protected String title;       // for some reason is throwing noSuchMethod
    protected Inventory inv;
    protected final dShop shop;

    protected final Set<Integer> dailyItemsSlots = Sets.newConcurrentHashSet();
    protected final Map<Integer, dItem> buttons = new ConcurrentHashMap<>();

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

        setRangeDailySlots(0, inv.getSize());
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

    protected void createListeners() {
        createClickInventoryListener();
        createDragEventListener();
    }

    private void setRangeDailySlots(int start, int endExclusive) {
        for (int i = start; i < endExclusive; i++) {
            dailyItemsSlots.add(i);
        }
    }

    private void createClickInventoryListener() {
        listeners.add(
                Events.subscribe(InventoryClickEvent.class, EventPriority.HIGHEST)
                        .filter(e -> e.getInventory().equals(inv))
                        .filter(e -> !ItemUtils.isEmpty(e.getCurrentItem()))
                        .handler(e -> {
                            e.setCancelled(true);
                            if (!isUpperInventory(e)) return;
                            if (!buttons.containsKey(e.getSlot())) return;

                            if (isDailySlot(e.getSlot())) {
                                if (e.isLeftClick()) createBuyTransaction(e);
                                if (e.isRightClick()) createSellTransaction(e);
                            } else
                                runItemAction(e);
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

    private boolean isUpperInventory(InventoryClickEvent e) {
        return e.getSlot() == e.getRawSlot();
    }

    private boolean isDailySlot(int slot) {
        return dailyItemsSlots.contains(slot);
    }

    private void createBuyTransaction(InventoryClickEvent e) {
        transaction.init((Player) e.getWhoClicked(), buttons.get(e.getSlot()), shop);
    }

    private void createSellTransaction(InventoryClickEvent e) {
        sellTransaction.create((Player) e.getWhoClicked(), buttons.get(e.getSlot()), shop);
    }

    private void runItemAction(InventoryClickEvent e) {
        dItem item = buttons.get(e.getSlot());
        if (item != null)
            item.getAction().stream((dAction, s) -> dAction.run((Player) e.getWhoClicked(), s));
    }

    public boolean addInventoryRow() {
        if (inv.getSize() == 54) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() + 9, title);
        inventoryUtils.translateContents(inv, aux);
        setRangeDailySlots(inv.getSize(), inv.getSize() + 9);
        inv = aux;
        return true;
    }

    public boolean removeInventoryRow() {
        if (inv.getSize() == 9) return false;

        Inventory aux = Bukkit.createInventory(null, inv.getSize() - 9, title);
        inventoryUtils.translateContents(inv, aux);
        buttons.entrySet().removeIf(entry -> entry.getKey() >= aux.getSize());
        IntStream.range(inv.getSize() - 9, inv.getSize()).forEach(dailyItemsSlots::remove);
        inv = aux;

        return true;
    }

    public void addButton(dItem item, int slot) {
        dItem cloned = item.clone();
        generateItemPrices(cloned);
        cloned.setSlot(slot);
        buttons.put(slot, cloned);
        dailyItemsSlots.remove(slot);
        inv.setItem(slot, cloned.getItem());
    }

    private void generateItemPrices(dItem item) {
        item.generateNewBuyPrice();
        item.generateNewSellPrice();
    }


    public dItem removeButton(int slot) {
        dItem result = buttons.remove(slot);
        if (result != null) {
            dailyItemsSlots.add(slot);
            inv.clear(slot);
        }
        return result;
    }

    public Map<Integer, dItem> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    public Set<Integer> getDailyItemsSlots() {
        return Collections.unmodifiableSet(dailyItemsSlots);
    }

    public void renovate(Player p) {
        RemoveAirItemsOnInventory();     // Just in case
        WeightedRandom<dItem> RRM = createWeightedRandom();
        RemoveAllDailyItems();
        Set<dItem> newRolledItems = rollNewDailyItems(RRM);
        newRolledItems.forEach(dItem -> addDailyItemToInventory(p, dItem));
    }

    private void RemoveAirItemsOnInventory() {
        buttons.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isAIR())
                .forEach(entry -> inv.clear(entry.getKey()));  // Clear AIR items
    }

    @NotNull
    private WeightedRandom<dItem> createWeightedRandom() {
        return WeightedRandom.fromCollection(getValidShopItems(), dItem::clone, this::getItemRarityWeight);
    }

    private void RemoveAllDailyItems() {
        for (Iterator<Map.Entry<Integer, dItem>> it = buttons.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, dItem> entry = it.next();
            if (dailyItemsSlots.contains(entry.getKey())) {
                it.remove();
                inv.clear(entry.getKey());
            }
        }
    }

    private Set<dItem> rollNewDailyItems(WeightedRandom<dItem> RRM) {
        Set<dItem> newItems = new HashSet<>();
        int addedButtons = 0;
        for (int i : getDailySlotsSorted()) {
            if (addedButtons >= shop.getItems().size()) break;
            if (i >= inv.getSize()) continue;

            inv.clear(i);
            dItem rolled = RRM.roll();
            if (rolled == null) break;
            generateItemPrices(rolled);
            rolled.setSlot(i);
            newItems.add(rolled);
            RRM.remove(rolled);
            addedButtons++;
        }
        return newItems;
    }

    protected void addDailyItemToInventory(Player p, dItem newItem) {
        buttons.put(newItem.getSlot(), newItem);
        inv.setItem(newItem.getSlot(), new shopItemsLore().applyLore(newItem.getItem().clone(), p));
    }

    private List<dItem> getValidShopItems() {
        return shop.getItems().stream()
                .filter(this::hasAvailableRarity)
                .filter(this::hasValidPrice)
                .collect(Collectors.toList());
    }

    private int getItemRarityWeight(dItem item) {
        return item.getRarity().getWeight();
    }

    @NotNull
    private List<Integer> getDailySlotsSorted() {
        return dailyItemsSlots.stream().sorted().collect(Collectors.toList());
    }

    private boolean hasAvailableRarity(dItem item) {
        return item.getRarity().getWeight() != 0;
    }

    private boolean hasValidPrice(dItem item) {
        return !(item.getBuyPrice().orElse(null).getPrice() < 0 &&
                item.getSellPrice().orElse(null).getPrice() < 0);
    }

    public void updateItem(Player own, updateItemEvent o) {

        if (buttons.values().stream().noneMatch(dItem -> dItem.getUid().equals(o.getItem().getUid()))) return;

        buttons.values().stream()
                .filter(dItem -> dItem.getUid().equals(o.getItem().getUid()))
                .findFirst()
                .ifPresent(dItem -> {
                    int slot = dItem.getSlot();
                    if (isUpdateItem(o)) {
                        updateItem(own, o, slot);
                    } else if (isNextAmount(o)) {
                        processNextAmount(o, dItem);
                    } else if (isDeleteItem(o)) {
                        deleteItem(slot);
                    }
                });
    }

    private boolean isUpdateItem(updateItemEvent o) {
        return o.getType().equals(updateItemEvent.updatetype.UPDATE_ITEM);
    }

    private boolean isNextAmount(updateItemEvent o) {
        return o.getType().equals(updateItemEvent.updatetype.NEXT_AMOUNT);
    }

    private boolean isDeleteItem(updateItemEvent o) {
        return o.getType().equals(updateItemEvent.updatetype.DELETE_ITEM);
    }

    private void updateItem(Player own, updateItemEvent o, int slot) {
        o.getItem().setSlot(slot);
        buttons.put(slot, o.getItem().clone());
        inv.setItem(slot, strategy.applyLore(o.getItem().getItem().clone(), own));
    }

    private void processNextAmount(updateItemEvent o, dItem dItem) {
        dStock stock = dItem.getStock();
        stock.decrement(o.getPlayer(), o.getAmount());
        if (stock.get(o.getPlayer()) <= 0) {
            stock.set(o.getPlayer(), -1);
        }
        Events.callEvent(new updateItemEvent(dItem, o.getAmount(), updateItemEvent.updatetype.UPDATE_ITEM, o.getShop()));
    }

    private void deleteItem(int slot) {
        dItem removed = removeButton(slot);
        if (removed != null) inv.setItem(slot, utils.getRedPane());
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

                Pair<String, Inventory> s = inventoryUtils.deserialize((String) dataInput.readObject());
                title = s.get1();
                inv = s.get2();

                dailyItemsSlots.addAll((Set<Integer>) dataInput.readObject());

                Object o = dataInput.readObject();
                if (o instanceof Set) ((Set<dItem>) o).forEach(dItem -> buttons.put(dItem.getSlot(), dItem));
                else buttons.putAll((Map<Integer, dItem>) o);
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
        cloned.RemoveAllDailyItems();
        cloned.destroy();

        cloned.getButtons().entrySet().stream()   // gets the AIR buttons back
                .filter(entry -> entry.getValue().isAIR())
                .forEach(entry -> cloned.inv
                        .setItem(entry.getKey(), entry.getValue().getItem()));

        return cloned;
    }

    public dInventory copy() {
        //return fromBase64(toBase64(), shop);
        dInventory newInv = new dInventory(this.title, this.inv.getSize(), this.shop);
        newInv.inv.setContents(this.inv.getContents());
        newInv.buttons.putAll(this.buttons);
        newInv.dailyItemsSlots.clear();
        newInv.dailyItemsSlots.addAll(dailyItemsSlots);

        return newInv;
    }

}
