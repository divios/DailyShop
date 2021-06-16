package io.github.divios.dailyrandomshop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ItemPrompt;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.customizerguis.changeBundleItem;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class addDailyGuiIH {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final dShop shop;
    private final Consumer<ItemStack> consumer;

    public static void open(Player p, dShop shop, Consumer<ItemStack> consumer) {
        new addDailyGuiIH(p, shop, consumer);
    }

    private addDailyGuiIH(Player p, dShop shop, Consumer<ItemStack> consumer) {
        this.p = p;
        this.shop = shop;
        this.consumer = consumer;

        body();
    }

    private void body() {

        InventoryGUI gui = new InventoryGUI(plugin, 27, conf_msg.ADD_ITEMS_TITLE);

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
                    new changeBundleItem(p, dItem.of(XMaterial.CHEST_MINECART.parseItem()), shop,
                            (player, uuids) -> {
                                gui.destroy();
                                dItem newBundle = dItem.of(XMaterial.CHEST_MINECART.parseItem());
                                newBundle.setBundle(uuids);
                                shop.addItem(newBundle);
                                shopGui.open(p, shop);
                            }, gui::open);
                }), 13);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.OAK_SIGN)
                        .setName(conf_msg.ADD_ITEMS_RETURN).addLore(conf_msg.ADD_ITEMS_RETURN_LORE)
                , e -> shopsManagerGui.open(p)), 22);

        IntStream.range(0, 27).forEach(value -> {
            if (utils.isEmpty(gui.getInventory().getItem(value)))
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        , e -> {
                        }), value);
        });


        gui.setDestroyOnClose(false);
        gui.open(p);
    }

}