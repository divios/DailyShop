package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;

public class tokenManagerHook implements Hook<TokenManager> {

    private TokenManager api = null;
    private boolean isHooked = false;

    tokenManagerHook() {
        hook();
    }

    public void hook() {
        if (Utils.isOperative("TokenManager")) {
            Log.info("Hooked to TokenManager");
            isHooked = true;
            api = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");
        }
    }

    @Override
    public boolean isOn() {
        return isHooked;
    }

    public TokenManager getApi() {
        return api;
    }

}
