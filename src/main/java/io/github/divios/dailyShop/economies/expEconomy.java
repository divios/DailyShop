package io.github.divios.dailyShop.economies;

import org.bukkit.entity.Player;

public class expEconomy extends economy {

    public expEconomy() {
        this ("");
    }

    protected expEconomy(String name) {
        super("", "exp", econTypes.exp);
    }

    @Override
    public void test() {
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        p.setExp((float) (p.getExp() - price));
    }

    @Override
    public void depositMoney(Player p, Double price) {
        p.giveExp(price.intValue());
    }

    @Override
    public double getBalance(Player p) {
        return p.getExp();
    }
}
