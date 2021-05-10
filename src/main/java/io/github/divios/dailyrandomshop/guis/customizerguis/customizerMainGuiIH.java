package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem.dailyMetadataType;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.settings.dailyGuiSettings;
import io.github.divios.dailyrandomshop.listeners.dynamicChatListener;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class customizerMainGuiIH implements InventoryHolder, Listener {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private static customizerMainGuiIH instance = null;
    private ItemStack newItem;

    private boolean amountFlag, commandsFlag, permsFlag, confirmGuiFlag, setItemsFlag, bundleFlag;

    private customizerMainGuiIH() {
    }

    public static void openInventory(Player p, ItemStack newItem) {
        if (instance == null) {
            instance = new customizerMainGuiIH();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        instance.newItem = newItem.clone();
        p.openInventory(instance.createInventory());
    }

    private Inventory createInventory() {

        dailyItem dItem = new dailyItem(newItem);

        amountFlag = dItem.hasMetadata(dailyMetadataType.rds_amount);
        commandsFlag = dItem.hasMetadata(dailyMetadataType.rds_commands);
        permsFlag = dItem.hasMetadata(dailyMetadataType.rds_permissions);
        confirmGuiFlag = (boolean) dItem.getMetadata(dailyMetadataType.rds_confirm_gui);
        setItemsFlag = dItem.hasMetadata(dailyMetadataType.rds_setItems);
        bundleFlag = dItem.hasMetadata(dailyMetadataType.rds_bundle);

        Inventory inv = Bukkit.createInventory(instance, 54, conf_msg.CUSTOMIZE_GUI_TITLE);

        ItemStack barrier = XMaterial.BARRIER.parseItem();
        utils.setDisplayName(barrier, "&c&lUNAVAILABLE");

        ItemStack changeEcon = XMaterial.EMERALD.parseItem();
        utils.setDisplayName(changeEcon, conf_msg.CUSTOMIZE_CHANGE_ECON);
        utils.setLore(changeEcon, conf_msg.CUSTOMIZE_CHANGE_ECON_LORE);

        ItemStack changeRarity = dailyItem.getItemRarity(newItem);
        utils.setLore(changeRarity, conf_msg.CUSTOMIZE_CHANGE_RARITY_LORE);

        ItemStack changeConfirmGui = XMaterial.LEVER.parseItem();
        utils.setDisplayName(changeConfirmGui, conf_msg.CUSTOMIZE_CHANGE_CONFIRM_GUI);
        utils.setLore(changeConfirmGui, utils.replaceOnLore(conf_msg.CUSTOMIZE_CHANGE_CONFIRM_GUI_LORE
                    , "\\{status}", "" +
                        confirmGuiFlag));

        ItemStack customizerItem = XMaterial.ANVIL.parseItem();   //Done button (anvil)
        utils.setDisplayName(customizerItem, conf_msg.CUSTOMIZE_CRAFT);
        utils.setLore(customizerItem, conf_msg.CUSTOMIZE_CRAFT_LORE);

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();    //Back sign
        utils.setDisplayName(returnItem, conf_msg.CUSTOMIZE_RETURN);
        utils.setLore(returnItem, conf_msg.CUSTOMIZE_RETURN_LORE);

        ItemStack rename = XMaterial.NAME_TAG.parseItem();    //Rename item
        utils.setDisplayName(rename, conf_msg.CUSTOMIZE_RENAME);
        utils.setLore(rename, conf_msg.CUSTOMIZE_RENAME_LORE);

        ItemStack changeMaterial = XMaterial.SLIME_BALL.parseItem();    //Change material
        utils.setDisplayName(changeMaterial, conf_msg.CUSTOMIZE_MATERIAL);
        utils.setLore(changeMaterial, conf_msg.CUSTOMIZE_MATERIAL_LORE);

        ItemStack changeLore = XMaterial.PAPER.parseItem();    //Change lore
        utils.setDisplayName(changeLore, conf_msg.CUSTOMIZE_LORE);
        utils.setLore(changeLore, conf_msg.CUSTOMIZE_LORE_LORE);
        utils.setLore(changeLore, Arrays.asList(""));
        utils.setLore(changeLore, newItem.getItemMeta().getLore());

        ItemStack editEnchantments = XMaterial.BOOK.parseItem();    //Change enchants
        utils.setDisplayName(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS);
        utils.setLore(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS_LORE);
        utils.setLore(editEnchantments, newItem.getEnchantments().entrySet().stream()
                .map(entry -> "&f&l" + entry.getKey()
                .getName() + ":" + entry.getValue()).collect(Collectors.toList()));

        ItemStack setAmount = XMaterial.STONE_BUTTON.parseItem();    //Change amount
        utils.setDisplayName(setAmount, conf_msg.CUSTOMIZE_AMOUNT);
        if (amountFlag)
            utils.setLore(setAmount, utils.replaceOnLore(conf_msg.CUSTOMIZE_AMOUNT_LORE,
                    "\\{amount}"
                    , "" + new dailyItem(newItem).getMetadata(dailyMetadataType.rds_amount)));
        else
            utils.setLore(setAmount, conf_msg.CUSTOMIZE_AMOUNT_ENABLE_LORE);


        ItemStack addRemoveCommands = XMaterial.COMMAND_BLOCK.parseItem();     //Change command item
        if (!commandsFlag) {
            utils.setDisplayName(addRemoveCommands, conf_msg.CUSTOMIZE_ENABLE_COMMANDS);
            utils.setLore(addRemoveCommands, utils.replaceOnLore(
                    conf_msg.CUSTOMIZE_ENABLE_COMMANDS_LORE, "\\{status}",
                    "" + new dailyItem(newItem)
                            .hasMetadata(dailyMetadataType.rds_commands)));
        } else {                                                                //add/remove commands
            utils.setDisplayName(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS);
            utils.setLore(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS_LORE);
            utils.setLore(addRemoveCommands, Arrays.asList(""));
            utils.setLore(addRemoveCommands, ((List<String>) new dailyItem(newItem)
                    .getMetadata(dailyMetadataType.rds_commands))
                    .stream().map(s -> utils.formatString("&f&l" + s)).collect(Collectors.toList()));
        }


        ItemStack perms = XMaterial.WRITTEN_BOOK.parseItem();   //add remove perms
        utils.setDisplayName(perms, conf_msg.CUSTOMIZE_PERMS);
        if (!permsFlag) {
            utils.setLore(perms, conf_msg.CUSTOMIZE_ENABLE_PERMS_LORE);
        }
        else {
            utils.setLore(perms, conf_msg.CUSTOMIZE_CHANGE_PERMS_LORE);
            utils.setLore(perms, Collections.singletonList(""));
            utils.setLore(perms, ( (List<String>) new dailyItem(newItem)
                    .getMetadata(dailyMetadataType.rds_permissions))
                    .stream().map(s -> utils.formatString("&f&l" + s)).collect(Collectors.toList()));
        }

        ItemStack setOfItems = XMaterial.CHEST_MINECART.parseItem();    //set of items
        utils.setDisplayName(setOfItems, conf_msg.CUSTOMIZE_SET);
        if (setItemsFlag) {
            utils.setLore(setOfItems, utils.replaceOnLore(
                    conf_msg.CUSTOMIZE_CHANGE_SET_LORE, "\\{amount}",
                    "" + new dailyItem(newItem)
                            .getMetadata(dailyMetadataType.rds_setItems)));
        } else {
            utils.setLore(setOfItems, conf_msg.CUSTOMIZE_ENABLE_SET_LORE);
        }

        ItemStack bundle = XMaterial.ITEM_FRAME.parseItem();                         //bundle
        utils.setDisplayName(bundle, "&f&lChange bundle items");
        utils.setLore(bundle, Arrays.asList("&6Right Click > &7To change items on the bundle"));

        ItemStack durability = XMaterial.DAMAGED_ANVIL.parseItem();
        utils.setDisplayName(durability, "&fChange item Durability");
        utils.setLore(durability, Arrays.asList("&6Right Click > &7To change items durability"));

        ItemFlag f = ItemFlag.HIDE_ENCHANTS;

        ItemStack hideEnchants = XMaterial.BLACK_BANNER.parseItem();    //add/remove enchants visible
        utils.setDisplayName(hideEnchants, conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS);
        utils.setLore(hideEnchants, utils.replaceOnLore(
                conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS_LORE, "\\{status}",
                "" + utils.hasFlag(newItem, f)));

        f = ItemFlag.HIDE_ATTRIBUTES;
        ItemStack hideAtibutes = XMaterial.BOOKSHELF.parseItem();    //add/remove attributes
        utils.setDisplayName(hideAtibutes, conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES);
        utils.setLore(hideAtibutes, utils.replaceOnLore(
                conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE, "\\{status}",
                "" + utils.hasFlag(newItem, f)));

        f = ItemFlag.HIDE_POTION_EFFECTS;
        ItemStack hideEffects = XMaterial.CAULDRON.parseItem(); //add/remove effects
        utils.setDisplayName(hideEffects, conf_msg.CUSTOMIZE_TOGGLE_EFFECTS);
        utils.setLore(hideEffects, utils.replaceOnLore(
                conf_msg.CUSTOMIZE_TOGGLE_EFFECTS_LORE, "\\{status}",
                "" + utils.hasFlag(newItem, f)));

        Integer[] auxList = {3, 5, 13};
        ItemStack item = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();             //fill black panes
        for (int j : auxList) {
            utils.setDisplayName(item, "&6");
            inv.setItem(j, item);
        }

        if(new dailyItem(newItem).hasMetadata(dailyMetadataType.rds_amount))
            newItem.setAmount((Integer) new dailyItem(newItem).
                    getMetadata(dailyMetadataType.rds_amount));

        inv.setItem(4, newItem);
        inv.setItem(0, changeEcon);
        if (conf_msg.ENABLE_RARITY) inv.setItem(8, changeRarity);
        inv.setItem(7, changeConfirmGui);
        inv.setItem(18, rename);
        inv.setItem(19, changeMaterial);
        inv.setItem(27, changeLore);
        inv.setItem(28, editEnchantments);
        if (!setItemsFlag) inv.setItem(21, setAmount);
        else inv.setItem(21, barrier);
        inv.setItem(22, addRemoveCommands);
        if (newItem.getType().getMaxDurability() != 0)
            inv.setItem(23, durability);
        inv.setItem(30, perms);
        if (!amountFlag) inv.setItem(31, setOfItems);
        else inv.setItem(31, barrier);
        if (bundleFlag)
            inv.setItem(32, bundle);
        inv.setItem(25, hideEnchants);
        inv.setItem(26, hideAtibutes);
        if (utils.isPotion(newItem)) {
            inv.setItem(35, hideEffects);
        }
        /* if (generateMMOItem != null) inv.setItem(40, generateMMOItem); */
        inv.setItem(47, returnItem);
        inv.setItem(49, customizerItem);

        return inv;
    }

    private void refresh (Player p) {
        openInventory(p, newItem);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() != e.getRawSlot()) return;

        if (utils.isEmpty(item)) return;

        if (e.getSlot() == 47) { //Boton de retornar
            dailyGuiSettings.openInventory(p);
        }

        else if (e.getSlot() == 49) { //Boton de craft
            ItemStack aux = dailyItem.getRawItem(newItem);
            if (!utils.isEmpty(aux)) {
                utils.translateAllItemData(newItem, aux);
                dailyGuiSettings.openInventory(p);
                buyGui.getInstance().updateItem(dailyItem.getUuid(newItem), buyGui.updateAction.update);
            }
            else dbManager.listDailyItems.put(new dailyItem(newItem, true)
                    .addNbt(dailyMetadataType.rds_itemEcon, new dailyItem.dailyItemPrice(conf_msg.DEFAULT_PRICE))
                    .craft(), 0D);
            dailyGuiSettings.openInventory(p);
        }


        else if (e.getSlot() == 0) {        /* Boton de cambiar economia */
            changeEcon.openInventory(p, newItem);
        }

        else if (e.getSlot() == 7) {        /* Boton de cambiar confirm Gui */
            if (confirmGuiFlag)
                new dailyItem(newItem).addNbt(dailyMetadataType.rds_confirm_gui, false).getItem();
            else
                new dailyItem(newItem).addNbt(dailyMetadataType.rds_confirm_gui, true).getItem();
            refresh(p);
        }

        else if(e.getSlot() == 8 && !utils.isEmpty(e.getCurrentItem())) {    /* Boton de cambiar rarity */
             new dailyItem(newItem).addNbt(dailyMetadataType.rds_rarity, "").getItem();
             refresh(p);
        }
        else if (e.getSlot() == 18) { // Boton de cambiar nombre
            new AnvilGUI.Builder()
                    .onClose(player ->
                            utils.runTaskLater(() -> openInventory(player, newItem), 1L))
                    .onComplete((player, text) -> {
                        utils.setDisplayName(newItem, text);
                        return AnvilGUI.Response.close();
                    })
                    .text(conf_msg.CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT)
                    .itemLeft(new ItemStack(newItem))
                    .title(conf_msg.CUSTOMIZE_RENAME_ANVIL_TITLE)
                    .plugin(main)
                    .open(p);
        }

        else if (e.getSlot() == 19) { // Boton de cambiar material
            changeMaterialGui.openInventory(p, newItem);
        }

        else if (e.getSlot() == 27) { // Boton de cambiar lore
            if (e.isRightClick()) {
                utils.removeLore(newItem, 1);
                refresh(p);
            } else if (e.isLeftClick())
                new dynamicChatListener(p, s -> {
                    if (!s.isEmpty()) {
                        utils.setLore(newItem, Arrays.asList(s));
                    }
                    refresh(p);
                }, conf_msg.CUSTOMIZE_RENAME_ANVIL_TITLE, "");
        }

        else if (e.getSlot() == 28) { // Boton de cambiar enchants
            if (e.isLeftClick()) changeEnchantments.openInventory(p, newItem);
            else if (e.isRightClick() && !newItem.getEnchantments().isEmpty())
                changeEnchantments.openInventory(p, newItem, newItem.getEnchantments());
        }

        else if (e.getSlot() == 21 && amountFlag) { // Boton de cambiar amount
            if(e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> utils.runTaskLater(() ->
                                openInventory(player, newItem), 1L))
                        .onComplete((player, text) -> {
                            try {
                                Integer.parseInt(text);
                            } catch (NumberFormatException err) {return AnvilGUI.Response.text("not integer");}
                            int i = Integer.parseInt(text);
                            if(i < 1 || i > 64) return AnvilGUI.Response.text("invalid amount");
                            new dailyItem(newItem)
                                    .addNbt(dailyMetadataType.rds_amount, text).getItem();
                            return AnvilGUI.Response.close();
                        })
                        .text("Change amount")
                        .itemLeft(newItem.clone())
                        .title("&6Change amount")
                        .plugin(main)
                        .open(p);
            }
            else if (e.isRightClick()) {
                new dailyItem(newItem).removeNbt(dailyMetadataType.rds_amount).getItem();
                newItem.setAmount(1);
                refresh(p);
            }
        }

        else if (e.getSlot() == 21 && !amountFlag && e.isLeftClick()) { // Boton de cambiar amount
            if (setItemsFlag) {
                p.sendMessage(conf_msg.PREFIX + utils.formatString("&7You can't enable this when " +
                        "the set feature is enable"));
                return;
            }
            new dailyItem(newItem).addNbt(dailyMetadataType.rds_amount, "" + 1).getItem();
            refresh(p);

        }

        else if (e.getSlot() == 22 && !new dailyItem(newItem)
                .hasMetadata(dailyMetadataType.rds_commands)) { // Boton de cambiar commands
            new dailyItem(newItem).addNbt(dailyMetadataType.rds_commands, "").getItem();
            refresh(p);
        }

        else if (e.getSlot() == 22 && new dailyItem(newItem)
                .hasMetadata(dailyMetadataType.rds_commands)) { // Boton de añadir/quitar commands
            if (e.isLeftClick()) {
                new dynamicChatListener(p, s -> {
                    if (!s.isEmpty())
                        new dailyItem(newItem).addNbt(dailyMetadataType.rds_commands, s).getItem();
                    refresh(p);
                }, utils.formatString("&7Input new command"), "");

            } else if (e.isRightClick() && !e.isShiftClick()) {
                List<String> s = (List<String>) new dailyItem(newItem)
                        .getMetadata(dailyMetadataType.rds_commands);
                new dailyItem(newItem).removeNbt(dailyMetadataType.rds_commands).getItem();
                if (!s.isEmpty()) {
                    s.remove(s.size() - 1);
                    s.forEach(s1 -> new dailyItem(newItem).addNbt(dailyMetadataType.rds_commands, s1).getItem());
                }
                refresh(p);
            } else if (e.isShiftClick() && e.isRightClick()) {
                new dailyItem(newItem).removeNbt(dailyMetadataType.rds_commands).getItem();
                refresh(p);
            }
        }

        else if (e.getSlot() == 23) {               //durability

            new AnvilGUI.Builder()
                    .onClose(player -> utils.runTaskLater(() ->
                            openInventory(player, newItem), 1L))
                    .onComplete((player, text) -> {
                        try {
                            Short.parseShort(text);
                        } catch (NumberFormatException err) {return AnvilGUI.Response.text("not integer");}
                        short i = Short.parseShort(text);
                        newItem.setDurability((short) (newItem.getType().getMaxDurability() - i));
                        return AnvilGUI.Response.close();
                    })
                    .text("Change durability")
                    .itemLeft(newItem.clone())
                    .title("&6Change durability")
                    .plugin(main)
                    .open(p);

        }

        else if (e.getSlot() == 32) {  //boton de bundle
            new changeBundleItem(p, newItem,
                    (player, itemStack) -> {
                    newItem = itemStack;
                    refresh(p);
            }, player -> refresh(p));
        }

        else if (e.getSlot() == 25) { // Boton de hide enchants
            ItemFlag f = ItemFlag.HIDE_ENCHANTS;
            if (utils.hasFlag(newItem, f)) utils.removeFlag(newItem, f);
            else utils.addFlag(newItem, f);
            refresh(p);
        }

        else if (e.getSlot() == 26) { // Boton de hide attributes
            ItemFlag f = ItemFlag.HIDE_ATTRIBUTES;
            if (utils.hasFlag(newItem, f)) utils.removeFlag(newItem, f);
            else utils.addFlag(newItem, f);
            refresh(p);
        }

        else if (e.getSlot() == 30 && !permsFlag) { // Boton de habilitar perms
            new dailyItem(newItem).addNbt(dailyMetadataType.rds_permissions, "").getItem();
            refresh(p);
        }

        else if (e.getSlot() == 30 && permsFlag) {  // Boton de añadir/quitar perms
            if (e.isLeftClick()) {
                new dynamicChatListener(p, s -> {
                    if (!s.isEmpty())
                        new dailyItem(newItem).addNbt(dailyMetadataType.rds_permissions, s).getItem();
                    refresh(p);
                }, utils.formatString("&7Input permission"), "");

            } else if (e.isRightClick() && !e.isShiftClick()) {
                List<String> s = (List<String>) new dailyItem(newItem)
                        .getMetadata(dailyMetadataType.rds_permissions);
                new dailyItem(newItem).removeNbt(dailyMetadataType.rds_permissions).getItem();
                if (!s.isEmpty()) {
                    s.remove(s.size() - 1);
                    s.forEach(s1 -> new dailyItem(newItem).addNbt(dailyMetadataType.rds_permissions, s1).getItem());
                }
                refresh(p);
            } else if (e.isShiftClick() && e.isRightClick()) {
                new dailyItem(newItem).removeNbt(dailyMetadataType.rds_permissions).getItem();
                refresh(p);
            }

        }

        else if (e.getSlot() == 31 && !setItemsFlag && e.isLeftClick()) {  // Boton de edit set
            if (amountFlag) {
                p.sendMessage(conf_msg.PREFIX + utils.formatString("&7You can't enable this when " +
                        "the stock feature is enable"));
                return;
            }
            new dailyItem(newItem).addNbt(dailyMetadataType.rds_setItems, "1").getItem();
            refresh(p);
        }

        else if (e.getSlot() == 31 && setItemsFlag) {
            if (e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> utils.runTaskLater(() ->
                                openInventory(player, newItem), 1L))
                        .onComplete((player, text) -> {
                            try {
                                Integer.parseInt(text);
                            } catch (NumberFormatException err) {return AnvilGUI.Response.text("not integer");}
                            int i = Integer.parseInt(text);
                            if(i < 1 || i > 64) return AnvilGUI.Response.text("invalid amount");
                            new dailyItem(newItem)
                                    .addNbt(dailyMetadataType.rds_setItems, text).getItem();
                            newItem.setAmount(Integer.parseInt(text));
                            return AnvilGUI.Response.close();
                        })
                        .text("Change amount")
                        .itemLeft(newItem.clone())
                        .title("&6Change amount")
                        .plugin(main)
                        .open(p);
            }
            else if (e.isRightClick()) {
                new dailyItem(newItem).removeNbt(dailyMetadataType.rds_setItems).getItem();
                newItem.setAmount(1);
                refresh(p);
            }
        }

        else if (e.getSlot() == 35 && e.getCurrentItem() != null) { // Boton de hide potion effects
            ItemFlag f = ItemFlag.HIDE_POTION_EFFECTS;
            if (utils.hasFlag(newItem, f)) utils.removeFlag(newItem, f);
            else utils.addFlag(newItem, f);
            refresh(p);
        }

    }

}
