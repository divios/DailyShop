package io.github.divios.dailyShop.economies;

import org.bukkit.entity.Player;

public class expEconomy extends abstractEconomy {

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
        p.setLevel((int) (p.getLevel() - price));
    }

    @Override
    public void depositMoney(Player p, Double price) {
        p.setLevel((int) (p.getLevel() + price));
    }

    @Override
    public double getBalance(Player p) {
        return p.getLevel();
    }
}
