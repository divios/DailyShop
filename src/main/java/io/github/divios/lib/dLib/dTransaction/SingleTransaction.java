package io.github.divios.lib.dLib.dTransaction;

import com.google.common.base.Preconditions;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SingleTransaction {

    static SingleTransactionBuilder create() {
        return new SingleTransactionBuilder();
    }

    public static final class SingleTransactionBuilder {

        private dShop shop;
        private Type type;
        private Player player;
        private dItem item;
        private int amount = 1;
        private boolean inventoryAction = true;
        private Consumer<Bill> onComplete;
        private BiConsumer<dItem, TransactionError> onFail;

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

        public SingleTransactionBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public SingleTransactionBuilder withAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public SingleTransactionBuilder withInventoryAction(boolean inventoryAction) {
            this.inventoryAction = inventoryAction;
            return this;
        }

        public SingleTransactionBuilder withOnComplete(Consumer<Bill> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public SingleTransactionBuilder withOnFail(BiConsumer<dItem, TransactionError> onFail) {
            this.onFail = onFail;
            return this;
        }

        public void execute() {
            Preconditions.checkNotNull(player);
            Preconditions.checkNotNull(shop);
            Preconditions.checkNotNull(item);
            Preconditions.checkArgument(amount > 0);

            if (onComplete == null)
                onComplete = bill -> {
                };
            onComplete = onComplete.andThen(shop::computeBill);

            if (onFail == null)
                onFail = (item, err) -> {
                    err.sendErrorMsg(player);
                    player.closeInventory();
                };

            if (type == Type.BUY) new BuyTransaction(shop, player, item, amount, inventoryAction, onComplete, onFail);
            else if (type == Type.SELL)
                new SellTransaction(shop, player, item, amount, inventoryAction, onComplete, onFail);
        }
    }

    public enum Type {
        BUY,
        SELL
    }

    public enum ErrorResponse {
        CONTINUE,
        END
    }

    private static final class BuyTransaction {

        private final dShop shop;
        private final Player player;
        private final Type type = Type.BUY;
        private final dItem item;
        private final int amount;
        private final boolean inventoryAction;
        private final Consumer<Bill> onComplete;
        private final BiConsumer<dItem, TransactionError> onFail;

        private BuyTransaction(dShop shop,
                               Player player,
                               dItem item,
                               int amount,
                               boolean inventoryAction,
                               Consumer<Bill> onComplete,
                               BiConsumer<dItem, TransactionError> onFail
        ) {
            this.shop = Objects.requireNonNull(shop);
            this.player = Objects.requireNonNull(player);
            this.item = Objects.requireNonNull(item);
            this.amount = amount;
            this.inventoryAction = inventoryAction;
            this.onComplete = onComplete;
            this.onFail = onFail;

            execute();
        }

        private void execute() {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Bill.BillBuilder bill = Bill.start(player, type, item.getEcon());

            double baseCost = item.getPlayerBuyPrice(player, shop) / item.getItem().getAmount();
            double finalPrice = baseCost * amount;
            bill.withItem(item.getID(), finalPrice, amount);

            if (!item.getEcon().hasMoney(player, finalPrice)) {
                onFail.accept(item, TransactionError.noMoney);
                return;
            }

            if (item.getDStock() != null) {
                int stock;
                if ((stock = shop.getStockForItem(item.getUUID()).get(player)) <= 0
                        || stock < amount) {        // Check actual item
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

            } else {

                List<String> bundle;
                if ((bundle = item.getBundle()) != null) {
                    bundle.stream()
                            .map(shop::getItem)
                            .filter(Objects::nonNull)
                            .forEach(newDItem -> ItemUtils.give(player, newDItem.getItem()));
                } else {
                    ItemStack[] playerItems = Arrays.copyOf(player.getInventory().getContents(), 36);
                    Inventory inv = Bukkit.createInventory(null, playerItems.length);
                    inv.setContents(playerItems);
                    ItemStack itemToGive = item.getItem();

                    int aux = amount;
                    while (aux != 0) {
                        int toRemove = Math.min(64, aux);
                        itemToGive.setAmount(toRemove);

                        if (!inv.addItem(itemToGive).isEmpty()) {
                            onFail.accept(item, TransactionError.noSpace);
                            return;
                        }
                        aux -= toRemove;
                    }

                    if (inventoryAction)
                        ItemUtils.give(player, item.getItem(), amount);
                }
            }

            item.getEcon().witchDrawMoney(player, finalPrice);
            Messages.MSG_BUY_ITEM.send(player,
                    Template.of("action", Lang.BUY_ACTION_NAME.getAsString(player)),
                    Template.of("item", ItemUtils.getName(item.getItem())),
                    Template.of("amount", amount),
                    Template.of("price", PrettyPrice.pretty(finalPrice)),
                    Template.of("currency", item.getEcon().getName())
            );
            DebugLog.info("Buy transaction finished on : " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + " ms");
            onComplete.accept(bill.printBill());
        }

    }

    private static final class SellTransaction {

        private final dShop shop;
        private final Player player;
        private final Type type = Type.SELL;
        private final dItem item;
        private final int amount;
        private final boolean inventoryAction;
        private final Consumer<Bill> onComplete;
        private final BiConsumer<dItem, TransactionError> onFail;

        private SellTransaction(dShop shop,
                                Player player,
                                dItem item,
                                int amount,
                                boolean inventoryAction,
                                Consumer<Bill> onComplete,
                                BiConsumer<dItem, TransactionError> onFail
        ) {
            this.shop = Objects.requireNonNull(shop);
            this.player = Objects.requireNonNull(player);
            this.item = Objects.requireNonNull(item);
            this.amount = amount;
            this.inventoryAction = inventoryAction;
            this.onComplete = onComplete;
            this.onFail = onFail;

            execute();
        }

        private void execute() {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Bill.BillBuilder bill = Bill.start(player, type, item.getEcon());

            double baseCost = item.getPlayerSellPrice(player, shop) / item.getItem().getAmount();
            double finalPrice = baseCost * amount;
            bill.withItem(item.getID(), finalPrice, amount);

            if (ItemUtils.count(player.getInventory(), item.getItem()) < amount) {
                onFail.accept(item, TransactionError.noEnoughItems);
                return;
            }

            item.getEcon().depositMoney(player, finalPrice);

            if (inventoryAction)
                ItemUtils.remove(player.getInventory(), item.getItem(), amount);   // Is already removed

            Messages.MSG_BUY_ITEM.send(player,
                    Template.of("action", Lang.SELL_ACTION_NAME.getAsString(player)),
                    Template.of("item", ItemUtils.getName(item.getItem())),
                    Template.of("amount", amount),
                    Template.of("price", PrettyPrice.pretty(finalPrice)),
                    Template.of("currency", item.getEcon().getName())
            );
            DebugLog.info("Buy transaction finished on : " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + " ms");
            onComplete.accept(bill.printBill());
        }

    }

}
