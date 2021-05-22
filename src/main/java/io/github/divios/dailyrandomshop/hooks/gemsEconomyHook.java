package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.DRShop;
import me.xanium.gemseconomy.api.GemsEconomyAPI;

class gemsEconomyHook {

    private static final DRShop main = DRShop.getInstance();
    private static GemsEconomyAPI gemsEcon = null;

    private gemsEconomyHook() {};

    public static void getInstance() {
            if(main.getServer().getPluginManager().getPlugin("GemsEconomy") != null) {
                main.getLogger().info("Hooked to GemsEconomy");
                gemsEcon = new GemsEconomyAPI();
            }
    }
    public static GemsEconomyAPI getGemsEcon() { return gemsEcon; }
}
