package io.github.divios.lib.managers;

import com.google.gson.JsonElement;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.newDItem;
import io.github.divios.lib.storage.databaseManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class WrappedShop extends dShop {

    private static final databaseManager dManager = DailyShop.get().getDatabaseManager();

    public static dShop wrap(dShop shop) {
        if (shop instanceof WrappedShop) return shop;
        return new WrappedShop(shop);
    }

    protected WrappedShop(String name) {
        super(name);
    }

    protected WrappedShop(String name, JsonElement inv, Timestamp timestamp, int timer) {
        super(name, inv, timestamp, timer);
    }

    protected WrappedShop(String name, JsonElement inv, Timestamp timestamp, int timer, Set<newDItem> items) {
        super(name, inv, timestamp, timer, items);
    }

    protected WrappedShop(dShop fromShop) {
        this(fromShop.getName(), fromShop.getGuis().getDefault().toJson(), fromShop.getTimestamp(), fromShop.getTimer(), new HashSet<>(fromShop.getItems()));
        this.set_announce(fromShop.get_announce());
        this.setDefault(fromShop.isDefault());
    }

    @Override
    public void rename(String s) {
        super.rename(s);
        dManager.renameShopAsync(name, s.toLowerCase());
    }

    @Override
    public synchronized void reStock() {
        super.reStock();
        //serializerApi.saveShopToFileAsync(this);     // save new timestamp
        dManager.updateTimeStampAsync(this.name, this.timestamp);
        dManager.updateGuiAsync(this.name, this.guis);
    }

    @Override
    public synchronized void addItem(@NotNull newDItem item) {
        super.addItem(item);
        dManager.addItemAsync(this.name, item);
    }

    @Override
    public synchronized void updateItem(@NotNull newDItem newItem) {
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
