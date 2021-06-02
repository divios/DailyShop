package io.github.divios.dailyrandomshop.guis.settings;

import com.google.gson.Gson;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.listeners.dynamicItemListener;
import io.github.divios.dailyrandomshop.redLib.inventorygui.InventoryGUI;
import io.github.divios.dailyrandomshop.redLib.inventorygui.ItemButton;
import io.github.divios.dailyrandomshop.redLib.itemutils.ItemBuilder;
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
import java.util.stream.IntStream;

public class addDailyItemGuiIH {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final Consumer<ItemStack> consumer;

    public static void openInventory(Player p, Consumer<ItemStack> consumer) {
        new addDailyItemGuiIH(p,  consumer);
    }

    private addDailyItemGuiIH(Player p, Consumer<ItemStack> consumer) {
        this.p = p;
        this.consumer = consumer;

        open();
    }

    private void open() {

        InventoryGUI gui =  new InventoryGUI(27, conf_msg.ADD_ITEMS_TITLE);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.REDSTONE_TORCH)
                .setName(conf_msg.ADD_ITEMS_FROM_ZERO).addLore(conf_msg.ADD_ITEMS_FROM_ZERO_LORE)
                , e -> consumer.accept(XMaterial.GRASS.parseItem())), 11);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.HOPPER)
                        .setName(conf_msg.ADD_ITEMS_FROM_EXISTING).addLore(conf_msg.ADD_ITEMS_FROM_EXISTING_LORE)
                , e -> {
                    new dynamicItemListener(p, (player, itemStack) ->
                            consumer.accept(itemStack));
                    p.closeInventory();
                }), 15);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.CHEST_MINECART)
                        .setName("&6&lCreate bundle").addLore("&7Create bundle")
                , e -> {
            /*new changeBundleItem(
                    p,
                    XMaterial.CHEST_MINECART.parseItem(),
                    (player, itemStack) ->
                            customizerMainGuiIH.openInventory(p,
                                    new dailyItem(itemStack).craft()),
                    player -> openInventory(p)); */
                }), 13);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.OAK_SIGN)
                        .setName(conf_msg.ADD_ITEMS_RETURN).addLore(conf_msg.ADD_ITEMS_RETURN_LORE)
                , e -> shopsManagerGui.open(p)), 22);

        IntStream.range(0, 27).forEach(value -> {
            if (utils.isEmpty(gui.getInventory().getItem(value)))
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        , e -> {}), value);
        });

        gui.getState().serialize();

        gui.preventPlayerInvSlots();
        gui.destroysOnClose();
        gui.open(p);
    }

}