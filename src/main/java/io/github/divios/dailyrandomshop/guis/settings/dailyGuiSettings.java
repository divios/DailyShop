package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.builders.itemsFactory;
import io.github.divios.dailyrandomshop.builders.lorestategy.dailySettingsLore;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.guis.customizerguis.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class dailyGuiSettings {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private static dailyGuiSettings instance = null;
    private Player p;

    private dailyGuiSettings () {}

    public static void openInventory(Player p) {
        if(instance == null) {
            instance = new dailyGuiSettings();
        }
        instance.p = p;
        new dynamicGui.Builder()
                .title(integer -> conf_msg.DAILY_ITEMS_MENU_TITLE)
                .contents(instance::Contents)
                .back(settingsGuiIH::openInventory)
                .addItems((inventory, integer) -> instance.setItems(inventory))
                .contentAction(instance::contentAction)
                .nonContentAction(instance::nonContentAction)
                .setSearch(false)
                .open(p);
    }

    public List<ItemStack> Contents() {
        List<ItemStack> contents = new ArrayList<>();

        for(Map.Entry<ItemStack, Double> entry: dbManager.listDailyItems.entrySet()) {
            contents.add(new itemsFactory.Builder(entry.getKey(), true)
                    .setLoreStrategy(new dailySettingsLore(), itemsFactory.strategy.add)
                    .getItem());
        }
        return contents;
    }

    public void setItems(Inventory inv) {
        ItemStack addItems = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(addItems, "&b&lAdd");
        utils.setLore(addItems, Arrays.asList("&7Click to add item"));
        inv.setItem(52, addItems);
    }

    public dynamicGui.Response contentAction(InventoryClickEvent e) {

        if (e.isLeftClick() && !e.isShiftClick()) {
            new AnvilGUI.Builder()
                    .onClose(player -> utils.runTaskLater(() -> {
                        dailyGuiSettings.openInventory(player);
                    }, 1L))
                    .onComplete((player, text) -> {
                        try {
                            Double.parseDouble(text);
                        } catch (NumberFormatException err) { return AnvilGUI.Response.text("Is not Integer"); }

                        String uuid = new itemsFactory.Builder(e.getCurrentItem()).getUUID();
                        utils.changePriceByUuid(uuid, Double.parseDouble(text), dbManager.listDailyItems);
                        buyGui.updateItem(uuid, buyGui.updateAction.update);
                        return AnvilGUI.Response.close();
                    })
                    .text("Change price")
                    .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                    .title("price")
                    .plugin(main)
                    .open(p);
        }

        else if (e.isRightClick() && !e.isShiftClick()) {
            new confirmIH(p, (player, aBoolean) -> {
                if (aBoolean) {
                    String uuid = new itemsFactory.Builder(e.getCurrentItem()).getUUID();
                    utils.removeItemByUuid(uuid, dbManager.listDailyItems);
                    buyGui.updateItem(uuid, buyGui.updateAction.delete);
                }
                dailyGuiSettings.openInventory(player);
            }, "&aConfirm");
        }

        else if (e.isLeftClick() && e.isShiftClick()) {
            String uuid = new itemsFactory.Builder(e.getCurrentItem()).getUUID();
            customizerMainGuiIH.openInventory(p,
                    utils.getItemByUuid(uuid, dbManager.listDailyItems).clone());
        }
        return dynamicGui.Response.nu();
    }

    public dynamicGui.Response nonContentAction(int slot, Player p) {
        if (slot == 52) {
            addDailyItemGuiIH.openInventory(p);
        }
        return dynamicGui.Response.nu();
    }


}
