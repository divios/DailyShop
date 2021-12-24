package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class reStockCommand {

    public JCommand getCommand() {
        return JCommand.create("reStock")
                .assertPermission("DailyRandomShop.restock")
                .assertUsage(FormatUtils.color("&8- &6/rdshop reStock [shop ] &8- &7Generates new items for the specific shop"))
                .withArguments(getShopsArgument())
                .executes((commandSender, values) -> {

                    if (values.get("dailyShop").getAsString().equalsIgnoreCase("--all")) {
                        DailyShop.get().getShopsManager().getShops().forEach(dShop::reStock);
                        return;
                    }

                    DailyShop.get().getShopsManager().getShop(values.get(0).getAsString())
                            .ifPresent(shop -> {
                                shop.reStock();
                                if (commandSender instanceof Player) shop.openShop((Player) commandSender);
                            });

                });
    }

    private Argument getShopsArgument() {
        return new StringArgument("dailyShop")
                .overrideSuggestions(() ->
                        Stream.concat(Stream.of("--all"),
                                        DailyShop.get().getShopsManager()
                                                .getShops()
                                                .stream()
                                                .map(dShop::getName)
                                )
                                .collect(Collectors.toList())
                )
                .setAsImperative();
    }

}
