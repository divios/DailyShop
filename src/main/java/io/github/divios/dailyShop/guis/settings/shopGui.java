package io.github.divios.dailyShop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.guis.confirmIH;
import io.github.divios.dailyShop.guis.customizerguis.customizerMainGuiIH;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsManagerLore;
import io.github.divios.dailyShop.utils.utils;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class shopGui {

    private static final DRShop plugin = DRShop.getInstance();
    private static final shopsManager sManager = shopsManager.getInstance();

    public static void open(Player p, String shop) {
        sManager.getShop(shop).ifPresent(shop1 -> open(p, shop1));
    }

    public static void open(Player p, dShop shop) {
        new dynamicGui.Builder()
                .contents(() -> contents(shop))
                .addItems((inv, i) -> addItems(inv, shop))
                .contentAction(e -> contentAction(e, shop))
                .nonContentAction((i, p1) -> nonContentAction(i, p1, shop))
                .setSearch(false)
                .back(p1 -> shopsManagerGui.open(p))
                .title(i -> conf_msg.DAILY_ITEMS_MENU_TITLE)
                .plugin(plugin)
                .open(p);
    }


    private static List<ItemStack> contents (dShop shop) {
        return shop.getItems().stream()
                .map(dItem -> dItem.getItem().clone())
                .collect(Collectors.toList());
    }

    private static void addItems(Inventory inv, dShop shop) {
        loreStrategy strategy = new shopItemsManagerLore(shop.getType());

        inv.setItem(53, new ItemBuilder(XMaterial.ANVIL.parseItem())
                .setName(conf_msg.DAILY_ITEMS_MENU_ADD).addLore(conf_msg.DAILY_ITEMS_MENU_ADD_LORE));

        Task.asyncDelayed(plugin,() -> IntStream.range(0, 45).forEach(value -> {
            ItemStack aux = inv.getItem(value);
            if (utils.isEmpty(aux)) return;
            aux = aux.clone();
            strategy.setLore(aux);
            inv.setItem(value, aux);
        }), 0);
    }

    private static dynamicGui.Response contentAction(InventoryClickEvent e, dShop shop) {
        e.setCancelled(true);

        if (utils.isEmpty(e.getCurrentItem())) return dynamicGui.Response.nu();

        Player p = (Player) e.getWhoClicked();
        UUID uid = dItem.getUid(e.getCurrentItem());

        if (e.isLeftClick())
            customizerMainGuiIH.open((Player) e.getWhoClicked(),
                    shop.getItem(uid).get(), shop);

        else if (e.isRightClick())
            new confirmIH(p, (player, aBoolean) -> {
                if (aBoolean)
                    shop.removeItem(uid);
                open(p, shop.getName());
            }, e.getCurrentItem(),
                    conf_msg.CONFIRM_GUI_ACTION_NAME, conf_msg.CONFIRM_MENU_YES, conf_msg.CONFIRM_MENU_NO);

        return dynamicGui.Response.nu();
    }

    private static dynamicGui.Response nonContentAction(int slot, Player p, dShop shop) {
        if (slot == 52) {
            addDailyGuiIH.open(p, shop, itemStack -> {
                shop.addItem(new dItem(itemStack));
                open(p, shop.getName());
            }, () -> open(p, shop));
        }
        return dynamicGui.Response.nu();
    }


}
