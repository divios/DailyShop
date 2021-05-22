package io.github.divios.dailyrandomshop.commands;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.commands.cmds.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class commandsManager implements CommandExecutor {

    private static final DRShop main = DRShop.getInstance();
    private static commandsManager instance = null;

    private commandsManager() {}

    public static commandsManager getInstance() {
        if (instance == null) {
            instance = new commandsManager();
            main.getCommand("dailyRandomShop").setExecutor(instance);
        }
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        dailyCommand dlCommand = null;

        if (args.length == 0) {
            dlCommand = new buyCmd();

        } else {

            if (args[0].equalsIgnoreCase("open")) {
                if (args.length == 2) {
                    dlCommand = new buyCmd(args[1]);
                }
                else dlCommand = new buyCmd();
            }

            else if (args[0].equalsIgnoreCase("sell")) {
                if (args.length == 2) {
                    dlCommand = new sellCmd(args[1]);
                }
                else dlCommand = new sellCmd();
            }

            else if (args[0].equalsIgnoreCase("addDailyItem")) {
                dlCommand = new addDailyItemCmd();
            }

            else if (args[0].equalsIgnoreCase("addSellItem")) {
                dlCommand = new addSellItemCmd();
            }

            else if (args[0].equalsIgnoreCase("settings")) {
                dlCommand = new settingsCmd();
            }

            else if (args[0].equalsIgnoreCase("renovate")) {
                dlCommand = new renovateCmd();
            }

            else if (args[0].equalsIgnoreCase("reload")) {
                dlCommand = new reloadCmd();
            }

            else if (args[0].equalsIgnoreCase("importShopGui+")
                    && args.length == 3) {
                dlCommand = new importShopGuiItemsCmd(args[1], args[2]);
            }

            else dlCommand = new helpCmd();
        }
        dlCommand.run(sender);
        return true;
    }
}
