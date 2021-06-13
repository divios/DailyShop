package io.github.divios.lib.itemHolder;

import io.github.divios.core_lib.misc.Msg;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public enum dAction {

    EMPTY((p,s) -> {}),
    OPEN_SHOP((p, s) -> {
        shopsManager.getInstance()
                .getShop(s).ifPresent(shop1 -> shop1.getGui().open(p));}),
    RUN_CMD((p, s) -> {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                Msg.singletonMsg(s).add("%player%", p.getName()).build());
    });

    private final BiConsumer<Player, String> action;

    dAction(BiConsumer<Player, String> action) {
        this.action = action;
    }

    public void run(Player p, String s) {
        action.accept(p, s);
    }

}