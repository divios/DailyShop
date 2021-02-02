package io.github.divios.dailyrandomshop.economies;

import org.bukkit.entity.Player;

public interface economy {

    boolean hasMoney(Player p, Double price);
    void waitchDrawMoney(Player p, Double price);
}
