package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.Events;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.utils.Collections;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class confirmGui{

    private static final DRShop main = DRShop.getInstance();

    private static final ItemStack add1 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 1");
    private static final ItemStack add5 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 5");
    private static final ItemStack add10 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 10");

    private static final ItemStack set64 = new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 64");

    private static final ItemStack rem1 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 1");
    private static final ItemStack rem5 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 5");
    private static final ItemStack rem10 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 10");

    private static final ItemStack set1 = new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 64");

    private static final ItemStack blackGlass = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE)
            .setName("&c");

    private static final int MAX_AMOUNT = 64 * 9 * 4;

    private final BiConsumer<ItemStack, Integer> c;
    private final Consumer<Player> b;

    private final ItemStack item;
    private final dShop.dShopT type;

    private final String title;
    private final String confirmLore;
    private final String backLore;

    private InventoryGUI gui;
    private int amount = 1;

    private confirmGui(
            Player p,
            BiConsumer<ItemStack, Integer> accept,
            Consumer<Player> back,
            ItemStack item,
            dShop.dShopT type,
            String title,
            String acceptLore,
            String backLore
    ) {
        this.c = accept;
        this.b = back;
        this.title = title;
        this.item = item.clone();
        this.type = type;
        this.confirmLore = acceptLore;
        this.backLore = backLore;

        Events.subscribe(InventoryCloseEvent.class)
                .filter(e -> e.getInventory().equals(gui.getInventory()))
                .filter(e -> e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                .biHandler((subscription, e) -> {
                    subscription.unregister();
                    gui.destroy();
                });

        init();
        gui.open(p);

    }

    public static void open(
            Player player,
            ItemStack item,
            dShop.dShopT type,
            BiConsumer<ItemStack, Integer> accept,
            Consumer<Player> back,
            String title,
            String acceptLore,
            String backLore
    ) {

        new confirmGui(player, accept, back, item, type, title, acceptLore, backLore);
    }

    private void init() {

        gui = new InventoryGUI(main, 54, title);

        gui.getInventory().addItem(item);
        IntStream.range(36, 54).forEach(value -> gui.getInventory().setItem(value, blackGlass));

        gui.addButton(ItemButton.create(add1, e -> {
            if (amount == MAX_AMOUNT) return;
            addItem(1);
            update();
        }), 42);

        gui.addButton(ItemButton.create(add5, e -> {
            if (amount >= MAX_AMOUNT - 5) return;
            addItem(5);
            update();
        }), 43);

        gui.addButton(ItemButton.create(add10, e -> {
            if (amount >= MAX_AMOUNT - 10) return;
            addItem(10);
            update();
        }), 44);

        gui.addButton(ItemButton.create(set64, e -> {
            if (amount >= MAX_AMOUNT - 64) return;
            addItem(64);
            update();
        }), 41);

        gui.addButton(ItemButton.create(rem1, e -> {
            if (amount == 1) return;
            remItem(1);
            update();
        }), 36);

        gui.addButton(ItemButton.create(rem10, e -> {
            if (amount <= 5) return;
            remItem(5);
            update();
        }), 37);

        gui.addButton(ItemButton.create(rem10, e -> {
            if (amount <= 10) return;
            remItem(10);
            update();
        }), 38);

        gui.addButton(ItemButton.create(set1, e -> {
            if (amount <= 64) return;
            remItem(64);
            update();
        }), 39);

        gui.addButton(53, ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setName(backLore).setLore(conf_msg.CONFIRM_GUI_RETURN_PANE_LORE)
                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"),
                e -> b.accept((Player) e.getWhoClicked())));

        gui.addButton(49, ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f")
                .setName(confirmLore)
                .addLore(Msg.singletonMsg(conf_msg.SELL_ITEM_NAME).add("\\{price}",
                        String.valueOf(amount * (type.equals(dShop.dShopT.buy) ?
                                dItem.of(item).getBuyPrice().get().getPrice():
                                dItem.of(item).getSellPrice().get().getPrice()))).build()),
                e -> c.accept(item, amount)));

        update();

    }

    private void update() {

        amount = ItemUtils.count(gui.getInventory(), item);
        Inventory inv = gui.getInventory();

        inv.setItem(36, amount > 1 ? rem1: blackGlass);
        inv.setItem(37, amount > 5 ? rem5: blackGlass);
        inv.setItem(38, amount > 10 ? rem10: blackGlass);
        inv.setItem(39, amount > 1 ? set1: blackGlass);
        inv.setItem(42, amount < MAX_AMOUNT ? add1: blackGlass);
        inv.setItem(43, amount < MAX_AMOUNT - 5 ? add5: blackGlass);
        inv.setItem(44, amount < MAX_AMOUNT - 10 ? add10: blackGlass);
        inv.setItem(41, amount < MAX_AMOUNT - 64 ? set64: blackGlass);

        gui.getInventory().setItem(49, new ItemBuilder(XMaterial.PLAYER_HEAD)
                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f")
                .setName(confirmLore)
                .addLore(Msg.singletonMsg(conf_msg.SELL_ITEM_NAME).add("\\{price}",
                        String.valueOf(amount * (type.equals(dShop.dShopT.buy) ?
                                dItem.of(item).getBuyPrice().get().getPrice():
                                dItem.of(item).getSellPrice().get().getPrice()))).build()));

    }

    private void addItem(int amount) {

        ItemStack toAdd = item.clone();
        toAdd.setAmount(amount);
        gui.getInventory().addItem(toAdd);

    }

    private void remItem(int amount) {
        int auxAmount = ItemUtils.count(gui.getInventory(), item) - amount;
        IntStream.range(0, 36).forEach(value -> gui.getInventory().clear(value));
        IntStream.range(0, auxAmount).forEach(value -> gui.getInventory().addItem(item));
    }

}
