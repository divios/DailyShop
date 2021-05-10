package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.guis.customizerguis.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.builders.itemBuildersHooks.itemsBuilderManager;
import io.github.divios.dailyrandomshop.lorestategy.dailySettingsLore;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
            contents.add(new dailyItem(entry.getKey(), true)
                    .addLoreStrategy(new dailySettingsLore())
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
                    .onClose(player -> utils.runTaskLater(() -> openInventory(player), 1L))
                    .onComplete((player, text) -> {
                        try {
                            Double.parseDouble(text);
                        } catch (NumberFormatException err) { return AnvilGUI.Response.text("Is not Integer"); }

                        new dailyItem(dailyItem.getRawItem(e.getCurrentItem()))
                                .addNbt(dailyItem.dailyMetadataType.rds_itemEcon,
                                        new dailyItem.dailyItemPrice(Double.parseDouble(text))).getItem();

                        buyGui.getInstance().updateItem(dailyItem.getUuid(e.getCurrentItem()),
                                buyGui.updateAction.update);
                        return AnvilGUI.Response.close();
                    })
                    .text(conf_msg.DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT)
                    .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                    .title(conf_msg.DAILY_ITEMS_MENU_ANVIL_TITLE)
                    .plugin(main)
                    .open(p);
        }

        else if (e.isRightClick() && !e.isShiftClick()) {
            new confirmIH(p, (player, aBoolean) -> {
                if (aBoolean) {
                    String uuid = dailyItem.getUuid(e.getCurrentItem());
                    dailyItem.removeItemByUuid(e.getCurrentItem());
                    buyGui.getInstance().updateItem(uuid, buyGui.updateAction.delete);
                }
                openInventory(player);
            }, dailyGuiSettings::openInventory
                    ,null, "&aConfirm", conf_msg.CONFIRM_MENU_YES, conf_msg.CONFIRM_MENU_NO);
        }

        else if (e.isLeftClick() && e.isShiftClick()) {
            customizerMainGuiIH.openInventory(p,
                    dailyItem.getRawItem(e.getCurrentItem()));
        }

        else if( e.isRightClick() && e.isShiftClick()) {
            if (utils.hasItem(e.getCurrentItem())) {
                p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_ITEM_ALREADY_ON_SALE);
                return dynamicGui.Response.nu();
            }
            dataManager.getInstance().listSellItems.put(new dailyItem(
                    dailyItem.getRawItem(e.getCurrentItem()), true)
                    .removeAllMetadata().getItem(), -1D);
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_ADDED_ITEM);
            sellGuiSettings.openInventory(p);
        }

        else if (e.getClick().equals(ClickType.DROP)) {
            if ( itemsBuilderManager.updateItem(dailyItem.getUuid(e.getCurrentItem())))
                openInventory(p);
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
