package io.github.divios.dailyrandomshop.hooks;

import me.xanium.gemseconomy.api.GemsEconomyAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class hooksManager {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static hooksManager instance = null;

    private hooksManager() {};

    public static hooksManager getInstance() {
        if (instance == null) {
            instance = new hooksManager();
            instance.init();
        }
        return instance;
    }

    private void init() {
        /* Initiate all hooks */
        vaultHook.hook();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            placeholderApiHook.getInstance();
        gemsEconomyHook.getInstance();
        bstatsHook.init();
    }

    public Economy getVault() {
        return vaultHook.getEcon();
    }

    public GemsEconomyAPI getGemsEcon() { return gemsEconomyHook.getGemsEcon(); }

}
