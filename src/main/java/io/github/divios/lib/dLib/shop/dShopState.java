package io.github.divios.lib.dLib.shop;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.view.ShopViewState;

import java.util.Map;
import java.util.TreeMap;

public class dShopState {
    private final String name;
    private final int timer;
    private final boolean announce;
    private final boolean Default;
    private final ShopAccount account;
    private final ShopOptions options;
    private final ShopViewState view;
    private final Map<String, dItem> items;

    public dShopState(String id,
                      int timer,
                      boolean announce,
                      boolean Default,
                      ShopOptions options,
                      ShopAccount account,
                      ShopViewState view,
                      Map<String, dItem> items) {
        this.name = id;
        this.timer = timer;
        this.options = options;
        this.announce = announce;
        this.Default = Default;
        this.account = account;
        this.view = view;
        this.items = new TreeMap<>(items);
    }

    public String getName() {
        return name;
    }

    public int getTimer() {
        return timer;
    }

    public boolean isAnnounce() {
        return announce;
    }

    public boolean isDefault() {
        return Default;
    }

    public ShopOptions getOptions() {
        return options;
    }

    public ShopAccount getAccount() {
        return account;
    }

    public ShopViewState getView() {
        return view;
    }

    public Map<String, dItem> getItems() {
        return items;
    }
}