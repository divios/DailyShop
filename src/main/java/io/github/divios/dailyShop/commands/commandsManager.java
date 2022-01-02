package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.jcommands.JCommand;

public class commandsManager {

    public void loadCommands() {
        JCommand.create("dailyShop")
                .withAliases("ds", "dShop", "rdshop", "dailyRandomShop")
                .withSubcommands(new openCommand().getCommand())
                .withSubcommands(new reStockCommand().getCommand())
                .withSubcommands(new managerCommand().getCommand())
                .withSubcommands(new addCommand().getCommand())
                .withSubcommands(new loggerCommand().getCommand())
                .withSubcommands(new importShops().getCommand())
                .withSubcommands(new reloadCommand().getCommand())
                .withSubcommands(new testNewStockCommand().getCommand())
                .executes((sender, valueMap) -> {
                    sender.sendMessage(FormatUtils.color("&8 ------- &6 Help &8 -------"));
                    sender.sendMessage(FormatUtils.color("&8- &6/rdshop open [shop] [player] &8- &7Opens a gui for yourself or for the given player"));
                    sender.sendMessage(FormatUtils.color("&8- &6/rdshop add [shop] &8- &7Opens the menu to add an item"));
                    sender.sendMessage(FormatUtils.color("&8- &6/rdshop reStock [shop ] &8- &7Generates new items for the specific shop"));
                    sender.sendMessage(FormatUtils.color("&8- &6/rdshop manager [player] &8- &7Opens the shops manager gui"));
                    sender.sendMessage(FormatUtils.color("&8- &6/rdshop import [plugin] [shop] [_shop] ] &8- &7Imports the given items _shop to a shop"));
                    sender.sendMessage(FormatUtils.color("&8- &6/rdshop log &8- &7Shows the log menu"));
                    sender.sendMessage(FormatUtils.color("&8- &6/rdshop reload &8- &7Reload plugin"));
                })
                .register();
    }

}
