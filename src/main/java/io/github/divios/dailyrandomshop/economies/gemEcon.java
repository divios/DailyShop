package io.github.divios.dailyrandomshop.economies;

import io.github.divios.dailyrandomshop.hooks.hooksManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.entity.Player;

public class gemEcon implements economy{

    private final Currency currency;
    private final static GemsEconomyAPI gemApi = hooksManager.getInstance().getGemsEcon();

    public gemEcon(String currency) {
        this.currency = gemApi.getCurrency(currency);
    }

    @Override
    public boolean hasMoney(Player p, Double price) {
        return gemApi.getBalance(p.getUniqueId(), currency) > price;
    }

    @Override
    public void waitchDrawMoney(Player p, Double price) {
        gemApi.withdraw(p.getUniqueId(), price, currency);
    }
}
