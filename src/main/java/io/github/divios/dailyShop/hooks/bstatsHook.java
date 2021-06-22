package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DRShop;
import org.bstats.bukkit.Metrics;

public class bstatsHook {

    private static final DRShop main = DRShop.getInstance();

    private bstatsHook() {};

    public static void init() {
        int pluginId = 9721;
        Metrics metrics = new Metrics(main, pluginId);

    }

}
