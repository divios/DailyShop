package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.guis.settings.shopsManagerGui;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class settingsCmd implements dailyCommand{


    @Override
    public void run(CommandSender sender) {

        if (! (sender instanceof Player)) {
            utils.noCmd(sender);
            return;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("DailyRandomShop.settings")) {
            utils.noPerms(p);
            return;
        }
        shopsManagerGui.open(p);
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.settings")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop settings &8 " +
                    "- &7Opens the settings menu"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.settings")) {
            s.add("settings");
        }
    }
}