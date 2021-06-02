package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.redLib.inventorygui.InventoryGUI;
import io.github.divios.dailyrandomshop.redLib.inventorygui.ItemButton;
import io.github.divios.dailyrandomshop.redLib.itemutils.ItemBuilder;
import io.github.divios.dailyrandomshop.utils.EventListener;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.function.Consumer;

public class miniCustomizeGui {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final ItemStack item;
    private final Consumer<ItemStack> consumer;
    private final InventoryGUI inv;
    private final EventListener<PlayerPickupItemEvent> preventPicks;

    public miniCustomizeGui(Player p,
                            ItemStack item,
                            Consumer<ItemStack> consumer) {
        this.p = p;
        this.item = item;
        this.consumer = consumer;
        this.inv = getGui();
        this.preventPicks = new EventListener<PlayerPickupItemEvent>(plugin,
                PlayerPickupItemEvent.class, EventPriority.HIGHEST, e -> {
            if (e.getPlayer().getUniqueId().equals(p.getUniqueId()))
                e.setCancelled(true);
        });

        inv.preventPlayerInvSlots();
        inv.setDestroyOnClose(false);
        inv.open(p);
    }


    private InventoryGUI getGui() {

        InventoryGUI gui = new InventoryGUI(54, "");

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.NAME_TAG)
                .setName("&b&lChange name").addLore("&7Click to change the item's name")
                , e -> new AnvilGUI.Builder()
                        .onClose((player) -> utils.runTaskLater(() -> inv.open(p), 1L))
                        .onComplete((player, s) -> {
                            utils.setDisplayName(item, s);
                            refreshItem();
                            return AnvilGUI.Response.close();
                        })
                        .title(utils.formatString("&cSet name"))
                        .itemLeft(item.clone())
                        .plugin(plugin)
                        .open(p)), 10);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SLIME_BALL)
                .setName("&b&lChange material").addLore("&7Click to change the item's material")
                , e -> changeMaterialGui.openInventory(p, (aBoolean, material) -> {
                    if (aBoolean)
                        item.setType(material);
                    refreshItem();
                    inv.open(p);
                })), 19);

        gui.addButton(ItemButton.create(item.clone(), e -> {}), 22);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SPRUCE_SIGN)
                .setName("&b&lGo back").addLore("&7Click to go back")
                , e-> {
                    consumer.accept(item);
                    inv.destroy();
                    preventPicks.unregister();
                }), 49);

        gui.preventPlayerInvSlots();
        return gui;
    }

    private void refreshItem() {
        inv.addButton(ItemButton.create(item.clone(), e -> {}), 22);
    }
}
