package io.github.divios.dailyrandomshop.guis.customizerguis;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.main;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
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

    private final Player p;
    private final ItemStack item;
    private final Runnable accept;
    private final Runnable back;

    public changePrice(
            Player p,
            ItemStack item,
            Runnable accept,
            Runnable back
    ) {

        this.p = p;
        this.item = item;
        this.accept = accept;
        this.back = back;

        Bukkit.getServer().getPluginManager().registerEvents(this, main.getInstance());
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

                        new dailyItem(this.item)
                                .addNbt(dailyItem.dailyMetadataType.rds_price,
                                        new dailyItem.dailyItemPrice(Double.parseDouble(text))).getItem();

                        buyGui.getInstance().updateItem(dailyItem.getUuid(this.item),
                                buyGui.updateAction.update);

                        accept.run();
                        return AnvilGUI.Response.close();
                    })
                    .text(conf_msg.DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT)
                    .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                    .title(conf_msg.DAILY_ITEMS_MENU_ANVIL_TITLE)
                    .plugin(main.getInstance())
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

                                    new dailyItem(this.item)
                                            .addNbt(dailyItem.dailyMetadataType.rds_price,
                                                    new dailyItem.dailyItemPrice(aux.get(),
                                                            Double.parseDouble(text1))).getItem();

                                    buyGui.getInstance().updateItem(dailyItem.getUuid(this.item),
                                            buyGui.updateAction.update);

                                    accept.run();
                                    return AnvilGUI.Response.close();
                                })
                                .text("input max price")
                                .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                                .title("Input max price")
                                .plugin(main.getInstance())
                                .open(p), 1L);
                        return AnvilGUI.Response.close();
                    })
                    .text("input min price")
                    .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                    .title("Input max price")
                    .plugin(main.getInstance())
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