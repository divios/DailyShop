package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DRShop;

public class shopGuiPlusHook {

    public static Object api = null;

    public static void hook() {
        DRShop.getInstance().getLogger().info("Hooked to ShopGUIPlus");
        api = new Object();
    }

    public static Object getApi() { return api; }

}
