package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import me.elementalgaming.ElementalGems.ElementalGems;
import me.elementalgaming.ElementalGems.GemAPI;
import org.bukkit.Bukkit;

public class elementalGemsHook implements Hook<GemAPI> {

    private static boolean hooked = false;

    elementalGemsHook() {
        tryToHook();
    }

    private void tryToHook() {
        if (Utils.isOperative("ElementalGems")) {
            Log.info("Hooked to ElementalGems");
            hooked = true;
        }
    }

    @Override
    public boolean isOn() {
        return hooked;
    }

    @Override
    public GemAPI getApi() {
        return isOn() ?((ElementalGems) Bukkit.getPluginManager().getPlugin("ElementalGems")).getGemAPI() : null;
    }
}
