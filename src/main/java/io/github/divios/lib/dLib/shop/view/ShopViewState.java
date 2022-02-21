package io.github.divios.lib.dLib.shop.view;

import io.github.divios.lib.dLib.dItem;

import java.util.HashMap;
import java.util.Map;

public class ShopViewState {

    private final String title;
    private final int size;
    private final Map<Integer, dItem> buttons;

    public ShopViewState(String title, int size, Map<Integer, dItem> buttons) {
        this.title = title;
        this.size = size;
        this.buttons = new HashMap<>(buttons);
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, dItem> getButtons() {
        return buttons;
    }

}
