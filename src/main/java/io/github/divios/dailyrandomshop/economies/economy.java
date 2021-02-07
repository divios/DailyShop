package io.github.divios.dailyrandomshop.economies;

import org.bukkit.entity.Player;

public interface economy {

    boolean hasMoney(Player p, Double price);
    void witchDrawMoney(Player p, Double price);
    void depositMoney(Player p, Double price);
}
