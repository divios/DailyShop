package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;

public class shopGuiPlusHook {

    public static Object api = null;

    public static void hook() {
        DailyShop.getInstance().getLogger().info("Hooked to ShopGUIPlus");
        api = new Object();
    }

    public static Object getApi() { return api; }

}
