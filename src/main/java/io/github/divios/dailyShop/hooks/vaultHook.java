package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class vaultHook {

    private static final boolean enabled;

    static {
        enabled = Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
        if (enabled)
            Bukkit.getServer().getLogger().info("Hooked to Vault");
    }

    public static boolean isEnabled() { return enabled; }

    public static Economy getEcon() {
        if (!enabled)
            return null;

        return Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

}
