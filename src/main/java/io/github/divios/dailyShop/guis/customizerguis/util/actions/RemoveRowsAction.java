package io.github.divios.dailyShop.guis.customizerguis.util.actions;

import io.github.divios.lib.dLib.shop.view.ShopView;

public class RemoveRowsAction implements Action {

    @Override
    public void execute(ShopView view) {
        view.decrementRows(1);
    }

    @Override
    public void undo(ShopView view) {

    }
}
