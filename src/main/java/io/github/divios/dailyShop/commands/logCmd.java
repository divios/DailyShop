package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.lib.dLib.log.LogGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class logCmd extends abstractCommand {

    public logCmd() {
        super(cmdTypes.PLAYERS);
    }

    @Override
    public String getName() {
        return "log";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&8- &6/rdshop log &8 " +
                    "- &7Shows the log menu");
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("DailyRandomShop.log");
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {
        return Collections.emptyList();
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        LogGui.builder()
                .withPlayer((Player) sender)
                .prompt();
    }
}
