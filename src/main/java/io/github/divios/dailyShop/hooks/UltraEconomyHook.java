package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;

public class UltraEconomyHook {

    private static final DailyShop plugin = DailyShop.get();
    private static UltraEconomyAPI api = null;

    public static void hook() {
        plugin.getLogger().info("Hooked to UltraEconomy");
        api = UltraEconomy.getAPI();
    }

    public static UltraEconomyAPI getApi() {
        return api;
    }

}
