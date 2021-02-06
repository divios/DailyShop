package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.settings.sellGuiSettings;
import io.github.divios.dailyrandomshop.listeners.dynamicItemListener;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.entity.Player;

import java.util.List;

public class addSellItemCmd implements dailyCommand{
    @Override
    public void run(Player p) {
        if (!p.hasPermission("DailyRandomShop.addSellItem")) {
            utils.noPerms(p);
            return;
        }
        new dynamicItemListener(p, (player, itemStack) -> {
            if (utils.hasItem(itemStack)) {
                p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_ITEM_ALREADY_ON_SALE);
                return;
            }
            dataManager.getInstance().listSellItems.put(itemStack, -1D);
            sellGuiSettings.openInventory(p);
        });
    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.addSellItem")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop addSellItem &8 " +
                    "- &7Click with an item to add it to the sell list"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.addSellItem")) {
            s.add("addSellItem");
        }
    }
}
