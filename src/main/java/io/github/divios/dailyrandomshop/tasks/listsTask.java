package io.github.divios.dailyrandomshop.tasks;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Bukkit;

class listsTask {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private static listsTask instance = null;

    private listsTask() {
    }

    public static listsTask getInstance() {
        if (instance == null) {
            instance = new listsTask();
            instance.initTask();
        }
        return instance;
    }

    void initTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            if (dbManager.listSellItemsHash != dbManager.listSellItems.hashCode()) {
                utils.sync(dbManager::updateSellItems);
                if(conf_msg.DEBUG) main.getLogger().info("Updated sell Items");
            }

            if (dbManager.listDailyItemsHash != dbManager.listDailyItems.hashCode()) {
                utils.sync(dbManager::updateBuyItems);
                if(conf_msg.DEBUG) main.getLogger().info("Updated daily Items");
            }

            if(dbManager.currentItemsHash != buyGui.getInstance().getCurrentItemsHash()) {
                utils.sync(dbManager::updateCurrentItems);
                if(conf_msg.DEBUG) main.getLogger().info("Updated current Items");
            }

        }, 1200L, 1200L);
    }

}
