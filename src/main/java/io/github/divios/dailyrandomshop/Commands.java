package io.github.divios.dailyrandomshop;

import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Locale;


public class Commands implements CommandExecutor {

    private final DailyRandomShop main;

    public Commands(DailyRandomShop main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        Player player = (Player) sender;

        if (args.length == 0 && sender instanceof Player) {
            sender.sendMessage(main.config.PREFIX + main.config.MSG_OPEN_SHOP);
            player.openInventory(main.BuyGui.getGui());
        } else {
            if (args[0].toLowerCase(Locale.ROOT).equals("renovate") && player.hasPermission("DailyRandomShop.renovate")) {
                main.BuyGui.createRandomItems();
                main.resetTime();
            } else if (args[0].toLowerCase(Locale.ROOT).equals("reload") && player.hasPermission("DailyRandomShop.reload")) {
                try {

                    main.reloadConfig();
                    player.sendMessage(main.config.PREFIX + main.config.MSG_RELOAD);
                    main.createConfig();
                    main.BuyGui.inicializeGui(true);
                    main.ConfirmGui = new confirmGui(main);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[0].toLowerCase(Locale.ROOT).equals("confirmgui")) {
                player.openInventory(main.ConfirmGui.getGui(new ItemStack(Material.ACACIA_BOAT)));
            }
        }


        return true;
    }

}
