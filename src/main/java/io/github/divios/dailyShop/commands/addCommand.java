package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.guis.settings.addDailyGuiIH;
import io.github.divios.dailyShop.guis.settings.shopGui;
import io.github.divios.dailyShop.guis.settings.shopsManagerGui;
import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.serialize.serializerApi;

import java.util.Arrays;
import java.util.stream.Collectors;

public class addCommand {

    private static final shopsManager sManager = DailyShop.get().getShopsManager();

    public JCommand getCommand() {
        return JCommand.create("add")
                .assertPermission("DailyRandomShop.addDailyItem")
                .assertUsage(FormatUtils.color("&8- &6/rdshop add [shop] &8- &7Opens the menu to add an item"))
                .withArguments(getShopsArgument())
                .executesPlayer((player, values) ->
                        sManager.getShop(values.get("dailyShop").getAsString()).ifPresent(shop ->
                                addDailyGuiIH.open(player, shop, itemStack -> {
                                    shop.addItem(dItem.of(itemStack)
                                            .setBuyPrice(Settings.DEFAULT_BUY.getValue().getAsDouble())
                                            .setSellPrice(Settings.DEFAULT_SELL.getValue().getAsDouble())
                                    );
                                    serializerApi.saveShopToFileAsync(shop);
                                    shopGui.open(player, shop);
                                }, () -> shopsManagerGui.open(player))))
                .executesConsole((consoleCommandSender, valueMap) -> {
                    consoleCommandSender.sendMessage("This command can only be executed by players");
                });
    }

    private Argument getShopsArgument() {
        return new StringArgument("dailyShop")
                .overrideSuggestions(() -> sManager.getShops()
                        .stream()
                        .map(dShop::getName)
                        .collect(Collectors.toList())
                )
                .setAsImperative();
    }

    private void test() {
        JCommand.create("Custom argument")
                .withArguments(new StringArgument("actions")
                        .overrideSuggestions(() -> Arrays.asList("sell", "buy", "sellAll"))        // You can set your own custom autocomplete arguments
                )
                .register();
    }

}
