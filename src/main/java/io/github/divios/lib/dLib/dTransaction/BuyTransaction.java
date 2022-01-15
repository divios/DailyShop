package io.github.divios.lib.dLib.dTransaction;

import io.github.divios.lib.dLib.confirmMenu.BuyConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
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
        if (item.isConfirmGui() && item.getBundle() == null) {
            BuyConfirmMenu.builder()
                    .withPlayer(buyer)
                    .withShop(shop)
                    .withItem(item)
                    .withOnCompleteAction(integer -> {
                        SingleTransaction.create()
                                .withPlayer(buyer)
                                .withShop(shop)
                                .withType(SingleTransaction.Type.BUY)
                                .withItem(item)
                                .withAmount(integer)
                                .execute();
                    })
                    .withFallback(() -> shop.openShop(buyer))
                    .prompt();
        } else {
            SingleTransaction.create()
                    .withPlayer(buyer)
                    .withShop(shop)
                    .withType(SingleTransaction.Type.BUY)
                    .withItem(item)
                    .withAmount(item.getItem().getAmount())
                    .withOnComplete(bill -> shop.computeBill(bill))
                    .execute();
        }
    }

}
