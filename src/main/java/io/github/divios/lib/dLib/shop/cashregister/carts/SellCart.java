package io.github.divios.lib.dLib.shop.cashregister.carts;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.events.checkoutEvent;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.confirmMenu.SellConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.cashregister.MultiplePreconditions.SellPreconditions;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class SellCart extends Cart {

    private static final SellPreconditions conditions = new SellPreconditions();

    public SellCart(dShop shop, Player p, dItem item) {
        super(shop, p, item);
    }

    @Override
    public void addToCart() {
        conditions.validate(shop, p, item, item.getItem().getAmount());
    }

    @Override
    public void confirmOperation() {
        if (!item.isConfirmGui()) {
            checkOut(item.getItem().getAmount());
            return;
        }

        SellConfirmMenu.builder()
                .withPlayer(p)
                .withShop(shop)
                .withItem(item)
                .withOnCompleteAction(this::checkOut)
                .withFallback(() -> shop.openShop(p))
                .prompt();
    }

    @Override
    public void checkOut(int amount) {
        if (!validatePostConditions(amount))
            return;

        double price = item.getPlayerFloorSellPrice(p, shop) * amount;
        item.getEcon().depositMoney(p, price);
        ItemUtils.remove(p.getInventory(), item.getItem(), amount);

        Messages.MSG_BUY_ITEM.send(p,
                Template.of("action", Lang.SELL_ACTION_NAME.getAsString(p)),
                Template.of("item", ItemUtils.getName(item.getItem())),
                Template.of("amount", amount),
                Template.of("price", PrettyPrice.pretty(price)),
                Template.of("currency", item.getEcon().getName())
        );

        Events.callEvent(new checkoutEvent(shop, Transactions.Type.SELL, p, item, amount, price));

        shop.openShop(p);
    }

    private boolean validatePostConditions(int amount) {
        try {
            conditions.validate(shop, p, item, amount);
        } catch (IllegalPrecondition err) {
            p.closeInventory();
            err.sendErrMsg(p);
            return false;
        }
        return true;
    }

}
