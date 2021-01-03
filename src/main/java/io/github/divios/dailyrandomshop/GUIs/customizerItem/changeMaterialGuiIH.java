package io.github.divios.dailyrandomshop.GUIs.customizerItem;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class changeMaterialGuiIH implements Listener, InventoryHolder {

    private final DailyRandomShop main;
    private final Player p;
    private final BiConsumer<Material, Boolean> bi;
    private final ArrayList<Material> materials;
    private final List<Integer> reservedSlots = new ArrayList<>();
    private final ArrayList<Inventory> invs = new ArrayList<>();
    private final ItemStack next = new ItemStack(Material.ARROW);
    private final ItemStack previous = new ItemStack(Material.ARROW);
    private final boolean isSearch;

    public changeMaterialGuiIH(DailyRandomShop main, Player p, BiConsumer<Material, Boolean> true_false,
                               Material[] listMaterials, boolean isSearch) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
        this.p = p;
        this.bi = true_false;
        this.materials = removeGarbageMaterial(new ArrayList<>(Arrays.asList(listMaterials)));
        this.isSearch = isSearch;

        for (int i = 0; i < 36; i++) {
            reservedSlots.add(i);
        }

        double nD = materials.size() / 36F;
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
                ChatColor.BOLD + "Choose the new Material");

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
        if (pos == 0 && materials.size() > 44) returnGui.setItem(53, next);
        if (pos == 1) {
            returnGui.setItem(53, next);
            returnGui.setItem(45, previous);
        }
        if (pos == 2 && materials.size() > 44) {
            returnGui.setItem(45, previous);
        }

        for (Material m : materials) {
            ItemStack item = new ItemStack(Material.GRASS);

            item.setType(m);

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

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 49) { //boton de salir
            unregisterEvents();
            bi.accept(null, false);
            return;
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 47) { //boton de buscar

            if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
                unregisterEvents();
                new changeMaterialGuiIH(main, p, bi, Material.values(), false);
            } else {
                AtomicBoolean response = new AtomicBoolean(false);
                final Material[][] auxmat = {Material.values()};
                new AnvilGUI.Builder()
                        .onClose(player -> {
                            Bukkit.getScheduler().runTaskLater(main, () -> new changeMaterialGuiIH(main, p, bi, auxmat[0], response.get()), 1);
                        })
                        .onComplete((player, text) -> {

                            ArrayList<Material> materials2 = new ArrayList<>();
                            for(Material m: materials) {
                                if(m.toString().toLowerCase().startsWith(text.toLowerCase())) {
                                    materials2.add(m);
                                }
                            }
                            auxmat[0] = materials2.toArray(new Material[0]);
                            unregisterEvents();
                            response.set(true);
                            return AnvilGUI.Response.close();

                        })
                        .text("")
                        .itemLeft(new ItemStack(XMaterial.COMPASS.parseMaterial()))
                        .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Insert text to search")
                        .plugin(main)
                        .open(p);
            }

        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 53
                && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) { //boton de atras

            p.openInventory(processNextGui(e.getView().getTopInventory(), 1));
            return;
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 45
                && e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) { //boton de siguiente

            p.openInventory(processNextGui(e.getView().getTopInventory(), -1));
            return;
        }

        if (e.getSlot() == e.getRawSlot() && reservedSlots.contains(e.getSlot()) && e.getCurrentItem() != null) { //algun material
            unregisterEvents();
            bi.accept(e.getCurrentItem().getType(), true);
            return;
        }

    }

    public ArrayList<Material> removeGarbageMaterial(ArrayList<Material> materials){
        Inventory inv = Bukkit.createInventory(null, 54, "");
        ArrayList<Material> materialsaux = new ArrayList<>();


        for (Material m: materials) {
            ItemStack item = new ItemStack(m);
            inv.setItem(0, item);
            Boolean err = false;
            try{
                inv.getItem(0).getType();
            } catch (NullPointerException e) {
                err = true;
            }
            if(!err) materialsaux.add(m);

        }
        return materialsaux;
    }

    public void unregisterEvents() {
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }



}
