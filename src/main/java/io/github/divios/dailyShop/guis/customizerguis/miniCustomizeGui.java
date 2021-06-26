package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.*;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.lib.itemHolder.dAction;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
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
    private final dShop shop;
    private final Consumer<ItemStack> consumer;
    private InventoryGUI inv;

    private boolean preventCloseB = false;      // preventClose glitch

    private final EventListener<PlayerPickupItemEvent> preventPicks;
    private final EventListener<InventoryCloseEvent> preventClose;

    public miniCustomizeGui(Player p,
                            dShop shop,
                            ItemStack item,
                            Consumer<ItemStack> consumer) {
        this.p = p;
        this.item = item;
        this.shop = shop;
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
                        .onClose((player) -> Task.syncDelayed(plugin, this::refresh, 1L))
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
                        if (aBoolean) {
                            item.setType(material.parseMaterial());
                            p.sendMessage(material.name());
                            if (material.name().contains("GLASS"))
                                item.setDurability(material.parseItem().getDurability());
                        }
                        refresh();
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
                                        Task.syncDelayed(plugin, this::refresh, 1L))
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

        Pair<dAction, String> action = new dItem(item).getAction();

        gui.addButton(43, ItemButton.create(new ItemBuilder(XMaterial.STICKY_PISTON)
            .setName("&c&lAdd actions").setLore("&7Action to perform when this", "&7item is clicked",
                        "", "&6Current action: &7" + action.get1() + ":" + action.get2()),
                e-> {
                    preventCloseB = false;
                    customizeAction.open(p, shop, (dAction, s) -> {
                        dItem aux = new dItem(item);
                        aux.setAction(dAction, s);
                        item = aux.getItem();
                        refresh();
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
                    refresh();

                }, inv::open, FormatUtils.color("&7Input base64 texture"),"");
        }), 34);

        gui.addButton(ItemButton.create(item.clone(), e -> {}), 22);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.OAK_DOOR)
                .setName("&b&lGo back").addLore("&7Click to go back")
                , e-> {
                    preventClose.unregister();
                    consumer.accept(item);
                    inv.destroy();
                    preventPicks.unregister();
                }), 49);

        //gui.preventPlayerInvSlots();
        gui.setDestroyOnClose(false);
        return gui;
    }

    private void refreshItem() {
        inv.addButton(ItemButton.create(item.clone(), e -> {}), 22);
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

        if (e.getInventory() != inv.getInventory()) return;

        if (!preventCloseB) return;

        preventPicks.unregister();
        Task.syncDelayed(plugin, () -> inv.open(p), 1L);
    }
}
