package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.*;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class miniCustomizeGui {

    private static final DailyShop plugin = DailyShop.getInstance();

    private final Player p;
    private ItemStack item;
    private final dShop shop;
    private final Consumer<ItemStack> consumer;
    private InventoryGUI inv;

    private boolean preventCloseB = false;      // preventClose glitch

    private final EventListener<PlayerPickupItemEvent> preventPicks;
    private final EventListener<InventoryCloseEvent> preventClose;

    @Deprecated
    public miniCustomizeGui(Player p,
                            dShop shop,
                            ItemStack item,
                            Consumer<ItemStack> consumer) {
        this.p = p;
        this.item = item;
        this.shop = shop;
        this.consumer = consumer;
        this.inv = getGui();
        this.preventPicks = new EventListener<>(
                PlayerPickupItemEvent.class, EventPriority.HIGHEST, e -> {
            if (e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                e.setCancelled(true);
        });

        this.preventClose = new EventListener<>(InventoryCloseEvent.class,
                EventPriority.HIGHEST, this::preventClose);

        new EventListener<>(PlayerKickEvent.class,
                (own, e) -> {
                    if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                        return;

                    preventClose.unregister();
                    preventPicks.unregister();
                    inv.destroy();
                    own.unregister();
                });

        new EventListener<>(PlayerQuitEvent.class,
                (own, e) -> {
                    if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                        return;

                    preventClose.unregister();
                    preventPicks.unregister();
                    inv.destroy();
                    own.unregister();
                });

        inv.open(p);
        preventCloseB = true;           // prevent close glitch
    }


    private InventoryGUI getGui() {

        InventoryGUI gui = new InventoryGUI(plugin, 54, "");

        IntStream.of(0, 1, 2, 9, 18, 38, 22, 31, 14, 51, 52, 53, 44, 35)
                .forEach(value -> gui.getInventory().setItem(value, ItemBuilder.of(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .setName("&c")));

        IntStream.of(3, 4, 13, 15, 16, 17, 26, 27, 36, 37, 39, 40, 49, 50)
                .forEach(value -> gui.getInventory().setItem(value, ItemBuilder.of(XMaterial.BLUE_STAINED_GLASS_PANE)
                        .setName("&c")));

        IntStream.of(10, 11, 12, 19, 20, 21, 28, 29, 30,
                5, 6, 7, 8, 23, 24, 25, 32, 33, 34, 41, 42, 43, 45, 46, 47, 48)
                .forEach(value -> gui.getInventory().setItem(value, ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE)
                        .setName("&c")));

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.NAME_TAG)
                .setName("&b&lChange name").addLore("&7Click to change the item's name")
                , e -> {
                    preventCloseB = false;
                    ChatPrompt.prompt(plugin, p, (s) -> {
                        item = ItemUtils.setName(item, s);
                        refreshItem();
                        Task.syncDelayed(plugin, this::refresh);
                    }, cause ->Task.syncDelayed(plugin, this::refresh), "&6&lInput Item Name", "");
                }
        ), 11);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.SLIME_BALL)
                .setName("&b&lChange material").addLore("&7Click to change the item's material")
                , e -> {
                    preventCloseB = false;
                    materialsPrompt.open(plugin, p, (aBoolean, material) -> {
                        if (aBoolean) {
                            item.setType(material.parseMaterial());
                            if (material.name().contains("GLASS"))
                                item.setDurability(material.parseItem().getDurability());
                        }
                        refresh();
                    });
                }
        ), 20);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PAPER)
                .setName("&b&lAdd/remove Lore").addLore("&7Left Click to add lore",
                        "&7Right Click to remove lore"),
                e -> {
                    preventCloseB = false;
                    if (e.isLeftClick()) {
                        ChatPrompt.prompt(plugin, p, (s) -> {
                            item = ItemUtils.addLore(item, s);
                            Task.syncDelayed(plugin, this::refresh);
                        }, player -> refresh(), "&e&lInput Lore", "");
                    } else if (e.isRightClick()) {
                        item = ItemUtils.removeLore(item, 1);
                        refreshItem();
                    }


        }), 29);

        Pair<dAction, String> action = new dItem(item).getAction();

        gui.addButton(24, ItemButton.create(ItemBuilder.of(XMaterial.STICKY_PISTON)
            .setName("&c&lAdd actions").setLore("&7Action to perform when this", "&7item is clicked",
                        "", "&6Current action: &7" + action.get1() + ":" + action.get2()),
                e-> {
                    preventCloseB = false;
                    customizeAction.open(p, shop, (dAction, s) -> {
                        dItem aux = new dItem(item);
                        aux.setAction(dAction, s);
                        item = aux.getItem();
                        refresh();
                    }, (p) -> refresh());
                }));

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PLAYER_HEAD)
            .setName("&e&lSet material as base64").setLore("&7Set this item's material",
                        "&7from url or base64").applyTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTljNzY5MDgzMTYzZTlhZWJkOGVkNWQ2NmJlYmNiOWRmMjFjYWJhZTYzYmFhYWEwZDNhYmUxNDIwYTRhYjU4ZiJ9fX0="),
                e -> {
                preventCloseB = false;
                p.closeInventory();
                ChatPrompt.prompt(plugin, p, (s) -> {
                    item.setType(XMaterial.PLAYER_HEAD.parseMaterial());
                    item = ItemUtils.applyTexture(item, s);
                    Task.syncDelayed(plugin, this::refresh);

                }, cause -> Task.syncDelayed(plugin, this::refresh),
                        FormatUtils.color("&7Input base64 texture"),"");
        }), 23);

        gui.addButton(ItemButton.create(item.clone(), e -> {}), 5);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                .setName("&b&lGo back").addLore("&7Click to go back")
                        .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                , e-> {
                    preventClose.unregister();
                    consumer.accept(item);
                    inv.destroy();
                    preventPicks.unregister();
                }), 8);

        //gui.preventPlayerInvSlots();
        gui.setDestroyOnClose(false);
        return gui;
    }

    private void refreshItem() {
        inv.addButton(ItemButton.create(item.clone(), e -> {}), 5);
    }

    private void refresh() {
        preventCloseB = false;
        inv.destroy();
        inv = getGui();
        refreshItem();
        inv.open(p);
        preventCloseB = true;
    }

    private void preventClose(InventoryCloseEvent e) {

        if (!e.getInventory().equals(inv.getInventory())) return;

        if (!preventCloseB) return;

        preventPicks.unregister();
        Task.syncDelayed(plugin, () -> inv.open(p), 1L);
    }

    public static miniCustomizeGuiBuilder builder() {
        return new miniCustomizeGuiBuilder();
    }

    public static final class miniCustomizeGuiBuilder {
        private Player p;
        private ItemStack item;
        private dShop shop;
        private Consumer<ItemStack> consumer;

        private miniCustomizeGuiBuilder() {}

        public miniCustomizeGuiBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public miniCustomizeGuiBuilder withItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public miniCustomizeGuiBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public miniCustomizeGuiBuilder withConsumer(Consumer<ItemStack> consumer) {
            this.consumer = consumer;
            return this;
        }

        public miniCustomizeGui build() {

            Preconditions.checkNotNull(p, "player null");
            Preconditions.checkNotNull(shop, "shop null");
            Preconditions.checkNotNull(item, "item null");
            Preconditions.checkNotNull(consumer, "consumer null");

            return new miniCustomizeGui(p, shop, item, consumer);
        }
    }
}
