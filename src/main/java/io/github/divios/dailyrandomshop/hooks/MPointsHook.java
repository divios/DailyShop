package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.main;
import me.yic.mpoints.MPointsAPI;

public class MPointsHook {

    private static final main plugin = main.getInstance();
    private static MPointsAPI api = null;

    public static void hook() {
        plugin.getLogger().info("Hooked to MPoints");
        api = new MPointsAPI();
    }

    public static MPointsAPI getApi() { return api; }

}
