package io.github.divios.dailyrandomshop.GUIs.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.customizerItem.customizerMainGuiIH;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class dailyGuiSettings implements Listener, InventoryHolder {

    private final DailyRandomShop main;
    private Inventory GUI;
    private final ItemStack exit = XMaterial.OAK_SIGN.parseItem();
    private final ItemStack next = new ItemStack(Material.ARROW);
    private final ItemStack create = new ItemStack(Material.ANVIL);
    private final ItemStack previous = new ItemStack(Material.ARROW);
    public final ArrayList<Inventory> invs = new ArrayList<>();
    private final ArrayList<Integer> reservedSlots = new ArrayList<>();

    public dailyGuiSettings(DailyRandomShop main) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;

        ItemMeta meta = exit.getItemMeta();
        meta.setDisplayName(main.config.DAILY_ITEMS_MENU_RETURN);
        exit.setItemMeta(meta);

        meta = create.getItemMeta();
        meta.setDisplayName(main.config.DAILY_ITEMS_MENU_ADD);
        List<String> lore = new ArrayList<>();
        for(String s: main.config.DAILY_ITEMS_MENU_ADD_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        create.setItemMeta(meta);

        meta = next.getItemMeta();
        meta.setDisplayName(main.config.DAILY_ITEMS_MENU_NEXT);
        next.setItemMeta(meta);

        meta = previous.getItemMeta();
        meta.setDisplayName(main.config.DAILY_ITEMS_MENU_PREVIOUS);
        previous.setItemMeta(meta);

        reservedSlots.add(45);
        reservedSlots.add(46);
        reservedSlots.add(47);
        reservedSlots.add(48);
        reservedSlots.add(49);
        reservedSlots.add(50);
        reservedSlots.add(51);
        reservedSlots.add(52);
        reservedSlots.add(53);

        initGui();
    }

    public void initGui() {

        double nD = main.listDailyItems.size() / 44F;
        int n = (int) Math.ceil(nD);

        GUI = Bukkit.createInventory(this, 54, main.config.DAILY_ITEMS_MENU_TITLE);
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
            Inventory firstInv = Bukkit.createInventory(this, 54, main.config.DAILY_ITEMS_MENU_TITLE);
            firstInv.setContents(GUI.getContents());
            invs.add(firstInv);
        }

    }

    public Inventory createGUI(int page, int pos) {
        int slot = 0;
        Inventory returnGui = Bukkit.createInventory(this, 54, main.config.DAILY_ITEMS_MENU_TITLE);
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

        lore.add(main.config.BUY_GUI_ITEMS_LORE_PRICE.replaceAll("\\{price}", String.format("%,.2f",price)));
        lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", main.utils.getRarityString(item)));
        lore.add("");
        for(String s: main.config.DAILY_ITEMS_MENU_ITEMS_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public Inventory getFirstGui() {
        return invs.get(0);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) {
            return;
        }

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 49) {
            new settingsGuiIH(main, p);
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 45 &&
                e.getCurrentItem() != null) {
            p.openInventory(main.DailyGuiSettings.processNextGui(e.getView().getTopInventory(), -1));
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 53 &&
                e.getCurrentItem() != null) {
            p.openInventory(main.DailyGuiSettings.processNextGui(e.getView().getTopInventory(), 1));
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 52 &&
                e.getCurrentItem() != null) {
            new addDailyItemGuiIH(main, p, e.getView().getTopInventory());
        }

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR ||
                e.getSlot() != e.getRawSlot() || reservedSlots.contains(e.getSlot())) {
            return;
        }

        ItemStack item = removeLore(e.getCurrentItem().clone());
        if (e.isLeftClick() && !e.isShiftClick()) {
            AtomicBoolean response = new AtomicBoolean(false);
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        Bukkit.getScheduler().runTaskLater(main, () -> p.openInventory(main.DailyGuiSettings.getFirstGui()), 1);
                    })
                    .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                        try {
                            Double price = Double.parseDouble(text);
                            while (Bukkit.getScheduler().isCurrentlyRunning(main.updateListID.getTaskId())){
                                main.utils.waitXticks(10);
                            }
                            main.utils.replacePriceOnList(main.listDailyItems, item, price);
                            //main.dbManager.updateDailyItemPrice(item, price);
                            HandlerList.unregisterAll(main.DailyGuiSettings);
                            main.DailyGuiSettings = new dailyGuiSettings(main);
                            response.set(true);
                            return AnvilGUI.Response.close();
                        } catch (NumberFormatException err) {
                            return AnvilGUI.Response.text("Not integer");
                        }

                    })
                    .text(main.config.DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT)
                    .itemLeft(new ItemStack(item))
                    .title(main.config.DAILY_ITEMS_MENU_ANVIL_TITLE)
                    .plugin(main)
                    .open(p);

        } else if (e.isRightClick()) {

            new confirmIH(p,  (p1, bool) -> {

                if (bool) {
                    while (Bukkit.getScheduler().isCurrentlyRunning(main.updateListID.getTaskId())){
                        main.utils.waitXticks(10);
                    }
                    main.utils.removeItemOnList(main.listDailyItems, item);
                    p1.sendMessage(main.config.PREFIX + main.config.MSG_REMOVED_ITEM);
                    HandlerList.unregisterAll(main.DailyGuiSettings);
                    main.DailyGuiSettings = new dailyGuiSettings(main);
                    //main.dbManager.deleteDailyItem(item);
                }
                p1.openInventory(main.DailyGuiSettings.getFirstGui());

            }, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Confirm", main);

        } else if (e.isLeftClick() && e.isShiftClick()) {
            new customizerMainGuiIH(main, (Player) e.getWhoClicked(), removeLore(e.getCurrentItem().clone()), removeLore(e.getCurrentItem().clone()));
        }
    }

    public ItemStack removeLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        int j = 0;
        while (j <= main.config.DAILY_ITEMS_MENU_ITEMS_LORE.size() + 2) {
            lore.remove(lore.size() - 1);
            j++;
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
