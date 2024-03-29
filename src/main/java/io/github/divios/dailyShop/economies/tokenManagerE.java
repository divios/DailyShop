package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.hooks.Hooks;
import org.bukkit.entity.Player;

public class tokenManagerE extends Economy {

    tokenManagerE() {
        this("");
    }

    tokenManagerE(String currency) {
        super(currency, "TokenManager", Economies.tokenManager);
    }

    @Override
    public void test() {
        Hooks.TOKEN_MANAGER.getApi().getShop("aa");
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        DailyShop.get().getLogger().info("" + price.longValue());
        Hooks.TOKEN_MANAGER.getApi().removeTokens(p, price.longValue());
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Hooks.TOKEN_MANAGER.getApi().addTokens(p, price.longValue());
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.TOKEN_MANAGER.getApi().getTokens(p).getAsLong();
    }
}
