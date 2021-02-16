package io.github.divios.dailyrandomshop.tasks;

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
            if (dbManager.listSellItemsHash != dbManager.listSellItems.hashCode())
                utils.async(dbManager::updateSellItems);

            if (dbManager.listDailyItemsHash != dbManager.listDailyItems.hashCode())
                utils.async(dbManager::updateBuyItems);

            if(dbManager.currentItemsHash != buyGui.getInstance().getCurrentItemsHash())
                utils.async(dbManager::updateCurrentItems);

        }, 12000L, 12000L);
    }

}
