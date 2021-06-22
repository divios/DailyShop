package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DRShop;
import me.yic.mpoints.MPointsAPI;

public class MPointsHook {

    private static final DRShop plugin = DRShop.getInstance();
    private static MPointsAPI api = null;

    public static void hook() {
        plugin.getLogger().info("Hooked to MPoints");
        api = new MPointsAPI();
    }

    public static MPointsAPI getApi() { return api; }

}
