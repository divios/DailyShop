package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;

public class UltraEconomyHook implements Hook<UltraEconomyAPI> {

    private UltraEconomyAPI api = null;
    private boolean isHooked = false;

    UltraEconomyHook() {
        hook();
    }

    public void hook() {
        if (Utils.isOperative("UltraEconomy")) {
            Log.info("Hooked to UltraEconomy");
            isHooked = true;
            api = UltraEconomy.getAPI();
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    public UltraEconomyAPI getApi() {
        return api;
    }

}
