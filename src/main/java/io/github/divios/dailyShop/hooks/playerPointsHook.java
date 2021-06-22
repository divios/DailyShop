package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DRShop;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

public class playerPointsHook {

    private static final DRShop plugin = DRShop.getInstance();
    private static PlayerPointsAPI api = null;

    public static void hook() {
        api = PlayerPoints.getInstance().getAPI();
        plugin.getLogger().info("Hooked to PlayerPoints");
    }

    public static PlayerPointsAPI getApi() {
        return api;
    }

}
