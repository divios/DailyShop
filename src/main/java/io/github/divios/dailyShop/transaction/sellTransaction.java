package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.guis.confirmGuiSell;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class sellTransaction {

    private static final DailyShop plugin = DailyShop.getInstance();

    public static void init(Player p, dItem item, dShop shop) {

        if (p.hasPermission("dailyrandomshop." + shop.getName() + ".negate.sell") && !p.isOp()) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALIDATE_SELL);
            return;
        }

        if (!item.getSellPrice().isPresent() || item.getSellPrice().get().getPrice() == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_SELL);
            shop.openShop(p);
            return;
        }

        if (item.getConfirm_gui()) {

            if (!item.getSetItems().isPresent()) {

                confirmGuiSell.open(shop, p, item, dShop.dShopT.sell,
                        (item1, amount) -> {
                            if (!shop.getItem(item.getUid()).isPresent()) {     // Last check
                                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_OPERATION);
                                return;
                            }
                            initTransaction(p, item, amount, shop);
                        },
                        player -> shop.openShop(p),
                        plugin.configM.getLangYml().CONFIRM_GUI_SELL_NAME,
                        plugin.configM.getLangYml().CONFIRM_GUI_YES,
                        plugin.configM.getLangYml().CONFIRM_GUI_NO);

            } else {

                confirmIH.builder()
                        .withPlayer(p)
                        .withItem(
                                ItemBuilder.of(item.getItem().clone())
                                        .addLore(
                                                Msg.msgList(plugin.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                                                        .add("\\{price}",
                                                        String.valueOf(item.getSellPrice().get().getPrice())).build()))
                        .withAction(aBoolean -> {
                            if (aBoolean) {
                                if (!shop.getItem(item.getUid()).isPresent()) {     // Last check
                                    Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_OPERATION);
                                    return;
                                }
                                initTransaction(p, item, item.getQuantity(), shop);
                            }
                            else
                                shop.openShop(p);
                        })
                        .withTitle(plugin.configM.getLangYml().CONFIRM_GUI_SELL_NAME)
                        .withConfirmLore(plugin.configM.getLangYml().CONFIRM_GUI_YES, plugin.configM.getLangYml().CONFIRM_GUI_YES_LORE)
                        .withCancelLore(plugin.configM.getLangYml().CONFIRM_GUI_NO, plugin.configM.getLangYml().CONFIRM_GUI_NO_LORE)
                        .prompt();
            }
        } else initTransaction(p, item, item.getQuantity(), shop);
    }

    private static void initTransaction(Player p, dItem item, int amount, dShop shop) {

        for (String perm : item.getPermsSell().orElse(Collections.emptyList())) {
            if (!p.hasPermission(perm)) {
                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_PERMS_ITEM);
                return;
            }
        }

        int removed = ItemUtils.count(p.getInventory(), item.getRawItem(), confirmGuiSell.getComparison(item.getItem()));

        if (removed < amount) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_ITEMS);
        }


        else {

            ItemUtils.remove(p.getInventory(), item.getRawItem(), amount, confirmGuiSell.getComparison(item.getItem()));
            p.updateInventory();

            item.getEconomy().depositMoney(p, item.getSellPrice().get().getPrice() *
                    (item.getSetItems().isPresent() ? 1 : amount));

            List<String> msg = Arrays.asList(Msg.singletonMsg(plugin.configM.getLangYml().MSG_BUY_ITEM)
                    .add("\\{action}", plugin.configM.getLangYml().MSG_SELL_ACTION)
                    .add("\\{amount}", "" + amount)
                    .add("\\{price}", "" + PriceWrapper.format(item.getSellPrice().get().getPrice() * amount))
                    .add("\\{currency}", item.getEconomy().getName()).build().split("\\{item}"));

            if (msg.size() == 1) {
                Msg.sendMsg(p, msg.get(0));
            } else {

                if (!item.getItem().getItemMeta().getDisplayName().isEmpty())
                    Msg.sendMsg(p, msg.get(0) + item.getDisplayName() + "&7" + msg.get(1));
                else
                    DailyShop.getInstance().getLocaleManager().sendMessage(p,
                            FormatUtils.color(DailyShop.getInstance().configM.getSettingsYml().PREFIX +
                                    msg.get(0) + "<item>" + "&7" + msg.get(1)), item.getItem().getType(), (short) 0, null);
            }
                shop.openShop(p);


        }

    }

}
