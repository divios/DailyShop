package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import me.realized.tokenmanager.util.Log;
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
import java.util.stream.IntStream;

public class confirmGui implements Listener, InventoryHolder {

    private static final DailyShop main = DailyShop.getInstance();

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
    private final BiConsumer<ItemStack, Integer> c;
    private final Consumer<Player> b;

    private final ItemStack item;
    private final dShop.dShopT type;

    private final String title;
    private final String confirmLore;
    private final String backLore;

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
        Bukkit.getPluginManager().registerEvents(this, main);
        init();

        p.openInventory(getInventory(item));
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
        add1 = new ItemBuilder(XMaterial.LIME_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 1");
        add5 = new ItemBuilder(XMaterial.LIME_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 16");
        add10 = new ItemBuilder(XMaterial.LIME_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 32");

        set64 = new ItemBuilder(XMaterial.LIME_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 64");

        rem1 = new ItemBuilder(XMaterial.RED_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 1");
        rem5 = new ItemBuilder(XMaterial.RED_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 16");
        rem10 = new ItemBuilder(XMaterial.RED_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 32");

        set1 =  new ItemBuilder(XMaterial.RED_CONCRETE)
                .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 64");

        back = new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setName(backLore).setLore(main.configM.getLangYml().CONFIRM_GUI_RETURN_PANE_LORE)
                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf");

        add5.setAmount(16);
        add10.setAmount(32);
        set64.setAmount(64);

        rem5.setAmount(16);
        rem10.setAmount(32);
        set1.setAmount(64);

        confirm = new ItemBuilder(XMaterial.PLAYER_HEAD)
                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f")
                .setName(confirmLore)
                .addLore(Msg.singletonMsg(main.configM.getLangYml().CONFIRM_GUI_BUY_NAME).add("\\{price}",
                        String.valueOf(item.getAmount() * (type.equals(dShop.dShopT.buy) ?
                                dItem.of(item).getBuyPrice().get().getPrice():
                                dItem.of(item).getSellPrice().get().getPrice()))).build());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public Inventory getInventory(ItemStack item) {
        Inventory inv = Bukkit.createInventory(this, 27, FormatUtils.color(title));

        IntStream.range(0, 27).forEach(value -> inv.setItem(value,
                ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")));

        inv.setItem(14, add1);
        inv.setItem(15, add5);
        inv.setItem(16, add10);
        inv.setItem(17, set64);
        inv.setItem(8, back);
        inv.setItem(22, confirm);
        inv.setItem(13, item);

        return inv;
    }

    private void updateInventory(Inventory inv, Player p) {
        int nStack = inv.getItem(13).getAmount();

        inv.setItem(9, nStack == 64 ? set1: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));
        inv.setItem(10, nStack > 32 ? rem10: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));
        inv.setItem(11, nStack > 16 ? rem5: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));
        inv.setItem(12, nStack > 1 ? rem1: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));
        inv.setItem(14, nStack < 64 ? add1: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));
        inv.setItem(15, nStack < 38 ? add5: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));
        inv.setItem(16, nStack < 32 ? add10: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));
        inv.setItem(17, nStack == 1 ? set64: ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c"));


        inv.setItem(22, new ItemBuilder(XMaterial.PLAYER_HEAD)
                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f")
                .setName(confirmLore)
                .addLore(Msg.singletonMsg(main.configM.getLangYml().CONFIRM_GUI_SELL_ITEM).add("\\{price}",
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

        if (e.getCurrentItem().getType().name().equals("GRAY_STAINED_GLASS_PANE"))
            return;

        int slot = e.getSlot();
        Inventory inv = e.getView().getTopInventory();
        ItemStack item = inv.getItem(13);
        Player p = (Player) e.getWhoClicked();

        if (slot == 8) b.accept(p);    /* Boton de back */
        if( slot == 22 ) c.accept(item, item.getAmount());     /* Boton de confirmar */

        else if (slot == 14) item.setAmount(item.getAmount() + 1);
        else if (slot == 15) item.setAmount(item.getAmount() + 16);
        else if (slot == 16) item.setAmount(item.getAmount() + 32);
        else if (slot == 17) item.setAmount(64);

        else if (slot == 9) item.setAmount(1);
        else if (slot == 10) item.setAmount(item.getAmount() - 32);
        else if (slot == 11) item.setAmount(item.getAmount() - 16);
        else if (slot == 12) item.setAmount(item.getAmount() - 1);

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