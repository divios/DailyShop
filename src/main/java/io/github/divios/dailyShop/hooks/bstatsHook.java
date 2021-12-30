package io.github.divios.dailyShop.hooks;

import io.github.divios.dailyShop.DailyShop;
import org.bstats.bukkit.Metrics;

public class bstatsHook implements Hook<Void> {

    bstatsHook() {
        hook();
    }

    private void hook() {
        int pluginId = 9721;
        Metrics metrics = new Metrics(DailyShop.get(), pluginId);
    }

    @Override
    public boolean isOn() {
        return true;
    }

    @Override
    public Void getApi() {
        return null;
    }
}
