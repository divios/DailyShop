package io.github.divios.dailyrandomshop.Tasks;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.Utils.ConfigUtils;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class UpdateTimer {

    public static void initTimer(DailyRandomShop main, boolean reload) {

        if (reload) return;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                if (main.time == 0) {
                    main.BuyGui.createRandomItems();
                    ConfigUtils.resetTime(main);
                    return;
                }
                main.time--;
                if (main.time % 180 == 0) {
                    try {
                        main.dbManager.updateTimer(main.time);
                    } catch (SQLException Ignore) {

                    }
                }
            }
        }, 20L, 20L);
    }
}
