package io.github.divios.lib.dLib.synchronizedGui;

import com.google.common.base.Objects;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.dailyShop.guis.customizerguis.customizeGui;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Abstract class that represents the basic operations of a syncMenu.
 * Handles automatically the creation of new guis and making sure it stays on sync
 * with the others.
 * <p>
 * Listens to event for updateItem, reStockShop and updateShop.
 * <p>
 * Children of this class only have to implement {@link #createMap() }
 */

public abstract class abstractSyncMenu implements syncMenu {

    protected final dShop shop;
    protected final Map<UUID, singleGui> guis;
    protected singleGui base;

    private final Set<Subscription> listeners = new HashSet<>();
    private final Map<UUID, Task> delayedGuisPromises = new HashMap<>();

    protected abstractSyncMenu(dShop shop) {
        this(shop, singleGui.create(shop));
    }

    protected abstractSyncMenu(dShop shop, singleGui gui) {
        this.shop = shop;
        this.guis = createMap();
        this.base = gui;

        ready();
    }

    /**
     * Inits listeners
     */

    private void ready() {
        listeners.add(
                Events.subscribe(updateItemEvent.class)
                        .filter(o -> o.getShop().equals(shop))
                        .handler(this::updateItems)
        );
        listeners.add(
                Events.subscribe(updateShopEvent.class)
                        .filter(o -> o.getShop().equals(shop))
                        .handler(this::updateBase)
        );
        listeners.add(
                Events.subscribe(InventoryCloseEvent.class)
                        .handler(this::checkClosedInv)
        );
    }

    /**
     * Synchronized method to check for a closed inventory
     *
     * @param o The InventoryCloseEvent triggered
     */
    private synchronized void checkClosedInv(InventoryCloseEvent o) {
        singleGui gui = guis.get(o.getPlayer().getUniqueId());
        if (gui != null && gui.getInventory().getInventory().equals(o.getInventory()))
            invalidate(o.getPlayer().getUniqueId());
    }

    /**
     * Synchronized method to update the base when the listener is triggered
     *
     * @param o
     */
    private synchronized void updateBase(updateShopEvent o) {
        base.destroy();
        base = singleGui.create(null, o.getNewInv(), shop);
        reStock(o.isSilent());
    }

    private synchronized void updateItems(updateItemEvent o) {
        base.updateItem(o);
        guis.forEach((uuid, singleGui) -> singleGui.updateItem(o));
    }

    protected abstract Map<UUID, singleGui> createMap();

    @Override
    public synchronized void generate(Player p) {
        guis.put(p.getUniqueId(), singleGui.create(p, base, shop));
    }

    @Override
    public synchronized @Nullable
    singleGui getGui(UUID key) {
        return guis.get(key);
    }

    @Override
    public boolean contains(UUID key) {
        return guis.containsKey(key);
    }

    @Override
    public synchronized Collection<singleGui> getMenus() {
        return Collections.unmodifiableCollection(this.guis.values());
    }

    @Override
    public synchronized void invalidate(UUID key) {
        singleGui removed = guis.remove(key);
        if (removed != null) removed.destroy();
        removeAndCancelPromise(key);
    }

    @Override
    public synchronized void invalidateAll() {
        guis.values().forEach(singleGui::destroy);
        guis.clear();
        delayedGuisPromises.values().forEach(Task::stop);
        delayedGuisPromises.clear();
    }

    @Override
    public synchronized void destroy() {
        guis.keySet().forEach(uuid -> Bukkit.getPlayer(uuid).closeInventory());     // Triggers invalidate
        invalidateAll();

        base.destroy();
    }

    @Override
    public synchronized void reStock(boolean silent) {
        Set<UUID> players = new HashSet<>(guis.keySet());
        players.removeAll(delayedGuisPromises.keySet());

        invalidateAll();                                            // close all inventories
        base.renovate();                                            // Renovates base
        players.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(this::generate));
        if (!silent)
            Msg.broadcast(
                    Msg.singletonMsg(DailyShop.getInstance().configM.getLangYml().MSG_RESTOCK)
                            .add("\\{shop}", shop.getName())
                            .build()
            );
    }

    @Override
    public synchronized void customizeGui(Player p) {
        customizeGui.open(p, shop, base.getInventory());
    }

    @Override
    public dInventory getDefault() {
        return base.getBase();
    }

    @Override
    public dShop getShop() {
        return shop;
    }

    @Override
    public String toJson() {
        return base.toJson();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(shop, base);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof syncMenu)) return false;

        syncMenu gui = (syncMenu) o;

        return shop.equals(gui.getShop())
                && guis.hashCode() == gui.getMenus().hashCode();
    }

    private void removeAndCancelPromise(UUID key) {
        Task promise = delayedGuisPromises.remove(key);
        if (promise != null) promise.stop();
    }

}
