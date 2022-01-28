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
                    .withOnCompleteAction(this::executeTransaction)
                    .withFallback(() -> shop.openShop(vendor))
                    .prompt();
        else
            executeTransaction(item.getItem().getAmount());
    }

    private void executeTransaction(int amount) {
        SingleTransaction.create()
                .withPlayer(vendor)
                .withShop(shop)
                .withType(SingleTransaction.Type.SELL)
                .withItem(item)
                .withAmount(amount)
                .withOnComplete(bill -> shop.openShop(vendor))
                .execute();
    }

}
