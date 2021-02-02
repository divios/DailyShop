package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class changeMaterialGui {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final List<ItemStack> contents = removeGarbageMaterial();
    private ItemStack newItem;
    private Player p;

    private changeMaterialGui() {}

    public static void openInventory(Player p, ItemStack newItem) {
        changeMaterialGui instance = new changeMaterialGui();
        instance.p = p;
        instance.newItem = newItem.clone();
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
        this.newItem.setType(e.getCurrentItem().getType());
        customizerMainGuiIH.openInventory(p, this.newItem);
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
        customizerMainGuiIH.openInventory(p, this.newItem);
    }

}
