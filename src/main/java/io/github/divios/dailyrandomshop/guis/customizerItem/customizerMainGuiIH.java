package io.github.divios.dailyrandomshop.guis.customizerItem;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.guis.confirmGui;
import io.github.divios.dailyrandomshop.guis.settings.dailyGuiSettings;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        Inventory inv = Bukkit.createInventory(this, 54, main.config.CUSTOMIZE_GUI_TITLE);


        ItemStack customizerItem = XMaterial.ANVIL.parseItem();   //Done button (anvil)
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(customizerItem.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_CRAFT);
        List<String> lore = new ArrayList<>();
        for (String s: main.config.CUSTOMIZE_CRAFT_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        customizerItem.setItemMeta(meta);

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();    //Back sign
        meta = Bukkit.getItemFactory().getItemMeta(returnItem.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_RETURN);
        lore = new ArrayList<>();
        for (String s: main.config.CUSTOMIZE_RETURN_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        returnItem.setItemMeta(meta);

        ItemStack rename = XMaterial.NAME_TAG.parseItem();    //Rename item
        meta = Bukkit.getItemFactory().getItemMeta(rename.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_RENAME);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_RENAME_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        lore.add("");
        meta.setLore(lore);
        rename.setItemMeta(meta);

        ItemStack changeMaterial = XMaterial.SLIME_BALL.parseItem();    //Change material
        meta = Bukkit.getItemFactory().getItemMeta(changeMaterial.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_MATERIAL);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_MATERIAL_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        lore.add("");
        meta.setLore(lore);
        changeMaterial.setItemMeta(meta);

        ItemStack changeLore = XMaterial.PAPER.parseItem();    //Change lore
        meta = Bukkit.getItemFactory().getItemMeta(changeLore.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_LORE);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_LORE_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        lore.add("");
        if (newItem.getItemMeta().hasLore()) {
            lore.addAll(newItem.getItemMeta().getLore());
        }
        meta.setLore(lore);
        changeLore.setItemMeta(meta);

        ItemStack editEnchantments = XMaterial.BOOK.parseItem();    //Change enchants
        meta = Bukkit.getItemFactory().getItemMeta(editEnchantments.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_ENCHANTS);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_ENCHANTS_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        lore.add("");
        for (Map.Entry<Enchantment, Integer> e : newItem.getEnchantments().entrySet()) {
            lore.add(ChatColor.WHITE + "" + ChatColor.WHITE +
                    e.getKey().getName().toUpperCase() + ":" + e.getValue());
        }
        meta.setLore(lore);
        editEnchantments.setItemMeta(meta);

        ItemStack setAmount = XMaterial.STONE_BUTTON.parseItem();    //Change amount
        meta = Bukkit.getItemFactory().getItemMeta(setAmount.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_AMOUNT);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_AMOUNT_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        setAmount.setItemMeta(meta);

        ItemStack makeCommand = XMaterial.COMMAND_BLOCK.parseItem();    //Change command item
        meta = Bukkit.getItemFactory().getItemMeta(makeCommand.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_ENABLE_COMMANDS);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_ENABLE_COMMANDS_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("\\{status}", "" + newItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))));
        }
        meta.setLore(lore);
        makeCommand.setItemMeta(meta);

        ItemStack addRemoveCommands = XMaterial.JUKEBOX.parseItem();    //add/remove commands
        meta = Bukkit.getItemFactory().getItemMeta(addRemoveCommands.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_CHANGE_COMMANDS);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_CHANGE_COMMANDS_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        addRemoveCommands.setItemMeta(meta);

        ItemStack hideEnchants = XMaterial.BLACK_BANNER.parseItem();    //add/remove enchants visible
        meta = Bukkit.getItemFactory().getItemMeta(hideEnchants.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_TOGGLE_ENCHANTS);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_TOGGLE_ENCHANTS_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("\\{status}", "" + newItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))));
        }
        meta.setLore(lore);
        hideEnchants.setItemMeta(meta);

        ItemStack hideAtibutes = XMaterial.BOOKSHELF.parseItem();    //add/remove attributes
        meta = Bukkit.getItemFactory().getItemMeta(hideAtibutes.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_TOGGLE_ATTRIBUTES);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("\\{status}", "" + newItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES))));
        }
        meta.setLore(lore);
        hideAtibutes.setItemMeta(meta);

        ItemStack hideEffects = XMaterial.END_CRYSTAL.parseItem(); //add/remove effects
        if (hideEffects == null) hideEffects = XMaterial.GUNPOWDER.parseItem();
        meta = Bukkit.getItemFactory().getItemMeta(hideEffects.getType());
        meta.setDisplayName(main.config.CUSTOMIZE_TOGGLE_EFFECTS);
        lore = new ArrayList<>();
        lore.add("");
        for (String s: main.config.CUSTOMIZE_TOGGLE_EFFECTS_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("\\{status}", "" + newItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))));
        }
        meta.setLore(lore);
        hideEffects.setItemMeta(meta);

        ItemStack generateMMOItem = null;
        if(main.utils.isMMOItem(newItem)) {
            generateMMOItem = XMaterial.BEACON.parseItem();    //scratch MMOItem
            meta = generateMMOItem.getItemMeta();
            meta.setDisplayName(main.config.CUSTOMIZE_TOGGLE_MMOITEM_SRATCH);
            lore = new ArrayList<>();
            lore.add("");
            for (String s: main.config.CUSTOMIZE_TOGGLE_MMOITEM_SRATCH_LORE) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("\\{status}", "" + main.utils.isItemScracth(newItem))));
            }
            meta.setLore(lore);
            generateMMOItem.setItemMeta(meta);
        }

        ItemStack changeRarity = null;
        if(main.getConfig().getBoolean("enable-rarity")) {
            switch(main.utils.getRarity(newItem)) {
                case 100: changeRarity = XMaterial.GRAY_DYE.parseItem(); main.utils.setRarityLore(changeRarity, 100); break;
                case 80: changeRarity = XMaterial.PINK_DYE.parseItem(); main.utils.setRarityLore(changeRarity, 80); break;
                case 60: changeRarity = XMaterial.MAGENTA_DYE.parseItem(); main.utils.setRarityLore(changeRarity, 60); break;
                case 40: changeRarity = XMaterial.PURPLE_DYE.parseItem(); main.utils.setRarityLore(changeRarity, 40); break;
                case 20: changeRarity = XMaterial.CYAN_DYE.parseItem(); main.utils.setRarityLore(changeRarity, 20); break;
                case 10: changeRarity = XMaterial.ORANGE_DYE.parseItem(); main.utils.setRarityLore(changeRarity, 10); break;
                case 5: changeRarity = XMaterial.YELLOW_DYE.parseItem(); main.utils.setRarityLore(changeRarity, 5); break;
            }

            meta = changeRarity.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Change rarity");
            changeRarity.setItemMeta(meta);
        }

        ItemStack changeCurrency = new ItemStack(Material.EMERALD);
        meta = Bukkit.getItemFactory().getItemMeta(changeCurrency.getType());
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Change item currency");
        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to change item currency");
        lore.add(ChatColor.GREEN + "Current: " + ChatColor.GRAY + main.utils.getEconomyType(newItem).getValue());
        meta.setLore(lore);
        changeCurrency.setItemMeta(meta);


        Integer[] auxList = {3, 5, 13};
        ItemStack item = main.utils.setItemAsFill(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());             //fill black panes
        for (int j: auxList) {
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            item.setItemMeta(meta);
            inv.setItem(j, item);
        }

        inv.setItem(0, changeCurrency);
        inv.setItem(4, newItem);
        if (changeRarity != null) inv.setItem(8, changeRarity);
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

        if(e.getSlot() != e.getRawSlot()) return;

        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        if ( e.getSlot() == 47) { //Boton de retornar
            p.openInventory(main.DailyGuiSettings.getFirstGui());
        }

        if ( e.getSlot() == 49) { //Boton de craft

            if (itemToReplace == null) {
                /*while (Bukkit.getScheduler().isCurrentlyRunning(main.updateListID.getTaskId())){
                    main.utils.waitXticks(10);
                }*/
                if( main.utils.listContaisItem(main.listDailyItems, newItem)) {
                    p.sendMessage(main.config.PREFIX + main.config.MSG_ITEM_ON_SALE);
                    return;
                }
                main.listDailyItems.put(newItem, 500.0);
               // main.dbManager.addDailyItem(newItem, 500.0);
            } else {
                /*while (Bukkit.getScheduler().isCurrentlyRunning(main.updateListID.getTaskId())){
                    main.utils.waitXticks(10);
                }*/
                Double price = main.utils.getItemPrice(main.listDailyItems, itemToReplace, false);
                main.utils.removeItemOnList(main.listDailyItems, itemToReplace);
                main.listDailyItems.put(newItem, price);
                //main.dbManager.deleteDailyItem(itemToReplace);
                //main.dbManager.addDailyItem(newItem, price);
            }
            HandlerList.unregisterAll(main.DailyGuiSettings);
            main.DailyGuiSettings = new dailyGuiSettings(main);

            p.openInventory(main.DailyGuiSettings.getFirstGui());
        }

        if ( e.getSlot() == 19) { // Boton de cambiar nombre
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
                    .text(main.config.CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT)
                    .itemLeft(new ItemStack(newItem))
                    .title(main.config.CUSTOMIZE_RENAME_ANVIL_TITLE)
                    .plugin(main)
                    .open(p);
        }

        if ( e.getSlot() == 20) { // Boton de cambiar material
            new changeMaterialGuiIH(main, p, (material, bool) -> {

                if (bool) {
                    newItem.setType(material);
                }
                new customizerMainGuiIH(main, p, newItem, itemToReplace);

            }, Material.values(), false);

        }

        if ( e.getSlot() == 0) { // Boton de cambiar lore
            new changeEconomy(main, p, newItem, (itemStack, aBoolean) -> {
                if(aBoolean) {
                    newItem = itemStack;
                }
                new customizerMainGuiIH(main, p, newItem, itemToReplace);
            });
        }

        if ( e.getSlot() == 28) { // Boton de cambiar lore
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
                        .text(main.config.CUSTOMIZE_CHANGE_LORE_DEFAULT_TEXT)
                        .itemLeft(new ItemStack(newItem))
                        .title(main.config.CUSTOMIZE_CHANGE_LORE_TITLE)
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

        if ( e.getSlot() == 29) { // Boton de cambiar enchants

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

        if ( e.getSlot() == 22) { // Boton de cambiar amount

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

        if ( e.getSlot() == 23) { // Boton de cambiar commands

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

        if ( e.getSlot() == 32 && e.getCurrentItem() != null ) { // Boton de cambiar commands

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
                        .text(main.config.CUSTOMIZE_ADD_COMMANDS_DEFAULT_TEXT)
                        .itemLeft(new ItemStack(newItem))
                        .title(main.config.CUSTOMIZE_ADD_COMMANDS_TITLE)
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

        if ( e.getSlot() == 25) { // Boton de hide enchants

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

        if ( e.getSlot() == 26) { // Boton de hide enchants

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

        if ( e.getSlot() == 35 && e.getCurrentItem() != null ) { // Boton de hide potion effects

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

        if ( e.getSlot() == 40 && e.getCurrentItem() != null ) { // Boton de scrath MMOItem

            if(e.isLeftClick()) {

                if (!main.utils.isItemScracth(newItem)) {
                    newItem = main.utils.setItemAsScracth(newItem);
                }
                else{
                    newItem = main.utils.removeItemScracth(newItem);
                }
                new customizerMainGuiIH(main, p , newItem, itemToReplace);
            }
        }

        if ( e.getSlot() == 8 && e.getCurrentItem() != null ) { // Boton de scrath MMOItem

            if(e.isLeftClick()) {
                newItem = main.utils.processNextRarity(newItem);
                new customizerMainGuiIH(main, p, newItem, itemToReplace);
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
