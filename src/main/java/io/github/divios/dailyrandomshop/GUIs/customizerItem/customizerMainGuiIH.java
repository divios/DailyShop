package io.github.divios.dailyrandomshop.GUIs.customizerItem;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import io.github.divios.dailyrandomshop.GUIs.settings.dailyGuiSettings;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
import java.util.Map;
import java.util.function.BiConsumer;

public class customizerMainGuiIH implements Listener, InventoryHolder {

    private DailyRandomShop main;
    private Player p;
    private final ItemStack itemToReplace;
    private ItemStack newItem;

    public customizerMainGuiIH(DailyRandomShop main, Player p, ItemStack newItem, ItemStack itemToReplace) {
        Bukkit.getPluginManager().registerEvents(this, main);

        this.newItem = newItem;
        this.itemToReplace = itemToReplace;
        this.main = main;
        this.p = p;

        p.openInventory(getInventory());
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, ChatColor.GREEN + "" +
                ChatColor.BOLD + "Customize item");


        ItemStack customizerItem = XMaterial.ANVIL.parseItem();   //Done button (anvil)
        ItemMeta meta = customizerItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD +
                "Craft Item");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to finish and add the item");
        meta.setLore(lore);
        customizerItem.setItemMeta(meta);

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();    //Back sign
        meta = returnItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD +
                "Return");
        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to cancel operation");
        meta.setLore(lore);
        returnItem.setItemMeta(meta);

        ItemStack rename = XMaterial.NAME_TAG.parseItem();    //Rename item
        meta = rename.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Rename");
        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to change the item name");
        meta.setLore(lore);
        rename.setItemMeta(meta);

        ItemStack changeMaterial = XMaterial.SLIME_BALL.parseItem();    //Change material
        meta = changeMaterial.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Change Material");
        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to change the item material");
        meta.setLore(lore);
        changeMaterial.setItemMeta(meta);

        ItemStack changeLore = XMaterial.PAPER.parseItem();    //Change lore
        meta = changeLore.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Change Lore");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "to add lore");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Right Click: " + ChatColor.GRAY + "to remove lore");
        lore.add(ChatColor.GOLD + "");
        if(newItem.getItemMeta().hasLore()) {
            lore.addAll(newItem.getItemMeta().getLore());
        }
        meta.setLore(lore);
        changeLore.setItemMeta(meta);

        ItemStack editEnchantments = XMaterial.BOOK.parseItem();    //Change enchants
        meta = editEnchantments.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Edit enchantments");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "to add Enchantment");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Right Click: " + ChatColor.GRAY + "to remove Enchantment");
        lore.add("");
        for (Map.Entry<Enchantment, Integer> e: newItem.getEnchantments().entrySet()) {
            lore.add(ChatColor.WHITE + "" + ChatColor.WHITE +
                    e.getKey().getName().toUpperCase() + ":" + e.getValue());
        }
        meta.setLore(lore);
        editEnchantments.setItemMeta(meta);

        ItemStack setAmount = XMaterial.STONE_BUTTON.parseItem();    //Change amount
        meta = setAmount.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Set Amount");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Change item amount");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Right Click: " + ChatColor.GRAY + "Remove amount");
        lore.add("");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Is item Amount? " + ChatColor.GRAY + main.utils.isItemAmount(newItem));
        meta.setLore(lore);
        setAmount.setItemMeta(meta);


        for (int j = 0; j < 9; j++) {
            ItemStack item = main.utils.setItemAsFill(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()); //fill black panes
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            item.setItemMeta(meta);
            inv.setItem(9 + j, item);
        }


        inv.setItem(4, newItem);
        inv.setItem(18, rename);
        inv.setItem(19, changeMaterial);
        inv.setItem(20, changeLore);
        inv.setItem(21, editEnchantments);
        inv.setItem(22, setAmount);
        inv.setItem(47, returnItem);
        inv.setItem(49, customizerItem);

        return inv;
    }


    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 47) { //Boton de retornar
            p.openInventory(main.DailyGuiSettings.getFirstGui());
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 49) { //Boton de craft

            if (itemToReplace == null) {
                newItem = main.utils.setItemAsDaily(newItem);
                main.listDailyItems.put(newItem, 500.0);
            } else {
                Double price = main.utils.getItemPrice(main.listDailyItems, itemToReplace, false);
                main.listDailyItems.remove(itemToReplace);
                main.listDailyItems.put(newItem, price);
            }
            main.DailyGuiSettings = new dailyGuiSettings(main);
            main.dbManager.updateDailyItems();
            p.openInventory(main.DailyGuiSettings.getFirstGui());
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 18) { // Boton de cambiar nombre
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        new customizerMainGuiIH(main, p, newItem, itemToReplace);
                    })
                    .onComplete((player, text) -> {

                        ItemMeta meta = newItem.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', text));
                        newItem.setItemMeta(meta);
                        new customizerMainGuiIH(main, p, newItem, itemToReplace);
                        return AnvilGUI.Response.close();
                    })
                    .text("")
                    .itemLeft(new ItemStack(newItem))
                    .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Write the new name")
                    .plugin(main)
                    .open(p);
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 19) { // Boton de cambiar material
            new changeMaterialGuiIH(main, p, (material, bool) -> {

                if (bool) {
                    newItem.setType(material);
                }
                new customizerMainGuiIH(main, p, newItem, itemToReplace);

            }, Material.values(), false);

        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 20) { // Boton de cambiar lore
            if(e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> {
                            new customizerMainGuiIH(main, p, newItem, itemToReplace);
                        })
                        .onComplete((player, text) -> {

                            ItemMeta meta = newItem.getItemMeta();
                            List<String> lore;
                            if (meta.hasLore()) lore = meta.getLore();
                            else lore = new ArrayList<>();

                            lore.add(ChatColor.translateAlternateColorCodes('&', text));
                            meta.setLore(lore);
                            newItem.setItemMeta(meta);
                            new customizerMainGuiIH(main, p, newItem, itemToReplace);
                            return AnvilGUI.Response.close();
                        })
                        .text("")
                        .itemLeft(new ItemStack(newItem))
                        .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Write the new name")
                        .plugin(main)
                        .open(p);
            } else if(e.isRightClick()) {
                ItemMeta meta = newItem.getItemMeta();
                List<String> lore;
                if (!meta.hasLore()) return;
                lore = meta.getLore();
                lore.remove(lore.size() - 1);
                meta.setLore(lore);
                newItem.setItemMeta(meta);

                new customizerMainGuiIH(main, p , newItem, itemToReplace);

            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 21) { // Boton de cambiar enchants

            if(e.isLeftClick()) {
                new applyEnchantsGuiIH(main, p, (entry, aBoolean) -> {

                    if(aBoolean) {
                        newItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                    }
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);

                }, false, Enchantment.values(), true);
            }

            if(e.isRightClick()) {
                new applyEnchantsGuiIH(main, p, (entry, aBoolean) -> {

                    if(aBoolean) {
                        newItem.removeEnchantment(entry.getKey());
                    }
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);

                }, false, newItem.getEnchantments().keySet().toArray(new Enchantment[0]), false);
            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 22) { // Boton de cambiar amount

            if(e.isLeftClick()) {
                new confirmGui(main, newItem, p, (aBoolean, itemStack) -> {

                    if(aBoolean) {
                        newItem.setAmount(itemStack.getAmount());
                        newItem = main.utils.setItemAsAmount(newItem);
                    }
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);

                }, "Set item amount", false);
            }

            if(e.isRightClick()) {
                if(main.utils.isItemAmount(newItem)) {

                    new customizerMainGuiIH(main, p , main.utils.removeItemAmount(newItem), itemToReplace);
                }
            }
        }

    }


    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getHolder() == this) {

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }

    }

}
