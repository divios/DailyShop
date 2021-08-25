package io.github.divios.dailyShop.tasks;

import io.github.divios.dailyShop.DailyShop;

public class taskManager {

    private static final DailyShop main = DailyShop.getInstance();
    private static taskManager instance = null;


    private taskManager() {
    }

    public static taskManager getInstance() {
        if (instance == null) {
            instance = new taskManager();
        }
        return instance;
    }



}
