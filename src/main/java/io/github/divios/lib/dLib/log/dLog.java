package io.github.divios.lib.dLib.log;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.log.options.dLogEntry;

public class dLog {

    /*
    Method to log entries into database. Is a shortcut for dataManager#addLogEntry
     */
    public static void log(dLogEntry entry) {
        DailyShop.get().getDatabaseManager().addLogEntryAsync(entry);
    }

}
