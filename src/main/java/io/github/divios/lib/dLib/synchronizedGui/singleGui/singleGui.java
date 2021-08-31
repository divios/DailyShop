package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

/**
 * Contract of a singleGui
 */

public interface singleGui {

    static singleGui fromJson(String json, dShop shop) {
        return new singleGuiImpl(null, shop, dInventory.fromBase64(json, shop));
    }

    static singleGui create(dShop shop) { return new singleGuiImpl(null, shop, new dInventory(shop.getName(), 27, shop)); }

    static singleGui create(Player p, singleGui base, dShop shop) {
        return new singleGuiImpl(p, shop, base);
    }

    static singleGui create(Player p, dInventory inv, dShop shop) { return new singleGuiImpl(p, shop, inv); }

    void updateItem(updateItemEvent o);

    void updateTask();

    void renovate();

    Player getPlayer();

    dInventory getBase();

    dInventory getInventory();

    dShop getShop();

    void destroy();

    default String toJson() {
        return getInventory().toBase64();
    }

    int hash();

}
