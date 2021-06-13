package io.github.divios.dailyrandomshop.economies;

import io.github.divios.dailyrandomshop.hooks.hooksManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.entity.Player;

public class gemEcon extends economy {

    private final Currency _currency;
    private transient final static GemsEconomyAPI gemApi = hooksManager.getInstance().getGemsEcon();

    public gemEcon(String currency) {
        super(currency, currency);
        this._currency = gemApi.getCurrency(currency);
    }

    public boolean hasMoney(Player p, Double price) {
        return gemApi.getBalance(p.getUniqueId(), _currency) > price;
    }

    public void witchDrawMoney(Player p, Double price) {
        gemApi.withdraw(p.getUniqueId(), price, _currency);
    }

    public void depositMoney(Player p, Double price) { gemApi.deposit(p.getUniqueId(), price, _currency); }
}
