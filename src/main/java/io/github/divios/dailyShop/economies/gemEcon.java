package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.hooksManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.entity.Player;

public class gemEcon extends economy {

    private final Currency _currency;
    private transient final static GemsEconomyAPI gemApi = hooksManager.getInstance().getGemsEcon();

    public gemEcon(String currency) {
        super(currency, currency, econTypes.gemsEconomy);
        this._currency = gemApi.getCurrency(currency);
    }

    @Override
    public void test() {
        gemApi.getClass().getName();
    }

    public void witchDrawMoney(Player p, Double price) {
        gemApi.withdraw(p.getUniqueId(), price, _currency);
    }

    public void depositMoney(Player p, Double price) { gemApi.deposit(p.getUniqueId(), price, _currency); }

    @Override
    public double getBalance(Player p) {
        return gemApi.getBalance(p.getUniqueId(), _currency);
    }
}
