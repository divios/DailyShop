package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;
import me.xanium.gemseconomy.api.GemsEconomyAPI;

class gemsEconomyHook {

    private static final DailyShop main = DailyShop.getInstance();
    private static GemsEconomyAPI gemsEcon = null;

    private gemsEconomyHook() {};

    public static void tryToHook() {
            if(main.getServer().getPluginManager().getPlugin("GemsEconomy") != null) {
                main.getLogger().info("Hooked to GemsEconomy");
                gemsEcon = new GemsEconomyAPI();
            }
    }
    public static GemsEconomyAPI getGemsEcon() { return gemsEcon; }
}
