package io.github.divios.dailyrandomshop.guis;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.lorestategy.confirmItemsLore;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class confirmGui implements Listener, InventoryHolder {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();

    private static confirmGui instance = null;
    private static ItemStack add1 = null;
    private static ItemStack add5;
    private static ItemStack add10;
    private static ItemStack rem1;
    private static ItemStack rem5;
    private static ItemStack rem10;
    private static ItemStack confirm;
    private static ItemStack back;
    private static final confirmItemsLore LoreStrategy = new confirmItemsLore();
    private BiConsumer<Player, ItemStack> c;
    private Consumer<Player> b;

    private confirmGui() {};

    public static void openInventory(
            Player p,
            ItemStack item,
            BiConsumer<Player, ItemStack> c,
            Consumer<Player> b
            ) {
        if (add1 == null) {
            init();
        }
        instance = new confirmGui();
        instance.c = c;
        instance.b = b;
        Bukkit.getPluginManager().registerEvents(instance, main);
        p.openInventory(instance.getInventory(item));
    }

    private static void init() {
        add1 = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
        add5 = add1.clone();
        add10 = add1.clone();

        utils.setDisplayName(add1, conf_msg.CONFIRM_GUI_ADD_PANE + " 1");
        utils.setDisplayName(add5, conf_msg.CONFIRM_GUI_ADD_PANE + " 5");
        utils.setDisplayName(add10, conf_msg.CONFIRM_GUI_ADD_PANE + " 10");

        rem1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        rem5 = rem1.clone();
        rem10 = rem1.clone();

        utils.setDisplayName(rem1, conf_msg.CONFIRM_GUI_REMOVE_PANE + " 1");
        utils.setDisplayName(rem5, conf_msg.CONFIRM_GUI_REMOVE_PANE + " 5");
        utils.setDisplayName(rem10, conf_msg.CONFIRM_GUI_REMOVE_PANE + " 10");

        back = XMaterial.OAK_SIGN.parseItem();
        utils.setDisplayName(back, "&c&lBack");

        confirm = XMaterial.EMERALD_BLOCK.parseItem();
        utils.setDisplayName(confirm, "&a&lConfirm");
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public Inventory getInventory(ItemStack item) {
        Inventory inv = Bukkit.createInventory(this, 45, utils.formatString("&a&lConfirm Gui"));

        ItemStack confirmAux = confirm.clone();
        LoreStrategy.setLore(confirmAux, item);

        inv.setItem(24, add1);
        inv.setItem(25, add5);
        inv.setItem(26, add10);
        inv.setItem(36, back);
        inv.setItem(40, confirmAux);
        inv.setItem(22, item);

        return inv;
    }

    private void updateInventory(Inventory inv, Player p) {
        int nstack = inv.getItem(22).getAmount();

        if( nstack > 1) inv.setItem(18, rem1);
        else inv.setItem(18, new ItemStack(Material.AIR));

        if( nstack > 5) inv.setItem(19, rem5);
        else inv.setItem(19, new ItemStack(Material.AIR));

        if( nstack > 10) inv.setItem(20, rem10);
        else inv.setItem(20, new ItemStack(Material.AIR));

        if( nstack < 64) inv.setItem(24, add1);
        else inv.setItem(24, new ItemStack(Material.AIR));

        if( nstack < 60) inv.setItem(25, add5);
        else inv.setItem(25, new ItemStack(Material.AIR));

        if( nstack < 55) inv.setItem(26, add10);
        else inv.setItem(26, new ItemStack(Material.AIR));

        if(utils.isEmpty(dailyItem.getRawItem(inv.getItem(22)))) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_REMOVED_ITEM);
            b.accept(p);     /* Close inv if item is deleted */
        }

        LoreStrategy.update(inv.getItem(40), inv.getItem(22));
        p.updateInventory();
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != instance) return;
        e.setCancelled(true);

        if (e.getSlot() != e.getRawSlot()) return;
        if(utils.isEmpty(e.getCurrentItem())) return;

        int slot = e.getSlot();
        Inventory inv = e.getView().getTopInventory();
        ItemStack item = inv.getItem(22);
        Player p = (Player) e.getWhoClicked();

        if (slot == 36) b.accept(p);    /* Boton de back */
        if( slot == 40 ) c.accept(p, item);     /* Boton de confirmar */

        if (slot == 24) item.setAmount(item.getAmount() + 1);
        else if (slot == 25) item.setAmount(item.getAmount() + 5);
        else if (slot == 26) item.setAmount(item.getAmount() + 10);

        else if (slot == 18) item.setAmount(item.getAmount() - 1);
        else if (slot == 19) item.setAmount(item.getAmount() - 5);
        else if (slot == 20) item.setAmount(item.getAmount() - 10);

        updateInventory(inv, p);
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;

        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

}
