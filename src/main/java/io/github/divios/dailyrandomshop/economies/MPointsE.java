package io.github.divios.dailyrandomshop.economies;

import io.github.divios.dailyrandomshop.hooks.hooksManager;
import me.yic.mpoints.MPointsAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class MPointsE implements economy{

    private final String point;
    private final MPointsAPI api = hooksManager.getInstance().getMPointsApi();

    public MPointsE(String point) { this.point = point; }

    @Override
    public boolean hasMoney(Player p, Double price) {
        return api.getbalance(point, p.getUniqueId()).doubleValue() >= price ;
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        api.changebalance(point, p.getUniqueId(), p.getName(), BigDecimal.valueOf(price), false);
    }

    @Override
    public void depositMoney(Player p, Double price) {
        api.changebalance(point, p.getUniqueId(), p.getName(), BigDecimal.valueOf(price), true);
    }
}
