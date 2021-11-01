package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.utils;
import me.elementalgaming.ElementalGems.GemAPI;
import me.xanium.gemseconomy.api.GemsEconomyAPI;

public class elementalGemsHook {

    private static final DailyShop main = DailyShop.getInstance();
    private static boolean hooked = false;

    private elementalGemsHook() {}

    public static void tryToHook() {
        if(utils.isOperative("ElementalGems")) {
            main.getLogger().info("Hooked to ElementalGems");
            hooked = true;
        }
    }
    public static boolean isHooked() {
        return hooked;
    }

}
