package io.github.divios.lib.dLib.shop;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.view.ShopViewState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class dShopState {
    private final String name;
    private final int timer;
    private final boolean announce;
    private final boolean Default;
    private final ShopAccount account;
    private final ShopOptions options;
    private final ShopViewState view;
    private final List<dItem> items;

    public dShopState(String id, int timer, boolean announce, boolean Default, ShopOptions options, ShopAccount account, ShopViewState view, Collection<dItem> items) {
        this.name = id;
        this.timer = timer;
        this.options = options;
        this.announce = announce;
        this.Default = Default;
        this.account = account;
        this.view = view;
        this.items = new ArrayList<>(items);
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

    public List<dItem> getItems() {
        return items;
    }
}