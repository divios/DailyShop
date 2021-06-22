package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.guis.confirmGui;
import io.github.divios.dailyShop.guis.confirmIH;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class sellTransaction {

    public static void init(Player p, dItem item, dShop shop) {

        if (!item.getSellPrice().isPresent() || item.getSellPrice().get().getPrice() == -1) {
            p.sendMessage(conf_msg.PREFIX + FormatUtils.color("&7That item cannot be sell"));
            shop.openGui(p);
            return;
        }

        if (item.getConfirm_gui()) {

            if (!item.getSetItems().isPresent()) {

                confirmGui.open(p, item.getItem(), dShop.dShopT.sell,
                        (p1, item1) ->
                                initTransaction(p, new dItem(item1), shop),
                        player -> shop.getGui().open(p),
                        FormatUtils.color("&a&lConfirm Sell"),
                        conf_msg.CONFIRM_MENU_YES,
                        conf_msg.CONFIRM_MENU_NO);

            } else {
                new confirmIH(p, (p1, aBool) -> {
                    if (aBool)
                        initTransaction(p1, item, shop);
                    else
                        shop.getGui().open(p1);
                }, new ItemBuilder(item.getItem().clone()).addLore("&6Price &7: " +
                            item.getSellPrice().get()),
                        FormatUtils.color("&a&lConfirm Sell"),
                        conf_msg.CONFIRM_MENU_YES, conf_msg.CONFIRM_MENU_NO);
            }
        } else initTransaction(p, item, shop);
    }

    private static void initTransaction(Player p, dItem item, dShop shop) {

        for (String perm : item.getPerms().orElse(Collections.emptyList())) {
            if (!p.hasPermission(perm)) {
                p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_PERMS_ITEM);
                return;
            }
        }

        List<ItemStack> similarItems = Arrays.stream(p.getInventory().getContents())
                .filter(itemStack -> !utils.isEmpty(itemStack)
                        && itemStack.isSimilar(item.getRawItem()))
                .collect(Collectors.toList());


        if (similarItems.isEmpty() || similarItems.stream()
                .mapToInt(ItemStack::getAmount).sum() < item.getAmount())
            p.sendMessage(conf_msg.PREFIX +
                FormatUtils.color("&7You dont have this item or the proper amount to sell it"));

        else {

            ItemStack aux = item.getRawItem().clone();
            aux.setAmount(item.getAmount());
            for (ItemStack similarItem : similarItems) {

                if (aux.getAmount() <= similarItem.getAmount()) {
                    similarItem.setAmount(similarItem.getAmount() - aux.getAmount());
                    aux.setAmount(0);
                    p.updateInventory();

                    item.getEconomy().depositMoney(p, item.getSellPrice().get().getPrice() *
                            (item.getSetItems().isPresent() ? 1:item.getAmount()));
                    p.sendMessage(Msg.singletonMsg(conf_msg.PREFIX + conf_msg.MSG_BUY_ITEM)
                            .add("\\{action}", "sell")
                            .add("\\{amount}", "" + item.getAmount())
                            .add("\\{price}", "" + item.getSellPrice().get().getPrice() * item.getAmount())
                            .add("\\{item}", item.getDisplayName() + FormatUtils.color("&7"))
                            .add("\\{currency}", item.getEconomy().getName()).build());
                    shop.openGui(p);
                    break;
                }

                else {
                    aux.setAmount(aux.getAmount() - similarItem.getAmount());
                    similarItem.setAmount(0);
                    p.updateInventory();
                }

            }
        }

    }

}
