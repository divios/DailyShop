package io.github.divios.lib.dLib.synchronizedGui;

import com.google.common.base.Objects;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.guis.customizerguis.customizeGui;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
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
        if (gui != null && gui.getInventory().getInventory().equals(o.getInventory())) {
            invalidate(o.getPlayer().getUniqueId());
        }
    }

    /**
     * Synchronized method to update the shop inventory. It will only update if
     * the inventory passed has any change compared to the actual inventory.
     * If they are not equal, then, if the new inventory has a different size, the hole
     * inventory is updated and new items are generated, if not, only the items that have changed will
     * be updated without restocking the shop.
     */
    public synchronized void updateBase(dInventory inv, boolean silent) {
        //if (inv.equals(base.getBase().skeleton())) return;     // Do not update if the invs are the same

        if (inv.getInventorySize() != base.getInventory().getInventorySize()) {  // If the inv has changed size update all
            base.destroy();
            base = singleGui.fromJson(inv.toBase64(), shop);
            reStock(silent);

        } else {        // If the inv has same size, update only buttons with the above logic

            Map<Integer, dItem> actualContent = new HashMap<>(base.getInventory().getButtonsSlots());
            Map<Integer, dItem> newContent = inv.getButtonsSlots();

            Set<Integer> dailySlots = base.getInventory().getDailyItemsSlots();

            for (int i = 0; i < base.getInventory().getInventorySize(); i++) {
                dItem aux1;
                dItem aux2;

                ItemStack actualItem = (aux1 = actualContent.get(i)) == null ? null : aux1.getItem();
                ItemStack newItem = (aux2 = newContent.get(i)) == null ? null : aux2.getItem();

                if (dailySlots.contains(i)) actualItem = null;      // If is a dailyItem, set as if nothing was there

                if (ItemUtils.isEmpty(actualItem) && ItemUtils.isEmpty(newItem)) continue;

                if (ItemUtils.isEmpty(actualItem) && !ItemUtils.isEmpty(newItem))
                    base.getInventory().addButton(newItem, i);

                else if (!ItemUtils.isEmpty(actualItem) && ItemUtils.isEmpty(newItem))
                    base.getInventory().removeButton(i);

                else if (!actualItem.isSimilar(newItem)) {
                    Log.warn("oke");
                    base.getInventory().addButton(newItem, i);
                }

            }

            Set<UUID> players = new HashSet<>(guis.keySet());       // Re-open to all players to update gui changes
            invalidateAll();
            players.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(this::generate));

        }
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
        base.restock();                                            // Renovates base
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
        return base.getInventory();
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
