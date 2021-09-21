package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.Schedulers;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.confirmMenu.buyConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.log.dLog;
import io.github.divios.lib.dLib.log.options.dLogEntry;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class transaction {

    private final static DailyShop plugin = DailyShop.getInstance();

    public static void init(Player p, dItem item, dShop shop) {

        if (p.hasPermission("dailyrandomshop." + shop.getName() + ".negate.buy") && !p.isOp()) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALIDATE_BUY);
            shop.openShop(p);
            return;
        }

        if (item.hasStock() && item.getStock().get(p) == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_OUT_STOCK);
            shop.openShop(p);
            return;
        }

        if (!item.getBuyPrice().isPresent() || item.getBuyPrice().get().getPrice() == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_BUY);
            shop.openShop(p);
            return;
        }

        if (item.getConfirm_gui()) {

            if (!item.getSetItems().isPresent()) {

                buyConfirmMenu.builder()
                        .withShop(shop)
                        .withPlayer(p)
                        .withItem(item)
                        .withOnCompleteAction(integer -> initTransaction(p, item, integer, shop))
                        .withFallback(() -> shop.openShop(p))
                        .prompt();

            } else {

                confirmIH.builder()
                        .withPlayer(p)
                        .withAction(aBoolean -> {
                            if (aBoolean) {
                                transaction.initTransaction(p, item, item.getQuantity(), shop);
                            }
                            else
                                shop.openShop(p);
                        })
                        .withItem(
                                ItemBuilder.of(item.getItem().clone()).addLore(
                                        Msg.msgList(plugin.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
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

        if (!lastCheck(p, shop, item, amount)
        ) {     // Last check
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_OPERATION);
            shop.openShop(p);
            return;
        }

        s.getEcon().witchDrawMoney(p, s.getPrice());

        s.getRunnables().forEach(Runnable::run);

        List<String> msg = Arrays.asList(Msg.singletonMsg(plugin.configM.getLangYml().MSG_BUY_ITEM)
                .add("\\{action}", plugin.configM.getLangYml().MSG_BUY_ACTION)
                .add("\\{amount}", "" + amount)
                .add("\\{price}", "" + PriceWrapper.format(s.getPrice()))
                .add("\\{currency}", s.getEcon().getName()).build().split("\\{item}"));

        dLog.log(
                dLogEntry.builder()
                        .withPlayer(p)
                        .withShopID(shop.getName())
                        .withItemUUID(item.getUid())
                        .withRawItem(item.getRawItem())
                        .withQuantity(amount)
                        .withType(dShop.dShopT.buy)
                        .withPrice(s.getPrice())
                        .build()
        );

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

        Schedulers.sync().run(() -> shop.openShop(p));

    }

    private static summary printSummary(Player p, UUID uid, dShop shop) throws transactionExc {
        return printSummary(p, shop.getItem(uid).orElse(dItem.AIR()),
                shop.getItem(uid).orElse(dItem.AIR()).getQuantity(), shop);
    }

    private static summary printSummary(Player p, dItem item, int amount, dShop shop) throws transactionExc {

        summary s = new summary();

        if (item.isAIR())
            return s;

        s.setEcon(item.getEconomy());

        boolean[] err = {false};
        item.getPermsBuy().ifPresent(perms -> perms        // perms check
                .forEach(s1 -> {
                    if (err[0]) return;

                    if (!p.hasPermission(s1))
                        err[0] = true;
                }));

        if (err[0]) throw new transactionExc(transactionExc.err.noPerms);

        if (item.hasStock()) {                          // Stock check

            int stock = 0;
            try {
                stock = dStock.searchStock(p, shop, item.getUid()).get(4, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.severe("There was a problem searching for a stock for the player " + p.getDisplayName());
                e.printStackTrace();
            }

            if (stock < amount) {
                throw new transactionExc(transactionExc.err.noStock);
            }

            s.addRunnable(() -> Bukkit.getPluginManager().callEvent(
                    new updateItemEvent(p, item, amount, updateItemEvent.updatetype.NEXT_AMOUNT, shop)));

        }

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
            s.addRunnable(() -> IntStream.range(0, amount).forEach(i -> p.getInventory().addItem(ItemBuilder.of(item.getRawItem()).setCount(1))));

        return s;
    }

    private static boolean lastCheck(Player p, dShop shop, dItem item, int amount) {

        boolean result = true;

        if (item.hasStock())  {
            CompletableFuture<Integer> callback = dStock.searchStock(p, shop, item.getUid());
            try {
                int c = callback.get(2, TimeUnit.SECONDS);
                result = c > 0 && c >= amount;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                result = false;
            }
        }
        return shop.getItem(item.getUid()).isPresent() && result;

    }

}