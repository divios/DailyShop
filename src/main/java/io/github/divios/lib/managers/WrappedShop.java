package io.github.divios.lib.managers;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.ShopAccount;
import io.github.divios.lib.dLib.shop.ShopGui;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.view.ShopView;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.storage.databaseManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class WrappedShop extends dShop {

    private static final databaseManager dManager = DailyShop.get().getDatabaseManager();

    public static dShop wrap(dShop shop) {
        if (shop instanceof WrappedShop) return shop;
        return new WrappedShop(shop);
    }

    private final dShop shop;

    private WrappedShop(dShop shop) {
        super(shop.getName());
        this.shop = shop;

        super.destroy();
    }

    @Override
    public void rename(String s) {
        shop.rename(s);
        dManager.renameShopAsync(shop.getName(), s.toLowerCase());
    }

    @Override
    public void reStock() {
        shop.reStock();
        dManager.updateTimeStampAsync(shop.getName(), shop.getTimestamp());
        dManager.updateGuiAsync(shop.getName(), shop.getGui());
    }

    @Override
    public void addItem(@NotNull dItem item) {
        shop.addItem(item);
        dManager.addItemAsync(shop.getName(), item);
    }

    @Override
    public void updateItem(@NotNull dItem newItem) {
        shop.updateItem(newItem);
        dManager.updateItemAsync(shop.getName(), newItem);
    }

    @Override
    public boolean removeItem(UUID uid) {
        if (!shop.removeItem(uid)) return false;
        dManager.deleteItemAsync(shop.getName(), uid);
        return true;
    }

    @Override
    public void setTimer(int timer) {
        shop.setTimer(timer);
        dManager.updateTimerAsync(shop.getName(), shop.getTimer());
    }

    @Override
    public void openShop(Player p) {
        shop.openShop(p);
    }

    @Override
    public void manageItems(Player p) {
        shop.manageItems(p);
    }

    @Override
    public void openCustomizeGui(Player p) {
        shop.openCustomizeGui(p);
    }

    @Override
    public String getName() {
        return (shop == null)
                ? ""
                : shop.getName();
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
    public Map<UUID, dItem> getMapItems() {
        return shop.getMapItems();
    }

    @NotNull
    @Override
    public Map<String, dItem> getCurrentItems() {
        return shop.getCurrentItems();
    }

    @Nullable
    @Override
    public dItem getItem(@NotNull String ID) {
        return shop.getItem(ID);
    }

    @Nullable
    @Override
    public dItem getItem(@NotNull UUID uid) {
        return shop.getItem(uid);
    }

    @Override
    public boolean hasItem(@NotNull String id) {
        return shop.hasItem(id);
    }

    @Override
    public boolean hasItem(UUID uid) {
        return shop.hasItem(uid);
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
    public void setItems(@NotNull Collection<dItem> items) {
        shop.setItems(items);
    }

    @Override
    public void updateShopGui(ShopGui inv) {
        shop.updateShopGui(inv);
    }

    @Override
    public void updateShopGui(ShopGui newGui, boolean isSilent) {
        shop.updateShopGui(newGui, isSilent);
    }
    
    @Override
    public ShopView getGui() {
        return shop.getGui();
    }

    @Override
    public void setAccount(ShopAccount account) {
        shop.setAccount(account);
    }

    @Override
    public void setTimestamp(Timestamp timestamp) {
        shop.setTimestamp(timestamp);
    }

    @Override
    public Timestamp getTimestamp() {
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
    }

    @Override
    public boolean isDefault() {
        return shop.isDefault();
    }

    @Override
    public void destroy() {
        shop.destroy();
    }

    @Override
    public String toString() {
        return shop.toString();
    }

}
