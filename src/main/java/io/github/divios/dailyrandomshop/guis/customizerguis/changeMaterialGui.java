package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.utils.utils;
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
    private static dynamicGui inventory = null;
    private ItemStack newItem;
    private Player p;

    private changeMaterialGui() {};

    public static void openInventory(Player p, ItemStack newItem) {
        changeMaterialGui instance = new changeMaterialGui();
        instance.p = p;
        instance.newItem = newItem;
        if(inventory != null) inventory.open(p);
        else inventory = new dynamicGui.Builder()
                .contents(instance::contents)
                .contentAction(instance::contentActions)
                .back(instance::backAction)
                .open(p);
    }

    public List<ItemStack> contents() {
        List<ItemStack> contents = new ArrayList<>();

        for(Material m: removeGarbageMaterial()) {
            ItemStack item = new ItemStack(m);
            utils.setDisplayName(item, "&f&l" + m.toString());
            contents.add(item);
        }
        return contents;
    }

    public dynamicGui.Response contentActions(InventoryClickEvent e) {
        newItem.setType(e.getCurrentItem().getType());
        customizerMainGuiIH.openInventory(p, newItem);
        return dynamicGui.Response.nu();
    }

    public ArrayList<Material> removeGarbageMaterial(){
        Inventory inv = Bukkit.createInventory(null, 54, "");
        ArrayList<Material> materialsaux = new ArrayList<>();

        for (Material m: Material.values()) {
            ItemStack item = new ItemStack(m);
            inv.setItem(0, item);
            Boolean err = false;
            try{
                inv.getItem(0).getType();
            } catch (NullPointerException e) {
                err = true;
            }
            if(!err) materialsaux.add(m);

        }
        return materialsaux;
    }

    public void backAction(Player p) {
        customizerMainGuiIH.openInventory(p, newItem);
    }

}
