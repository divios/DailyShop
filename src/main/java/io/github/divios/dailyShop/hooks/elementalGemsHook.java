package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;

public class elementalGemsHook {

    private static final DailyShop main = DailyShop.get();
    private static boolean hooked = false;

    private elementalGemsHook() {
    }

    public static void tryToHook() {
        if (Utils.isOperative("ElementalGems")) {
            main.getLogger().info("Hooked to ElementalGems");
            hooked = true;
        }
    }

    public static boolean isHooked() {
        return hooked;
    }

}
