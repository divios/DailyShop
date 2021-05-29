package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.guis.customizerguis.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class shopGui {

    private static final DRShop plugin = DRShop.getInstance();
    private static final shopsManager sManager = shopsManager.getInstance();

    public static void open(Player p, String shop) {
        new dynamicGui.Builder()
                .contents(() -> contents(shop))
                .addItems((inv, i) -> addItems(inv))
                .contentAction(e -> contentAction(e, shop))
                .nonContentAction((i, p1) -> nonContentAction(i, p1, shop))
                .setSearch(false)
                .back(p1 -> shopsManagerGui.open(p))
                .title(i -> utils.formatString(utils.formatString("&f&lShop Manager")))
                .open(p);
    }


    private static List<ItemStack> contents (String name){
        List<ItemStack> items = new ArrayList<>();
        shopsManager.getInstance().getShop(name)
                .getItems().forEach(dItem -> items.add(dItem.getItem()));

        return items;
    }

    private static void addItems(Inventory inv) {
        ItemStack addItems = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(addItems, "&f&lAdd new item");

        inv.setItem(52, addItems);
    }

    private static dynamicGui.Response contentAction(InventoryClickEvent e, String shopName) {
        e.setCancelled(true);

        if (utils.isEmpty(e.getCurrentItem())) return dynamicGui.Response.nu();

        Player p = (Player) e.getWhoClicked();
        dShop shop = sManager.getShop(shopName);
        UUID uid = dItem.getUid(e.getCurrentItem());

        if (e.isLeftClick())
            customizerMainGuiIH.openInventory((Player) e.getWhoClicked(),
                    shop.getItem(uid), shop);

        else if (e.isRightClick())
            new confirmIH(p, (player, aBoolean) -> {
                if (aBoolean)
                    shop.removeItem(uid);
                open(p, shopName);
            }, player -> open(player, shopName), e.getCurrentItem(),
                    "", "", "");

        return dynamicGui.Response.nu();
    }

    private static dynamicGui.Response nonContentAction(int slot, Player p, String name) {
        if (slot == 52) {
            addDailyItemGuiIH.openInventory(p, itemStack -> {
                shopsManager.getInstance().getShop(name)
                        .addItem(new dItem(itemStack));
                open(p, name);
            });
        }
        return dynamicGui.Response.nu();
    }


}
