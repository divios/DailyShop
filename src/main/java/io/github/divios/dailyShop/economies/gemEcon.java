package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.Hooks;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.entity.Player;

public class gemEcon extends Economy {

    private final Currency _currency;

    gemEcon(String currency) {
        super(currency, currency, Economies.gemsEconomy);
        this._currency = Hooks.GEMS_ECONOMY.getApi().getCurrency(currency);
    }

    @Override
    public void test() {
        Hooks.GEMS_ECONOMY.getApi().getClass().getName();
    }

    public void witchDrawMoney(Player p, Double price) {
        Hooks.GEMS_ECONOMY.getApi().withdraw(p.getUniqueId(), price, _currency);
    }

    public void depositMoney(Player p, Double price) {
        Hooks.GEMS_ECONOMY.getApi().deposit(p.getUniqueId(), price, _currency);
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.GEMS_ECONOMY.getApi().getBalance(p.getUniqueId(), _currency);
    }
}
