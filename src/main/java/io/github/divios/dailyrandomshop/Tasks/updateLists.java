package io.github.divios.dailyrandomshop.Tasks;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;

public class updateLists {

    public static void initTask(DailyRandomShop main, boolean reload) {

        if (reload) return;

        main.updateListID = Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {

            main.dbManager.updateAllDailyItems();
            main.dbManager.updateAllSellItems();

        }, 12000L, 12000L);
    }

}
