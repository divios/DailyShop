package io.github.divios.dailyShop.commands;

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
                .executesPlayer((player, values) -> player.sendMessage("Help here"))
                .register();
    }

}
