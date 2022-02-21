package io.github.divios.dailyShop.guis.customizerguis.util.actions;

import io.github.divios.lib.dLib.shop.view.ShopView;

public class RemoveAction implements Action {

    private final int slot;

    public RemoveAction(int slot) {
        this.slot = slot;
    }

    @Override
    public void execute(ShopView view) {
        view.clear(slot);
    }

    @Override
    public void undo(ShopView view) {

    }
}
