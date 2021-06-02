package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.guis.customizerguis.customizeGui;
import io.github.divios.dailyrandomshop.lorestategy.loreStrategy;
import io.github.divios.dailyrandomshop.lorestategy.shopsManagerLore;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.dataManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class shopsManagerGui {

    private static final DRShop plugin = DRShop.getInstance();
    private static final shopsManager sManager = shopsManager.getInstance();
    private static final dataManager dManager = dataManager.getInstance();

    private static final loreStrategy loreItem = new shopsManagerLore();

    public static void open(Player p) {
        new dynamicGui.Builder()
                .contents(shopsManagerGui::contents)
                .addItems((inventory, integer) -> setItems(inventory))
                .contentAction(shopsManagerGui::contentAction)
                .nonContentAction(shopsManagerGui::nonContentAction)
                .back(player -> p.closeInventory())
                .setSearch(false)
                .title(i -> utils.formatString("&f&lShops Manager"))
                .open(p);
    }

    private static List<ItemStack> contents() {
        List<ItemStack> iShops = new ArrayList<>();
        shopsManager.getInstance().getShops().forEach(dShop -> {
            ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
            utils.applyTexture(item, "7e3deb57eaa2f4d403ad57283ce8b41805ee5b6de912ee2b4ea736a9d1f465a7");
            utils.setDisplayName(item, "&f&l" + dShop.getName());
            utils.setLore(item, Collections.singletonList("&6Shop type: &7" + dShop.getType().name()));
            loreItem.setLore(item);

            iShops.add(item);
        });
        return iShops;
    }

    private static void setItems(Inventory inv) {
        ItemStack item = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(item, "&f&lCreate Shop");

        inv.setItem(52, item);
    }

    private static dynamicGui.Response contentAction(InventoryClickEvent e) {
        if (utils.isEmpty(e.getCurrentItem())) {
            e.setCancelled(true);
            return dynamicGui.Response.nu();
        }

        ItemStack selected = e.getCurrentItem();
        dShop shop = sManager.getShop(utils.trimString(utils.getDisplayName(selected)));
        Player p = (Player) e.getWhoClicked();

        if (e.isShiftClick() && e.isLeftClick())
            shop.getGui().open(p);

        else if (e.isShiftClick() && e.isRightClick())
            customizeGui.open(p, shop);

        else if (e.getClick().equals(ClickType.MIDDLE)) {
            new AnvilGUI.Builder()
                    .onClose(player -> utils.runTaskLater(() -> open(p), 1L))
                    .onComplete((player, s) -> {
                        if (s.isEmpty())
                            return AnvilGUI.Response.text("Cat be empty");
                        dManager.renameShop(shop.getName(), s);
                        shop.setName(s);
                        return AnvilGUI.Response.close();
                    })
                    .title(utils.formatString("&c&lRename shop"))
                    .text("Rename Shop")
                    .plugin(plugin)
                    .open(p);
        }

        else if (e.isRightClick()) {
            new confirmIH(p, (player, aBoolean) -> {
                if (aBoolean)
                    shopsManager.getInstance().deleteShop(shop.getName());
                open(player);
            }, selected,
                    conf_msg.CONFIRM_GUI_NAME,
                    conf_msg.CONFIRM_MENU_YES, conf_msg.CONFIRM_MENU_NO);
            return dynamicGui.Response.nu();
        }

        else
            shopGui.open(p, shop.getName());
        return dynamicGui.Response.nu();
    }

    private static dynamicGui.Response nonContentAction(Integer slot, Player p) {
        if (slot == 52) {
            new AnvilGUI.Builder()
                    .onComplete((player, s) -> {
                        shopsManager.getInstance().createShop(s, dShop.dShopT.buy);
                        return AnvilGUI.Response.close();
                    })
                    .onClose(player -> utils.runTaskLater(() -> open(p), 1L))
                    .text("input shop name")
                    .plugin(plugin)
                    .open(p);
        }
        return dynamicGui.Response.nu();
    }


}
