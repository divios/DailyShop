package io.github.divios.lib.dLib.synchronizedGui;

import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import io.github.divios.core_lib.Events;
import io.github.divios.core_lib.event.SingleSubscription;
import io.github.divios.core_lib.event.Subscription;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.events.reStockShopEvent;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.dailyShop.guis.customizerguis.customizeGui;
import io.github.divios.dailyShop.guis.settings.shopGui;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class abstractSyncMenu implements syncMenu {

    protected final dShop shop;
    protected final BiMap<UUID, singleGui> guis;
    protected singleGui base;
    private boolean isAvailable = true;

    private final Set<SingleSubscription> listeners = new HashSet<>();

    protected abstractSyncMenu(dShop shop) {
        this.shop = shop;
        this.guis = createMap();

        ready();
    }

    /**
     * Inits listeners
     */

    private void ready() {

        listeners.add(
                Events.subscribe(reStockShopEvent.class)
                        .filter(o -> o.getShop().equals(shop))
                        .handler(o -> renovate())
        );

        listeners.add(
                Events.subscribe(updateItemEvent.class)
                        .filter(o -> o.getShop().equals(shop))
                        .handler(o -> this.updateItems(o.getItem(), o.getType()))
        );

        listeners.add(
                Events.subscribe(updateShopEvent.class)
                        .filter(o -> o.getShop().equals(shop))
                        .handler(o -> {
                                    if (o.isResponse()) updateBase(o);
                                    isAvailable = true;
                                }
                        )

        );

        listeners.add(
                Events.subscribe(InventoryCloseEvent.class)
                    .handler(this::checkClosedInv)
        );

    }

    /**
     * Synchronized method to check for a closed inventory
     * @param o The InventoryCloseEvent triggered
     */
    private synchronized void checkClosedInv(InventoryCloseEvent o) {
        singleGui gui = guis.get(o.getPlayer().getUniqueId());

        if (gui != null) invalidate(o.getPlayer().getUniqueId());
    }

    /**
     * Synchronized method to update the base when the listener is trigger
     *
     * @param o
     */
    private synchronized void updateBase(updateShopEvent o) {
        base = singleGui.create(null, o.getInv(), shop);
        renovate();
    }

    protected abstract BiMap<UUID, singleGui> createMap();

    @Override
    public synchronized void generate(Player p) {
        if (!isAvailable) {
            Msg.sendMsg(p, "The shop is currently under maintenance, come again later");
            return;
        }
        guis.put(p.getUniqueId(), singleGui.create(p, base, shop));
    }

    @Override
    public synchronized @Nullable singleGui get(UUID key) {
        return guis.get(key);
    }

    @Override
    public synchronized Collection<singleGui> getMenus() {
        return Collections.unmodifiableCollection(this.guis.values());
    }

    @Override
    public synchronized int size() {
        return guis.size();
    }

    @Override
    public synchronized void invalidate(UUID key) {
        singleGui removed = guis.remove(key);
        if (removed == null) return;
        removed.destroy();
    }

    @Override
    public synchronized void invalidateAll() {
        guis.values().forEach(singleGui::destroy);
        guis.clear();
    }

    @Override
    public synchronized void destroy() {
        invalidateAll();
        listeners.forEach(Subscription::unregister);
        listeners.clear();
        base.destroy();
    }

    @Override
    public synchronized void renovate() {
        Set<UUID> players = guis.keySet();
        invalidateAll();                                            // close all inventories
        base.renovate();                                            // Renovates base
        players.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(this::generate));
        // TODO broadcast msg??
    }

    @Override
    public synchronized void customizeGui(Player p) {
        if (!isAvailable) {
            Msg.sendMsg(p, "Someone is already editing this shop");
            return;
        }
        isAvailable = false;
        invalidateAll();                                            // Close all inventories
        customizeGui.open(p, shop, base.getInventory());
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

    private synchronized void updateItems(dItem item, updateItemEvent.updatetype type) {
        base.updateItem(item, type);
        guis.forEach((uuid, singleGui) -> singleGui.updateItem(item, type));
    }


}
