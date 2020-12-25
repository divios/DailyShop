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
            commands.add("reload");
            commands.add("renovate");
            commands.add("sell");
            return commands;
        }

        return null;
    }
}
