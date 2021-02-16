package io.github.divios.dailyrandomshop.tasks;

import io.github.divios.dailyrandomshop.database.dataManager;
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

    private void initTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            if (dbManager.listSellItemsHash != dbManager.listSellItems.hashCode())
                dbManager.updateAsyncSellItems();

            if (dbManager.listDailyItemsHash != dbManager.listDailyItems.hashCode())
                dbManager.updateAsyncBuyItems();

            dbManager.updateAsyncCurrentItems();
        }, 2000L, 2000L);
    }

}
