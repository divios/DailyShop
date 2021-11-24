package io.github.divios.lib.managers;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WrappedShop extends dShop {

    public static dShop wrap(dShop shop) {
        if (shop instanceof WrappedShop) return shop;
        return new WrappedShop(shop);
    }

    protected WrappedShop(String name) {
        super(name);
    }

    protected WrappedShop(String name, dShopT type) {
        super(name, type);
    }

    protected WrappedShop(String name, dShopT type, String base64, Timestamp timestamp, int timer) {
        super(name, type, base64, timestamp, timer);
    }

    protected WrappedShop(String name, dShopT type, String base64, Timestamp timestamp, int timer, Set<dItem> items) {
        super(name, type, base64, timestamp, timer, items);
    }

    protected WrappedShop(dShop fromShop) {
        this(fromShop.getName(), dShopT.buy, fromShop.getGuis().getDefault().toBase64(), fromShop.getTimestamp(), fromShop.getTimer(), new HashSet<>(fromShop.getItems()));
    }

    @Override
    public void rename(String s) {
        dManager.renameShop(name, s.toLowerCase());
        super.rename(s);
    }

    @Override
    protected synchronized void reStock() {
        super.reStock();
        dManager.updateTimeStamp(this.name, this.timestamp);
        dManager.asyncUpdateGui(this.name, this.guis);
    }

    @Override
    public synchronized void addItem(dItem item) {
        super.addItem(item);
        super.dManager.addItem(this.name, item);
    }

    @Override
    public synchronized void updateItem(UUID uid, dItem newItem) {
        super.updateItem(uid, newItem);
        dManager.updateItem(getName(), newItem);
    }

    @Override
    public synchronized boolean removeItem(UUID uid) {
        if (!super.removeItem(uid)) return false;
        dManager.deleteItem(this.name, uid);
        return true;
    }

    /**
     * Sets the items of this shop
     */
    @Override
    public synchronized void setItems(@NotNull Set<dItem> items) {

        Map<UUID, dItem> newItems = new HashMap<>();
        items.forEach(dItem -> newItems.put(dItem.getUid(), dItem));            // Cache values for a O(1) search

        for (Iterator<Map.Entry<UUID, dItem>> it = this.items.entrySet().iterator(); it.hasNext(); ) {          // Remove items that are not on the newItems list
            Map.Entry<UUID, dItem> entry = it.next();
            if (newItems.containsKey(entry.getKey())) continue;

            Events.callEvent(new updateItemEvent(entry.getValue(), updateItemEvent.updatetype.DELETE_ITEM, this));
            dManager.deleteItem(name, entry.getKey());
            it.remove();
        }

        items.forEach(this::addItem);       // Replace the old values for the new ones
    }

    @Override
    public synchronized void setTimer(int timer) {
        super.setTimer(timer);
        dManager.updateTimer(this.name, this.timer);
    }

}
