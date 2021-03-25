package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class helpCmd implements dailyCommand{

    @Override
    public void run(CommandSender sender) {

        if (! (sender instanceof Player)) {
            utils.noCmd(sender);
            return;
        }

        Player p = (Player) sender;
        String path = "io.github.divios.dailyrandomshop.commands.cmds.";
        p.sendMessage(conf_msg.PREFIX + utils.formatString("&e&lPlugin help"));
        Arrays.stream(allCmds.values()).forEach(cmd -> {
            try {
                ((dailyCommand) Class.forName(path + cmd.name() + "Cmd").newInstance()).help(p);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.help")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop help &8 " +
                    "- &7Displays help"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.help")) {
            s.add("help");
        }
    }
}