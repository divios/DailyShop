package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class buyCmd implements dailyCommand{

    private final String playerStr;

    public buyCmd () {
        this.playerStr = null;
    }

    public buyCmd(String playerStr) {
        this.playerStr = playerStr;
    }

    @Override
    public void run(CommandSender sender) {

        if (playerStr != null && Bukkit.getPlayer(playerStr) != null) {

            if (!sender.hasPermission("DailyRandomShop.open.others")) {
                utils.noPerms(sender);
                return;
            }

            //buyGui.getInstance().openInventory(Bukkit.getPlayer(playerStr));
            return;
        }

        if (! (sender instanceof Player)) {
            utils.noCmd(sender);
            return;
        }
        Player p = (Player) sender;

        if (!p.hasPermission("DailyRandomShop.open")) {
            utils.noPerms(p);
            return;
        }
        //buyGui.getInstance().openInventory(p);
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.open")) {
            p.sendMessage(FormatUtils.color("&6&l>> &6/rdshop open &8 " +
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
