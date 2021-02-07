package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.entity.Player;

import java.util.List;

public class buyCmd implements dailyCommand{
    @Override
    public void run(Player p) {
        if (!p.hasPermission("DailyRandomShop.open")) {
            utils.noPerms(p);
            return;
        }
        buyGui.getInstance().openInventory(p);
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.open")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop open &8 " +
                    "- &7Opens the dailyShop"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.open")) {
            s.add("open");
        }
    }
}
