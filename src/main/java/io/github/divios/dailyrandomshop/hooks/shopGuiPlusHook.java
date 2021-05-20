package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.main;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.ShopGuiPlusApi;

public class shopGuiPlusHook {

    public static Object api = null;

    public static void hook() {
        main.getInstance().getLogger().info("Hooked to ShopGUIPlus");
        api = new Object();
    }

    public static Object getApi() { return api; }

}
