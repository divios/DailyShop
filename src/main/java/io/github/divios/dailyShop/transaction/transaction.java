package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.Schedulers;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.guis.confirmGui;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.stream.IntStream;

public class transaction {

    private final static DailyShop plugin = DailyShop.getInstance();

    public static void init(Player p, dItem item, dShop shop) {

        if (item.getStock().orElse(0) == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_OUT_STOCK);
            shop.openGui(p);
            return;
        }

        if (!item.getBuyPrice().isPresent() || item.getBuyPrice().get().getPrice() == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_BUY);
            shop.openGui(p);
            return;
        }

        if (item.getConfirm_gui()) {

            if (!item.getStock().isPresent() &&
                    !item.getSetItems().isPresent()) {

                confirmGui.open(p, item.getItem(), dShop.dShopT.buy,
                        (item1, amount) -> {
                            transaction.initTransaction(p, new dItem(item1), amount, shop);
                        }, player -> shop.open(p),
                        plugin.configM.getLangYml().CONFIRM_GUI_BUY_NAME,
                        plugin.configM.getLangYml().CONFIRM_GUI_YES,
                        plugin.configM.getLangYml().CONFIRM_GUI_NO);

            } else {

                confirmIH.builder()
                        .withPlayer(p)
                        .withAction(aBoolean -> {
                            if (aBoolean)
                                transaction.initTransaction(p, item, item.getAmount(), shop);
                            else
                                shop.open(p);
                        })
                        .withItem(
                                ItemBuilder.of(item.getItem().clone()).addLore(
                                        Msg.singletonMsg(plugin.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                                                .add("\\{price}",
                                                        String.valueOf(item.getSellPrice().get().getPrice())).build()
                                ))
                        .withTitle(plugin.configM.getLangYml().CONFIRM_GUI_BUY_NAME)
                        .withConfirmLore(plugin.configM.getLangYml().CONFIRM_GUI_YES, plugin.configM.getLangYml().CONFIRM_GUI_YES_LORE)
                        .withCancelLore(plugin.configM.getLangYml().CONFIRM_GUI_NO, plugin.configM.getLangYml().CONFIRM_GUI_NO_LORE)
                        .prompt();
            }
        } else initTransaction(p, item, 1, shop);

    }

    private static void initTransaction(Player p, dItem item, int amount, dShop shop) {

        summary s = null;
        try {
            s = printSummary(p, item, amount, shop);

            if (!s.getEcon().hasMoney(p, s.getPrice()))
                throw new transactionExc(transactionExc.err.noMoney);

            if (utils.inventoryFull(p.getInventory()) < s.getSlots())
                throw new transactionExc(transactionExc.err.noSpace);

        } catch (transactionExc e) {
            e.sendErrorMsg(p);
            return;
        }

        s.getEcon().witchDrawMoney(p, s.getPrice());

        s.getRunnables().forEach(Runnable::run);

        Msg.sendMsg(p, Msg.singletonMsg(plugin.configM.getLangYml().MSG_BUY_ITEM)
                .add("\\{action}", "bought")
                .add("\\{amount}", "" + amount)
                .add("\\{price}", "" + s.getPrice())
                .add("\\{item}", item.getDisplayName() + FormatUtils.color("&7"))
                .add("\\{currency}", s.getEcon().getName()).build());

        Schedulers.sync().runLater(() -> shop.open(p), 1L);

    }

    private static summary printSummary(Player p, UUID uid, dShop shop) throws transactionExc {
        return printSummary(p, shop.getItem(uid).orElse(dItem.AIR()),
                shop.getItem(uid).orElse(dItem.AIR()).getAmount(), shop);
    }

    private static summary printSummary(Player p, dItem item, int amount, dShop shop) throws transactionExc {

        summary s = new summary();

        if (item.isAIR())
            return s;

        s.setEcon(item.getEconomy());

        boolean[] err = {false};
        item.getPerms().ifPresent(perms -> perms        // perms check
                .forEach(s1 -> {
                    if (err[0]) return;

                    if (!p.hasPermission(s1))
                        err[0] = true;
                }));

        if (err[0]) throw new transactionExc(transactionExc.err.noPerms);

        item.getStock().ifPresent(integer -> {
            s.addRunnable(() -> Bukkit.getPluginManager().callEvent(
                    new updateItemEvent(item, updateItemEvent.updatetype.NEXT_AMOUNT, shop)));

            item.setAmount(1);
        });

        transactionExc[] err1 = {null};
        item.getBundle().ifPresent(uuids ->         // bundle check
                uuids.forEach(uuid -> {
                    IntStream.range(0, amount)
                            .forEach(value ->
                                    {
                                        try {
                                            s.concat(printSummary(p, uuid, shop));
                                        } catch (transactionExc e) {
                                            err1[0] = e;
                                        }
                                    }
                            );

                    s.setPrice(s.getPrice() + item.getBuyPrice().orElse(dPrice.empty()).getPrice());
                }));

        if (err1[0] != null) throw err1[0];

        IntStream.range(0, amount).forEach(i ->
                item.getCommands().ifPresent(strings -> strings.forEach(s1 ->
                        s.addRunnable(() ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        Msg.singletonMsg(s1).add("%player%", p.getName())
                                                .build())))));


        /// A PARTIR DE AQUI YA SOLO COMPROBAR SLOTS Y PRICE ///

        s.setPrice(s.getPrice() + (item.getSetItems().isPresent() ?
                item.getBuyPrice().orElse(dPrice.empty()).getPrice() :
                item.getBuyPrice().orElse(dPrice.empty()).getPrice() * amount));


        s.setSlots(item.getMaxStackSize() == 1 ?
                s.getSlots() + amount : s.getSlots() + 1);

        if (!item.getCommands().isPresent() &&
                !item.getBundle().isPresent())
            s.addRunnable(() -> {
                ItemStack aux = item.getRawItem();
                aux.setAmount(1);
                IntStream.range(0, amount).
                        forEach(i -> p.getInventory().addItem(aux));
            });

        return s;
    }

}