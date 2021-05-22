package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.events.expiredTimerEvent;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class renovateCmd implements dailyCommand{


    @Override
    public void run(CommandSender p) {

        if (!p.hasPermission("DailyRandomShop.renovate")) {
            utils.noPerms(p);
            return;
        }
        //taskManager.getInstance().resetTimer();
        Bukkit.getPluginManager().callEvent(new expiredTimerEvent());
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.renovate")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop renovate &8 " +
                    "- &7Renovates daily items and resets timer"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.renovate")) {
            s.add("renovate");
        }
    }
}
