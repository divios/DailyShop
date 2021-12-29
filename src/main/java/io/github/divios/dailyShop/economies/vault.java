package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.hooksManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class vault extends abstractEconomy {

    private transient static final Economy vault = hooksManager.getInstance().getVault();

    public vault() {
        this("");
    }

    public vault(String currency) {
        super(currency, () -> "Vault", econTypes.vault);
    }

    @Override
    public void test() {

    }

    public void witchDrawMoney(Player p, Double price) {
        vault.withdrawPlayer(p, price);
    }

    public void depositMoney(Player p, Double price) {
        vault.depositPlayer(p, price);
    }

    @Override
    public double getBalance(Player p) {
        return vault.getBalance(p);
    }
}
