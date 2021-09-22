package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.hooks.hooksManager;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.entity.Player;

public class tokenManagerE extends economy {

    public tokenManagerE() { this(""); }

    public tokenManagerE(String currency) { super(currency, "TokenManager", econTypes.tokenManager); }

    private transient static final TokenManager api = hooksManager.getInstance().getTokenManagerApi();

    @Override
    public void test() {
        api.getShop("aa");
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        DailyShop.getInstance().getLogger().info("" + price.longValue());
        api.removeTokens(p, price.longValue());
    }

    @Override
    public void depositMoney(Player p, Double price) {
        api.addTokens(p, price.longValue());
    }

    @Override
    public double getBalance(Player p) {
        return api.getTokens(p).getAsLong();
    }
}
