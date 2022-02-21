package io.github.divios.dailyShop.guis.customizerguis.util.actions;

import io.github.divios.lib.dLib.shop.view.ShopView;

public interface Action {

    void execute(ShopView view);

    void undo(ShopView view);

}
