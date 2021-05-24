package io.github.divios.dailyrandomshop.economies;

import org.bukkit.entity.Player;

import java.io.Serializable;

public interface economy extends Serializable {

    boolean hasMoney(Player p, Double price);
    void witchDrawMoney(Player p, Double price);
    void depositMoney(Player p, Double price);
}
