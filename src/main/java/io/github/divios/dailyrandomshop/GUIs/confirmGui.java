package io.github.divios.dailyrandomshop.GUIs;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class confirmGui implements Listener, InventoryHolder {

    private final ArrayList<Integer> interactSlots = new ArrayList();//{18, 19, 20, 24, 25, 26};
    private final ItemStack add1 = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
    private final ItemStack add5 = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
    private final ItemStack add10 = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
    private final ItemStack rem1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
    private final ItemStack rem5 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
    private final ItemStack rem10 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();

    private final ItemStack back = XMaterial.OAK_SIGN.parseItem();
    private final ItemStack confirm = XMaterial.EMERALD_BLOCK.parseItem();

    private final Inventory confirmGui = Bukkit.createInventory(null, 45, "");
    private final List<ItemStack> interactItems = new ArrayList<>();
    private final BiConsumer<Boolean, ItemStack> bi;
    private final String title;
    private final boolean price;

    private final DailyRandomShop main;

    public confirmGui(DailyRandomShop main, ItemStack item, Player p,
                      BiConsumer<Boolean, ItemStack> bi, String title, boolean price) {
        Bukkit.getPluginManager().registerEvents(this, main);

        this.price = price;
        this.main = main;
        this.bi = bi;
        this.title = title;
        interactItems.add(rem1);
        interactItems.add(rem5);
        interactItems.add(rem10);
        interactItems.add(add1);
        interactItems.add(add5);
        interactItems.add(add10);

        interactSlots.add(18);
        interactSlots.add(19);
        interactSlots.add(20);
        interactSlots.add(24);
        interactSlots.add(25);
        interactSlots.add(26);
        interactSlots.add(36);
        interactSlots.add(40);

        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED  + "" + ChatColor.BOLD + main.config.CONFIRM_GUI_RETURN_NAME);
        back.setItemMeta(meta);

        meta = confirm.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + main.config.CONFIRM_GUI_CONFIRM_PANE);
        confirm.setItemMeta(meta);

        createGui();

        Inventory inv = getInventory();

        inv.setItem(22, item);

        p.openInventory(inv);
    }

    public void createGui() {
        int[] aux = {1, 5, 10, 1, 5, 10};

        for (int i = 0; i < aux.length; i++) {

            ItemMeta meta = interactItems.get(i).getItemMeta();
            if (i < 3)  meta.setDisplayName(ChatColor.RED + main.config.CONFIRM_GUI_REMOVE_PANE + " " + aux[i]);
            else meta.setDisplayName(ChatColor.GREEN + main.config.CONFIRM_GUI_ADD_PANE + " " + aux[i]);

            interactItems.get(i).setItemMeta(meta);

            if ( i > 2 ) confirmGui.setItem(interactSlots.get(i), interactItems.get(i));
        }

        confirmGui.setItem(36, back);
        confirmGui.setItem(40, confirm);

    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, title);

        inv.setContents(confirmGui.getContents());

        return inv;
    }

    public void updateGui(Inventory inv) {
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

        ItemStack item = inv.getItem(22);
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if(meta.hasLore()) lore = meta.getLore();
        else lore = new ArrayList<>();

        if(price) {
            lore.remove(lore.size() - 1);
            lore.remove(lore.size() - 1);
            lore.add(main.config.BUY_GUI_ITEMS_LORE_PRICE.replaceAll("\\{price}", String.format("%,.2f",main.utils.getItemPrice(main.listDailyItems, item, true) * nstack)));
            meta.setLore(lore);
            item.setItemMeta(meta);
            main.utils.setRarityLore(item, main.utils.getRarity(item));
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        int[] aux = {1, 5, 10, 1, 5, 10};

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);

        if (e.getRawSlot() != e.getSlot() || !(interactSlots.contains(e.getSlot()))) {
            return;
        }

        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == 36) { //boton de salir

            bi.accept(false, null);
            return;
            /*p.openInventory(main.BuyGui.getGui());
            try {
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
            } catch (NoSuchFieldError ignored) {}
            return; */
        }

        ItemStack item = e.getView().getTopInventory().getItem(22);

        if (e.getSlot() == 40) { //boton de aceptar
            //Double price = main.utils.getItemPrice(main.listDailyItems, item, true) * item.getAmount();

            bi.accept(true, item);
            return;
            /*main.utils.giveItem(p, price, e.getView().getBottomInventory(), item);
            return; */
        }

        if (e.getView().getTopInventory().getItem(e.getSlot()) == null) return;

        if (interactSlots.indexOf(e.getSlot()) > 2)
            item.setAmount(item.getAmount() + aux[interactSlots.indexOf(e.getSlot())]);
        else item.setAmount(item.getAmount() - aux[interactSlots.indexOf(e.getSlot())]);

        e.getView().getTopInventory().setItem(22, item);

        try {
            p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
        } catch (NoSuchFieldError ignored) {}
        updateGui(e.getView().getTopInventory());
        p.updateInventory();

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }
    }


}
