package io.github.divios.dailyrandomshop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.guis.customizerguis.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.guis.settings.addDailyItemGuiIH;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class addDailyItemCmd extends abstractCommand {

    public addDailyItemCmd() {
        super(cmdTypes.BOTH);
    }

    @Override
    public String getName() {
        return "addDailyItemCmd";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&6&l>> &6/rdshop addDailyItems &8 " +
                "- &7Opens the menu to add an item");
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("DailyRandomShop.addDailyItem");
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
        addDailyItemGuiIH.open((Player) sender,
                itemStack -> customizerMainGuiIH.openInventory(
                                (Player) sender,
                                new dItem(itemStack),
                                args.get(0)
                ));
    }
}
