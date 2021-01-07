package io.github.divios.dailyrandomshop.GUIs.customizerItem;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import io.github.divios.dailyrandomshop.GUIs.settings.dailyGuiSettings;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class customizerMainGuiIH implements Listener, InventoryHolder {

    private final DailyRandomShop main;
    private final Player p;
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
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "to add lore");
        lore.add(ChatColor.GOLD + "Right Click: " + ChatColor.GRAY + "to remove lore");
        lore.add(ChatColor.GOLD + "");
        if (newItem.getItemMeta().hasLore()) {
            lore.addAll(newItem.getItemMeta().getLore());
        }
        meta.setLore(lore);
        changeLore.setItemMeta(meta);

        ItemStack editEnchantments = XMaterial.BOOK.parseItem();    //Change enchants
        meta = editEnchantments.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Edit enchantments");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "to add Enchantment");
        lore.add(ChatColor.GOLD + "Right Click: " + ChatColor.GRAY + "to remove Enchantment");
        lore.add("");
        for (Map.Entry<Enchantment, Integer> e : newItem.getEnchantments().entrySet()) {
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
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "Change item amount");
        lore.add(ChatColor.GOLD + "Right Click: " + ChatColor.GRAY + "Remove amount");
        lore.add("");
        lore.add(ChatColor.GOLD + "Status " + ChatColor.GRAY + main.utils.isItemAmount(newItem));
        meta.setLore(lore);
        setAmount.setItemMeta(meta);

        ItemStack makeCommand = XMaterial.COMMAND_BLOCK.parseItem();    //Change command item
        meta = makeCommand.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Set item reward as Commands");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "Toggle status");
        lore.add("");
        lore.add(ChatColor.GOLD + "Status " + ChatColor.GRAY + main.utils.isCommandItem(newItem));
        meta.setLore(lore);
        makeCommand.setItemMeta(meta);

        ItemStack addRemoveCommands = XMaterial.JUKEBOX.parseItem();    //add/remove commands
        meta = addRemoveCommands.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Set commands to run");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "Add command to item");
        lore.add(ChatColor.GOLD + "Right Click: " + ChatColor.GRAY + "Remove command");
        lore.add("");
        for (String s : main.utils.getItemCommand(newItem)) {
            lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + s);
        }
        meta.setLore(lore);
        addRemoveCommands.setItemMeta(meta);

        ItemStack hideEnchants = XMaterial.BLACK_BANNER.parseItem();    //add/remove enchants visible
        meta = hideEnchants.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Make enchant visible/invisible");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "Toggle status");
        lore.add("");
        lore.add(ChatColor.GOLD + "Status " + ChatColor.GRAY + newItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
        meta.setLore(lore);
        hideEnchants.setItemMeta(meta);

        ItemStack hideAtibutes = XMaterial.BOOKSHELF.parseItem();    //add/remove attributes
        meta = hideAtibutes.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Make attributes visible/invisible");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "Toggle status");
        lore.add("");
        lore.add(ChatColor.GOLD + "Status " + ChatColor.GRAY + newItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
        meta.setLore(lore);
        hideAtibutes.setItemMeta(meta);

        ItemStack hideEffects = XMaterial.END_CRYSTAL.parseItem();    //add/remove effects
        meta = hideEffects.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                "Make potion effects visible/invisible");
        lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "Toggle status");
        lore.add("");
        lore.add(ChatColor.GOLD + "Status " + ChatColor.GRAY + newItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS));
        meta.setLore(lore);
        hideEffects.setItemMeta(meta);

        ItemStack generateMMOItem = null;
        if(main.utils.isMMOItem(newItem)) {
            generateMMOItem = XMaterial.BEACON.parseItem();    //scratch MMOItem
            meta = generateMMOItem.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD +
                    "Generate MMOItem from Scratch");
            lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Left Click: " + ChatColor.GRAY + "Toggle status");
            lore.add("");
            lore.add(ChatColor.GOLD + "Status " + ChatColor.GRAY + main.utils.isItemScracth(newItem));
            lore.add("");
            lore.add(ChatColor.GRAY + "Toggle this if you want, upon purchase,");
            lore.add(ChatColor.GRAY + "that the item is generated from scratch so");
            lore.add(ChatColor.GRAY + "in scenarios where items have random stats,");
            lore.add(ChatColor.GRAY + "the new item is generated correctly");
            meta.setLore(lore);
            generateMMOItem.setItemMeta(meta);
        }


        for (int j = 0; j < 9; j++) {
            ItemStack item = main.utils.setItemAsFill(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()); //fill black panes
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            item.setItemMeta(meta);
            inv.setItem(9 + j, item);
        }

        inv.setItem(4, newItem);
        inv.setItem(19, rename);
        inv.setItem(20, changeMaterial);
        inv.setItem(28, changeLore);
        inv.setItem(29, editEnchantments);
        inv.setItem(22, setAmount);
        inv.setItem(23, makeCommand);
        if (main.utils.isCommandItem(newItem)) inv.setItem(32, addRemoveCommands);
        inv.setItem(25, hideEnchants);
        inv.setItem(26, hideAtibutes);
        if (newItem.getType() == XMaterial.POTION.parseMaterial() ||
                newItem.getType() == XMaterial.SPLASH_POTION.parseMaterial()) {
            inv.setItem(35, hideEffects);
        }
        if (generateMMOItem != null) inv.setItem(40, generateMMOItem);
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
                main.dbManager.addDailyItem(newItem, 500.0);
            } else {
                Double price = main.utils.getItemPrice(main.listDailyItems, itemToReplace, false);
                main.listDailyItems.remove(itemToReplace);
                main.listDailyItems.put(newItem, price);
                main.dbManager.deleteDailyItem(itemToReplace);
                main.dbManager.addDailyItem(newItem, price);
            }
            main.DailyGuiSettings = new dailyGuiSettings(main);

            p.openInventory(main.DailyGuiSettings.getFirstGui());
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 19) { // Boton de cambiar nombre
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        Bukkit.getScheduler().runTaskLater(main, () -> new customizerMainGuiIH(main, p, newItem, itemToReplace), 1);
                    })
                    .onComplete((player, text) -> {

                        ItemMeta meta = newItem.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', text));
                        newItem.setItemMeta(meta);
                        //new customizerMainGuiIH(main, p, newItem, itemToReplace);
                        return AnvilGUI.Response.close();
                    })
                    .text("")
                    .itemLeft(new ItemStack(newItem))
                    .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Write the new name")
                    .plugin(main)
                    .open(p);
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 20) { // Boton de cambiar material
            new changeMaterialGuiIH(main, p, (material, bool) -> {

                if (bool) {
                    newItem.setType(material);
                }
                new customizerMainGuiIH(main, p, newItem, itemToReplace);

            }, Material.values(), false);

        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 28) { // Boton de cambiar lore
            if (e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> {
                            Bukkit.getScheduler().runTaskLater(main, () -> new customizerMainGuiIH(main, p, newItem, itemToReplace), 1);
                        })
                        .onComplete((player, text) -> {

                            ItemMeta meta = newItem.getItemMeta();
                            List<String> lore;
                            if (meta.hasLore()) lore = meta.getLore();
                            else lore = new ArrayList<>();

                            lore.add(ChatColor.translateAlternateColorCodes('&', text));
                            meta.setLore(lore);
                            newItem.setItemMeta(meta);
                            //new customizerMainGuiIH(main, p, newItem, itemToReplace);
                            return AnvilGUI.Response.close();
                        })
                        .text("")
                        .itemLeft(new ItemStack(newItem))
                        .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Write the new name")
                        .plugin(main)
                        .open(p);

            } else if (e.isRightClick()) {
                ItemMeta meta = newItem.getItemMeta();
                List<String> lore;
                if (!meta.hasLore()) return;
                lore = meta.getLore();
                lore.remove(lore.size() - 1);
                meta.setLore(lore);
                newItem.setItemMeta(meta);

                new customizerMainGuiIH(main, p, newItem, itemToReplace);

            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 29) { // Boton de cambiar enchants

            if (e.isLeftClick()) {
                new applyEnchantsGuiIH(main, p, (entry, aBoolean) -> {

                    if (aBoolean) {
                        newItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                    }
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);

                }, false, Enchantment.values(), true);
            }

            if (e.isRightClick()) {
                new applyEnchantsGuiIH(main, p, (entry, aBoolean) -> {

                    if (aBoolean) {
                        newItem.removeEnchantment(entry.getKey());
                    }
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);

                }, false, newItem.getEnchantments().keySet().toArray(new Enchantment[0]), false);
            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 22) { // Boton de cambiar amount

            if (e.isLeftClick()) {
                new confirmGui(main, newItem, p, (aBoolean, itemStack) -> {

                    if (aBoolean) {
                        newItem.setAmount(itemStack.getAmount());
                        newItem = main.utils.setItemAsAmount(newItem);
                    }
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);

                }, "Set item amount", false);
            }

            if (e.isRightClick()) {
                if (main.utils.isItemAmount(newItem)) {

                    new customizerMainGuiIH(main, p, main.utils.removeItemAmount(newItem), itemToReplace);
                }
            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 23) { // Boton de cambiar commands

            if (e.isLeftClick()) {
                if (main.utils.isCommandItem(newItem)) {
                    newItem = main.utils.removeItemCommand(newItem);
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);
                } else {
                    newItem = main.utils.setItemAsCommand(newItem, new ArrayList<>());
                    new customizerMainGuiIH(main, p, newItem, itemToReplace);
                }
            }

        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 32 && e.getCurrentItem() != null &&
                e.getCurrentItem().getType() != Material.AIR) { // Boton de cambiar commands

            if (e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> {
                            Bukkit.getScheduler().runTaskLater(main, () -> new customizerMainGuiIH(main, p, newItem, itemToReplace), 1);
                        })
                        .onComplete((player, text) -> {
                            List<String> s = main.utils.getItemCommand(newItem);
                            s.add(text);
                            newItem = main.utils.setItemAsCommand(newItem, s);

                            return AnvilGUI.Response.close();
                        })
                        .text("")
                        .itemLeft(new ItemStack(newItem))
                        .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Write command to be added")
                        .plugin(main)
                        .open(p);
            }

            if (e.isRightClick()) {
                List<String> s = main.utils.getItemCommand(newItem);
                if (!s.isEmpty()) s.remove(s.size() - 1);
                newItem = main.utils.setItemAsCommand(newItem, s);

                new customizerMainGuiIH(main, p, newItem, itemToReplace);
            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 25) { // Boton de hide enchants

            if (e.isLeftClick()) {
                ItemMeta meta = newItem.getItemMeta();
                if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    newItem.setItemMeta(meta);
                } else {
                    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                    newItem.setItemMeta(meta);
                }
                new customizerMainGuiIH(main, p, newItem, itemToReplace);
            }

        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 26) { // Boton de hide enchants

            if (e.isLeftClick()) {
                ItemMeta meta = newItem.getItemMeta();
                if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    newItem.setItemMeta(meta);
                } else {
                    meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    newItem.setItemMeta(meta);
                }
                new customizerMainGuiIH(main, p, newItem, itemToReplace);
            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 35 && e.getCurrentItem() != null &&
                    e.getCurrentItem().getType() != Material.AIR) { // Boton de hide potion effects

            if(e.isLeftClick()) {
                ItemMeta meta = newItem.getItemMeta();
                if (!meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) {
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                    newItem.setItemMeta(meta);
                }
                else{
                    meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                    newItem.setItemMeta(meta);
                }
                new customizerMainGuiIH(main, p , newItem, itemToReplace);
            }
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 40 && e.getCurrentItem() != null &&
                e.getCurrentItem().getType() != Material.AIR) { // Boton de scrath MMOItem

            if(e.isLeftClick()) {
                ItemMeta meta = newItem.getItemMeta();
                if (!main.utils.isItemScracth(newItem)) {
                    newItem = main.utils.setItemAsScracth(newItem);
                }
                else{
                    newItem = main.utils.removeItemScracth(newItem);
                }
                new customizerMainGuiIH(main, p , newItem, itemToReplace);
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
