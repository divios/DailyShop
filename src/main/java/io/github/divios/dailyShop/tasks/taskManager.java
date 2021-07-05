package io.github.divios.dailyShop.tasks;

import io.github.divios.dailyShop.DRShop;

public class taskManager {

    private static final DRShop main = DRShop.getInstance();
    private static taskManager instance = null;


    private taskManager() {
    }

    public static taskManager getInstance() {
        if (instance == null) {
            instance = new taskManager();
            updatePlaceholdersTask.load();
        }
        return instance;
    }



}
