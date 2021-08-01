package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.entity.Player;

public interface singleGui {

    static singleGui create(Player p, singleGui base) {
        return new singleGuiImpl(p, base);
    }

    void updateItem(dItem item, updateItemEvent.updatetype type);

    void updatePlaceholders();

    void renovate();

    Player getPlayer();

    InventoryGUI getInventory();

    void destroy();

    singleGui clone();

}
