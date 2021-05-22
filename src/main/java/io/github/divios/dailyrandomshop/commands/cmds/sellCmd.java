package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class sellCmd implements dailyCommand{

    private final String playerStr;

    public sellCmd () {
        this.playerStr = null;
    }

    public sellCmd(String playerStr) {
        this.playerStr = playerStr;
    }


    @Override
    public void run(CommandSender sender) {

        if (playerStr != null && Bukkit.getPlayer(playerStr) != null) {

            if (!sender.hasPermission("DailyRandomShop.sell.others")) {
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

        if (!p.hasPermission("DailyRandomShop.sell")) {
            utils.noPerms(p);
            return;
        }
        //sellGui.openInventory(p);
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.sell")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop sell &8 " +
                    "- &7Open market"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.sell")) {
            s.add("sell");
        }
    }
}
