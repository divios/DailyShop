package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.DRShop;
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
