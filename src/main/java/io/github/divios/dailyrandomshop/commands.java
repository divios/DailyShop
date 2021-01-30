package io.github.divios.dailyrandomshop;

import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.settings.settingsGuiIH;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class commands implements CommandExecutor {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private final dataManager dbManager = dataManager.getInstance();
    private static commands instance = null;

    private commands() {};

    public static commands getInstance() {
        if (instance == null) {
            instance = new commands();
            main.getCommand("dailyRandomShop").setExecutor(instance);
        }
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) {

        } else {
            if (args[0].equalsIgnoreCase("settings")) {
                settingsGuiIH.getInstance(p);
            }
        }

        return true;
    }
}
