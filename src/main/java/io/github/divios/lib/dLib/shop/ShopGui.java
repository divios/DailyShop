package io.github.divios.lib.dLib.shop;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.LimitHelper;
import io.github.divios.dailyShop.utils.NMSUtils.SetSlotPacket;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.util.DailyItemsMap;
import io.github.divios.lib.dLib.shop.util.NMSContainerID;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class ShopGui {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, (JsonSerializer<dItem>) (dItem, type, jsonSerializationContext) -> dItem.toJson())
            .registerTypeAdapter(dItem.class, (JsonDeserializer<dItem>) (jsonElement, type, jsonDeserializationContext) -> dItem.fromJson(jsonElement))
            .create();

    private static final TypeToken<ConcurrentHashMap<UUID, dItem>> buttonsToken = new TypeToken<ConcurrentHashMap<UUID, dItem>>() {
    };
    private static final TypeToken<ConcurrentSkipListSet<Integer>> dailySlotsToken = new TypeToken<ConcurrentSkipListSet<Integer>>() {
    };

    public static ShopGui fromJson(dShop shop, JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(!object.get("title").isJsonNull());
        Preconditions.checkArgument(!object.get("inventory").isJsonNull());

        String title = object.get("title").getAsString();

        Inventory inv = inventoryUtils.fromJson(object.get("inventory"));
        ConcurrentSkipListSet<Integer> dailyItemsSlots = gson.fromJson(object.get("dailySlots"), dailySlotsToken.getType());
        ConcurrentHashMap<UUID, dItem> buttons = gson.fromJson(object.get("buttons"), buttonsToken.getType());

        ShopGui gui = new ShopGui(shop, title, inv);

        buttons.values().forEach(dItem -> {
            if (dailyItemsSlots.contains(dItem.getSlot()))
                gui.dailyItemsMap.put(dItem);
            else
                gui.buttons.put(dItem.getSlot(), dItem);
        });

        return gui;
    }

    dShop shop;

    private final ConcurrentHashMap<UUID, Player> viewers;
    private String title;
    private Inventory inv;

    private final ConcurrentHashMap<Integer, dItem> buttons;
    private final DailyItemsMap dailyItemsMap;

    private UpdateTask updateTask;
    private final Set<Subscription> listeners;

    public ShopGui(dShop shop) {
        this(shop, shop == null ? "" : shop.getName());
    }

    public ShopGui(dShop shop, String title) {
        this(shop, title, Bukkit.createInventory(null, 36, title));
    }

    public ShopGui(dShop shop, String title, Inventory inv) {
        this.shop = shop;
        this.title = title;

        this.viewers = new ConcurrentHashMap<>();
        this.title = (shop == null) ? "" : shop.getName();
        this.inv = inv;

        this.buttons = new ConcurrentHashMap<>();
        this.dailyItemsMap = new DailyItemsMap();

        this.listeners = new HashSet<>();

        createListeners();
    }

    private void createListeners() {
        listeners.add(
                Events.subscribe(InventoryCloseEvent.class)
                        .filter(e -> e.getInventory().equals(inv))
                        .handler(e -> viewers.remove(e.getPlayer().getUniqueId()))
        );

        listeners.add(
                Events.subscribe(InventoryClickEvent.class)
                        .filter(e -> e.getInventory().equals(inv))
                        .handler(this::clickHandler)
        );
    }

    public void open(Player p) {
        if (updateTask == null)
            updateTask = new UpdateTask();

        viewers.put(p.getUniqueId(), p);
        p.openInventory(inv);
    }

    public void close(Player p) {
        Player removed;
        if ((removed = viewers.remove(p.getUniqueId())) == null) return;

        removed.closeInventory();
    }

    public dShop getShop() {
        return shop;
    }

    public Inventory getInv() {
        return inv;
    }

    public int size() {
        return inv.getSize();
    }

    public Set<Integer> getDailySlots() {
        Set<Integer> dailySlots = new HashSet<>();

        IntStream.range(0, inv.getSize())
                .filter(value -> !buttons.containsKey(value))
                .forEach(dailySlots::add);

        return dailySlots;
    }

    public Collection<Player> getViewers() {
        return Collections.unmodifiableCollection(viewers.values());
    }

    public String getTitle() {
        return title;
    }

    public Map<Integer, dItem> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    private Map<String, dItem> values = null;

    public Map<String, dItem> getDailyItems() {
        if (values == null) values = new dailyItems();
        return values;
    }

    public void setTitle(String title) {
        this.title = title;

        Inventory newInv = Bukkit.createInventory(null, inv.getSize(), title);
        newInv.setContents(inv.getContents());
        inv = newInv;

        viewers.values().forEach(player -> player.openInventory(inv));
    }

    public void setSize(int size) {
        int comparator;
        if ((comparator = Integer.compare(size(), size)) != 0)
            if (comparator < 0)
                incrementRows((size - size()) / 9);
            else
                decrementRows((size() - size) / 9);
    }

    public void incrementRows(int rows) {
        Validate.isTrue(rows >= 0, "N times cannot be less than 0. Got " + rows);
        if (inv.getSize() == 54) return;

        int newSize = Math.min(54, inv.getSize() + (rows * 9));

        Inventory newInv = Bukkit.createInventory(null, newSize, title);
        newInv.setContents(inv.getContents());
        inv = newInv;

        viewers.values().forEach(player -> player.openInventory(inv));
    }

    public void decrementRows(int rows) {
        Validate.isTrue(rows >= 0, "N times cannot be less than 0. Got " + rows);
        if (inv.getSize() == 9) return;

        int newSize = Math.max(9, inv.getSize() - (rows * 9));
        Inventory newInv = Bukkit.createInventory(null, newSize, title);
        newInv.setContents(Arrays.copyOfRange(inv.getContents(), 0, newSize));
        inv = newInv;

        // Removed buttons/dailyItems out of newInv bounds
        buttons.entrySet().removeIf(entry -> entry.getKey() >= newSize);
        dailyItemsMap.removeIf(dItem -> dItem.getSlot() >= newSize);

        viewers.values().forEach(player -> player.openInventory(inv));
    }

    public void setButton(int slot, dItem item) {
        Validate.isTrue((slot >= 0) && (slot < inv.getSize()), "Slot out if bounds. Got " + slot + " : " + inv.getSize());

        dailyItemsMap.remove(slot);     // remove dailyItems if any

        dItem clone = item.clone();
        clone.setSlot(slot);

        buttons.put(slot, clone);
        if (!item.isAir()) inv.setItem(slot, item.getItem());
    }

    public void removeButton(int slot) {
        Validate.isTrue((slot >= 0) && (slot < inv.getSize()), "Slot out if bounds. Got " + slot);

        buttons.remove(slot);
        inv.clear(slot);
    }

    public void setButtons(Map<Integer, dItem> buttons) {
        clearButtons();
        buttons.forEach(this::setButton);
    }

    public void updateDailyItem(dItem updatedItem) {
        dItem oldItem;
        if ((oldItem = dailyItemsMap.get(updatedItem.getID())) == null) return;

        int slot = oldItem.getSlot();
        setDailyItem(slot, updatedItem);
    }

    private void setDailyItem(int slot, dItem item) {
        Validate.isTrue(!buttons.containsKey(slot), "Cannot set a dailyItem in a button slot");

        dItem clone = item.clone();
        clone.generateNewBuyPrice();
        clone.generateNewSellPrice();
        clone.setSlot(slot);

        dailyItemsMap.put(clone);
        inv.setItem(slot, shopItemsLore.applyLore(clone, null, shop));
    }

    public void removeDailyItem(String id) {
        dItem toRemove;
        if ((toRemove = dailyItemsMap.remove(id)) == null) return;

        inv.clear(toRemove.getSlot());
    }

    public void setDailyItems(Queue<dItem> dailyItems) {
        clearDailyItems();

        for (int index = 0; index < inv.getSize(); index++) {
            if (buttons.containsKey(index)) continue;

            dItem item;
            if ((item = dailyItems.poll()) == null) break;     // dailyItems is empty

            setDailyItem(index, item);
        }
    }

    public void clearButtons() {
        for (Iterator<Integer> slots = buttons.keySet().iterator(); slots.hasNext(); ) {
            inv.clear(slots.next());
            slots.remove();
        }
    }

    public void clearDailyItems() {
        for (Iterator<dItem> iterator = dailyItemsMap.iterator(); iterator.hasNext(); ) {
            inv.clear(iterator.next().getSlot());
            iterator.remove();
        }
    }

    public void destroy() {
        for (Iterator<Subscription> iterator = listeners.iterator(); iterator.hasNext(); ) {
            iterator.next().unregister();
            iterator.remove();
        }
        for (Iterator<Player> iterator = viewers.values().iterator(); iterator.hasNext(); ) {
            iterator.next().closeInventory();
            iterator.remove();
        }
        if (updateTask != null)
            updateTask.stop();
    }

    private void clickHandler(InventoryClickEvent e) {
        e.setCancelled(true);
        Schedulers.sync().run(this.updateTask::updateTask);

        if (ItemUtils.isEmpty(e.getCurrentItem())) return;
        if (e.getSlot() != e.getRawSlot()) return;  // is not upper inventory

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if (buttons.containsKey(slot)) {
            buttons.get(slot).getAction().execute(p);
        } else if (dailyItemsMap.contains(slot))
            dailyItemAction(p, dailyItemsMap.get(e.getSlot()).clone(), e.getClick());

    }

    private void dailyItemAction(Player p, dItem dailyItem, ClickType clickType) {
        if (clickType.isLeftClick()) {
            if (!buyPreconditions(dailyItem, p)) return;

            double basePrice = dailyItem.getPlayerBuyPrice(p, shop) / dailyItem.getItem().getAmount();
            if (!dailyItem.getEcon().hasMoney(p, basePrice)) {
                Messages.MSG_NOT_MONEY.send(p);
                return;
            }

            int limit;
            if ((limit = LimitHelper.getPlayerLimit(p, shop, dailyItem, Transactions.Type.BUY)) == 0) {
                Messages.MSG_LIMIT.send(p);
                return;
            }
            DebugLog.info("limit: " + limit);

            Transactions.BuyTransaction()
                    .withShop(shop)
                    .withBuyer(p)
                    .withItem(dailyItem)
                    .execute();

        } else if (clickType.isRightClick()) {
            if (!sellPreconditions(dailyItem, p)) return;

            int limit;
            if ((limit = LimitHelper.getPlayerLimit(p, shop, dailyItem, Transactions.Type.SELL)) == 0) {
                Messages.MSG_LIMIT.send(p);
                return;
            }
            DebugLog.info("limit: " + limit);

            Transactions.SellTransaction()
                    .withShop(shop)
                    .withVendor(p)
                    .withItem(dailyItem)
                    .execute();
        }
    }

    private boolean buyPreconditions(dItem itemClicked, Player player) {
        DebugLog.info("ItemClicked: " + itemClicked.toJson());

        if (itemClicked.getBuyPerms() != null
                && !itemClicked.getBuyPerms().stream().allMatch(player::hasPermission)) {
            Messages.MSG_NOT_PERMS.send(player);
            return false;
        }

        if (itemClicked.getDStock() != null && itemClicked.getPlayerStock(player) <= 0) {
            Messages.MSG_NOT_STOCK.send(player);
            return false;
        }

        if (itemClicked.getBuyPrice() < 0) {
            Messages.MSG_INVALID_BUY.send(player);
            return false;
        }

        Inventory cloneInv = Bukkit.createInventory(null, 36);
        cloneInv.setContents(Arrays.copyOf(player.getInventory().getContents(), 36));
        if (!cloneInv.addItem(itemClicked.getItem()).isEmpty()) {
            Messages.MSG_INV_FULL.send(player);
            return false;
        }
        return true;
    }

    private boolean sellPreconditions(dItem itemClicked, Player player) {
        if (itemClicked.getSellPerms() != null
                && !itemClicked.getSellPerms().stream().allMatch(player::hasPermission)) {
            Messages.MSG_NOT_PERMS.send(player);
            return false;
        }

        if (itemClicked.getSellPrice() <= 0) {
            Messages.MSG_INVALID_SELL.send(player);
            return false;
        }

        if (ItemUtils.count(player.getInventory(), itemClicked.getItem()) <= 0) {
            Messages.MSG_NOT_ITEMS.send(player);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ShopGui{" +
                "shop=" + shop +
                ", viewers=" + viewers +
                ", inv=" + inv +
                ", buttons=" + buttons +
                ", dailyItemsMap=" + dailyItemsMap +
                ", listeners=" + listeners +
                '}';
    }

    public JsonElement toJson() {

        HashMap<UUID, dItem> buttons = new HashMap<>();
        Set<Integer> dailyItemsSlots = new HashSet<>();

        this.buttons.values().forEach(dItem -> buttons.put(dItem.getUUID(), dItem));

        this.dailyItemsMap.forEach(dItem -> {
            dailyItemsSlots.add(dItem.getSlot());
            buttons.put(dItem.getUUID(), dItem);
        });

        return JsonBuilder.object()
                .add("title", title)
                .add("inventory", inventoryUtils.toJson(title, inv))
                .add("dailySlots", gson.toJsonTree(dailyItemsSlots, dailySlotsToken.getType()))
                .add("buttons", gson.toJsonTree(buttons, buttonsToken.getType()))
                .build();
    }

    private static final ExecutorService asyncPool = Executors.newCachedThreadPool();

    private class UpdateTask {

        private final Task task;

        public UpdateTask() {
            task = Schedulers.async().runRepeating(
                    this::updateTask,
                    0, TimeUnit.SECONDS,
                    200, TimeUnit.MILLISECONDS
            );
        }

        private void updateTask() {
            if (viewers.isEmpty()) return;
            viewers.forEach((uuid, player) -> asyncPool.execute(() -> sendPackets(player)));
        }

        private void sendPackets(Player player) {
            buttons.forEach((integer, dItem) -> {
                if (dItem.isAir()) return;

                ItemStack aux = dItem.getItem();
                ItemStack toSend = aux.clone();

                String newName = Utils.JTEXT_PARSER.parse(ItemUtils.getName(aux), player);
                List<String> newLore = Utils.JTEXT_PARSER.parse(ItemUtils.getLore(aux), player);

                toSend = ItemUtils.setName(toSend, newName);
                toSend = ItemUtils.setLore(toSend, newLore);

                SetSlotPacket.send(player, toSend, dItem.getSlot(), NMSContainerID.getPlayerInventoryID(player));
            });

            dailyItemsMap.forEach(dItem -> {
                ItemStack toSend = shopItemsLore.applyLore(dItem, player, shop);
                try { SetSlotPacket.send(player, toSend, dItem.getSlot(), NMSContainerID.getPlayerInventoryID(player));}
                catch (Exception ignored) {}        // WindowId can throw null pointer due to no synchronization
            });
        }

        public void stop() {
            task.stop();
        }

    }

    public class dailyItems implements Map<String, dItem> {

        @Override
        public int size() {
            return dailyItemsMap.size();
        }

        @Override
        public boolean isEmpty() {
            return dailyItemsMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return dailyItemsMap.contains((String) key);
        }

        @Override
        public boolean containsValue(Object value) {
            boolean contains = false;
            for (Iterator<dItem> iterator = dailyItemsMap.iterator(); iterator.hasNext(); ) {
                if (iterator.next().equals(value)) {
                    contains = true;
                    break;
                }
            }

            return contains;
        }

        @Override
        public dItem get(Object key) {
            return dailyItemsMap.get((String) key);
        }

        @Nullable
        @Override
        public dItem put(String key, dItem value) {
            updateDailyItem(value.setID(key));
            return null;
        }

        @Override
        public dItem remove(Object key) {
            removeDailyItem((String) key);
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ? extends dItem> m) {
            m.forEach(this::put);
        }

        @Override
        public void clear() {
            clearDailyItems();
        }

        @NotNull
        @Override
        public Set<String> keySet() {
            return null;
        }

        @NotNull
        @Override
        public Collection<dItem> values() {
            return null;
        }

        @NotNull
        @Override
        public Set<Entry<String, dItem>> entrySet() {
            return null;
        }
    }

}
