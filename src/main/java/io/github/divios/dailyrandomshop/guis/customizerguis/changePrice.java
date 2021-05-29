package io.github.divios.dailyrandomshop.guis.customizerguis;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;


public class changePrice implements InventoryHolder, Listener {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final dItem item;
    private final dShop shop;
    private final Runnable accept;
    private final Runnable back;

    public changePrice(
            Player p,
            dItem item,
            dShop shop,
            Runnable accept,
            Runnable back
    ) {

        this.p = p;
        this.item = item;
        this.shop = shop;
        this.accept = accept;
        this.back = back;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        p.openInventory(getInventory());
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Change price");

        ItemStack fixedPrice = XMaterial.SUNFLOWER.parseItem();
        utils.setDisplayName(fixedPrice, "&6&lSet fixed price");
        utils.setLore(fixedPrice, Arrays.asList("&7The item 'll always have", "&7the given price"));

        ItemStack intervalPrice = XMaterial.REPEATER.parseItem();
        utils.setDisplayName(intervalPrice, "&c&lSet interval");
        utils.setLore(intervalPrice, Arrays.asList("&7The price of the item",
                "&7will take a random value between", "&7the given interval"));

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();
        utils.setDisplayName(returnItem, conf_msg.CONFIRM_GUI_RETURN_NAME);
        utils.setLore(returnItem, conf_msg.CONFIRM_GUI_RETURN_PANE_LORE);

        inv.setItem(11, fixedPrice);
        inv.setItem(15, intervalPrice);

        inv.setItem(22, returnItem);

        return inv;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;
        e.setCancelled(true);

        if (e.getSlot() != e.getRawSlot()) return;

        if (e.getSlot() == 22)
            back.run();

        else if (e.getSlot() == 11) {
            new AnvilGUI.Builder()
                    .onComplete((player, text) -> {
                        try {
                            Double.parseDouble(text);
                        } catch (NumberFormatException err) {
                            return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER); }

                        //item.setPDouble.parseDouble(text))).getItem();

                        //buyGui.getInstance().updateItem(dailyItem.getUuid(this.item),
                          //      buyGui.updateAction.update);

                        accept.run();
                        return AnvilGUI.Response.close();
                    })
                    .text(conf_msg.DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT)
                    .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                    .title(conf_msg.DAILY_ITEMS_MENU_ANVIL_TITLE)
                    .plugin(DRShop.getInstance())
                    .open(p);

        } else if (e.getSlot() == 15) {

            AtomicDouble aux = new AtomicDouble();

            new AnvilGUI.Builder()
                    .onComplete((player, text) -> {
                        try {
                            Double.parseDouble(text);
                        } catch (NumberFormatException err) {
                            return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER); }

                        aux.set(Double.parseDouble(text));
                        utils.runTaskLater(() -> new AnvilGUI.Builder()
                                .onComplete((player1, text1) -> {
                                    try {
                                        Double.parseDouble(text1);
                                    } catch (NumberFormatException err) {
                                        return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER); }

                                    if (aux.get() >= Double.parseDouble(text1))
                                        return AnvilGUI.Response.text("Max price can't be lower than min price");

                                    //new dailyItem(this.item)
                                      //      .addNbt(dailyItem.dMeta.rds_price,
                                          //          new dailyItem.dailyItemPrice(aux.get(),
                                        //                    Double.parseDouble(text1))).getItem();

                                    //buyGui.getInstance().updateItem(dailyItem.getUuid(this.item),
                                      //      buyGui.updateAction.update);

                                    accept.run();
                                    return AnvilGUI.Response.close();
                                })
                                .text("input max price")
                                .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                                .title("Input max price")
                                .plugin(plugin)
                                .open(p), 1L);
                        return AnvilGUI.Response.close();
                    })
                    .text("input min price")
                    .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                    .title("Input max price")
                    .plugin(plugin)
                    .open(p);
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() != this) return;

        utils.runTaskLater(() -> {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryDragEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);
        }, 1L);
    }


}