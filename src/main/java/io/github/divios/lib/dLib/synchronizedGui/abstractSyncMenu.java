package io.github.divios.lib.dLib.synchronizedGui;

import com.google.common.base.Objects;
import com.google.gson.JsonElement;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.guis.customizerguis.customizeGui;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.newDItem;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
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

public abstract class abstractSyncMenu implements syncMenu, Cloneable {

    protected dShop shop;
    protected Map<UUID, singleGui> guis;
    protected singleGui base;

    private Set<Subscription> listeners = new HashSet<>();

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

        /*int comparator;
        if ((comparator = Integer.compare(base.getInventory().getInventorySize(), inv.getInventorySize())) != 0) {
            if (comparator < 0)
                IntStream.range(0, (inv.getInventorySize() - base.getInventory().getInventorySize()) / 9)
                        .forEach(value -> base.getInventory().removeInventoryRow());
            else
                IntStream.range(0, (base.getInventory().getInventorySize() - inv.getInventorySize()) / 9)
                        .forEach(value -> base.getInventory().addInventoryRow());
        } */

        DebugLog.info("Updating base of shop " + shop.getName());
        if (inv.getInventorySize() != base.getInventory().getInventorySize()) {  // If the inv has changed size update all
            DebugLog.info("Different sizes, force update");
            base.destroy();
            base = singleGui.fromJson(inv.toJson(), shop);
            reStock(silent);

        } else {        // If the inv has same size, update only buttons with the above logic
            DebugLog.info("Same size, updating items only");
            if (!inv.getInventoryTitle().equals(base.getInventory().getInventoryTitle())) {
                DebugLog.info("Updated title");
                base.getInventory().setInventoryTitle(inv.getInventoryTitle());
            }

            Map<Integer, newDItem> actualContent = new HashMap<>(base.getInventory().getButtonsSlots());
            Map<Integer, newDItem> newContent = inv.getButtonsSlots();

            Set<Integer> dailySlots = base.getInventory().getDailyItemsSlots();

            for (int i = 0; i < base.getInventory().getInventorySize(); i++) {
                newDItem aux1;
                newDItem aux2;

                ItemStack actualItem = (aux1 = actualContent.get(i)) == null ? null : aux1.getItem();
                ItemStack newItem = (aux2 = newContent.get(i)) == null ? null : aux2.getItem();

                if (dailySlots.contains(i)) actualItem = null;      // If is a dailyItem, set as if nothing was there

                if (actualItem == null && newItem == null) continue;

                if (newItem == null)
                    base.getInventory().removeButton(i);
                else if (actualItem == null || !aux1.isSimilar(aux2))
                    base.getInventory().addButton(aux2, i);

            }

            Set<UUID> players = new HashSet<>(guis.keySet());       // Re-open to all players to update gui changes
            invalidateAll();
            players.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(this::generate));

        }

    }

    public synchronized void updateItem(updateItemEvent o) {
        base.updateItem(o);
        guis.forEach((uuid, singleGui) -> singleGui.updateItem(o));
    }

    protected abstract Map<UUID, singleGui> createMap();

    @Override
    public synchronized void generate(Player p) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        guis.put(p.getUniqueId(), base.deepCopy(p));
        DebugLog.info("Time elapsed to generate singleGui: " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + " ns");
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
    }

    @Override
    public synchronized void invalidateAll() {
        guis.values().forEach(singleGui::destroy);
        guis.clear();
    }

    @Override
    public synchronized void destroy() {
        guis.keySet().forEach(uuid -> {
            Player p;
            if ((p = Bukkit.getPlayer(uuid)) != null)
                p.closeInventory();
        });
        invalidateAll();

        listeners.forEach(Subscription::unregister);
        listeners.clear();

        base.destroy();
    }

    @Override
    public synchronized void reStock(boolean silent) {
        Set<UUID> players = new HashSet<>(guis.keySet());

        invalidateAll();                                            // close all inventories
        base.restock();                                             // Renovates base

        players.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(this::generate));
        if (!silent)
            Messages.MSG_RESTOCK.broadcast(
                    Template.of("shop", shop.getName())
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
    public JsonElement toJson() {
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

    @Override
    public Object copy(dShop shop) {
        abstractSyncMenu clone = (abstractSyncMenu) clone();
        clone.shop = shop;

        return clone;
    }

    @Override
    public Object clone() {
        try {
            abstractSyncMenu clone = (abstractSyncMenu) super.clone();

            clone.base = base.deepCopy(null);
            clone.guis = clone.createMap();
            clone.listeners = new HashSet<>();
            clone.ready();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
