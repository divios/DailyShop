package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.utils.MMOUtils;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.Utils;
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

    private final static DailyShop plugin = DailyShop.get();

    public static void init(Player p, dItem item, dShop shop) {

        if (p.hasPermission("dailyrandomshop." + shop.getName() + ".negate.buy") && !p.isOp()) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALIDATE_BUY);
            //shop.openShop(p);
            return;
        }

        if (item.hasStock() && item.getStock().get(p) == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_OUT_STOCK);
            //shop.openShop(p);
            return;
        }

        if (!item.getBuyPrice().isPresent() || item.getBuyPrice().get().getPrice() == -1) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_BUY);
            //shop.openShop(p);
            return;
        }

        if (inventoryUtils.playerEmptySlots(p) <= 0) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INV_FULL);
            return;
        }

        if (!item.getEconomy().hasMoney(p, item.getBuyPrice().get().getPrice())) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_MONEY);
            //shop.openShop(p);
            return;
        }

        if (item.isConfirmGuiEnabled()) {

            buyConfirmMenu.builder()
                    .withShop(shop)
                    .withPlayer(p)
                    .withItem(item)
                    .withOnCompleteAction(integer -> initTransaction(p, item, integer, shop))
                    .withFallback(() -> shop.openShop(p))
                    .prompt();

        } else initTransaction(p, item, item.getQuantity(), shop);

    }

    private static void initTransaction(Player p, dItem item, int amount, dShop shop) {

        summary s = null;
        try {
            s = printSummary(p, item, amount, shop);

            if (!s.getEcon().hasMoney(p, s.getPrice()))
                throw new transactionExc(transactionExc.err.noMoney);

            if (Utils.inventoryFull(p.getInventory()) < s.getSlots())
                throw new transactionExc(transactionExc.err.noSpace);

        } catch (transactionExc e) {
            e.sendErrorMsg(p);
            p.closeInventory();
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
                        .withType(dLogEntry.Type.BUY)
                        .withPrice(s.getPrice())
                        .build()
        );

        if (msg.size() == 1) {
            Msg.sendMsg(p, msg.get(0));
        } else {

            if (!item.getItem().getItemMeta().getDisplayName().isEmpty())
                Msg.sendMsg(p, msg.get(0) + item.getDisplayName() + "&7" + msg.get(1));
            else
                DailyShop.get().getLocaleManager().sendMessage(p,
                        FormatUtils.color(DailyShop.get().configM.getSettingsYml().PREFIX +
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

            s.addRunnable(() -> Events.callEvent(
                    new updateItemEvent(p, item.getUid(), amount, updateItemEvent.type.NEXT_AMOUNT, shop)));

        }

        transactionExc[] err1 = {null};
        item.getBundle().ifPresent(uuids ->         // bundle check
                uuids.forEach(uuid -> {
                    IntStream.range(0, amount)
                            .forEach(value ->
                                    {
                                        try {
                                            s.concat(printSummary(p, UUID.nameUUIDFromBytes(uuid.getBytes()), shop));
                                        } catch (transactionExc e) {
                                            err1[0] = e;
                                        }
                                    }
                            );
                }));

        if (err1[0] != null) throw err1[0];

        IntStream.range(0, amount).forEach(i ->
                item.getCommands().ifPresent(strings -> strings.forEach(s1 ->
                        s.addRunnable(() ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        Msg.singletonMsg(s1).add("%player%", p.getName())
                                                .build())))));


        /// A PARTIR DE AQUI YA SOLO COMPROBAR SLOTS Y PRICE ///

        s.setPrice(item.getBuyPrice().orElse(dPrice.empty()).getPrice() * amount);

        s.setSlots(item.getMaxStackSize() == 1 ?
                s.getSlots() + amount : s.getSlots() + 1);

        if (!item.getCommands().isPresent() &&
                !item.getBundle().isPresent())
            s.addRunnable(() -> giveOptimizedItem(p, item, amount));

        return s;
    }

    private static boolean lastCheck(Player p, dShop shop, dItem item, int amount) {

        boolean result = true;

        if (item.hasStock()) {
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

    private static void giveOptimizedItem(Player player, dItem itemToGive, int amount) {

        if (MMOUtils.isMMOItemsOn() && MMOUtils.isMMOItem(itemToGive.getRawItem())) {
            if (itemToGive.getRawItem(true).equals(itemToGive.getRawItem(true))) {
                ItemUtils.give(player, itemToGive.getRawItem(), amount);
            } else
                IntStream.range(0, amount).forEach(value -> player.getInventory().addItem(ItemBuilder.of(itemToGive.getRawItem(true)).setCount(1)));
        } else ItemUtils.give(player, itemToGive.getRawItem(), amount);

    }

}