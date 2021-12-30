package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import me.yic.mpoints.MPointsAPI;

public class MPointsHook implements Hook<MPointsAPI> {

    private MPointsAPI api = null;
    private boolean isHooked = false;

    MPointsHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (Utils.isOperative("MPoints")) {
            Log.info("Hooked to MPoints");
            isHooked = true;
            api = new MPointsAPI();
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    public MPointsAPI getApi() {
        return api;
    }

}
