package io.github.divios.dailyrandomshop.economies;

import io.github.divios.dailyrandomshop.hooks.hooksManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class vault implements economy{

    private static Economy vault = hooksManager.getInstance().getVault();

    @Override
    public boolean hasMoney(Player p, Double price) {
        return vault.has(p, price);
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        vault.withdrawPlayer(p, price);
    }
}
