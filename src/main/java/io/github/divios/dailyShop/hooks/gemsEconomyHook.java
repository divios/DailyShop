package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import me.xanium.gemseconomy.api.GemsEconomyAPI;

public class gemsEconomyHook implements Hook<GemsEconomyAPI> {

    private GemsEconomyAPI gemsEcon = null;
    private boolean isHook = false;

    gemsEconomyHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (DailyShop.get().getServer().getPluginManager().getPlugin("GemsEconomy") != null) {
            Log.info("Hooked to GemsEconomy");
            gemsEcon = new GemsEconomyAPI();
            isHook = true;
        }
    }

    @Override
    public boolean isOn() {
        return isHook;
    }

    @Override
    public GemsEconomyAPI getApi() {
        return gemsEcon;
    }
}
