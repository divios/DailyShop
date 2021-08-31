package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class test extends abstractCommand {

    public test() {
        super(cmdTypes.PLAYERS);
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getPerms() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {
        return Collections.emptyList();
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        String json = dStockFactory.GLOBAL(3).toJson();
        Log.warn(json);
       dStock stock = dStock.fromJson(json);
        Log.warn(String.valueOf(stock.getDefault()));
    }
}
