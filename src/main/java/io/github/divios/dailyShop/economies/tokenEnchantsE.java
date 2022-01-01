package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.Hooks;
import org.bukkit.entity.Player;

public class tokenEnchantsE extends economy {

    tokenEnchantsE() {
        this("");
    }

    tokenEnchantsE(String currency) {
        super(currency, "TokenEnchants", Economies.tokenEnchants);
    }

    @Override
    public void test() {
        Hooks.TOKEN_ENCHANT.getApi().getBalanceTop();
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        Hooks.TOKEN_ENCHANT.getApi().removeTokens(p, price);
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Hooks.TOKEN_ENCHANT.getApi().addTokens(p, price);
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.TOKEN_ENCHANT.getApi().getTokens(p);
    }
}
