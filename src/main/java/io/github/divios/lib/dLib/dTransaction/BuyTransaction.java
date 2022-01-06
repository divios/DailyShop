package io.github.divios.lib.dLib.dTransaction;

import io.github.divios.lib.dLib.confirmMenu.sellConfirmMenu;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.newDItem;
import org.bukkit.entity.Player;

public class BuyTransaction {

    private dShop shop;
    private Player vendor;
    private newDItem item;

    public BuyTransaction withShop(dShop shop) {
        this.shop = shop;
        return this;
    }

    public BuyTransaction withVendor(Player vendor) {
        this.vendor = vendor;
        return this;
    }

    public BuyTransaction withItem(newDItem item) {
        this.item = item;
        return this;
    }

    public void execute() {
        sellConfirmMenu.builder()
                .withPlayer(vendor)
                .withShop(shop)
                .withItem(item)
                .withOnCompleteAction(integer -> {
                    SingleTransaction.create()
                            .withPlayer(vendor)
                            .withShop(shop)
                            .withType(SingleTransaction.Type.BUY)
                            .withItem(item)
                            .withAmount(integer)
                            .execute();
                })
                .withFallback(() -> shop.openShop(vendor))
                .build();
    }

}
