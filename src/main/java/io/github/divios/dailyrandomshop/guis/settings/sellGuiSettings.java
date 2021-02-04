package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.listeners.dynamicItemListener;
import io.github.divios.dailyrandomshop.lorestategy.sellSettingsLore;
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

public class sellGuiSettings {

        private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
        private static final dataManager dbManager = dataManager.getInstance();
        private static sellGuiSettings instance = null;
        private Player p;

        private sellGuiSettings () {}

        public static void openInventory(Player p) {
            if(instance == null) {
                instance = new sellGuiSettings();
            }
            instance.p = p;
            new dynamicGui.Builder()
                    .title(integer -> conf_msg.SELL_ITEMS_MENU_TITLE)
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

            for(Map.Entry<ItemStack, Double> entry: dbManager.listSellItems.entrySet()) {
                contents.add(new dailyItem(entry.getKey(), true)
                        .addLoreStrategy(new sellSettingsLore())
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

                            utils.changeItemPrice(e.getCurrentItem(), Double.parseDouble(text));
                            return AnvilGUI.Response.close();
                        })
                        .text(conf_msg.SELL_ITEMS_MENU_ANVIL_DEFAULT_TEXT)
                        .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                        .title(conf_msg.SELL_ITEMS_MENU_ANVIL_TITLE)
                        .plugin(main)
                        .open(p);
            }

            else if (e.isRightClick() && !e.isShiftClick()) {
                new confirmIH(p, (player, aBoolean) -> {
                    if (aBoolean) {
                        utils.removeItem(e.getCurrentItem());
                    }
                    openInventory(player);
                }, sellGuiSettings::openInventory
                        ,"&aConfirm");
            }

            return dynamicGui.Response.nu();
        }

        public dynamicGui.Response nonContentAction(int slot, Player p) {
            if (slot == 52) {
                p.closeInventory();
                new dynamicItemListener(p, (player, itemStack) -> {
                    if (utils.hasItem(itemStack)) {
                        p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_ITEM_ALREADY_ON_SALE);
                        return;
                    }
                    dataManager.getInstance().listSellItems.put(itemStack, -1D);
                    openInventory(p);
                });
            }
            return dynamicGui.Response.nu();
        }
}
