package io.github.divios.dailyrandomshop.hooks;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;

public class tokenEnchantsHook {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static TokenEnchantAPI teAPI = null;

    static void hook() {
        main.getLogger().info("Hooked to TokenEnchants");
        teAPI = TokenEnchantAPI.getInstance();
    }

    static TokenEnchantAPI getEcon() {
        return teAPI;
    }

}
