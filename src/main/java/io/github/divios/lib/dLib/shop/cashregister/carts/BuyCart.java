package io.github.divios.lib.dLib.shop.cashregister.carts;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.events.checkoutEvent;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.confirmMenu.BuyConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.cashregister.MultiplePreconditions.BuyPostconditions;
import io.github.divios.lib.dLib.shop.cashregister.MultiplePreconditions.BuyPreconditions;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class BuyCart extends Cart {

    private static final BuyPreconditions preconditions = new BuyPreconditions();
    private static final BuyPostconditions postConditions = new BuyPostconditions();

    private double price;
    private int amount;

    public BuyCart(dShop shop, Player p, dItem item) {
        super(shop, p, item);
    }

    @Override
    public void addToCart() {
        preconditions.validate(shop, p, item, item.getItem().getAmount());
    }

    @Override
    public void confirmOperation() {
        if (!item.isConfirmGui()) {
            checkOut(item.getItem().getAmount());
            return;
        }

        BuyConfirmMenu.builder()
                .withPlayer(p)
                .withShop(shop)
                .withItem(item)
                .withOnCompleteAction(this::checkOut)
                .withFallback(() -> shop.openShop(p))
                .prompt();
    }

    @Override
    public void checkOut(int amount) {
        long start = System.currentTimeMillis();
        this.amount = amount;

        postConditions.validate(shop, p, item, amount);
        price = item.getPlayerFloorBuyPrice(p, shop) * amount;

        item.getEcon().witchDrawMoney(p, price);
        executeAction();

        DebugLog.info("Buy transaction finished on : " + (System.currentTimeMillis() - start) + " ms");

        Messages.MSG_BUY_ITEM.send(p,
                Template.of("action", Lang.BUY_ACTION_NAME.getAsString(p)),
                Template.of("item", ItemUtils.getName(item.getItem())),
                Template.of("amount", amount),
                Template.of("price", PrettyPrice.pretty(price)),
                Template.of("currency", item.getEcon().getName())
        );

        Events.callEvent(new checkoutEvent(shop, Transactions.Type.BUY, p, item, amount));

        shop.openShop(p);
    }

    private void executeAction() {
        if (item.hasCommands())
            runCommands();
        else
            giveItem();
    }

    private void runCommands() {
        item.getCommands()
                .forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                Utils.JTEXT_PARSER
                                        .withTag("%", "%")
                                        .withTemplate("player", p.getName())
                                        .withTemplate("amount", String.valueOf(amount))
                                        .withTemplate("price", String.valueOf(price))
                                        .parse(s)
                        )
                );
    }

    private void giveItem() {
        List<String> bundle;
        if ((bundle = item.getBundle()) != null) {
            bundle.stream()
                    .map(shop::getItem)
                    .filter(Objects::nonNull)
                    .forEach(newDItem -> ItemUtils.give(p, newDItem.getItem()));
        } else
            ItemUtils.give(p, item.getItem(), amount);

    }

}
