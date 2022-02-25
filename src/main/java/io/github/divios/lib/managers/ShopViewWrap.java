package io.github.divios.lib.managers;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.view.ShopView;
import io.github.divios.lib.dLib.shop.view.ShopViewState;
import io.github.divios.lib.dLib.shop.view.buttons.DailyItemFactory;
import io.github.divios.lib.serialize.serializerApi;
import io.github.divios.lib.storage.databaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ShopViewWrap extends ShopView {

    private static final databaseManager dManager = DailyShop.get().getDatabaseManager();

    public static ShopView wrap(dShop shop, ShopView view) {
        if (view instanceof ShopViewWrap) return view;
        return new ShopViewWrap(shop, view);
    }

    private final dShop shop;
    private final ShopView wrap;

    public ShopViewWrap(dShop shop, ShopView view) {
        super("", Bukkit.createInventory(null, 27), null);
        super.destroy();

        this.shop = shop;
        this.wrap = view;
    }

    @Override
    public void open(Player p) {
        wrap.open(p);
    }

    @Override
    public void setTitle(String title) {
        wrap.setTitle(title);

        dManager.updateGuiAsync(shop.getName(), wrap);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void setSize(int size) {
        wrap.setSize(size);

        dManager.updateGuiAsync(shop.getName(), wrap);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void setItemFactory(DailyItemFactory itemFactory) {
        wrap.setItemFactory(itemFactory);
    }

    @Override
    public void setPaneItem(int slot, dItem item) {
        wrap.setPaneItem(slot, item);

        dManager.updateGuiAsync(shop.getName(), wrap);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void clear(int slot) {
        wrap.clear(slot);

        dManager.updateGuiAsync(shop.getName(), wrap);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void setDailyItems(Queue<dItem> dailyItems) {
        wrap.setDailyItems(dailyItems);

        dManager.updateGuiAsync(shop.getName(), wrap);
    }

    @Override
    public void incrementRows(int rows) {
        wrap.incrementRows(rows);

        dManager.updateGuiAsync(shop.getName(), wrap);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void decrementRows(int rows) {
        wrap.decrementRows(rows);

        dManager.updateGuiAsync(shop.getName(), wrap);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void setState(ShopViewState state) {
        wrap.setState(state);

        dManager.updateGuiAsync(shop.getName(), wrap);
        serializerApi.saveShopToFileAsync(shop);
    }

    @Override
    public void destroy() {
        wrap.destroy();
    }

    @Override
    public dailyItems getDailyItems() {
        return wrap.getDailyItems();
    }

    @Override
    public String getTitle() {
        return wrap.getTitle();
    }

    @Override
    public int getSize() {
        return wrap.getSize();
    }

    @Override
    public Map<Integer, dItem> getButtons() {
        return wrap.getButtons();
    }

    @Override
    public List<HumanEntity> getViewers() {
        return wrap.getViewers();
    }

    @Override
    public ShopViewState toState() {
        return wrap.toState();
    }

    @Override
    public String toString() {
        return wrap.toString();
    }
}
