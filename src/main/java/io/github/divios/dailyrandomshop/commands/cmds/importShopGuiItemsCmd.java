package io.github.divios.dailyrandomshop.commands.cmds;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
import io.github.divios.dailyrandomshop.utils.utils;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class importShopGuiItemsCmd implements dailyCommand {

    private final String shop;
    private final String action;

    public importShopGuiItemsCmd() {
        this.shop = "";
        this.action = "";
    }

    public importShopGuiItemsCmd(String shop, String action) {
        this.shop = shop;
        this.action = action;
    }

    @Override
    public void run(CommandSender p) {
        if (!p.hasPermission("DailyRandomShop.import.shopGuiPlus")) {
            utils.noPerms(p);
            return;
        }

        if (hooksManager.getInstance().getShopGuiPlusApi() == null) {
            p.sendMessage(conf_msg.PREFIX + utils.formatString("ShopGuiPlus isn't enable"));
            return;
        }

        if (!(action.equalsIgnoreCase("buy") ||
                action.equalsIgnoreCase("sell"))) {
            p.sendMessage(conf_msg.PREFIX + utils.formatString("&cInvalid action.&7 Supported: Sell, Buy"));
            return;
        }

        if (ShopGuiPlusApi.getShop(shop) == null) {
            p.sendMessage(conf_msg.PREFIX + utils.formatString("&cThat shop doesnt exist"));
            return;
        }

        ShopGuiPlusApi.getShop(shop).getShopItems().forEach(shopItem -> {
            if (action.equalsIgnoreCase("Buy")) {
                ItemStack item = new dailyItem(shopItem.getItem(), true)
                        .addNbt(dailyItem.dailyMetadataType.rds_price,
                                new dailyItem.dailyItemPrice(shopItem.getBuyPrice())).craft();

                if (item.getAmount() > 1) {
                    new dailyItem(item)
                            .addNbt(dailyItem.dailyMetadataType.rds_setItems,
                                    "" + item.getAmount()).getItem();
                }

                dataManager.getInstance().listDailyItems.put(item, 0D);
            }
            else {
                ItemStack item = shopItem.getItem();
                double price = utils.round(shopItem.getSellPrice() / item.getAmount(), 2);
                item.setAmount(1);
                dataManager.getInstance().listSellItems.put(shopItem.getItem(),
                        price);
            }
        });

        p.sendMessage(conf_msg.PREFIX + utils.formatString("&7Items imported successfully"));

    }

    @Override
    public void help(Player p) {
        if (p.hasPermission("DailyRandomShop.import.shopGuiPlus")) {
            p.sendMessage(utils.formatString("&6&l>> &6/rdshop importShopGui+ {shop} {action} &8 " +
                    "- &7Imports the given shop for sell/buy"));
        }
    }

    @Override
    public void command(Player p, List<String> s) {
        if (p.hasPermission("DailyRandomShop.import.shopGuiPlus")) {
            s.add("importShopGui+");
        }
    }
}
