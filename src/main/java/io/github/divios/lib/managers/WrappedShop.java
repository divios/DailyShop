package io.github.divios.lib.managers;

import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.serialize.serializerApi;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WrappedShop extends dShop {

    public static dShop wrap(dShop shop) {
        if (shop instanceof WrappedShop) return shop;
        return new WrappedShop(shop);
    }

    protected WrappedShop(String name) {
        super(name);
    }

    protected WrappedShop(String name, String base64, Timestamp timestamp, int timer) {
        super(name, base64, timestamp, timer);
    }

    protected WrappedShop(String name, String base64, Timestamp timestamp, int timer, Set<dItem> items) {
        super(name, base64, timestamp, timer, items);
    }

    protected WrappedShop(dShop fromShop) {
        this(fromShop.getName(), fromShop.getGuis().getDefault().toBase64(), fromShop.getTimestamp(), fromShop.getTimer(), new HashSet<>(fromShop.getItems()));
    }

    @Override
    public void rename(String s) {
        dManager.renameShopAsync(name, s.toLowerCase());
        super.rename(s);
    }

    @Override
    public synchronized void reStock() {
        super.reStock();
        serializerApi.saveShopToFileAsync(this);     // save new timestamp
        dManager.updateTimeStampAsync(this.name, this.timestamp);
        dManager.updateGuiAsync(this.name, this.guis);
    }

    @Override
    public synchronized void addItem(dItem item) {
        super.addItem(item);
        super.dManager.addItemAsync(this.name, item);
    }

    @Override
    public synchronized void updateItem(dItem newItem) {
        super.updateItem(newItem);
        dManager.updateItemAsync(getName(), newItem);
    }

    @Override
    public synchronized boolean removeItem(UUID uid) {
        if (!super.removeItem(uid)) return false;
        dManager.deleteItemAsync(this.name, uid);
        return true;
    }

    @Override
    public synchronized void setTimer(int timer) {
        super.setTimer(timer);
        dManager.updateTimerAsync(this.name, this.timer);
    }

}
