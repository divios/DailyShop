package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.main;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class reloadCmd implements dailyCommand{
    @Override
    public void run(CommandSender p) {
        if (!p.hasPermission("DailyRandomShop.reload")) {
            utils.noPerms(p);
            return;
        }
        p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_RELOAD);
        main.getInstance().realoadPlugin();
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.reload")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop reload &8 " +
                    "- &7Reload config"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.reload")) {
            s.add("reload");
        }
    }
}
