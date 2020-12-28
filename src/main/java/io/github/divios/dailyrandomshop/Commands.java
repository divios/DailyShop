package io.github.divios.dailyrandomshop;

import io.github.divios.dailyrandomshop.Utils.ConfigUtils;
import io.github.divios.dailyrandomshop.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
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

        if (args.length == 0) {

            if (!(sender instanceof Player) ) return true;

            if(!(sender.hasPermission("DailyRandomShop.open" ))) {
                sender.sendMessage(main.config.PREFIX + main.config.MSG_NOT_PERMS);
                return true;
            }
            sender.sendMessage(main.config.PREFIX + main.config.MSG_OPEN_SHOP);
            Player p = (Player) sender;
            p.openInventory(main.BuyGui.getGui());
        } else {
            if (args[0].toLowerCase(Locale.ROOT).equals("renovate") && sender.hasPermission("DailyRandomShop.renovate")) {
                main.BuyGui.inicializeGui(true);
                ConfigUtils.resetTime(main);
            } else if (args[0].toLowerCase(Locale.ROOT).equals("reload") && sender.hasPermission("DailyRandomShop.reload")) {
                try {
                    //ConfigUtils.CloseAllInventories(main);
                    main.reloadConfig();
                    sender.sendMessage(main.config.PREFIX + main.config.MSG_RELOAD);
                    ConfigUtils.reloadConfig(main, true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[0].toLowerCase(Locale.ROOT).equals("sell") && (sender instanceof Player) &&
                        main.getConfig().getBoolean("enable-sell-gui")){

                if (!sender.hasPermission("DailyRandomShop.sell") ){
                    sender.sendMessage(main.config.PREFIX + main.config.MSG_NOT_PERMS);
                    return true;
                }
                Player p = (Player) sender;
                //p.openInventory(main.SellGui.createSellInv());
            } else if (args[0].toLowerCase(Locale.ROOT).equals("adddailyitem")) {
                Player p = (Player) sender;
                ItemStack item = p.getInventory().getItemInMainHand();

                if (item == null || item.getType() == Material.AIR) {
                    p.sendMessage(main.config.PREFIX + main.config.MSG_ADD_DAILY_ITEM_ERROR_ITEM);
                    return true;
                }

                if (args.length == 1) {
                    p.sendMessage(main.config.PREFIX + main.config.MSG_ADD_DAILY_ITEM_ERROR_PRICE);
                    return true;
                }

                item = main.utils.setItemAsDaily(item);
                main.listItem.put(item, Double.parseDouble(args[1]));
                try {
                    ConfigUtils.migrateItemToConfig(main, item, Double.parseDouble(args[1]));
                } catch (IOException e) {
                    p.sendMessage(main.config.PREFIX + "Something went wrong while adding the item");
                    e.printStackTrace();
                }
                p.sendMessage(main.config.PREFIX + "Added item successfully");
            }
        }


        return true;
    }

}
