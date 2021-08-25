package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.events.reStockShopEvent;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class reStock extends abstractCommand {

    public reStock() {
        super(cmdTypes.BOTH);
    }

    @Override
    public String getName() {
        return "reStock";
    }

    @Override
    public boolean validArgs(List<String> args) {
        if (args.size() <= 0)
            return false;

        else return shopsManager.getInstance().getShop(args.get(0)).isPresent();

    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&8- &6/rdshop reStock [shop]&8 " +
                "- &7Generates new items for the specific shop");
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("DailyRandomShop.restock");
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {
        if (args.size() == 1)
            return shopsManager.getInstance().getShops()
                    .stream()
                    .map(dShop::getName)
                    .collect(Collectors.toList());

        return Collections.emptyList();
    }

    @Override
    public void run(CommandSender sender, List<String> args) {

        shopsManager.getInstance().getShop(args.get(0))
                .ifPresent(shop -> {
                    Bukkit.getPluginManager().callEvent(new reStockShopEvent(shop));
                    shop.open((Player) sender);
                        }
                );

    }
}
