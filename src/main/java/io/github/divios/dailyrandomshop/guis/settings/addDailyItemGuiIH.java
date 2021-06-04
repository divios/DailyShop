package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ItemPrompt;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        InventoryGUI gui =  new InventoryGUI(plugin, 27, conf_msg.ADD_ITEMS_TITLE);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.REDSTONE_TORCH)
                .setName(conf_msg.ADD_ITEMS_FROM_ZERO).addLore(conf_msg.ADD_ITEMS_FROM_ZERO_LORE)
                , e -> consumer.accept(XMaterial.GRASS.parseItem())), 11);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.HOPPER)
                        .setName(conf_msg.ADD_ITEMS_FROM_EXISTING).addLore(conf_msg.ADD_ITEMS_FROM_EXISTING_LORE)
                , e -> {
                    new ItemPrompt(plugin, p, (player, itemStack) -> consumer.accept(itemStack),
                            player -> p.sendMessage(conf_msg.MSG_TIMER_EXPIRED)
                            , conf_msg.MSG_ADD_ITEM_TITLE, conf_msg.MSG_ADD_ITEM_SUBTITLE);
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

        //gui.preventPlayerInvSlots();
        gui.destroysOnClose();
        gui.open(p);
    }

}