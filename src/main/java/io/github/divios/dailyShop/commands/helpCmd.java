package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.commands.CommandManager;
import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class helpCmd extends abstractCommand {

    public helpCmd() {
        super(cmdTypes.PLAYERS);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&8- &6/rdshop help&8 " +
                "- &7Shows this prompt");
    }

    @Override
    public List<String> getPerms() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {
        return null;
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        sender.sendMessage("");
        sender.sendMessage(FormatUtils.color("&6&lDailyShop Help &7Version &6" +
                DailyShop.getInstance().getDescription().getVersion()));
        CommandManager.getCmds().stream()
                .filter(absC -> {
                    for (String perms : absC.getPerms())
                        if (!sender.hasPermission(perms))
                        return false;
                    return true;
                })
                .forEach(absC -> sender.sendMessage(absC.getHelp()));
        sender.sendMessage("");
    }
}