package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.dailyshopparser.Api;
import io.github.divios.dailyshopparser.DailyShopParser;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import me.realized.tokenmanager.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class parser extends abstractCommand {

    public parser() {
        super(cmdTypes.BOTH);
    }

    @Override
    public String getName() {
        return "parser";
    }

    @Override
    public boolean validArgs(List<String> args) {

        if (args.size() >= 2) {
            return (args.get(0).equalsIgnoreCase("convert") ||
                    args.get(0).equalsIgnoreCase("parse"));
            }

        return false;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("dailyrandomshop.parser");
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {

        if (args.size() == 1) {
            return Arrays.asList("convert", "parse");
        }

        if (args.size() == 2) {
            return shopsManager.getInstance().getShops().stream()
                    .map(dShop::getName)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public void run(CommandSender sender, List<String> args) {

        if (!utils.isOperative("DailyShopParser")) {
            if (sender instanceof Player) Msg.sendMsg((Player) sender, "You need the parser module installed to run this action, you can get it here");
            else sender.sendMessage("You don't have the parser module installed to run this action, you can get it here");
            return;
        }

        Api api = DailyShopParser.getApi();
        dShop shop = shopsManager.getInstance().getShop(args.get(1)).orElse(null);

        if (args.get(0).equals("parse")) {
            api.deserialize(args.get(1));
            if (sender instanceof Player) Msg.sendMsg((Player) sender, "Parsed all items correctly");
        }

        if (args.get(0).equals("convert")) {
            if (shop == null) {
                sender.sendMessage("That shop doesn't exist");
                return;
            }
            api.serialize(shop);
            if (sender instanceof Player) Msg.sendMsg((Player) sender, "Converted all items correctly");
        }

    }
}
