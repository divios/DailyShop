package io.github.divios.dailyrandomshop.GUIs;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class confirmGui {

    private final int[] interactSlots = {9, 10, 11, 15, 16, 17};
    private final ItemStack add1 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
    private final ItemStack add5 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
    private final ItemStack add10 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
    private final ItemStack rem1 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
    private final ItemStack rem5 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
    private final ItemStack rem10 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();

    private final ItemStack back = new ItemStack(Material.ARROW);
    private final ItemStack confirm = XMaterial.GREEN_STAINED_GLASS.parseItem();

    private final Inventory confirmGui = Bukkit.createInventory(null, 27, "");
    private List<ItemStack> interactItems = new ArrayList<>();

    private final DailyRandomShop main;

    public confirmGui(DailyRandomShop main) {
        this.main = main;
        interactItems.add(rem1);
        interactItems.add(rem5);
        interactItems.add(rem10);
        interactItems.add(add1);
        interactItems.add(add5);
        interactItems.add(add10);

        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED  + "" + ChatColor.BOLD + main.config.CONFIRM_GUI_RETURN_NAME);
        back.setItemMeta(meta);

        meta = confirm.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + main.config.CONFIRM_GUI_CONFIRM_PANE);
        confirm.setItemMeta(meta);

        createGui();
    }

    public void createGui() {
        int[] aux = {1, 5, 10, 1, 5, 10};

        for (int i = 0; i < aux.length; i++) {

            ItemMeta meta = interactItems.get(i).getItemMeta();
            if (i < 3)    meta.setDisplayName(ChatColor.RED + main.config.CONFIRM_GUI_REMOVE_PANE + " " + aux[i]);
            else meta.setDisplayName(ChatColor.GREEN + main.config.CONFIRM_GUI_ADD_PANE + " " + aux[i]);

            interactItems.get(i).setItemMeta(meta);

            if ( i > 2 ) confirmGui.setItem(interactSlots[i], interactItems.get(i));
        }

        confirmGui.setItem(18, back);
        confirmGui.setItem(22, confirm);

    }

    public Inventory getGui(ItemStack item) {
        Inventory inv = Bukkit.createInventory(null, 27, main.config.CONFIRM_GUI_NAME + ChatColor.GREEN);

        inv.setContents(confirmGui.getContents());

        inv.setItem(13, item);

        return inv;
    }

    public void updateGui(Inventory inv) {
        int nstack = inv.getItem(13).getAmount();

        if( nstack > 1) inv.setItem(9, rem1);
        else inv.setItem(9, new ItemStack(Material.AIR));

        if( nstack > 5) inv.setItem(10, rem5);
        else inv.setItem(10, new ItemStack(Material.AIR));

        if( nstack > 10) inv.setItem(11, rem10);
        else inv.setItem(11, new ItemStack(Material.AIR));

        if( nstack < 64) inv.setItem(15, add1);
        else inv.setItem(15, new ItemStack(Material.AIR));

        if( nstack < 60) inv.setItem(16, add5);
        else inv.setItem(16, new ItemStack(Material.AIR));

        if( nstack < 55) inv.setItem(17, add10);
        else inv.setItem(17, new ItemStack(Material.AIR));

        ItemStack item = inv.getItem(13);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        lore.remove(lore.size() - 1);
        lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", "" + main.utils.getItemPrice(main.listDailyItems, item, true) * nstack));
        meta.setLore(lore);

        item.setItemMeta(meta);
    }

}
