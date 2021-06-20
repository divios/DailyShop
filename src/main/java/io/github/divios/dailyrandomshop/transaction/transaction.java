package io.github.divios.dailyrandomshop.transaction;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.vault;
import io.github.divios.dailyrandomshop.events.updateItemEvent;
import io.github.divios.dailyrandomshop.guis.confirmGui;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dPrice;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.stream.IntStream;

public class transaction {

    private final static DRShop main = DRShop.getInstance();

    public static void init(Player p, dItem item, dShop shop) {
        if (item.getConfirm_gui()) {

            if (!item.getStock().isPresent() &&
                    !item.getSetItems().isPresent()) {

                confirmGui.open(p, item.getItem(),
                        (p1, item1) -> {
                            transaction.initTransaction(p, new dItem(item1), shop);
                        }, player -> shop.getGui().open(p));

            } else {
                new confirmIH(p, (p1, aBool) -> {
                    if (aBool)
                        transaction.initTransaction(p1, item, shop);
                    else
                        shop.getGui().open(p1);
                }, item.getItem(), "", "", "");
            }
        } else initTransaction(p, item, shop);

    }

    private static void initTransaction(Player p, dItem item, dShop shop) {

        summary s = null;
        try {
            s = printSummary(p, item, shop);

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

        p.sendMessage(Msg.singletonMsg(conf_msg.PREFIX + conf_msg.MSG_BUY_ITEM)
                .add("\\{action}", "bought")
                .add("\\{amount}", "" + item.getAmount())
                .add("\\{price}", "" + s.getPrice())
                .add("\\{item}", item.getDisplayName() + FormatUtils.color("&7"))
                .add("\\{currency}", item.getEconomy().getName()).build());

        shop.getGui().open(p);

    }

    private static summary printSummary(Player p, UUID uid, dShop shop) throws transactionExc {
        return printSummary(p, shop.getItem(uid).orElse(dItem.AIR()), shop);
    }

    private static summary printSummary(Player p, dItem item, dShop shop) throws transactionExc {

        summary s = new summary();

        if (item.isAIR())
            return s;

        s.setEcon(new vault());
        //s.setEcon(getEconomyStrategy(p, item)); // get item economy //TODO

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
                    IntStream.range(0, item.getAmount())
                            .forEach(value ->
                                    {
                                        try {
                                            s.concat(printSummary(p, uuid, shop));
                                        } catch (transactionExc e) {
                                            err1[0] = e;
                                        }
                                    }
                            );

                    s.setPrice(shop.getType().equals(dShop.dShopT.sell) ?
                            item.getSellPrice().orElse(dPrice.empty()).getPrice() :
                            item.getBuyPrice().orElse(dPrice.empty()).getPrice()); //todo
                }));

        if (err1[0] != null) throw err1[0];

        IntStream.range(0, item.getAmount()).forEach(i ->
                item.getCommands().ifPresent(strings -> strings.forEach(s1 ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                Msg.singletonMsg(s1).add("%player%", p.getName())
                                        .build()))));


        /// A PARTIR DE AQUI YA SOLO COMPROBAR SLOTS Y PRICE ///

        s.setPrice(s.getPrice() + (item.getSetItems().isPresent() ?   // Todo: distinct between buy and sell price
                item.getBuyPrice().orElse(dPrice.empty()).getPrice() :
                item.getBuyPrice().orElse(dPrice.empty()).getPrice() * item.getAmount()));


        s.setSlots(item.getMaxStackSize() == 1 ?
                s.getSlots() + item.getAmount() : s.getSlots() + 1);

        if (!item.getCommands().isPresent() &&
                !item.getBundle().isPresent())
            s.addRunnable(() -> {
                ItemStack aux = item.getRawItem();
                aux.setAmount(1);
                IntStream.range(0, item.getAmount()).
                        forEach(i -> p.getInventory().addItem(aux));
            });

        return s;
    }

    /*
    private static economy getEconomyStrategy(Player p, ItemStack item) {
        AbstractMap.SimpleEntry<String, String> e =
                (AbstractMap.SimpleEntry<String, String>) new dailyItem(item).
                        getMetadata(dailyItem.dMeta.rds_tEcon);

        economy econ = new vault();

        if (e == null) return econ;

        else if (e.getKey().equals(econTypes.gemsEconomy.name())) {
            if (hooksManager.getInstance().getGemsEcon() != null &&
                    hooksManager.getInstance().getGemsEcon().plugin
                            .getCurrencyManager().currencyExist(e.getValue()))
                econ = new gemEcon(e.getValue());
        }
        else if (e.getKey().equals(econTypes.tokenEnchants.name())) {
            econ = new tokenEnchantsE();
        }
        else if (e.getKey().equals(econTypes.tokenManager.name())) {
            econ = new tokenManagerE();
        }

        else if (e.getKey().equals(econTypes.MPoints.name())) {
            econ = new MPointsE(e.getValue());
        }

        else if (e.getKey().equals(econTypes.playerPoints.name())) {
            econ = new playerPointsE();
        }

        try {  //try if exits
            econ.hasMoney(p, 0D);
        } catch (Exception err) { econ = new vault(); }

        return econ;
    }

    private static String getEconName(ItemStack item) {
        String currency;
        try {
            currency = ((AbstractMap.SimpleEntry<String, String>) new dailyItem(item).
                    getMetadata(dailyItem.dMeta.rds_tEcon)).getKey();
        } catch (Exception e) { currency = conf_msg.VAULT_CUSTOM_NAME; }

        return currency;
    } */

}