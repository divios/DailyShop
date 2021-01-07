package io.github.divios.dailyrandomshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    private List<String> commands = new ArrayList<>();

    public TabComplete() {
        commands.add("reload");
        commands.add("renovate");
        commands.add("sell");
        commands.add("addDailyItem");
        commands.add("addSellItem");
        commands.add("settings");
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> aux = new ArrayList<>();

       if (args.length == 0) return commands;

        if (args.length >= 1) {

            for (String s: commands) {
                if (s.startsWith(args[0].toLowerCase())) {
                    aux.add(s);
                }
            }

            return aux;
        }

        return null;
    }
}
