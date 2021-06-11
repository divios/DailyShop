package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyrandomshop.DRShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class miniCustomizeGui {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private ItemStack item;
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

        //inv.preventPlayerInvSlots();
        inv.setDestroyOnClose(false);
        inv.open(p);
    }


    private InventoryGUI getGui() {

        InventoryGUI gui = new InventoryGUI(plugin, 54, "");

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.NAME_TAG)
                .setName("&b&lChange name").addLore("&7Click to change the item's name")
                , e -> new AnvilGUI.Builder()
                        .onClose((player) -> Task.syncDelayed(plugin, () -> inv.open(p), 1L))
                        .onComplete((player, s) -> {
                            item = ItemUtils.setName(item, s);
                            refreshItem();
                            return AnvilGUI.Response.close();
                        })
                        .title(FormatUtils.color("&cSet name"))
                        .itemLeft(item.clone())
                        .plugin(plugin)
                        .open(p)), 10);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SLIME_BALL)
                .setName("&b&lChange material").addLore("&7Click to change the item's material")
                , e -> materialsPrompt.open(plugin, p, (aBoolean, material) -> {
                    if (aBoolean)
                        item.setType(material);
                    refreshItem();
                    inv.open(p);
                })), 19);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE)
                .setName("&a&lSet as AIR").setLore("&7Set this slot as air, meaning it will",
                        "&7be empty but no dailyItem will appear"),
                e -> {
                    consumer.accept(XMaterial.AIR.parseItem());
                    inv.destroy();
                    preventPicks.unregister();
                }), 44);

        gui.addButton(ItemButton.create(item.clone(), e -> {}), 22);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SPRUCE_SIGN)
                .setName("&b&lGo back").addLore("&7Click to go back")
                , e-> {
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
}
