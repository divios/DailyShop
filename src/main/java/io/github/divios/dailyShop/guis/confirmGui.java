package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
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

    private  ItemStack add1 = null;
    private  ItemStack add5;
    private  ItemStack add10;
    private  ItemStack rem1;
    private  ItemStack rem5;
    private  ItemStack rem10;
    private  ItemStack confirm;
    private  ItemStack back;
    private  ItemStack set64;
    private  ItemStack set1;
    private final BiConsumer<Player, ItemStack> c;
    private final Consumer<Player> b;

    private final ItemStack item;
    private final dShop.dShopT type;

    private final String title;
    private final String confirmLore;
    private final String backLore;

    private confirmGui(
            Player p,
            BiConsumer<Player, ItemStack> accept,
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
        Bukkit.getPluginManager().registerEvents(this, main);
        init();

        p.openInventory(getInventory(item));
    }

    public static void open(
            Player player,
            ItemStack item,
            dShop.dShopT type,
            BiConsumer<Player, ItemStack> accept,
            Consumer<Player> back,
            String title,
            String acceptLore,
            String backLore
            ) {

        new confirmGui(player, accept, back, item, type, title, acceptLore, backLore);

    }

    private void init() {
        add1 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 1");
        add5 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 5");
        add10 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_ADD_PANE + " 10");

        set64 = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName("&a&lSet to 64");

        rem1 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 1");
        rem5 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 5");
        rem10 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(conf_msg.CONFIRM_GUI_REMOVE_PANE + " 10");

        set1 = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName("&c&lSet to 1");

        back = new ItemBuilder(XMaterial.OAK_DOOR)
                .setName(backLore).setLore(conf_msg.CONFIRM_GUI_RETURN_PANE_LORE);

        confirm = new ItemBuilder(XMaterial.EMERALD_BLOCK)
                .setName(confirmLore)
                .addLore(Msg.singletonMsg(conf_msg.SELL_ITEM_NAME).add("\\{price}",
                        String.valueOf(item.getAmount() * (type.equals(dShop.dShopT.buy) ?
                        dItem.of(item).getBuyPrice().get().getPrice():
                        dItem.of(item).getSellPrice().get().getPrice()))).build());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public Inventory getInventory(ItemStack item) {
        Inventory inv = Bukkit.createInventory(this, 45, title);

        inv.setItem(24, add1);
        inv.setItem(25, add5);
        inv.setItem(26, add10);
        inv.setItem(16, set64);
        inv.setItem(36, back);
        inv.setItem(40, confirm);
        inv.setItem(22, item);

        return inv;
    }

    private void updateInventory(Inventory inv, Player p) {
        int nStack = inv.getItem(22).getAmount();

        inv.setItem(18, nStack > 1 ? rem1: XMaterial.AIR.parseItem());
        inv.setItem(10, nStack > 1 ? set1: XMaterial.AIR.parseItem());
        inv.setItem(19, nStack > 5 ? rem5: XMaterial.AIR.parseItem());
        inv.setItem(20, nStack > 10 ? rem10: XMaterial.AIR.parseItem());
        inv.setItem(24, nStack < 64 ? add1: XMaterial.AIR.parseItem());
        inv.setItem(25, nStack < 60 ? add5: XMaterial.AIR.parseItem());
        inv.setItem(26, nStack < 55 ? add10: XMaterial.AIR.parseItem());
        inv.setItem(16, nStack < 64 ? set64: XMaterial.AIR.parseItem());


        inv.setItem(40, new ItemBuilder(XMaterial.EMERALD_BLOCK)
                .setName(confirmLore)
                .addLore(Msg.singletonMsg(conf_msg.SELL_ITEM_NAME).add("\\{price}",
                        String.valueOf(nStack * (type.equals(dShop.dShopT.buy) ?
                                dItem.of(item).getBuyPrice().get().getPrice():
                                dItem.of(item).getSellPrice().get().getPrice()))).build()));
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

        if (slot == 10) item.setAmount(1);
        else if (slot == 24) item.setAmount(item.getAmount() + 1);
        else if (slot == 25) item.setAmount(item.getAmount() + 5);
        else if (slot == 26) item.setAmount(item.getAmount() + 10);

        else if (slot == 16) item.setAmount(64);
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
