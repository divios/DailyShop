package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class changeMaterialGui {

    private static final DRShop main = DRShop.getInstance();
    private static final List<ItemStack> contents = removeGarbageMaterial();
    private dItem newItem;
    private Player p;
    private String shopName;

    private changeMaterialGui() {}

    public static void openInventory(Player p, dItem newItem, String shopName) {
        changeMaterialGui instance = new changeMaterialGui();
        instance.p = p;
        instance.newItem = newItem;
        instance.shopName = shopName;
            new dynamicGui.Builder()
                .contents(instance::contents)
                .contentAction(instance::contentActions)
                .back(instance::backAction)
                .open(p).getinvs();
    }

    private List<ItemStack> contents() {
        return contents;
    }

    private dynamicGui.Response contentActions(InventoryClickEvent e) {
        this.newItem.setMaterial(XMaterial.matchXMaterial(e.getCurrentItem()).parseMaterial());
        customizerMainGuiIH.openInventory(p, this.newItem, shopName);
        return dynamicGui.Response.nu();
    }

    private static List<ItemStack> removeGarbageMaterial(){
        Inventory inv = Bukkit.createInventory(null, 54, "");
        List<ItemStack> materialsaux = new ArrayList<>();

        for (XMaterial m: XMaterial.values()) {
            ItemStack item = m.parseItem();
            inv.setItem(0, item);
            boolean err = false;
            try{
                inv.getItem(0).getType();
            } catch (NullPointerException e) {
                err = true;
            }
            if(!err) {
                utils.setDisplayName(item, "&f&l" + m.toString());
                materialsaux.add(item);
            }

        }
        return materialsaux;
    }

    public void backAction(Player p) {
        //customizerMainGuiIH.openInventory(p, this.newItem);
    }

}
