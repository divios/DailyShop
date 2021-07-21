package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.guis.confirmGui;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

import java.util.Collections;

public class sellTransaction {

    private static final DailyShop plugin = DailyShop.getInstance();

    public static void init(Player p, dItem item, dShop shop) {

        if (!item.getSellPrice().isPresent() || item.getSellPrice().get().getPrice() == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_SELL);
            shop.openGui(p);
            return;
        }

        if (item.getConfirm_gui()) {

            if (!item.getSetItems().isPresent()) {

                confirmGui.open(p, item.getItem(), dShop.dShopT.sell,
                        (item1, amount) ->
                                initTransaction(p, new dItem(item1), amount, shop),
                        player -> shop.getGui().open(p),
                        plugin.configM.getLangYml().CONFIRM_GUI_SELL_NAME,
                        plugin.configM.getLangYml().CONFIRM_GUI_YES,
                        plugin.configM.getLangYml().CONFIRM_GUI_NO);

            } else {

                confirmIH.builder()
                        .withPlayer(p)
                        .withItem(
                                new ItemBuilder(item.getItem().clone())
                                        .addLore(
                                                Msg.singletonMsg(plugin.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                                                        .add("\\{price}",
                                                        String.valueOf(item.getSellPrice().get().getPrice())).build()))
                        .withAction(aBoolean -> {
                            if (aBoolean)
                                initTransaction(p, item, item.getAmount(), shop);
                            else
                                shop.getGui().open(p);
                        })
                        .withTitle(plugin.configM.getLangYml().CONFIRM_GUI_SELL_NAME)
                        .withConfirmLore(plugin.configM.getLangYml().CONFIRM_GUI_YES, plugin.configM.getLangYml().CONFIRM_GUI_YES_LORE)
                        .withCancelLore(plugin.configM.getLangYml().CONFIRM_GUI_NO, plugin.configM.getLangYml().CONFIRM_GUI_NO_LORE)
                        .prompt();
            }
        } else initTransaction(p, item, item.getAmount(), shop);
    }

    private static void initTransaction(Player p, dItem item, int amount, dShop shop) {

        for (String perm : item.getPerms().orElse(Collections.emptyList())) {
            if (!p.hasPermission(perm)) {
                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_PERMS_ITEM);
                return;
            }
        }

        int removed = ItemUtils.count(p.getInventory(), item.getRawItem());

        if (removed < amount)
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_ITEMS);

        else {

            ItemUtils.remove(p.getInventory(), item.getRawItem(), amount);
            p.updateInventory();

            item.getEconomy().depositMoney(p, item.getSellPrice().get().getPrice() *
                    (item.getSetItems().isPresent() ? 1 : amount));

            Msg.sendMsg(p, Msg.singletonMsg(plugin.configM.getLangYml().MSG_BUY_ITEM)
                    .add("\\{action}", "sell")
                    .add("\\{amount}", "" + amount)
                    .add("\\{price}", "" + item.getSellPrice().get().getPrice() * amount)
                    .add("\\{item}", item.getDisplayName() + FormatUtils.color("&7"))
                    .add("\\{currency}", item.getEconomy().getName()).build());
            shop.openGui(p);


        }

    }

}
