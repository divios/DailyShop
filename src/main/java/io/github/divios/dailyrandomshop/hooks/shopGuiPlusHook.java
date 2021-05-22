package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.DRShop;

public class shopGuiPlusHook {

    public static Object api = null;

    public static void hook() {
        DRShop.getInstance().getLogger().info("Hooked to ShopGUIPlus");
        api = new Object();
    }

    public static Object getApi() { return api; }

}
