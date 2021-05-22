package io.github.divios.dailyrandomshop.tasks;

import io.github.divios.dailyrandomshop.DRShop;

public class taskManager {

    private static final DRShop main = DRShop.getInstance();
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
