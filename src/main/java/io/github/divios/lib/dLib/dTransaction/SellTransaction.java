package io.github.divios.lib.dLib.dTransaction;

import io.github.divios.lib.dLib.confirmMenu.sellConfirmMenu;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.newDItem;
import org.bukkit.entity.Player;

public class SellTransaction {

    private dShop shop;
    private Player vendor;
    private newDItem item;

    public SellTransaction withShop(dShop shop) {
        this.shop = shop;
        return this;
    }

    public SellTransaction withVendor(Player vendor) {
        this.vendor = vendor;
        return this;
    }

    public SellTransaction withItem(newDItem item) {
        this.item = item;
        return this;
    }

    public void execute() {
        if (item.isConfirmGui())
            sellConfirmMenu.builder()
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
                    .execute();
    }

}
