package io.github.divios.dailyrandomshop.commands;

import io.github.divios.dailyrandomshop.commands.cmds.allCmds;
import io.github.divios.dailyrandomshop.commands.cmds.dailyCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class tabComplete implements TabCompleter {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static tabComplete instance = null;
    private static final List<dailyCommand> cmdCommands = getCmds();

    private tabComplete() { }

    public static tabComplete getInstance() {
        if(instance == null) {
            instance = new tabComplete();
            main.getCommand("rdshop").setTabCompleter(instance);
        }
        return instance;
    }

    private static List<dailyCommand> getCmds() {
        List<dailyCommand> commandsList = new ArrayList<>();

        String path = "io.github.divios.dailyrandomshop.commands.cmds.";

        Arrays.stream(allCmds.values()).forEach(cmd -> {
            try {
                commandsList.add((dailyCommand) Class.forName(path + cmd.name() + "Cmd").newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return commandsList;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> commandsList = new ArrayList<>();

        cmdCommands.forEach(cmd -> cmd.command((Player) sender, commandsList));

        if (args.length == 0) return commandsList;

        return commandsList.stream().filter(s -> s.toLowerCase(Locale.ROOT)
                .startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}
