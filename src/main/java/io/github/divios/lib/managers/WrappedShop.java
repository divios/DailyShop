package io.github.divios.lib.managers;

import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.checkoutEvent;
import io.github.divios.dailyShop.events.reStockShopEvent;
import io.github.divios.dailyShop.events.shopViewUpdateEvent;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.*;
import io.github.divios.lib.dLib.shop.view.ShopView;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.serialize.serializerApi;
import io.github.divios.lib.storage.databaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused"})
public final class WrappedShop implements Listener, dShop {

    private static final databaseManager dManager = DailyShop.get().getDatabaseManager();

    public static dShop wrap(dShop shop) {
        if (shop instanceof WrappedShop) return shop;
        return new WrappedShop(shop);
    }

    private final dShop shop;

    private WrappedShop(dShop shop) {
        this.shop = shop;
        Bukkit.getPluginManager().registerEvents(this, DailyShop.get());
    }

    @EventHandler
    private void restockListener(reStockShopEvent e) {
        if (!Objects.equals(e.getShop(), shop)) return;

        Schedulers.sync().run(() -> {
            dManager.updateGuiAsync(getName(), shop.getView());
            dManager.updateTimeStampAsync(getName(), getTimestamp());
        });
    }

    @EventHandler
    private void shopViewListener(shopViewUpdateEvent e) {
        if (!Objects.equals(getGui(), e.gui)) return;

        serializerApi.saveShopToFileAsync(shop);
        dManager.updateGuiAsync(getName(), getGui());
    }

    @Override
    public void openShop(@NotNull Player p) {
        shop.openShop(p);
    }

    @Override
    public String getName() {
        return shop.getName();
    }

    @Override
    public void rename(String name) {
        serializerApi.deleteShopAsync(getName());
        shop.rename(name);

        dManager.renameShopAsync(getName(), name.toLowerCase());
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public int size() {
        return shop.size();
    }

    @NotNull
    @Override
    public Set<dItem> getItems() {
        return shop.getItems();
    }

    @NotNull
    @Override
    public Map<String, dItem> getMapItems() {
        return shop.getMapItems();
    }

    @NotNull
    @Override
    public Map<String, dItem> getCurrentItems() {
        return shop.getCurrentItems();
    }

    @Nullable
    @Override
    public dItem getItem(@NotNull String id) {
        return shop.getItem(id);
    }

    @Override
    public boolean hasItem(@NotNull String id) {
        return shop.hasItem(id);
    }

    @Override
    public dStock getStockForItem(String id) {
        return shop.getStockForItem(id);
    }

    @Override
    public ShopAccount getAccount() {
        return shop.getAccount();
    }

    @Override
    public LogCache getShopCache() {
        return shop.getShopCache();
    }

    @Override
    public void reStock() {
        DebugLog.warn("restock");
        shop.reStock();
        // The update is done in the listener
    }

    @Override
    public void updateItem(@NotNull dItem newItem) {
        DebugLog.warn("updateItem");
        shop.updateItem(newItem);

        dManager.addItemAsync(getName(), newItem);
        dManager.updateGuiAsync(getName(), shop.getView());
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void setItems(@NotNull Map<String, dItem> items) {
        dShop.super.setItems(items);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void addItem(@NotNull dItem item) {
        shop.addItem(item);
        DebugLog.info("Added item %s", item.getID());

        dManager.addItemAsync(getName(), item);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public boolean removeItem(String id) {
        DebugLog.severe("removeditem");
        if (!shop.removeItem(id)) return false;

        dManager.deleteItemAsync(getName(), id);
        dManager.updateGuiAsync(getName(), shop.getView());
        serializerApi.saveShopToFileAsync(shop);
        return true;
    }

    @Override
    public void computeBill(checkoutEvent e) {
        if (Objects.equals(shop, e.getShop()))
            shop.computeBill(e);
    }

    @Override
    public ShopView getView() {
        return shop.getView();
    }

    @Override
    public void setAccount(ShopAccount account) {
        shop.setAccount(account);

        if (account != null) dManager.updateAccountAsync(getName(), account);
        else dManager.removeAccountAsync(getName());

        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public ShopOptions getOptions() {
        return shop.getOptions();
    }

    @Override
    public ShopView getGui() {
        return shop.getView();
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        shop.setTimestamp(timestamp);
        dManager.updateTimeStampAsync(getName(), getTimestamp());
    }

    @Override
    public LocalDateTime getTimestamp() {
        return shop.getTimestamp();
    }

    @Override
    public int getTimer() {
        return shop.getTimer();
    }

    @Override
    public boolean get_announce() {
        return shop.get_announce();
    }

    @Override
    public void set_announce(boolean announce_restock) {
        shop.set_announce(announce_restock);
    }

    @Override
    public void setDefault(boolean aDefault) {
        shop.setDefault(aDefault);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void setOptions(ShopOptions options) {
        shop.setOptions(options);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public boolean isDefault() {
        return shop.isDefault();
    }

    @Override
    public void setTimer(int timer) {
        shop.setTimer(timer);

        dManager.updateTimerAsync(getName(), getTimer());
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void destroy() {
        shop.destroy();

        reStockShopEvent.getHandlerList().unregister(this);
        shopViewUpdateEvent.getHandlerList().unregister(this);
    }

    @Override
    public void setState(dShopState state) {
        dShop.super.setState(state);

        DebugLog.info("Updated setState");
        dManager.updateGuiAsync(getName(), getView());
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public dShopState toState() {
        return shop.toState();
    }

    @Override
    public String toString() {
        return shop.toString();
    }

}
