package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import net.brcdev.shopgui.ShopGuiPlusApi;

public class shopGuiPlusHook implements Hook<ShopGuiPlusApi> {

    private ShopGuiPlusApi api = null;
    private boolean isHook = false;

    shopGuiPlusHook() {
        tryToHook();
    }

    public void tryToHook() {
        if (Utils.isOperative("ShopGUIPlus")) {
            Log.info("Hooked to ShopGUIPlus");
            isHook = true;
            api = new ShopGuiPlusApi();
        }
    }

    @Override
    public boolean isOn() {
        return isHook;
    }

    public ShopGuiPlusApi getApi() {
        return api;
    }

}
