package io.github.divios.lib.dLib.dTransaction;

import com.google.common.base.Preconditions;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.newDItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SingleTransaction {

    private final dShop shop;
    private final Type type;
    private final Player player;
    private final newDItem item;
    private final int amount;
    private final Consumer<Bill> onComplete;
    private final BiConsumer<newDItem, TransactionError> onFail;

    static SingleTransactionBuilder create() {
        return new SingleTransactionBuilder();
    }

    private SingleTransaction(dShop shop,
                              Type type,
                              Player player,
                              newDItem item,
                              int amount,
                              Consumer<Bill> onComplete,
                              BiConsumer<newDItem, TransactionError> onFail
    ) {
        this.shop = shop;
        this.type = type;
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.onComplete = onComplete;
        this.onFail = onFail;

        startTransaction();
    }


    private void startTransaction() {

        Bill.BillBuilder bill = Bill.start(player, type, item.getEcon());

        double baseCost = (type == Type.BUY) ?
                item.getPlayerBuyPrice(player, shop)
                : item.getPlayerSellPrice(player, shop);
        baseCost = baseCost / item.getItem().getAmount();

        double finalPrice = baseCost * amount;
        bill.withItem(item.getID(), finalPrice, amount);

        if (!item.getEcon().hasMoney(player, finalPrice)) {
            onFail.accept(item, TransactionError.noMoney);
            return;
        }

        if (item.getDStock() != null) {
            if (item.getPlayerStock(player) <= 0) {
                onFail.accept(item, TransactionError.noStock);
                return;
            }
        }

        List<String> commands;
        if ((commands = item.getCommands()) != null) {
            commands.forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            Utils.JTEXT_PARSER
                                    .withTag("%", "%")
                                    .withTemplate("player", player.getName())
                                    .parse(s)
                    )
            );
            onComplete.accept(bill.printBill());
            return;
        }

        ItemStack[] playerItems = Arrays.copyOf(player.getInventory().getContents(), 36);
        Inventory inv = Bukkit.createInventory(null, playerItems.length);
        ItemStack itemToGive = item.getItem();

        int aux = amount;
        while (aux != 0) {
            int toRemove = (aux / 64) < 1 ? amount : 64;
            itemToGive.setAmount(toRemove);

            if (!inv.addItem(itemToGive).isEmpty()) {
                onFail.accept(item, TransactionError.noSpace);
                return;
            }
            aux -= toRemove;
        }

        // TODO bundle
        item.getEcon().witchDrawMoney(player, finalPrice);

        if (type == Type.BUY)
            ItemUtils.give(player, item.getItem(), amount);
        else
            ItemUtils.remove(player.getInventory(), item.getItem(), amount);

        Messages.MSG_BUY_ITEM.send(player,
                Template.of("action", Lang.BUY_ACTION_NAME.getAsString(player)),
                Template.of("item", ItemUtils.getName(itemToGive)),
                Template.of("amount", amount),
                Template.of("price", finalPrice),
                Template.of("currency", item.getEcon().getName())
        );
        onComplete.accept(bill.printBill());

    }

    public enum Type {
        BUY,
        SELL
    }

    public enum ErrorResponse {
        CONTINUE,
        END
    }


    public static final class SingleTransactionBuilder {

        private dShop shop;
        private Type type;
        private Player player;
        private newDItem item;
        private int amount = 1;
        private Consumer<Bill> onComplete;
        private BiConsumer<newDItem, TransactionError> onFail;

        private SingleTransactionBuilder() {
        }

        public static SingleTransactionBuilder aSingleTransaction() {
            return new SingleTransactionBuilder();
        }

        public SingleTransactionBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public SingleTransactionBuilder withType(Type type) {
            this.type = type;
            return this;
        }

        public SingleTransactionBuilder withPlayer(Player player) {
            this.player = player;
            return this;
        }

        public SingleTransactionBuilder withItem(newDItem item) {
            this.item = item;
            return this;
        }

        public SingleTransactionBuilder withAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public SingleTransactionBuilder withOnComplete(Consumer<Bill> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public SingleTransactionBuilder withOnFail(BiConsumer<newDItem, TransactionError> onFail) {
            this.onFail = onFail;
            return this;
        }

        public void execute() {
            Preconditions.checkNotNull(player);
            Preconditions.checkNotNull(shop);
            Preconditions.checkNotNull(item);
            Preconditions.checkArgument(amount > 0);

            if (onComplete == null)
                onComplete = bill -> shop.computeBill(bill);

            if (onFail == null)
                onFail = (item, err) -> {
                    err.sendErrorMsg(player);
                    player.closeInventory();
                };

            new SingleTransaction(shop, type, player, item, amount, onComplete, onFail);
        }
    }
}
