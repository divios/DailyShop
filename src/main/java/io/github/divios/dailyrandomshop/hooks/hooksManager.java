package io.github.divios.dailyrandomshop.hooks;

import net.milkbowl.vault.economy.Economy;

public class hooksManager {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static hooksManager instance = null;

    private hooksManager() {};

    public static hooksManager getInstance() {
        if (instance == null) {
            instance = new hooksManager();
            instance.init();
        }
        return instance;
    }

    private void init() {
        /* TODO: Initiate all hooks */
        vaultHook.hook();
    }

    public Economy getEcon() {
        return vaultHook.getEcon();
    }

}
