package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class addDailyItemCmd implements dailyCommand{
    @Override
    public void run(CommandSender sender) {

        if (! (sender instanceof Player)) {
            utils.noCmd(sender);
            return;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("DailyRandomShop.addDailyItem")) {
            utils.noPerms(p);
            return;
        }
        //addDailyItemGuiIH.openInventory(p);
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.addDailyItem")) {
            p.sendMessage(FormatUtils.color("&6&l>> &6/rdshop addDailyItems &8 " +
                    "- &7Opens the menu to add an item"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.addDailyItem")) {
            s.add("addDailyItem");
        }
    }
}
