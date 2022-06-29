package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.guis.settings.addDailyGuiIH;
import io.github.divios.dailyShop.guis.settings.shopsItemsManagerGui;
import io.github.divios.dailyShop.guis.settings.shopsManagerGui;
import io.github.divios.dailyShop.utils.valuegenerators.FixedValueGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.ValueGenerator;
import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;

import java.util.Arrays;
import java.util.stream.Collectors;

public class addCommand {

    private static final ValueGenerator DEFAULT_BUY_GENERATOR = new FixedValueGenerator(500);
    private static final ValueGenerator DEFAULT_SELL_GENERATOR = new FixedValueGenerator(20);

    public JCommand getCommand() {
        return JCommand.create("add")
                .assertPermission("DailyRandomShop.addDailyItem")
                .assertUsage(FormatUtils.color("&8- &6/rdshop add [shop] &8- &7Opens the menu to add an item"))
                .withArguments(getShopsArgument())
                .executesPlayer((player, values) ->
                        DailyShop.get().getShopsManager().getShop(values.get("dailyShop").getAsString()).ifPresent(shop ->
                                addDailyGuiIH.open(player, shop, itemStack -> {
                                    shop.addItem(dItem.of(itemStack, shop)
                                            .setBuyPrice(ValueGenerator.fromJsonOptional(Settings.DEFAULT_BUY.getAsJson())
                                                    .orElse(DEFAULT_BUY_GENERATOR)
                                            )
                                            .setSellPrice(ValueGenerator.fromJsonOptional(Settings.DEFAULT_SELL.getAsJson())
                                                    .orElse(DEFAULT_SELL_GENERATOR)
                                            )
                                    );
                                    shopsItemsManagerGui.open(player, shop);
                                }, () -> shopsManagerGui.open(player))))
                .executesConsole((consoleCommandSender, valueMap) -> {
                    consoleCommandSender.sendMessage("This command can only be executed by players");
                });
    }

    private Argument getShopsArgument() {
        return new StringArgument("dailyShop")
                .overrideSuggestions(() -> DailyShop.get().getShopsManager().getShops()
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
