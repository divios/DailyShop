package io.github.divios.dailyShop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.guis.confirmIH;
import io.github.divios.dailyShop.guis.customizerguis.customizeGui;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopsManagerLore;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.dataManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                .title(i -> conf_msg.SHOPS_MANAGER_TITLE)
                .plugin(plugin)
                .open(p);
    }

    private static List<ItemStack> contents() {
        return shopsManager.getInstance().getShops().stream().map(dShop ->
                new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setName("&f&l" + dShop.getName())
                .applyTexture("7e3deb57eaa2f4d403ad57283ce8b41805ee5b6de912ee2b4ea736a9d1f465a7"))
            .peek(loreItem::setLore)
            .collect(Collectors.toList());
    }

    private static void setItems(Inventory inv) {
        inv.setItem(52, new ItemBuilder(XMaterial.ANVIL)
                .setName(conf_msg.SHOPS_MANAGER_CREATE));
    }

    private static dynamicGui.Response contentAction(InventoryClickEvent e) {
        if (utils.isEmpty(e.getCurrentItem())) {
            e.setCancelled(true);
            return dynamicGui.Response.nu();
        }

        ItemStack selected = e.getCurrentItem();
        dShop shop = sManager.getShop(FormatUtils.stripColor(utils.getDisplayName(selected))).get();
        Player p = (Player) e.getWhoClicked();

        if (e.isShiftClick() && e.isLeftClick())
            customizeGui.open(p, shop);

        else if (e.getClick().equals(ClickType.MIDDLE)) {
            new AnvilGUI.Builder()
                    .onClose(player -> Task.syncDelayed(plugin, () -> open(p), 1L))
                    .onComplete((player, s) -> {
                        if (s.isEmpty())
                            return AnvilGUI.Response.text("Cat be empty");

                        dManager.renameShop(shop.getName(), s);
                        shop.setName(s);
                        return AnvilGUI.Response.close();
                    })
                    .title(conf_msg.SHOPS_MANAGER_RENAME)
                    .text(FormatUtils.stripColor(conf_msg.SHOPS_MANAGER_RENAME))
                    .plugin(plugin)
                    .open(p);
        }

        else if (e.getClick().equals(ClickType.DROP)) {
            new AnvilGUI.Builder()
                    .onClose(player -> Task.syncDelayed(plugin, () -> open(p), 1L))
                    .onComplete((player, s) -> {
                        int time;
                        try {
                            time = Integer.parseInt(s);
                        } catch (Exception err) {return  AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);}

                        if (time < 50) return AnvilGUI.Response.text("Time cannot be less than 50");
                        shop.setTimer(time);
                        return AnvilGUI.Response.close();
                    })
                    .itemLeft(XMaterial.CLOCK.parseItem())
                    .plugin(plugin)
                    .open(p);
        }

        else if (e.isRightClick()) {
            new confirmIH(p, (player, aBoolean) -> {
                if (aBoolean)
                    shopsManager.getInstance().deleteShop(shop.getName());
                open(player);
            }, selected,
                    conf_msg.CONFIRM_GUI_ACTION_NAME,
                    conf_msg.CONFIRM_MENU_YES, conf_msg.CONFIRM_MENU_NO);
            return dynamicGui.Response.nu();
        }

        else
            shopGui.open(p, shop.getName());
        return dynamicGui.Response.nu();
    }

    private static dynamicGui.Response nonContentAction(Integer slot, Player p) {

        if (slot != 52) return dynamicGui.Response.nu();

        new AnvilGUI.Builder()
                .onComplete((player, s) -> {

                    if (s.isEmpty())
                        return AnvilGUI.Response.text("Cat be empty");

                    if (sManager.getShop(s).isPresent())
                        return AnvilGUI.Response.text("Already exits");

                    shopsManager.getInstance().createShop(s, dShop.dShopT.buy);
                    return AnvilGUI.Response.close();
                })
                .onClose(player -> Task.syncDelayed(plugin, () -> open(p), 1L))
                .title(conf_msg.SHOPS_MANAGER_NEWSHOP)
                .text(FormatUtils.stripColor(conf_msg.SHOPS_MANAGER_NEWSHOP))
                .plugin(plugin)
                .open(p);

        return dynamicGui.Response.nu();
    }


}
