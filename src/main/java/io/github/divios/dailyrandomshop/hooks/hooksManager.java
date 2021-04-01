package io.github.divios.dailyrandomshop.hooks;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.dailyrandomshop.utils.utils;
import me.realized.tokenmanager.api.TokenManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import net.milkbowl.vault.economy.Economy;

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
        if (utils.isOperative("PlaceholderAPI"))
            placeholderApiHook.getInstance();
        if (utils.isOperative("TokenEnchant")) {
            tokenEnchantsHook.hook();
        }
        if (utils.isOperative("TokenManager")) {
            tokenManagerHook.hook();
        }
        gemsEconomyHook.getInstance();
        bstatsHook.init();
    }

    public Economy getVault() {
        return vaultHook.getEcon();
    }

    public GemsEconomyAPI getGemsEcon() { return gemsEconomyHook.getGemsEcon(); }

    public TokenEnchantAPI getTokenEnchantApi() {return tokenEnchantsHook.getEcon(); }

    public TokenManager getTokenManagerApi() { return tokenManagerHook.getApi(); }

}
