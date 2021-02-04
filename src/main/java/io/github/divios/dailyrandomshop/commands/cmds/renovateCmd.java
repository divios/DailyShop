package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.events.expiredTimerEvent;
import io.github.divios.dailyrandomshop.tasks.taskManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class renovateCmd implements dailyCommand{


    @Override
    public void run(Player p) {
        if (!p.hasPermission("DailyRandomShop.renovate")) {
            utils.noPerms(p);
            return;
        }
        taskManager.getInstance().resetTimer();
        Bukkit.getPluginManager().callEvent(new expiredTimerEvent());
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.renovate")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop renovate &8 " +
                    "- &7Renovates daily items and resets timer"));
        }
    }
}
