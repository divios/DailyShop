package io.github.divios.dailyShop.economies;

import dev.unnm3d.gettoni.Gettoni;
import org.bukkit.entity.Player;

public class gettoniE extends Economy{

    protected gettoniE() {
        super("", "gettoni", Economies.gettoni);
    }

    @Override
    public void test() {

    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        Gettoni.getLeadAccs().stream()
                .filter(account -> account.getUuid().equals(p.getUniqueId()))
                .findFirst()
                .ifPresent(account -> {
                    account.setGettoni((int) Math.ceil(account.getGettoni() - price));
                });
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Gettoni.getLeadAccs().stream()
                .filter(account -> account.getUuid().equals(p.getUniqueId()))
                .findFirst()
                .ifPresent(account -> {
                    account.setGettoni((int) Math.ceil(account.getGettoni() + price));
                });
    }

    @Override
    public double getBalance(Player p) {
        double[] balance = {0.0};
        Gettoni.getLeadAccs().stream()
                .filter(account -> account.getUuid().equals(p.getUniqueId()))
                .findFirst()
                .ifPresent(account -> balance[0] = account.getGettoni());

        return balance[0];
    }
}
