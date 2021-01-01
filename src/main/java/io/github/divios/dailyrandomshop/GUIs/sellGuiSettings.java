package io.github.divios.dailyrandomshop.GUIs;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class sellGuiSettings {


    private final DailyRandomShop main;
    private Inventory GUI;
    private final ItemStack exit = XMaterial.BARRIER.parseItem();
    private final ItemStack next = new ItemStack(Material.ARROW);
    private final ItemStack previous = new ItemStack(Material.ARROW);
    public final ArrayList<Inventory> invs = new ArrayList<>();

    public sellGuiSettings(DailyRandomShop main) {
        this.main = main;

        ItemMeta meta = exit.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Exit");
        exit.setItemMeta(meta);

        meta = next.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Next");
        next.setItemMeta(meta);

        meta = previous.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Previous");
        previous.setItemMeta(meta);

        initGui();
    }

    public void initGui() {

        double nD = main.listSellItems.size() / 44F;
        int n = (int) Math.ceil(nD);

        GUI = Bukkit.createInventory(null, 54, main.config.SELL_SETTINGS_TITLE + ChatColor.BOLD);
        GUI.setItem(49, exit);

        for(int i = 0; i<n; i++) {
            if (i + 1 == n) {
                invs.add(createGUI(i + 1, 2));
            }
            else if (i==0) invs.add(createGUI(i+1, 0));
            else invs.add(createGUI(i+1, 1));
        }

    }

    public Inventory createGUI(int page, int pos) {
        int slot = 0;
        Inventory returnGui = Bukkit.createInventory(null, 54, main.config.SELL_SETTINGS_TITLE + ChatColor.BOLD);
        returnGui.setContents(GUI.getContents());
        if(pos == 0 && main.listSellItems.size() > 44) returnGui.setItem(53, next);
        if(pos == 1) {
            returnGui.setItem(53, next);
            returnGui.setItem(45, previous);
        }
        if(pos == 2 && main.listSellItems.size() > 44) {
            returnGui.setItem(45, previous);
        }

        for(Map.Entry<ItemStack, Double> i: main.listSellItems.entrySet()) {
            ItemStack item = i.getKey().clone();
            setLore(item, i.getValue());

            if (slot == 45 * page) break;
            if (slot >= (page - 1) * 45) returnGui.setItem(slot - (page - 1) * 45, item);

            slot++;
        }
        return returnGui;
    }

    public Inventory processNextGui(Inventory inv, int dir) {
        return invs.get(invs.indexOf(inv) + dir);
    }

    public void setLore(ItemStack item, Double price) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        List<String> lore = null;
        if (meta != null && meta.hasLore() ) lore = meta.getLore();
        else lore = new ArrayList<>();

        lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", "" + price));
        lore.add("");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Right click: " + ChatColor.GRAY + "Change price");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left click: " + ChatColor.GRAY + "Remove item");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public Inventory getFirstGui() {
        return invs.get(0);
    }

}
