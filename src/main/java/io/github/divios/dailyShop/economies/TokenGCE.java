package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.Hooks;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class TokenGCE extends Economy {

    protected TokenGCE() {
        this("");
    }

    protected TokenGCE(String currency) {
        super(currency, () -> "TokenGC", Economies.tokenGC);
    }

    @Override
    public void test() {
        Hooks.TOKEN_GC_HOOK.getApi().getClass().getName();
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        Hooks.TOKEN_GC_HOOK.getApi().removeTokens(p.getUniqueId(), (long) Math.floor(price));
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Hooks.TOKEN_GC_HOOK.getApi().addTokens(p.getUniqueId(), (long) Math.floor(price));
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.TOKEN_GC_HOOK.getApi().getTokens(p.getUniqueId());
    }
}
