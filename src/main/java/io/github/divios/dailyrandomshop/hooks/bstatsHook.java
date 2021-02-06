package io.github.divios.dailyrandomshop.hooks;

import org.bstats.bukkit.Metrics;

public class bstatsHook {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

    private bstatsHook() {};

    public static void init() {
        int pluginId = 9721;
        Metrics metrics = new Metrics(main, pluginId);

    }

}
