package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.PlayerArgument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class openCommand {

    public Collection<JCommand> getCommand() {
        return Arrays.asList(getDefaultCommand(), getSelfCommand(), getOtherCommand());
    }

    private JCommand getDefaultCommand() {
        return JCommand.create("open")
                .assertPermission("DailyRandomShop.open")
                .assertUsage(FormatUtils.color("&8- &6/rdshop open [shop] [player] &8- &7Opens a gui for yourself or for the given player"))
                .executesPlayer((player, values) ->
                        DailyShop.get().getShopsManager().getDefaultShop().ifPresent(shop -> shop.openShop(player)));
    }

    private JCommand getSelfCommand() {
        return JCommand.create("open")
                .assertPermission("DailyRandomShop.open")
                .assertUsage(FormatUtils.color("&8- &6/rdshop open [shop] [player] &8- &7Opens a gui for yourself or for the given player"))
                .withArguments(getShopsArgument())
                .executesPlayer((player, values) ->
                        DailyShop.get().getShopsManager().getShop(values.get("dailyShop").getAsString())
                                .ifPresent(shop -> {
                                    if (!player.hasPermission("DailyRandomShop.open." + shop.getName())) {
                                        Messages.MSG_NOT_PERMS.send(player);
                                        return;
                                    }
                                    shop.openShop(player);
                                }));
    }

    private JCommand getOtherCommand() {
        return JCommand.create("open")
                .assertPermission("DailyRandomShop.open.others")
                .assertUsage(FormatUtils.color("&8- &6/rdshop open [shop] [player] &8- &7Opens a gui for yourself or for the given player"))
                .withArguments(getShopsArgument(), new PlayerArgument("target"))
                .executes((commandSender, args) -> {
                    DailyShop.get().getShopsManager().getShop(args.get("dailyShop").getAsString())
                            .ifPresent(shop -> {
                                Player p = args.get("target").getAsPlayer();
                                shop.openShop(p);
                            });
                });
    }

    private Argument getShopsArgument() {
        return new StringArgument("dailyShop")
                .overrideSuggestions(() -> DailyShop.get().getShopsManager()
                        .getShops()
                        .stream()
                        .map(dShop::getName)
                        .collect(Collectors.toList())
                )
                .setAsImperative();
    }

}
