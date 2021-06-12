package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.lib.itemHolder.dItem;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class miniCustomizeGui {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private ItemStack item;
    private final Consumer<ItemStack> consumer;
    private final InventoryGUI inv;

    private boolean preventCloseB = false;      // preventClose glitch

    private final EventListener<PlayerPickupItemEvent> preventPicks;
    private final EventListener<InventoryCloseEvent> preventClose;

    public miniCustomizeGui(Player p,
                            ItemStack item,
                            Consumer<ItemStack> consumer) {
        this.p = p;
        this.item = item;
        this.consumer = consumer;
        this.inv = getGui();
        this.preventPicks = new EventListener<>(plugin,
                PlayerPickupItemEvent.class, EventPriority.HIGHEST, e -> {
            if (e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                e.setCancelled(true);
        });

        this.preventClose = new EventListener<>(plugin, InventoryCloseEvent.class,
                EventPriority.HIGHEST, this::preventClose);

        new EventListener<>(plugin, PlayerKickEvent.class,
                (own, e) -> {
                    if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                        return;

                    preventClose.unregister();
                    preventPicks.unregister();
                    inv.destroy();
                    own.unregister();
                });

        new EventListener<>(plugin, PlayerQuitEvent.class,
                (own, e) -> {
                    if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                        return;

                    preventClose.unregister();
                    preventPicks.unregister();
                    inv.destroy();
                    own.unregister();
                });

        //inv.preventPlayerInvSlots();
        inv.setDestroyOnClose(false);
        inv.open(p);
        preventCloseB = true;           // prevent close glitch
    }


    private InventoryGUI getGui() {

        InventoryGUI gui = new InventoryGUI(plugin, 54, "");

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.NAME_TAG)
                .setName("&b&lChange name").addLore("&7Click to change the item's name")
                , e -> {
                    preventCloseB = false;
                    new AnvilGUI.Builder()
                        .onClose((player) -> Task.syncDelayed(plugin, () -> {
                            inv.open(p);
                            preventCloseB = true;
                            }, 1L))
                        .onComplete((player, s) -> {
                            item = ItemUtils.setName(item, s);
                            refreshItem();
                            return AnvilGUI.Response.close();
                        })
                        .title(FormatUtils.color("&cSet name"))
                        .itemLeft(item.clone())
                        .plugin(plugin)
                        .open(p);
                }
        ), 10);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SLIME_BALL)
                .setName("&b&lChange material").addLore("&7Click to change the item's material")
                , e -> {
                    preventCloseB = false;
                    materialsPrompt.open(plugin, p, (aBoolean, material) -> {
                        if (aBoolean)
                            item.setType(material);
                        refreshItem();
                        inv.open(p);
                        preventCloseB = true;
                    });
                }
        ), 19);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.PAPER)
                .setName("&b&lAdd/remove Lore").addLore("&7Left Click to add lore",
                        "&7Right Click to remove lore"),
                e -> {
                    preventCloseB = false;
                    if (e.isLeftClick()) {
                        new AnvilGUI.Builder()
                                .onClose(player ->
                                        Task.syncDelayed(plugin, () -> {
                                            refreshItem();
                                            inv.open(p);
                                            preventCloseB = true;
                                            }, 1L))
                                .onComplete((player, s) -> {
                                    item = ItemUtils.addLore(item, s);
                                    return AnvilGUI.Response.close();
                                })
                                .title(FormatUtils.color("&cAdd lore"))
                                .text("lore")
                                .itemLeft(item)
                                .plugin(plugin)
                                .open(p);
                    } else if (e.isRightClick()) {
                        item = ItemUtils.removeLore(item, 1);
                        refreshItem();
                    }


        }), 28);

        gui.addButton(43, ItemButton.create(new ItemBuilder(XMaterial.STICKY_PISTON)
            .setName("&c&lAdd actions").setLore("&7Action to perform when this", "&7item is clicked"),
                e-> {
                    preventCloseB = false;
                    customizeAction.open(p, (dAction, s) -> {
                        dItem aux = new dItem(item);
                        aux.setAction(dAction, s);
                        item = aux.getItem();
                        refreshItem();
                        inv.open(p);
                    });
                }));

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
            .setName("&e&lSet material as base64").setLore("&7Set this item's material",
                        "&7from url or base64").applyTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTljNzY5MDgzMTYzZTlhZWJkOGVkNWQ2NmJlYmNiOWRmMjFjYWJhZTYzYmFhYWEwZDNhYmUxNDIwYTRhYjU4ZiJ9fX0="),
                e -> {
                preventCloseB = false;
                p.closeInventory();
                new ChatPrompt(plugin, p, (player, s) -> {
                    item.setType(XMaterial.PLAYER_HEAD.parseMaterial());
                    item = ItemUtils.applyTexture(item, s);
                    refreshItem();
                    inv.open(p);
                    preventCloseB = true;

                }, inv::open, "", FormatUtils.color("Input base64 texture"));
        }), 34);

        gui.addButton(ItemButton.create(item.clone(), e -> {}), 22);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SPRUCE_SIGN)
                .setName("&b&lGo back").addLore("&7Click to go back")
                , e-> {
                    preventClose.unregister();
                    consumer.accept(item);
                    inv.destroy();
                    preventPicks.unregister();
                }), 49);

        //gui.preventPlayerInvSlots();
        return gui;
    }

    private void refreshItem() {
        inv.addButton(ItemButton.create(item.clone(), e -> {}), 22);
    }

    private void preventClose(InventoryCloseEvent e) {

        if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
            return;

        if (!preventCloseB) return;

        Task.syncDelayed(plugin, () -> inv.open(p), 1L);
    }
}
