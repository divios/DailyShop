package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.guis.settings.addDailyGuiIH;
import io.github.divios.dailyShop.guis.settings.shopGui;
import io.github.divios.dailyShop.guis.settings.shopsManagerGui;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class add extends abstractCommand {

    private static final shopsManager sManager = shopsManager.getInstance();

    public add() {
        super(cmdTypes.BOTH);
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&6&l>> &6/rdshop add [shop]&8 " +
                "- &7Opens the menu to add an item");
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("DailyShop.addDailyItem");
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {
        if (args.size() == 1)
            return shopsManager.getInstance().getShops()
                    .stream()
                    .map(dShop::getName)
                    .collect(Collectors.toList());
        return null;
    }

    @Override
    public void run(CommandSender sender, List<String> args) {


        addDailyGuiIH.open((Player) sender, sManager.getShop(args.get(0)).get(),
                itemStack ->
                        sManager.getShop(args.get(0))
                        .ifPresent(shop -> {
                            shop.addItem(new dItem(itemStack));
                            shopGui.open((Player) sender, shop);
                        })
                , () -> shopsManagerGui.open((Player) sender));
    }
}
