package io.github.divios.lib.dLib.shop;

import com.google.gson.JsonElement;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.checkoutEvent;
import io.github.divios.dailyShop.events.reStockShopEvent;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.guis.customizerguis.customizeGui;
import io.github.divios.dailyShop.guis.settings.shopsItemsManagerGui;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.registry.RecordBook;
import io.github.divios.lib.dLib.registry.RecordBookEntry;
import io.github.divios.lib.dLib.shop.cashregister.CashRegister;
import io.github.divios.lib.dLib.shop.util.RandomItemSelector;
import io.github.divios.lib.dLib.shop.view.ShopView;
import io.github.divios.lib.dLib.shop.view.ShopViewFactory;
import io.github.divios.lib.dLib.shop.view.buttons.DailyItemFactory;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class dShopImp implements Listener, dShop {

    protected static final DailyShop plugin = DailyShop.get();

    protected String name;
    protected Map<String, dItem> items = Collections.synchronizedMap(new LinkedHashMap<>());
    protected Map<String, dItem> currentItems;

    protected final LogCache logCache = new LogCache();
    protected final CashRegister cashRegister = new CashRegister(this);

    protected ShopAccount account;
    protected ShopView gui;
    protected ShopOptions options = ShopOptions.DEFAULT;

    protected LocalDateTime timestamp;
    protected int timer;

    protected boolean announce_restock = true;
    protected boolean isDefault = false;

    protected Set<Task> tasks = new HashSet<>();

    protected dShopImp() {
    }

    public dShopImp(String name) {
        this(name, Settings.DEFAULT_TIMER.getValue().getAsInt());
    }

    public dShopImp(String name, int timer) {
        this(name, timer, LocalDateTime.now(), new ArrayList<>());
    }

    public dShopImp(String name, int timer, LocalDateTime timestamp) {
        this(name, timer, timestamp, new ArrayList<>());
    }

    public dShopImp(String name, int timer, LocalDateTime timestamp, Collection<dItem> items) {
        this.name = name.toLowerCase();
        this.timer = timer;
        this.timestamp = timestamp;

        items.forEach(dItem -> this.items.put(dItem.getID(), dItem));

        this.gui = ShopViewFactory.createGui(this);
        this.currentItems = gui.getDailyItems();

        Bukkit.getPluginManager().registerEvents(this, DailyShop.get());
        startTimerTask();
    }

    public dShopImp(String name, JsonElement gui, LocalDateTime timestamp, int timer) {
        this(name, gui, timestamp, timer, new HashSet<>());
    }

    public dShopImp(String name, JsonElement gui, LocalDateTime timestamp, int timer, Set<dItem> items) {
        this.name = name.toLowerCase();
        this.timestamp = timestamp;
        this.timer = timer;
        items.forEach(dItem -> this.items.put(dItem.getID(), dItem));

        this.gui = ShopViewFactory.fromJson(gui, new DailyItemFactory(this));
        this.currentItems = this.gui.getDailyItems();

        Bukkit.getPluginManager().registerEvents(this, DailyShop.get());
        startTimerTask();
    }

    protected void startTimerTask() {
        tasks.add(
                Schedulers.sync().runRepeating(() -> {

                    if (timer == -1) return;
                    if (ChronoUnit.SECONDS.between(timestamp, LocalDateTime.now()) >= (timer - 1)) {
                        DebugLog.info("Restocked by timer");
                        reStock();
                    }

                }, 1, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS)
        );
    }

    /**
     * Opens the actual shop for the player
     */
    public void openShop(@NotNull Player p) {
        gui.open(p);
    }

    /**
     * Opens the gui to manage the items of this shop
     */
    public void manageItems(Player p) {
        shopsItemsManagerGui.open(p, this);
    }

    /**
     * Opens the gui to customize the display of this shop
     */
    public void openCustomizeGui(Player p) {
        customizeGui.open(p, this);
    }

    /**
     * Gets the name of the shop
     */
    public String getName() {
        return name.toLowerCase();
    }

    /**
     * Sets the name of the shop
     */
    public void rename(String name) {
        this.name = name;
    }

    /**
     * Returns the amount of items in this shop
     */

    public int size() {
        return items.size();
    }

    /**
     * Gets a copy the items in the shop
     *
     * @return returns a List of dItems. Note that this list is a copy of the original,
     * any change made to it won't affect the original one
     */
    public @NotNull
    Set<dItem> getItems() {
        return items.values().stream()
                .filter(Objects::nonNull)
                .map(dItem::clone)
                .collect(Collectors.toSet());
    }

    /**
     * Returns an unmodifiable view of the items map
     */
    public @NotNull
    Map<String, dItem> getMapItems() {
        return Collections.unmodifiableMap(items);
    }

    public @NotNull
    Map<String, dItem> getCurrentItems() {
        return Collections.unmodifiableMap(currentItems);
    }

    /**
     * Gets the item by ID
     *
     * @param ID the ID to search
     * @return null if it does not exist
     */
    public @Nullable
    dItem getItem(@NotNull String ID) {
        dItem item;
        return ((item = items.get(ID)) == null) ? null : item.clone();
    }

    public boolean hasItem(@NotNull String id) {
        return items.containsKey(id);
    }

    /**
     * Gets the dStock for a current daily item. Returns null if the shop does not
     * have that item on sale or the item has no stock defined.
     */
    public dStock getStockForItem(String id) {
        dItem itemToSearch = currentItems.get(id);
        if (itemToSearch == null) return null;

        return itemToSearch.getDStock();
    }

    public ShopAccount getAccount() {
        return account;
    }

    public LogCache getShopCache() {
        return logCache;
    }

    /**
     * Restocks the items of this shop.
     */
    public void reStock() {
        timestamp = LocalDateTime.now();

        logCache.clear();
        if (account != null)
            account.generateNewBalance();

        long start = System.currentTimeMillis();

        Queue<dItem> rolledItems = RandomItemSelector.roll(getItems(),
                (gui.getSize()) - gui.getButtons().size());
        gui.setDailyItems(rolledItems);
        currentItems = gui.getDailyItems();

        Events.callEvent(new reStockShopEvent(this));
        DebugLog.info("Time elapsed to restock shop %s : %d", name, System.currentTimeMillis() - start);

        if (announce_restock)
            Messages.MSG_RESTOCK.broadcast(Template.of("shop", name));
    }

    /**
     * Updates the item of the shop
     */
    public void updateItem(@NotNull dItem newItem) {
        if (!items.containsKey(newItem.getID())) {
            addItem(newItem);
        } else {
            items.put(newItem.getID(), newItem);
            if (currentItems.containsKey(newItem.getID()))
                currentItems.put(newItem.getID(), newItem);
        }
    }

    /**
     * Adds an item to this shop
     *
     * @param item item to be added
     */
    public void addItem(@NotNull dItem item) {
        if (items.containsKey(item.getID()))
            updateItem(item);
        else
            items.put(item.getID(), item);
    }

    /**
     * Removes an item from the shop
     *
     * @param id of the item to be removed
     * @return true if the item was removed. False if not
     */
    public boolean removeItem(String id) {
        dItem removed = items.remove(id);
        if (removed == null) return false;

        currentItems.remove(removed.getID());
        return true;
    }

    @EventHandler
    public void computeBill(checkoutEvent e) {
        if (!Objects.equals(e.getShop(), this)) return;

        DebugLog.info("Received bill on shop " + name);
        logCache.register(e.getPlayer().getUniqueId(), e.getItem().getID(), e.getAmount(), e.getType());

        if (account != null) {
            if (e.getType() == Transactions.Type.BUY)
                account.deposit(e.getPrice());
            else
                account.withdraw(e.getPrice());
        }

        dItem shopItem = currentItems.get(e.getItem().getID());

        currentItems.computeIfPresent(shopItem.getID(), (s1, dItem) -> {        // compute stock
            if (e.getType() == Transactions.Type.BUY)
                dItem.decrementStock(e.getPlayer(), e.getAmount());

            else if (e.getType() == Transactions.Type.SELL && dItem.getDStock().incrementsOnSell())
                dItem.incrementStock(e.getPlayer(), e.getAmount());

            return dItem;
        });

        RecordBook.registerEntry(                       // Log bill on database
                RecordBookEntry.createEntry()
                        .withPlayer(e.getPlayer())
                        .withShopID(name)
                        .withItemID(e.getItem().getID())
                        .withRawItem(e.getItem().getItem())
                        .withQuantity(e.getAmount())
                        .withType(e.getType())
                        .withPrice(e.getPrice())
                        .create());
    }

    /**
     * Return the dGui of this shop
     */
    public ShopView getView() {
        return gui;
    }

    public void setAccount(ShopAccount account) {
        if (this.account == null || !this.account.isSimilar(account))
            this.account = account;
    }

    public ShopOptions getOptions() {
        return options;
    }

    @Override
    public ShopView getGui() {
        return gui;
    }

    public void setOptions(ShopOptions options) {
        this.options = options;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
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

    private void destroyTask() {
        for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext(); ) {
            iterator.next().stop();
            iterator.remove();
        }
    }

    public void destroy() {
        gui.destroy();
        cashRegister.destroy();
        checkoutEvent.getHandlerList().unregister(this);
        destroyTask();
    }

    public dShopState toState() {
        return new dShopState(name, timer, announce_restock, isDefault, options, account, gui.toState(), items.values());
    }

    @Override
    public String toString() {
        return "dShop{" +
                "name='" + name + '\'' +
                ", items=" + items +
                ", gui=" + gui +
                ", timestamp=" + timestamp +
                ", timer=" + timer +
                ", tasks=" + tasks +
                '}';
    }

}
