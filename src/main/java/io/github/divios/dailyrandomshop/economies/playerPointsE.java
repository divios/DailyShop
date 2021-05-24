package io.github.divios.dailyrandomshop.economies;


import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

public class playerPointsE implements economy {

    private transient static final PlayerPointsAPI api = PlayerPoints.getInstance().getAPI();

    @Override
    public boolean hasMoney(Player p, Double price) {
        return api.look(p.getUniqueId()) >= (int) Math.round(price);
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        api.takeAsync(p.getUniqueId(), (int) Math.round(price));
    }

    @Override
    public void depositMoney(Player p, Double price) {
        api.giveAsync(p.getUniqueId(), (int) Math.round(price));
    }
}
