package io.github.divios.dailyShop.economies;

import org.bukkit.entity.Player;

public class expEconomy extends Economy {

    expEconomy() {
        this("");
    }

    expEconomy(String name) {
        super("", "exp", Economies.exp);
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
