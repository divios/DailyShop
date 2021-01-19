package io.github.divios.dailyrandomshop.tasks;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.utils.ConfigUtils;
import org.bukkit.Bukkit;

public class updateTimer {

    public static void initTimer(DailyRandomShop main, boolean reload) {

        if (reload) return;

        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            if (main.time == 0) {
                main.BuyGui.inicializeGui(true);
                ConfigUtils.resetTime(main);
                return;
            }
            main.time--;
            if (main.time % 180 == 0) {
                main.dbManager.updateTimer(main.time);
            }
        }, 20L, 20L);
    }
}
