package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.guis.settings.shopsManagerGui;
import io.github.divios.jcommands.JCommand;

public class managerCommand {

    public JCommand getCommand() {
        return JCommand.create("manager")
                .assertPermission("DailyRandomShop.settings")
                .assertUsage(FormatUtils.color("&8- &6/rdshop manager [player] &8- &7Opens the shops manager gui"))
                .executesPlayer((player, values) -> shopsManagerGui.open(player))
                .executesConsole((consoleCommandSender, valueMap) ->
                        consoleCommandSender.sendMessage("This command can only be executed by players"));
    }

}