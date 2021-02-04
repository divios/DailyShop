package io.github.divios.dailyrandomshop.hooks;

import me.xanium.gemseconomy.api.GemsEconomyAPI;

class gemsEconomyHook {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
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
