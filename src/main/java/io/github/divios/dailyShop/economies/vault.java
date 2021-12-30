package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.Hooks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class vault extends abstractEconomy {

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
        Hooks.VAULT.getApi().withdrawPlayer(p, price);
    }

    public void depositMoney(Player p, Double price) {
        Hooks.VAULT.getApi().depositPlayer(p, price);
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.VAULT.getApi().getBalance(p);
    }
}
