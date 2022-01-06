package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.newDItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class miniCustomizeGui {

    private static final DailyShop plugin = DailyShop.get();

    private final Player p;
    private newDItem item;
    private final dShop shop;
    private final Consumer<newDItem> consumer;
    private InventoryGUI inv;

    private boolean preventCloseB = false;      // preventClose glitch

    private final Subscription preventPicks;
    private final Subscription preventClose;

    public miniCustomizeGui(Player p,
                            dShop shop,
                            newDItem item,
                            Consumer<newDItem> consumer) {
        this.p = p;
        this.item = item;
        this.shop = shop;
        this.consumer = consumer;
        this.inv = getGui();
        this.preventPicks = createItemPickUpEvent(p);
        this.preventClose = createPreventCloseEvent();

        createPlayerKickEvent();
        createPlayerQuickEvent();

        inv.open(p);
        preventCloseB = true;           // prevent close glitch

    }

    @NotNull
    private Subscription createPreventCloseEvent() {
        return Events.subscribe(InventoryCloseEvent.class, EventPriority.HIGHEST)
                .handler(this::preventClose);
    }

    private void createPlayerQuickEvent() {
        new EventListener<>(PlayerQuitEvent.class,
                (own, e) -> {
                    if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                        return;

                    preventClose.unregister();
                    preventPicks.unregister();
                    inv.destroy();
                    own.unregister();
                });
    }

    private void createPlayerKickEvent() {
        new EventListener<>(PlayerKickEvent.class,
                (own, e) -> {
                    if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                        return;

                    preventClose.unregister();
                    preventPicks.unregister();
                    inv.destroy();
                    own.unregister();
                });
    }

    @NotNull
    private Subscription createItemPickUpEvent(Player p) {
        return Events.subscribe(PlayerPickupItemEvent.class, EventPriority.HIGHEST)
                .handler(e -> {
                    if (e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                        e.setCancelled(true);
                });
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
                        ItemStack toChange = item.getItem();
                        toChange = ItemUtils.setName(toChange, s);
                        item.setItem(toChange);
                        refreshItem();
                        Schedulers.sync().run(this::refresh);
                    }, cause -> Schedulers.sync().run(this::refresh), "&6&lInput Item Name", "");
                }
        ), 11);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.SLIME_BALL)
                        .setName("&b&lChange material").addLore("&7Click to change the item's material")
                , e -> {
                    preventCloseB = false;
                    materialsPrompt.open(plugin, p, (aBoolean, material) -> {
                        if (aBoolean) {
                            ItemStack toChange = item.getItem();
                            toChange = ItemUtils.setMaterial(toChange, material);
                            item.setItem(toChange);
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
                        ChatPrompt.builder()
                                .withPlayer(p)
                                .withResponse(s -> {
                                    ItemStack toChange = item.getItem();
                                    item.setItem(ItemUtils.addLore(toChange, s));
                                    Schedulers.sync().run(this::refresh);
                                })
                                .withCancel(cancelReason -> refresh())
                                .withTitle("&e&lInput Lore")
                                .prompt();

                    } else if (e.isRightClick()) {

                        if (ItemUtils.getLore(item.getItem()).isEmpty()) return;

                        ItemStack toChange = item.getItem();
                        toChange = ItemUtils.removeLore(toChange, ItemUtils.getLore(toChange).size() - 1);
                        item.setItem(toChange);
                        refreshItem();
                    }


                }), 29);

        newDItem.WrapperAction action = item.getAction();

        gui.addButton(24, ItemButton.create(ItemBuilder.of(XMaterial.STICKY_PISTON)
                        .setName("&c&lAdd actions").setLore("&7Action to perform when this", "&7item is clicked",
                                "", "&6Current action: &7" + action.getAction().name() + ":" + action.getData()),
                e -> {
                    preventCloseB = false;
                    customizeAction.open(p, shop, (dAction, s) -> {
                        item.setAction(dAction, s);
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
                                ItemStack toChange = item.getItem();
                                toChange = ItemUtils.setMaterial(toChange, XMaterial.PLAYER_HEAD);
                                toChange = ItemUtils.applyTexture(toChange, s);
                                item.setItem(toChange);
                                Schedulers.sync().run(this::refresh);

                            }, cause -> Schedulers.sync().run(this::refresh),
                            FormatUtils.color("&7Input base64 texture"), "");
                }), 23);

        gui.addButton(ItemButton.create(item.getItem(), e -> {
        }), 5);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                        .setName("&b&lGo back").addLore("&7Click to go back")
                        .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                , e -> {
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
        inv.addButton(ItemButton.create(item.getItem(), e -> {
        }), 5);
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
        Schedulers.sync().runLater(() -> inv.open(p), 1L);
    }

    public static miniCustomizeGuiBuilder builder() {
        return new miniCustomizeGuiBuilder();
    }

    public static final class miniCustomizeGuiBuilder {
        private Player p;
        private newDItem item;
        private dShop shop;
        private Consumer<newDItem> consumer;

        private miniCustomizeGuiBuilder() {
        }

        public miniCustomizeGuiBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public miniCustomizeGuiBuilder withItem(newDItem item) {
            this.item = item;
            return this;
        }

        public miniCustomizeGuiBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public miniCustomizeGuiBuilder withConsumer(Consumer<newDItem> consumer) {
            this.consumer = consumer;
            return this;
        }

        public miniCustomizeGui build() {

            Preconditions.checkNotNull(p, "player null");
            Preconditions.checkNotNull(shop, "shop null");
            Preconditions.checkNotNull(consumer, "consumer null");

            return new miniCustomizeGui(p, shop, item, consumer);
        }
    }
}
