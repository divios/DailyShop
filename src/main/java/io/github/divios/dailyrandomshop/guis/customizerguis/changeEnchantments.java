package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
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

    private static final DRShop plugin = DRShop.getInstance();

    private static changeEnchantments instance = null;
    private static final List<ItemStack> contentsList = contents();
    private Player p;
    private dItem ditem;
    private dShop shop;
    private Map<Enchantment, Integer> e;

    private changeEnchantments () {}

    public static void openInventory(Player p, dItem ditem, dShop shop) {
        instance = new changeEnchantments();
        instance.p = p;
        instance.ditem = ditem;
        instance.shop = shop;
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
        customizerMainGuiIH.openInventory(p, ditem, shop);
    }

    private dynamicGui.Response contentAction(InventoryClickEvent e) {
        AtomicBoolean response = new AtomicBoolean(false);
        String s = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName());

        new AnvilGUI.Builder()
                .onClose(player -> utils.runTaskLater(() -> {
                    customizerMainGuiIH.openInventory(p, ditem, shop);
                }, 1L))
                .onComplete((player, text) -> {
                    try {
                        Integer.parseInt(text);
                    } catch (NumberFormatException err) { return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER); }
                    ditem.addEnchantments(Enchantment.getByName(s), Integer.parseInt(text));
                    response.set(true);
                    return AnvilGUI.Response.close();
                })
                .text("Set Enchantment lvl")
                .itemLeft(ditem.getItem().clone())
                .title("Set Enchantment lvl")
                .plugin(plugin)
                .open(p);

        return dynamicGui.Response.nu();
    }

    public static void openInventory(Player p, dItem ditem, Map<Enchantment, Integer> e, dShop shop) {
        instance = new changeEnchantments();
        instance.p = p;
        instance.ditem = ditem;
        instance.e = e;
        instance.shop = shop;
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
        ditem.removeEnchantments(Enchantment.getByName(entry[0]));
        customizerMainGuiIH.openInventory(p, ditem, instance.shop);
        return dynamicGui.Response.nu();
    }

}