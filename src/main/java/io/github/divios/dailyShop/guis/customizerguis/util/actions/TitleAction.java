package io.github.divios.dailyShop.guis.customizerguis.util.actions;

import io.github.divios.lib.dLib.shop.view.ShopView;

public class TitleAction implements Action {

    private final String title;

    public TitleAction(String title) {
        this.title = title;
    }

    @Override
    public void execute(ShopView view) {
        view.setTitle(title);
    }

    @Override
    public void undo(ShopView view) {

    }
}
