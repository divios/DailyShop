package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.jcommands.JCommand;
import io.github.divios.lib.dLib.log.LogGui;

public class loggerCommand {

    public JCommand getCommand() {
        return JCommand.create("log")
                .assertPermission("DailyRandomShop.log")
                .assertUsage(FormatUtils.color("&8- &6/rdshop log &8- &7Shows the log menu"))
                .executesPlayer((player, values) -> {
                    LogGui.builder()
                            .withPlayer(player)
                            .prompt();
                })
                .executesConsole((consoleCommandSender, valueMap) ->
                        consoleCommandSender.sendMessage("This command can only be executed by players"));
    }

}
