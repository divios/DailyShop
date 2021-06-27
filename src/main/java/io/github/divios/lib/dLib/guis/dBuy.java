package io.github.divios.lib.dLib.guis;

import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.transaction.sellTransaction;
import io.github.divios.dailyShop.transaction.transaction;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dGui;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class dBuy extends dGui {

    public dBuy(String title, Inventory inv, dShop shop) {
        super(title, inv, shop);
    }

    public dBuy(String title, int size, dShop shop) {
        super(title, size, shop);
    }

    public dBuy(String base64, dShop shop) {
        super(base64, shop);
    }

    public dBuy(dShop shop) { super(shop); }

    @Override
    public void closeAll() {
        new ArrayList<>(inv.getViewers())
                .forEach(humanEntity -> {
                    humanEntity.sendMessage(conf_msg.PREFIX +
                            FormatUtils.color("This shop is now under maintenance, " +
                                    "come again in a few minutes"));
                    humanEntity.closeInventory();
                });
    }

    @Override
    protected void _renovate(dItem newItem, int slot) {
        newItem.setSlot(slot);
        buttons.add(newItem);

        ItemStack itemToAdd = newItem.getItem().clone();
        new shopItemsLore(shop.getType()).setLore(itemToAdd);
        inv.setItem(slot, itemToAdd);
    }

    @Override
    protected void updateItem(dItem item, updateItemEvent.updatetype type) {
        if (super.buttons.stream().noneMatch(dItem -> dItem.getUid().equals(item.getUid()))) return;

        buttons.stream().filter(dItem -> dItem.getUid().equals(item.getUid()))
                .findFirst()
                .ifPresent(dItem -> {

                    if (type.equals(updateItemEvent.updatetype.UPDATE_ITEM)) {

                        item.setSlot(dItem.getSlot());
                        buttons.remove(dItem);
                        buttons.add(item);
                        ItemStack itemWithLore = item.getItem().clone();
                        strategy.setLore(itemWithLore);
                        inv.setItem(dItem.getSlot(), itemWithLore);

                    } else if (type.equals(updateItemEvent.updatetype.NEXT_AMOUNT)) {

                        dItem.setStock(dItem.getStock().orElse(0) - 1);

                        if (dItem.getStock().orElse(0) <= 0) {
                            dItem.setStock(-1);
                        }

                        updateItem(dItem, updateItemEvent.updatetype.UPDATE_ITEM);

                    } else if (type.equals(updateItemEvent.updatetype.DELETE_ITEM)) {

                        buttons.stream()
                                .filter(dItem1 -> dItem1.getUid().equals(item.getUid()))
                                .findFirst()
                                .ifPresent(dItem1 -> {
                                    buttons.remove(dItem1);
                                    inv.setItem(dItem1.getSlot(), utils.getRedPane());
                                });
                    }
                });
    }

    @Override
    protected void initListeners() {
        super.clickEvent = new EventListener<>(plugin, InventoryClickEvent.class,
                EventPriority.HIGHEST, e -> {

            if (!e.getInventory().equals(inv)) return;

            e.setCancelled(true);

            if (utils.isEmpty(e.getCurrentItem())) return;

            if (openSlots.contains(e.getSlot()))
                buttons.stream()
                        .filter(ditem -> ditem.getUid().equals(dItem.getUid(e.getCurrentItem())))
                        .findFirst()
                        .ifPresent(dItem -> {
                            if (e.isLeftClick())
                                transaction.init(
                                    (Player) e.getWhoClicked(), dItem, shop);
                            else {
                                sellTransaction.init(
                                        (Player) e.getWhoClicked(), dItem, shop);
                            }
                        });

            else {
                buttons.stream().filter(dItem -> dItem.getSlot() == e.getSlot())
                        .findFirst().ifPresent(dItem -> dItem.getAction()
                        .stream((dAction, s) -> dAction.run((Player) e.getWhoClicked(), s)));
            }

        });

        super.dragEvent = new EventListener<>(plugin, InventoryDragEvent.class,
                e -> {
                    if (!e.getInventory().equals(inv)) return;

                    e.setCancelled(true);

                });

        super.openEvent = new EventListener<>(plugin, InventoryOpenEvent.class,
                EventPriority.HIGHEST, e -> {
            if (!e.getInventory().equals(inv)) return;

            if (!available) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("The shop is closed... come again in some minutes");
            }
        });
    }

    @Override
    protected void destroy() {
        closeAll();
        clickEvent.unregister();
        dragEvent.unregister();
        openEvent.unregister();
    }


}
