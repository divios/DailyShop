package io.github.divios.dailyrandomshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if( sender.hasPermission("DailyRandomShop.reload")) commands.add("reload");
            if( sender.hasPermission("DailyRandomShop.renovate")) commands.add("renovate");
            if( sender.hasPermission("DailyRandomShop.sell")) commands.add("sell");
            if( sender.hasPermission("DailyRandomShop.addDailyItem")) commands.add("addDailyItem");
            if( sender.hasPermission("DailyRandomShop.addSellItem")) commands.add("addSellItem");
            if( sender.hasPermission("DailyRandomShop.settings")) commands.add("Settings");
            return commands;
        }

        return null;
    }
}
