package io.github.divios.dailyrandomshop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Bukkit;
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

    private static final DRShop main = DRShop.getInstance();

    private static ItemStack add1 = null;
    private static ItemStack add5;
    private static ItemStack add10;
    private static ItemStack rem1;
    private static ItemStack rem5;
    private static ItemStack rem10;
    private static ItemStack confirm;
    private static ItemStack back;
    private final BiConsumer<Player, ItemStack> c;
    private final Consumer<Player> b;

    private confirmGui(
            Player p,
            BiConsumer<Player,
            ItemStack> c, Consumer<Player> b,
            ItemStack item
            ) {
        this.c = c;
        this.b = b;
        Bukkit.getPluginManager().registerEvents(this, main);
        p.openInventory(getInventory(item));
    }

    public static void open(
            Player p,
            ItemStack item,
            BiConsumer<Player, ItemStack> c,
            Consumer<Player> b
            ) {
        if (add1 == null) {
            init();
        }
        
        new confirmGui(p, c, b, item);

    }

    private static void init() {
        add1 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 1");
        add5 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 5");
        add10 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 10");

        rem1 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 1");
        rem5 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 5");
        rem10 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 10");

        back = new ItemBuilder(XMaterial.OAK_SIGN)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE);

        confirm = new ItemBuilder(XMaterial.EMERALD_BLOCK)
                .setName(conf_msg.CONFIRM_GUI_CONFIRM_PANE);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public Inventory getInventory(ItemStack item) {
        Inventory inv = Bukkit.createInventory(this, 45, conf_msg.CONFIRM_GUI_NAME);

        ItemStack confirmAux = confirm.clone();
        //LoreStrategy.setLore(confirmAux, item);

        inv.setItem(24, add1);
        inv.setItem(25, add5);
        inv.setItem(26, add10);
        inv.setItem(36, back);
        inv.setItem(40, confirmAux);
        inv.setItem(22, item);

        return inv;
    }

    private void updateInventory(Inventory inv, Player p) {
        int nStack = inv.getItem(22).getAmount();

        inv.setItem(18, nStack > 1 ? rem1: XMaterial.AIR.parseItem());
        inv.setItem(19, nStack > 5 ? rem5: XMaterial.AIR.parseItem());
        inv.setItem(20, nStack > 10 ? rem10: XMaterial.AIR.parseItem());
        inv.setItem(24, nStack < 64 ? add1: XMaterial.AIR.parseItem());
        inv.setItem(25, nStack < 60 ? add5: XMaterial.AIR.parseItem());
        inv.setItem(26, nStack < 55 ? add10: XMaterial.AIR.parseItem());


        //LoreStrategy.update(inv.getItem(40), inv.getItem(22));
        p.updateInventory();
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
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
