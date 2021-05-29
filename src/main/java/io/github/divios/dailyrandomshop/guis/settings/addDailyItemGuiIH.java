package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.listeners.dynamicItemListener;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.Consumer;

public class addDailyItemGuiIH implements InventoryHolder, Listener {

    private static final DRShop plugin = DRShop.getInstance();
    private Inventory inv;

    private final Player p;
    private final Consumer<ItemStack> consumer;

    public static void openInventory(Player p, Consumer<ItemStack> consumer) {

        addDailyItemGuiIH instance = new addDailyItemGuiIH(p,  consumer);

        p.openInventory(instance.getInventory());
    }

    private addDailyItemGuiIH(Player p, Consumer<ItemStack> consumer) {
        this.p = p;
        this.consumer = consumer;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.init();
    }

    private void init() {

        inv = Bukkit.createInventory(this, 27, conf_msg.ADD_ITEMS_TITLE);

        ItemStack fromZero = XMaterial.REDSTONE_TORCH.parseItem();
        utils.setDisplayName(fromZero, conf_msg.ADD_ITEMS_FROM_ZERO);
        utils.setLore(fromZero, conf_msg.ADD_ITEMS_FROM_ZERO_LORE);

        ItemStack fromItem = XMaterial.HOPPER.parseItem();
        utils.setDisplayName(fromItem, conf_msg.ADD_ITEMS_FROM_EXISTING);
        utils.setLore(fromItem, conf_msg.ADD_ITEMS_FROM_EXISTING_LORE);

        ItemStack bundleItem = XMaterial.CHEST_MINECART.parseItem();
        utils.setDisplayName(bundleItem, "&6&lCreate bundle");
        utils.setLore(bundleItem, Arrays.asList("&7Create bundle"));

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();
        utils.setDisplayName(returnItem, conf_msg.ADD_ITEMS_RETURN);
        utils.setLore(returnItem, conf_msg.ADD_ITEMS_RETURN_LORE);


        inv.setItem(11, fromZero);
        inv.setItem(15, fromItem);
        inv.setItem(13, bundleItem);
        inv.setItem(22, returnItem);

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (utils.isEmpty(item)) {
                inv.setItem(i, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() != e.getRawSlot()) return;

        if (e.getSlot() == 22) {    //return
            shopsManagerGui.open(p);
        }

        if (e.getSlot() == 11) { //from zero
            consumer.accept(XMaterial.GRASS.parseItem());
        }

        else if (e.getSlot() == 15) {  //from item
            new dynamicItemListener(p, (player, itemStack) ->
                    consumer.accept(itemStack));
            p.closeInventory();
        }

        else if (e.getSlot() == 13) {  //bundle item

            /*new changeBundleItem(
                    p,
                    XMaterial.CHEST_MINECART.parseItem(),
                    (player, itemStack) ->
                            customizerMainGuiIH.openInventory(p,
                                    new dailyItem(itemStack).craft()),
                    player -> openInventory(p)); */
        }
    }

}