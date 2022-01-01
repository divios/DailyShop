package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.Hooks;
import org.bukkit.entity.Player;

public class vault extends economy {

    vault() {
        this("");
    }

    vault(String currency) {
        super(currency, () -> "Vault", Economies.vault);
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
