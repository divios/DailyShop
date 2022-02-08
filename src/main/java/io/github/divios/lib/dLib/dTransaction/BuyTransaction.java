package io.github.divios.lib.dLib.dTransaction;

import io.github.divios.lib.dLib.confirmMenu.BuyConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class BuyTransaction {

    private dShop shop;
    private Player buyer;
    private dItem item;

    public BuyTransaction withShop(dShop shop) {
        this.shop = shop;
        return this;
    }

    public BuyTransaction withBuyer(Player buyer) {
        this.buyer = buyer;
        return this;
    }

    public BuyTransaction withItem(dItem item) {
        this.item = item;
        return this;
    }

    public void execute() {
        if (item.isConfirmGui() && item.getBundle() == null)
            BuyConfirmMenu.builder()
                    .withPlayer(buyer)
                    .withShop(shop)
                    .withItem(item)
                    .withOnCompleteAction(this::executeTransaction)
                    .withFallback(() -> shop.openShop(buyer))
                    .prompt();
        else
            executeTransaction(item.getItem().getAmount());
    }

    private void executeTransaction(int amount) {
        SingleTransaction.create()
                .withPlayer(buyer)
                .withShop(shop)
                .withType(SingleTransaction.Type.BUY)
                .withItem(item)
                .withAmount(amount)
                .withOnComplete(bill -> shop.openShop(buyer))
                .execute();
    }

}
