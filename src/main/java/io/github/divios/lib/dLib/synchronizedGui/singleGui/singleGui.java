package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import com.google.gson.JsonElement;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

/**
 * Contract of a singleGui
 */

public interface singleGui {

    static singleGui fromJson(JsonElement json, dShop shop) {
        return new singleGuiImpl(null, shop, dInventory.fromJson(json));
    }

    static singleGui create(dShop shop) {
        return new singleGuiImpl(null, shop, new dInventory(shop.getName(), 27));
    }

    static singleGui create(Player p, singleGui base, dShop shop) {
        return new singleGuiImpl(p, shop, base);
    }

    void updateItem(updateItemEvent o);

    void updateTask();

    void restock();

    Player getPlayer();

    dInventory getInventory();

    dShop getShop();

    singleGui copy(Player p);

    void destroy();

    default JsonElement toJson() {
        return getInventory().toJson();
    }

    int hash();

}
