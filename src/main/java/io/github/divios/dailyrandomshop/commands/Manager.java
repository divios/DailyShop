package io.github.divios.dailyrandomshop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyrandomshop.guis.settings.shopsManagerGui;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Manager extends abstractCommand {

    public Manager() {
        super(cmdTypes.PLAYERS);
    }

    @Override
    public String getName() {
        return "manager";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&6&l>> &6/rdshop manager [player]&8 " +
                "- &7Opens the settings menu");
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("DailyRandomShop.settings");
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {
        if (args.size() == 1)
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        return null;
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
            shopsManagerGui.open(args.size() > 0 ?
                    Bukkit.getPlayer(args.get(0)) : (Player) sender);

    }
}