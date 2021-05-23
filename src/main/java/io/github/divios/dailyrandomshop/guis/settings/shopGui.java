package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class shopGui {

    private static final DRShop plugin = DRShop.getInstance();

    public static void open(Player p, String shop) {
        new dynamicGui.Builder()
                .contents(() -> contents(shop))
                .addItems((inv, i) -> addItems(inv))
                .contentAction(shopGui::contentAction)
                .nonContentAction((i, p1) -> nonContentAction(i, p1, shop))
                .setSearch(false)
                .back(p1 -> shopsManagerGui.open(p))
                .open(p);
    }


    private static List<ItemStack> contents (String name){
        List<ItemStack> items = new ArrayList<>();
        shopsManager.getInstance().getShop(name)
                .getItems().forEach(dItem -> {
            items.add(dItem.getItem());
        });

        return items;
    }

    private static void addItems(Inventory inv) {
        ItemStack addItems = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(addItems, "&f&lAdd new item");

        inv.setItem(52, addItems);
    }

    private static dynamicGui.Response contentAction(InventoryClickEvent e) {
        e.setCancelled(true);
        return dynamicGui.Response.nu();
    }

    private static dynamicGui.Response nonContentAction(int slot, Player p, String name) {
        if (slot == 52) {
            ItemStack newItem = XMaterial.GRASS.parseItem();
            shopsManager.getInstance().getShop(name).addItem(new dItem(newItem));
            open(p, name);
        }
        return dynamicGui.Response.nu();
    }


}
