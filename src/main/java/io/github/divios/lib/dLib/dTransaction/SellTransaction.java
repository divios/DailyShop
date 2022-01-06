package io.github.divios.lib.dLib.dTransaction;

import io.github.divios.lib.dLib.confirmMenu.SellConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

public class SellTransaction {

    private dShop shop;
    private Player vendor;
    private dItem item;

    public SellTransaction withShop(dShop shop) {
        this.shop = shop;
        return this;
    }

    public SellTransaction withVendor(Player vendor) {
        this.vendor = vendor;
        return this;
    }

    public SellTransaction withItem(dItem item) {
        this.item = item;
        return this;
    }

    public void execute() {
        if (item.isConfirmGui())
            SellConfirmMenu.builder()
                    .withPlayer(vendor)
                    .withShop(shop)
                    .withItem(item)
                    .withOnCompleteAction(integer -> {
                        SingleTransaction.create()
                                .withPlayer(vendor)
                                .withShop(shop)
                                .withType(SingleTransaction.Type.SELL)
                                .withItem(item)
                                .withAmount(integer)
                                .execute();
                    })
                    .withFallback(() -> shop.openShop(vendor))
                    .prompt();
        else
            SingleTransaction.create()
                    .withPlayer(vendor)
                    .withShop(shop)
                    .withType(SingleTransaction.Type.SELL)
                    .withItem(item)
                    .withAmount(item.getItem().getAmount())
                    .withOnComplete(bill -> shop.computeBill(bill))
                    .execute();
    }

}
