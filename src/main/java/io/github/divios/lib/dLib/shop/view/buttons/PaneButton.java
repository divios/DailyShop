package io.github.divios.lib.dLib.shop.view.buttons;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class PaneButton implements Button {

    private static final ItemStack AIR = new ItemStack(Material.AIR);

    private final dItem pane;

    public PaneButton(dItem pane) {
        this.pane = pane;
    }

    @Override
    public void execute(InventoryClickEvent e) {
        pane.getAction().execute((Player) e.getWhoClicked());
    }

    @Override
    public ItemStack getItem(Player player) {
        if (pane.isAir()) return AIR;

        ItemStack aux = pane.getItem();
        ItemStack toSend = aux.clone();

        String newName = Utils.JTEXT_PARSER.parse(ItemUtils.getName(aux), player);
        List<String> newLore = Utils.JTEXT_PARSER.parse(ItemUtils.getLore(aux), player);

        toSend = ItemUtils.setName(toSend, newName);
        toSend = ItemUtils.setLore(toSend, newLore);

        return toSend;
    }

}
