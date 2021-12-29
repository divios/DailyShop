package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.MMOUtils;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.confirmMenu.buyConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.log.dLog;
import io.github.divios.lib.dLib.log.options.dLogEntry;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class transaction {

    public static void init(Player p, dItem item, dShop shop) {

        if (p.hasPermission("dailyrandomshop." + shop.getName() + ".negate.buy") && !p.isOp()) {
            Messages.MSG_INVALIDATE_BUY.send(p);
            //shop.openShop(p);
            return;
        }

        if (item.hasStock() && item.getStock().get(p) == -1) {
            Messages.MSG_OUT_STOCK.send(p);
            //shop.openShop(p);
            return;
        }

        if (!item.getBuyPrice().isPresent() || item.getBuyPrice().get().getPrice() == -1) {
            Messages.MSG_INVALID_BUY.send(p);
            //shop.openShop(p);
            return;
        }

        if (inventoryUtils.playerEmptySlots(p) <= 0) {
            Messages.MSG_INV_FULL.send(p);
            return;
        }

        if (!item.getEconomy().hasMoney(p, item.getBuyPrice().get().getPriceForPlayer(p, shop, item.getID(), priceModifier.type.BUY))) {
            Messages.MSG_NOT_MONEY.send(p);
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

        summary s;
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
            Messages.MSG_INVALID_OPERATION.send(p);
            shop.openShop(p);
            return;
        }

        s.getEcon().witchDrawMoney(p, s.getPrice());

        s.getRunnables().forEach(Runnable::run);

        shop.openShop(p);

        Messages.MSG_BUY_ITEM.send(p,
                Template.of("action", Lang.BUY_ACTION_NAME.getAsString(p)),
                Template.of("item", item.getDisplayName()),
                Template.of("amount", amount),
                Template.of("price", PriceWrapper.format(s.getPrice())),
                Template.of("currency", s.getEcon().getName())
        );

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

            int stock = dStock.searchStock(p, shop, item.getUid());

            if (stock < amount) {
                throw new transactionExc(transactionExc.err.noStock);
            }

            s.addRunnable(() -> Events.callEvent(
                    new updateItemEvent(p, item.getUid(), amount, updateItemEvent.type.NEXT_AMOUNT, shop)));

        }

        transactionExc[] err1 = {null};
        item.getBundle().ifPresent(uuids ->         // bundle check
                uuids.forEach(uuid ->

                        IntStream.range(0, amount)
                                .forEach(value ->
                                        {
                                            try {
                                                s.concat(printSummary(p, UUID.nameUUIDFromBytes(uuid.getBytes()), shop));
                                            } catch (transactionExc e) {
                                                err1[0] = e;
                                            }
                                        }
                                )));

        if (err1[0] != null) throw err1[0];

        IntStream.range(0, amount).forEach(i ->
                item.getCommands().ifPresent(strings -> strings.forEach(s1 ->
                                s.addRunnable(() ->
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                                Utils.JTEXT_PARSER
                                                        .withTag("%", "%")
                                                        .withTemplate("player", p.getName())
                                                        .parse(s1)
                                        )
                                )
                        )
                )
        );


        /// A PARTIR DE AQUI YA SOLO COMPROBAR SLOTS Y PRICE ///

        s.setPrice(item.getBuyPrice().orElse(dPrice.empty()).getPriceForPlayer(p, shop, item.getID(), priceModifier.type.BUY) * amount);

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
            int callback = dStock.searchStock(p, shop, item.getUid());
            result = callback > 0 && callback >= amount;
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