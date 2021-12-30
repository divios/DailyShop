package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.utils.Log;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class vaultHook implements Hook<Economy> {

    vaultHook() {
        Log.info("Hooked to Vault");
    }

    @Override
    public boolean isOn() {
        return true;
    }

    @Nullable
    @Override
    public Economy getApi() {
        return Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }
}
