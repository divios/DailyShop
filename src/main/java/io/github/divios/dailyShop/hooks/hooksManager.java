package io.github.divios.dailyShop.hooks;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.core_lib.hooks.vaultHook;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;
import me.realized.tokenmanager.api.TokenManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.yic.mpoints.MPointsAPI;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;

public class hooksManager {

    private static final DailyShop main = DailyShop.get();
    private static hooksManager instance = null;

    private hooksManager() {
    }

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

        if (Utils.isOperative("PlaceholderAPI"))
            placeholderApiHook.getInstance();
        if (Utils.isOperative("TokenEnchant")) {
            tokenEnchantsHook.hook();
        }
        if (Utils.isOperative("TokenManager")) {
            tokenManagerHook.hook();
        }

        if (Utils.isOperative("MPoints")) {
            MPointsHook.hook();
        }

        if (Utils.isOperative("PlayerPoints"))
            playerPointsHook.hook();

        if (Utils.isOperative("ShopGUIPlus"))
            shopGuiPlusHook.hook();

        if (Utils.isOperative("UltraEconomy"))
            UltraEconomyHook.hook();

        elementalGemsHook.tryToHook();
        gemsEconomyHook.tryToHook();
        bstatsHook.init();
    }

    public Economy getVault() {
        return vaultHook.getEcon();
    }

    public GemsEconomyAPI getGemsEcon() {
        return gemsEconomyHook.getGemsEcon();
    }

    public TokenEnchantAPI getTokenEnchantApi() {
        return tokenEnchantsHook.getEcon();
    }

    public TokenManager getTokenManagerApi() {
        return tokenManagerHook.getApi();
    }

    public MPointsAPI getMPointsApi() {
        return MPointsHook.getApi();
    }

    public PlayerPointsAPI getPlayerPointsApi() {
        return playerPointsHook.getApi();
    }

    public UltraEconomyAPI getUltraEconomyApi() {
        return UltraEconomyHook.getApi();
    }

    public Object getShopGuiPlusApi() {
        return shopGuiPlusHook.getApi();
    }

}
