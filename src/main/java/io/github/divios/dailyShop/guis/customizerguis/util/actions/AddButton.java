package io.github.divios.dailyShop.guis.customizerguis.util.actions;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.view.ShopView;

public class AddButton implements Action {

    private final int slot;
    private final dItem item;

    public AddButton(int slot, dItem item) {
        this.slot = slot;
        this.item = item;
    }

    @Override
    public void execute(ShopView view) {
        view.setPaneItem(slot, item.clone());
    }

    @Override
    public void undo(ShopView view) {

    }
}
