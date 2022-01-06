package io.github.divios.dailyShop.economies;


import io.github.divios.dailyShop.hooks.Hooks;
import org.bukkit.entity.Player;

public class playerPointsE extends economy {

    playerPointsE() {
        this("");
    }

    playerPointsE(String currency) {
        super(currency, "PlayerPoints", Economies.playerPoints);
    }

    @Override
    public void test() {
        Hooks.PLAYER_POINTS.getApi().getClass().getName();
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        Hooks.PLAYER_POINTS.getApi().take(p.getUniqueId(), (int) Math.round(price));
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Hooks.PLAYER_POINTS.getApi().give(p.getUniqueId(), (int) Math.round(price));
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.PLAYER_POINTS.getApi().look(p.getUniqueId());
    }
}
