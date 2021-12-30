package io.github.divios.dailyShop.hooks;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;

public class tokenEnchantsHook implements Hook<TokenEnchantAPI> {

    private TokenEnchantAPI api = null;
    private boolean isHooked = false;

    tokenEnchantsHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (Utils.isOperative("TokenEnchant")) {
            Log.info("Hooked to TokenEnchants");
            isHooked = true;
            api = TokenEnchantAPI.getInstance();
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    @Override
    public TokenEnchantAPI getApi() {
        return api;
    }
}
