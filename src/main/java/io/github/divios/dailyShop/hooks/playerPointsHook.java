package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

public class playerPointsHook implements Hook<PlayerPointsAPI> {

    private PlayerPointsAPI api = null;
    private boolean isHook = false;

    playerPointsHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (Utils.isOperative("PlayerPoints")) {
            api = PlayerPoints.getInstance().getAPI();
            isHook = true;
            Log.info("Hooked to PlayerPoints");
        }
    }

    @Override
    public boolean isOn() {
        return isHook;
    }

    public PlayerPointsAPI getApi() {
        return api;
    }

}
