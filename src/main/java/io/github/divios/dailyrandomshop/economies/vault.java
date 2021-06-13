package io.github.divios.dailyrandomshop.economies;

import io.github.divios.dailyrandomshop.hooks.hooksManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class vault extends economy {

    private transient static final Economy vault = hooksManager.getInstance().getVault();

    public vault() {
        this("");
    }

    public vault(String currency) {
        super(currency, "Vault");
    }

    public boolean hasMoney(Player p, Double price) {
        return vault.has(p, price);
    }

    public void witchDrawMoney(Player p, Double price) {
        vault.withdrawPlayer(p, price);
    }

    public void depositMoney(Player p, Double price) { vault.depositPlayer(p, price); }
}
