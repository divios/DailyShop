package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.Hooks;
import me.yic.mpoints.MPointsAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class MPointsE extends abstractEconomy {

    public MPointsE(String point) {
        super(point, point, econTypes.MPoints);
    }

    @Override
    public void test() {
        Hooks.M_POINTS.getClass().getName();
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        Hooks.M_POINTS.getApi().changebalance(super.currency, p.getUniqueId(), p.getName(), BigDecimal.valueOf(price), false);
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Hooks.M_POINTS.getApi().changebalance(super.currency, p.getUniqueId(), p.getName(), BigDecimal.valueOf(price), true);
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.M_POINTS.getApi().getbalance(super.currency, p.getUniqueId()).doubleValue();
    }
}
