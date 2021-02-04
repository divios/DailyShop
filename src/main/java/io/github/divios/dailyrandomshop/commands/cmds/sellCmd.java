package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.guis.sellGui;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.entity.Player;

public class sellCmd implements dailyCommand{

    @Override
    public void run(Player p) {
        if (!p.hasPermission("DailyRandomShop.sell")) {
            utils.noPerms(p);
            return;
        }
        sellGui.openInventory(p);
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.sell")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop sell &8 " +
                    "- &7Open market"));
        }
    }
}
