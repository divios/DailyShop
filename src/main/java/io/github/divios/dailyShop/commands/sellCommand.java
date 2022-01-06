package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.jcommands.JCommand;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.inventory.ItemStack;

public class sellCommand {

    public JCommand getCommand() {
        return JCommand.create("sell")
                .assertPermission("DailyRandomShop.sell")
                .assertUsage("/ds sell")
                .withSubcommands(getSellAllCommand(), getSellHandCommand(), getSellGuiCommand());
    }

    private JCommand getSellAllCommand() {
        return new JCommand("all")
                .assertPermission("DailyRandomShop.sell.all")
                .assertUsage(FormatUtils.color("&8- &6/rdshop open [shop] [player] &8- &7Opens a gui for yourself or for the given player"))
                .executesPlayer((player, valueMap) -> {
                    DailyShop.get().getShopsManager().getShops().forEach(shop -> {

                    });
                });
    }

    private JCommand getSellHandCommand() {
        return new JCommand("hand")
                .assertPermission("DailyRandomShop.sell.hand")
                .assertUsage(FormatUtils.color("&8- &6/rdshop open [shop] [player] &8- &7Opens a gui for yourself or for the given player"))
                .executesPlayer((player, valueMap) -> {
                    ItemStack itemToSell = player.getItemInHand();
                    //dItem itemOnShop;

                    for (dShop shop : DailyShop.get().getShopsManager().getShops()) {
                        boolean found = false;
                        /*for (dItem shopItem : shop.getItems()) {
                            if (shopItem.getRealItem().isSimilar(itemToSell)) {
                                found = true;
                                itemOnShop = shopItem.clone();
                                break;
                            }
                        } */
                        if (found) break;
                    }

                    // initTransaction

                });
    }

    private JCommand getSellGuiCommand() {
        return new JCommand("gui")
                .assertPermission("DailyRandomShop.sell.gui")
                .assertUsage(FormatUtils.color("&8- &6/rdshop open [shop] [player] &8- &7Opens a gui for yourself or for the given player"))
                .executesPlayer((player, valueMap) -> {
                    // TODO
                });
    }

}
