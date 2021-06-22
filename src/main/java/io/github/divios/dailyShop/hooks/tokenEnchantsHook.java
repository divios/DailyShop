package io.github.divios.dailyShop.hooks;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.dailyShop.DRShop;

public class tokenEnchantsHook {

    private static final DRShop main = DRShop.getInstance();
    private static TokenEnchantAPI teAPI = null;

    static void hook() {
        main.getLogger().info("Hooked to TokenEnchants");
        teAPI = TokenEnchantAPI.getInstance();
    }

    static TokenEnchantAPI getEcon() {
        return teAPI;
    }

}
