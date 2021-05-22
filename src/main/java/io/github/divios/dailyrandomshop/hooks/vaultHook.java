package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.DRShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

class vaultHook {

    private static Economy econ = null;

    private static final DRShop main = DRShop.getInstance();

    static void hook() {
        if (!setupEconomy()) {
            main.getLogger().severe(String.format(
                    "[%s] - Disabled due to no Vault dependency found!", main.getDescription().getName()));
            main.getServer().getPluginManager().disablePlugin(main);
        }
        else main.getLogger().info("Hooked to Vault");
    }

    static boolean setupEconomy() {
        if (main.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = main.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    static Economy getEcon() {
        return econ;
    }

}
