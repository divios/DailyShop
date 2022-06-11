package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.utils;
import net.milkbowl.vault.economy.Economy;

public class hooksManager {

    private static final DailyShop main = DailyShop.getInstance();
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

        if (utils.isOperative("ShopGUIPlus"))
            shopGuiPlusHook.hook();

        bstatsHook.init();
    }

    public Economy getVault() {
        return vaultHook.getEcon();
    }

    public Object getShopGuiPlusApi() { return shopGuiPlusHook.getApi(); }

}