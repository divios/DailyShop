package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;
import org.bstats.bukkit.Metrics;

public class bstatsHook {

    private static final DailyShop main = DailyShop.get();

    private bstatsHook() {};

    public static void init() {
        int pluginId = 9721;
        Metrics metrics = new Metrics(main, pluginId);

    }

}
