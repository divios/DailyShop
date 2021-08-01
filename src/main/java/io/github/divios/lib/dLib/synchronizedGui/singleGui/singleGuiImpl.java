package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class singleGuiImpl implements singleGui{

    private final Player p;
    private InventoryGUI base;


    protected singleGuiImpl(Player p, singleGui base) {
        this.p = p;
        this.base = base.clone().getInventory();
        this.base.open(p);
    }

    @Override
    public void updateItem(dItem item, updateItemEvent.updatetype type) {

    }

    @Override
    public void updatePlaceholders() {

    }

    @Override
    public void renovate() {

    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public InventoryGUI getInventory() {
        return base;
    }

    @Override
    public void destroy() {
        new ArrayList<>(base.getInventory().getViewers())
                .forEach(HumanEntity::closeInventory);
        base.destroy();
        base = null;
    }

    @Override
    public singleGui clone() {
        return null;
    }
}
