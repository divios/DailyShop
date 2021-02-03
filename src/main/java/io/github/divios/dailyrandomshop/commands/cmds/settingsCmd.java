package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.guis.settings.settingsGuiIH;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.entity.Player;

public class settingsCmd implements dailyCommand{


    @Override
    public void run(Player p) {
        if (!p.hasPermission("DailyRandomShop.settings")) {
            utils.noPerms(p);
            return;
        }
        settingsGuiIH.openInventory(p);
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.settings")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop settings &8 " +
                    "- &7Opens the settings menu"));
        }
    }
}
