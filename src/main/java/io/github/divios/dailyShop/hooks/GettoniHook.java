package io.github.divios.dailyShop.hooks;

import dev.unnm3d.gettoni.Gettoni;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import me.yic.mpoints.MPointsAPI;
import org.jetbrains.annotations.Nullable;

public class GettoniHook implements Hook<Gettoni> {

    private MPointsAPI api = null;
    private boolean isHooked = false;

    GettoniHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (Utils.isOperative("Gettoni")) {
            Log.info("Hooked to Gettoni");
            isHooked = true;
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Nullable
    @Override
    public Gettoni getApi() {
        return null;
    }
}
