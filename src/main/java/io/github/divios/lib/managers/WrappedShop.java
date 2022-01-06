package io.github.divios.lib.managers;

import com.google.gson.JsonElement;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.databaseManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class WrappedShop extends dShop {

    private static final databaseManager dManager = DailyShop.get().getDatabaseManager();

    public WrappedShop(String name) {
        super(name);
    }

    public WrappedShop(String name, int timer) {
        super(name, timer);
    }

    public WrappedShop(String name, int timer, Timestamp timestamp) {
        super(name, timer, timestamp);
    }

    public WrappedShop(String name, int timer, Timestamp timestamp, Collection<dItem> items) {
        super(name, timer, timestamp, items);
    }

    public WrappedShop(String name, JsonElement gui, Timestamp timestamp, int timer) {
        super(name, gui, timestamp, timer);
    }

    public WrappedShop(String name, JsonElement gui, Timestamp timestamp, int timer, Set<dItem> items) {
        super(name, gui, timestamp, timer, items);
    }

    @Override
    public void rename(String s) {
        super.rename(s);
        dManager.renameShopAsync(super.getName(), s.toLowerCase());
    }

    @Override
    public void reStock() {
        DebugLog.warn("restock");
        super.reStock();
        //serializerApi.savesuperToFileAsync(this);     // save new timestamp
        dManager.updateTimeStampAsync(super.getName(), super.getTimestamp());
        dManager.updateGuiAsync(super.getName(), super.getGuis());
    }

    @Override
    public void addItem(@NotNull dItem item) {
        super.addItem(item);
        dManager.addItemAsync(super.getName(), item);
    }

    @Override
    public void updateItem(@NotNull dItem newItem) {
        DebugLog.warn("updateItem");
        super.updateItem(newItem);
        dManager.updateItemAsync(super.getName(), newItem);
    }

    @Override
    public boolean removeItem(UUID uid) {
        DebugLog.severe("removeditem");
        if (!super.removeItem(uid)) return false;
        dManager.deleteItemAsync(super.getName(), uid);
        return true;
    }

    @Override
    public void setTimer(int timer) {
        super.setTimer(timer);
        dManager.updateTimerAsync(super.getName(), super.getTimer());
    }

}
