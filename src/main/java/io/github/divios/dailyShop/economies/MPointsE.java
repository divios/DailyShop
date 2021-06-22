package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.hooksManager;
import me.yic.mpoints.MPointsAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class MPointsE extends economy {

    private transient static final MPointsAPI api = hooksManager.getInstance().getMPointsApi();

    public MPointsE(String point) { super(point, point); }

    @Override
    public void test() {
        api.getClass().getName();
    }

    @Override
    public boolean hasMoney(Player p, Double price) {
        return api.getbalance(super.currency, p.getUniqueId()).doubleValue() >= price ;
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        api.changebalance(super.currency, p.getUniqueId(), p.getName(), BigDecimal.valueOf(price), false);
    }

    @Override
    public void depositMoney(Player p, Double price) {
        api.changebalance(super.currency, p.getUniqueId(), p.getName(), BigDecimal.valueOf(price), true);
    }
}
