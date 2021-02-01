package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class changeEnchantments {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static changeEnchantments instance = null;
    private static final List<ItemStack> contentsList = contents();
    private Player p;
    private ItemStack newItem;
    private Map<Enchantment, Integer> e;

    private changeEnchantments () {}

    public static void openInventory(Player p, ItemStack newItem) {
        instance = new changeEnchantments();
        instance.p = p;
        instance.newItem = newItem.clone();
            new dynamicGui.Builder()
                    .contents(instance::getContents)
                    .contentAction(instance::contentAction)
                    .back(instance::backAction)
                    .open(p);
    }

    private static List<ItemStack> contents() {
        List<ItemStack> contents = new ArrayList<>();
        for(Enchantment e : Enchantment.values()) {
            ItemStack item = XMaterial.BOOK.parseItem();
            utils.setDisplayName(item, "&f&l" + e.getName());
            contents.add(item);
        }
        return contents;
    }

    private List<ItemStack> getContents() {
        return contentsList;
    }

    private void backAction(Player p) {
        customizerMainGuiIH.openInventory(p, newItem);
    }

    private dynamicGui.Response contentAction(InventoryClickEvent e) {
        AtomicBoolean response = new AtomicBoolean(false);
        String s = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName());

        new AnvilGUI.Builder()
                .onClose(player -> utils.runTaskLater(() -> {
                    customizerMainGuiIH.openInventory(p, newItem);
                }, 1L))
                .onComplete((player, text) -> {
                    try {
                        Integer.parseInt(text);
                    } catch (NumberFormatException err) { return AnvilGUI.Response.text("Is not Integer"); }
                    newItem.addUnsafeEnchantment(Enchantment.getByName(s), Integer.parseInt(text));
                    response.set(true);
                    return AnvilGUI.Response.close();
                })
                .text("Set Enchantment lvl")
                .itemLeft(new ItemStack(newItem))
                .title("Set Enchantment lvl")
                .plugin(main)
                .open(p);

        return dynamicGui.Response.nu();
    }

    public static void openInventory(Player p, ItemStack newItem, Map<Enchantment, Integer> e) {
        instance = new changeEnchantments();
        instance.p = p;
        instance.newItem = newItem;
        instance.e = e;
        new dynamicGui.Builder()
                .contents(instance::contentsX)
                .contentAction(instance::contentActionX)
                .back(instance::backAction)
                .open(p);
    }

    private List<ItemStack> contentsX() {
        List<ItemStack> contents = new ArrayList<>();
        for(Map.Entry<Enchantment, Integer> e : e.entrySet()) {
            ItemStack item = XMaterial.ENCHANTED_BOOK.parseItem();
            utils.setDisplayName(item, "&f&l" + e.getKey().getName() + ":" + e.getValue());
            contents.add(item);
        }
        return contents;
    }

    private dynamicGui.Response contentActionX(InventoryClickEvent e) {
        String[] entry = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName()).split(":");
        newItem.removeEnchantment(Enchantment.getByName(entry[0]));
        customizerMainGuiIH.openInventory(p, newItem);
        return dynamicGui.Response.nu();
    }

}
