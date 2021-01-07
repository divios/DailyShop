package io.github.divios.dailyrandomshop.GUIs.settings;

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

public class dailyGuiSettings {

    private final DailyRandomShop main;
    private Inventory GUI;
    private final ItemStack exit = XMaterial.OAK_SIGN.parseItem();
    private final ItemStack next = new ItemStack(Material.ARROW);
    private final ItemStack create = new ItemStack(Material.ANVIL);
    private final ItemStack previous = new ItemStack(Material.ARROW);
    public final ArrayList<Inventory> invs = new ArrayList<>();

    public dailyGuiSettings(DailyRandomShop main) {
        this.main = main;

        ItemMeta meta = exit.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Return");
        exit.setItemMeta(meta);

        meta = create.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Add");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to add an item");
        meta.setLore(lore);
        create.setItemMeta(meta);

        meta = next.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Next");
        next.setItemMeta(meta);

        meta = previous.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Previous");
        previous.setItemMeta(meta);

        initGui();
    }

    public void initGui() {

        double nD = main.listDailyItems.size() / 44F;
        int n = (int) Math.ceil(nD);

        GUI = Bukkit.createInventory(null, 54, main.config.DAILY_SETTINGS_TITLE + ChatColor.AQUA);
        GUI.setItem(52, create);
        GUI.setItem(49, exit);

        for(int i = 0; i<n; i++) {
            if (i + 1 == n) {
                invs.add(createGUI(i + 1, 2));
            }
            else if (i==0) invs.add(createGUI(i+1, 0));
            else invs.add(createGUI(i+1, 1));
        }

        if (invs.isEmpty()) {
            Inventory firstInv = Bukkit.createInventory(null, 54, main.config.DAILY_SETTINGS_TITLE + ChatColor.AQUA);
            firstInv.setContents(GUI.getContents());
            invs.add(firstInv);
        }

    }

    public Inventory createGUI(int page, int pos) {
        int slot = 0;
        Inventory returnGui = Bukkit.createInventory(null, 54, main.config.DAILY_SETTINGS_TITLE + ChatColor.AQUA);
        returnGui.setContents(GUI.getContents());
        if(pos == 0 && main.listDailyItems.size() > 44) returnGui.setItem(53, next);
        if(pos == 1) {
            returnGui.setItem(53, next);
            returnGui.setItem(45, previous);
        }
        if(pos == 2 && main.listDailyItems.size() > 44) {
            returnGui.setItem(45, previous);
        }

        for(Map.Entry<ItemStack, Double> i: main.listDailyItems.entrySet()) {
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

        lore.add(main.config.BUY_GUI_ITEMS_LORE.replaceAll("\\{price}", String.format("%,.2f",price)));
        lore.add("");
        lore.add(ChatColor.GOLD + "Left click: " + ChatColor.GRAY + "Change price");
        lore.add(ChatColor.GOLD + "Right click: " + ChatColor.GRAY + "Remove item");
        lore.add(ChatColor.GOLD + "Shift Left click: " + ChatColor.GRAY + "Customize item");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public Inventory getFirstGui() {
        return invs.get(0);
    }

}
