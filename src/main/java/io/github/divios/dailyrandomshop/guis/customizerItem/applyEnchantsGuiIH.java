package io.github.divios.dailyrandomshop.guis.customizerItem;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class applyEnchantsGuiIH implements Listener, InventoryHolder {

    private final DailyRandomShop main;
    private final Player p;
    private final BiConsumer <AbstractMap.SimpleEntry<Enchantment, Integer>, Boolean> bi;

    private final List<Enchantment> enchants;
    private final List<Integer> reservedSlots = new ArrayList<>();
    private final ArrayList<Inventory> invs = new ArrayList<>();
    private final ItemStack next = new ItemStack(Material.ARROW);
    private final ItemStack previous = new ItemStack(Material.ARROW);

    private final boolean isSearch;
    private final boolean addEnchant;

    public applyEnchantsGuiIH(DailyRandomShop main, Player p, BiConsumer<AbstractMap.SimpleEntry <Enchantment, Integer>,
            Boolean> bi, Boolean isSearch, Enchantment[] enchants, boolean addEnchant) {
        Bukkit.getPluginManager().registerEvents(this, main);

        this.main = main;
        this.p = p;
        this.bi = bi;
        this.isSearch = isSearch;
        this.enchants = new ArrayList<>(Arrays.asList(enchants));
        this.addEnchant = addEnchant;

        for (int i = 0; i < 36; i++) {
            reservedSlots.add(i);
        }

        double nD = this.enchants.size() / 36F;
        int n = (int) Math.ceil(nD);

        for (int i = 0; i < n; i++) {
            if (i + 1 == n) {
                invs.add(createGUI(i + 1, 2));
            } else if (i == 0) invs.add(createGUI(i + 1, 0));
            else invs.add(createGUI(i + 1, 1));
        }

        if (invs.isEmpty()) {
            Inventory firstInv = Bukkit.createInventory(this, 54, ChatColor.WHITE + "" +
                    ChatColor.BOLD + "Choose the new Material");
            firstInv.setContents(getInventory().getContents());
            invs.add(firstInv);
        }

        p.openInventory(invs.get(0));
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, ChatColor.WHITE + "" +
                ChatColor.BOLD + "Choose enchant to apply");

        ItemStack backItem = XMaterial.OAK_SIGN.parseItem();   //back button
        ItemMeta meta = backItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD +
                "Return");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click go back");
        meta.setLore(lore);
        backItem.setItemMeta(meta);

        ItemStack searchItem;
        if (!isSearch) {
            searchItem = XMaterial.COMPASS.parseItem(); //back button
            meta = searchItem.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD +
                    "Search");
            lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to search item");
            meta.setLore(lore);
            searchItem.setItemMeta(meta);
        } else {
            searchItem = XMaterial.REDSTONE_BLOCK.parseItem();   //back button
            meta = searchItem.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD +
                    "Search");
            lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to cancel the search");
            meta.setLore(lore);
            searchItem.setItemMeta(meta);
        }

        inv.setItem(47, searchItem);
        inv.setItem(49, backItem);

        return inv;
    }

    public Inventory processNextGui(Inventory inv, int dir) {
        return invs.get(invs.indexOf(inv) + dir);
    }

    public Inventory createGUI(int page, int pos) {
        int slot = 0;
        Inventory returnGui = Bukkit.createInventory(this, 54, ChatColor.WHITE + "" +
                ChatColor.BOLD + "Choose the new Material");

        returnGui.setContents(getInventory().getContents());
        if (pos == 0 && enchants.size() > 44) returnGui.setItem(53, next);
        if (pos == 1) {
            returnGui.setItem(53, next);
            returnGui.setItem(45, previous);
        }
        if (pos == 2 && enchants.size() > 44) {
            returnGui.setItem(45, previous);
        }

        for (Enchantment m : enchants) {
            ItemStack item = new ItemStack(Material.GRASS);

            item.setType(XMaterial.ENCHANTED_BOOK.parseMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + "" +
                    ChatColor.BOLD + m.getName());
            item.setItemMeta(meta);

            if (slot == 36 * page) break;
            if (slot >= (page - 1) * 36) returnGui.setItem(slot - (page - 1) * 36, item);


            slot++;

        }
        return returnGui;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);

        if(e.getSlot() != e.getRawSlot()) return;

        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        if ( e.getSlot() == 49) { //boton de salir
            unregisterEvents();
            bi.accept(null, false);
            return;
        }

        if ( e.getSlot() == 47) { //boton de buscar

            if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
                unregisterEvents();
                new applyEnchantsGuiIH(main, p, bi,false, Enchantment.values(), addEnchant);
            } else {
                AtomicBoolean response = new AtomicBoolean(false);
                final Enchantment[][] auxenchant= {Enchantment.values()};
                new AnvilGUI.Builder()
                        .onClose(player -> {
                            Bukkit.getScheduler().runTaskLater(main, () -> new applyEnchantsGuiIH(main, p, bi, response.get(), auxenchant[0], addEnchant), 1);
                        })
                        .onComplete((player, text) -> {

                            ArrayList<Enchantment> enchants2 = new ArrayList<>();
                            for (Enchantment m : enchants) {
                                if (m.getName().toLowerCase().startsWith(text.toLowerCase())) {
                                    enchants2.add(m);
                                }
                            }
                            response.set(true);
                            auxenchant[0] = enchants2.toArray(new Enchantment[0]);
                            unregisterEvents();
                            return AnvilGUI.Response.close();

                        })
                        .text("Insert text to search")
                        .itemLeft(new ItemStack(XMaterial.COMPASS.parseMaterial()))
                        .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Insert text to search")
                        .plugin(main)
                        .open(p);
            }

        }

        if ( e.getSlot() == 53) { //boton de atras

            p.openInventory(processNextGui(e.getView().getTopInventory(), 1));
            return;
        }

        if ( e.getSlot() == 45) { //boton de siguiente

            p.openInventory(processNextGui(e.getView().getTopInventory(), -1));
            return;
        }

        if ( reservedSlots.contains(e.getSlot()) ) { //algun material
            unregisterEvents();
            AtomicBoolean response = new AtomicBoolean(false);
            Enchantment enchant = Enchantment.getByName(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll(ChatColor.WHITE
                    + "" +  ChatColor.BOLD, ""));
            AtomicInteger lvl = new AtomicInteger();
            if(addEnchant) {
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        if(!response.get()) Bukkit.getScheduler().runTaskLater(main, () -> new applyEnchantsGuiIH(main, player, bi,false, Enchantment.values(), true), 1);
                        else {
                            AbstractMap.SimpleEntry<Enchantment, Integer> entry = new AbstractMap.SimpleEntry<>(enchant, lvl.get());
                            bi.accept(entry, true);
                        }
                    })
                    .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                        try {
                            lvl.set(Integer.parseInt(text));
                            response.set(true);
                            return AnvilGUI.Response.close();
                        } catch (NumberFormatException err ) {
                            return AnvilGUI.Response.text("Is not Integer");
                        }

                    })
                    .text("Set Enchantment lvl")
                    .itemLeft(XMaterial.ENCHANTED_BOOK.parseItem())
                    .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Set Enchantment lvl")
                    .plugin(main)
                    .open(p);
         } else bi.accept(new AbstractMap.SimpleEntry<>(enchant, 0), true);

            return;
        }

    }

    public void unregisterEvents() {
        InventoryClickEvent.getHandlerList().unregister(this);
    }


}
