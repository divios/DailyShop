package io.github.divios.dailyrandomshop.commands;

import io.github.divios.core_lib.commands.CommandManager;
import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import org.bukkit.command.CommandSender;

import java.util.List;

public class helpCmd extends abstractCommand {

    static{
        new helpCmd();
    }

    private helpCmd() {
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
        return null;
    }

    @Override
    public List<String> getPerms() {
        return null;
    }

    @Override
    public List<String> getTabCompletition(String[] args) {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        CommandManager.getCmds().stream()
                .filter(absC -> {
                    for (String perms : absC.getPerms())
                        if (!sender.hasPermission(perms))
                        return false;
                    return true;
                })
                .forEach(absC -> sender.sendMessage(absC.getHelp()));
    }
}