package io.github.divios.dailyrandomshop.hooks;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.core_lib.hooks.vaultHook;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.utils.utils;
import me.realized.tokenmanager.api.TokenManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.yic.mpoints.MPointsAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;

public class hooksManager {

    private static final DRShop main = DRShop.getInstance();
    private static hooksManager instance = null;

    private hooksManager() {}

    public static hooksManager getInstance() {
        if (instance == null) {
            instance = new hooksManager();
            instance.init();
        }
        return instance;
    }

    private void init() {
        /* Initiate all hooks */
        if (!vaultHook.isEnabled()) {
            main.getLogger().severe(String.format(
                    "[%s] - Disabled due to no Vault dependency found!", main.getDescription().getName()));
            main.getPluginLoader().disablePlugin(main);
        }

        if (utils.isOperative("PlaceholderAPI"))
            placeholderApiHook.getInstance();
        if (utils.isOperative("TokenEnchant")) {
            tokenEnchantsHook.hook();
        }
        if (utils.isOperative("TokenManager")) {
            tokenManagerHook.hook();
        }

        if (utils.isOperative("MPoints")) {
            MPointsHook.hook();
        }

        if (utils.isOperative("PlayerPoints"))
            playerPointsHook.hook();

        if (utils.isOperative("ShopGUIPlus"))
            shopGuiPlusHook.hook();

        gemsEconomyHook.getInstance();
        bstatsHook.init();
    }

    public Economy getVault() {
        return vaultHook.getEcon();
    }

    public GemsEconomyAPI getGemsEcon() { return gemsEconomyHook.getGemsEcon(); }

    public TokenEnchantAPI getTokenEnchantApi() {return tokenEnchantsHook.getEcon(); }

    public TokenManager getTokenManagerApi() { return tokenManagerHook.getApi(); }

    public MPointsAPI getMPointsApi() { return MPointsHook.getApi(); }

    public PlayerPointsAPI getPlayerPointsApi() { return  playerPointsHook.getApi(); }

    public Object getShopGuiPlusApi() { return shopGuiPlusHook.getApi(); }
}
