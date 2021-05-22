package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.DRShop;
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
