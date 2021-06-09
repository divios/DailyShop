package io.github.divios.dailyrandomshop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class settingsCmd extends abstractCommand {

    static {
        new settingsCmd();
    }

    private settingsCmd() {
        super(cmdTypes.PLAYERS);
    }

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&6&l>> &6/rdshop settings &8 " +
                "- &7Opens the settings menu");
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("DailyRandomShop.settings");
    }

    @Override
    public List<String> getTabCompletition(String[] args) {
        if (args.length == 2)
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getDisplayName)
                    .collect(Collectors.toList());
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {

    }
}