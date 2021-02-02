package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.builders.factory.itemsFactory;
import io.github.divios.dailyrandomshop.builders.factory.itemsFactory.dailyMetadataType;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.settings.dailyGuiSettings;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class customizerMainGuiIH implements InventoryHolder, Listener {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private static customizerMainGuiIH instance = null;
    private ItemStack newItem;

    private customizerMainGuiIH() {
    }

    public static void openInventory(Player p, ItemStack newItem) {
        if (instance == null) {
            instance = new customizerMainGuiIH();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        instance.newItem = newItem;
        p.openInventory(instance.createInventory());
    }

    private Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(instance, 54, conf_msg.CUSTOMIZE_GUI_TITLE);

        ItemStack changeRarity = utils.getItemRarity(
                (Integer) new itemsFactory.Builder(newItem).getMetadata(dailyMetadataType.rds_rarity));
        utils.setLore(changeRarity, Arrays.asList("&7Click to change rarity"));

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
        utils.setLore(changeLore, newItem.getItemMeta().getLore());

        ItemStack editEnchantments = XMaterial.BOOK.parseItem();    //Change enchants
        utils.setDisplayName(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS);
        utils.setLore(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS_LORE);
        utils.setLore(editEnchantments, newItem.getEnchantments().entrySet().stream()
                .map(entry -> "&f&l" + entry.getKey()
                .getName() + ":" + entry.getValue()).collect(Collectors.toList()));

        ItemStack setAmount = XMaterial.STONE_BUTTON.parseItem();    //Change amount
        utils.setDisplayName(setAmount, conf_msg.CUSTOMIZE_AMOUNT);
        utils.setLore(setAmount, conf_msg.CUSTOMIZE_AMOUNT_LORE);

        ItemStack makeCommand = XMaterial.COMMAND_BLOCK.parseItem();    //Change command item
        utils.setDisplayName(makeCommand, conf_msg.CUSTOMIZE_ENABLE_COMMANDS);
        utils.setLore(makeCommand, utils.replaceOnLore(
                conf_msg.CUSTOMIZE_ENABLE_COMMANDS_LORE, "\\{status}",
                "" + new itemsFactory.Builder(newItem)
                        .hasMetadata(itemsFactory.dailyMetadataType.rds_commands)));

        ItemStack addRemoveCommands = XMaterial.JUKEBOX.parseItem();    //add/remove commands
        utils.setDisplayName(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS);
        utils.setLore(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS_LORE);
        utils.setLore(addRemoveCommands, ( (List<String>) new itemsFactory.Builder(newItem)
                .getMetadata(dailyMetadataType.rds_commands))
                .stream().map(s -> utils.formatString("&f&l" + s)).collect(Collectors.toList()));

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
        ItemStack hideEffects = XMaterial.END_CRYSTAL.parseItem(); //add/remove effects
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

        if(new itemsFactory.Builder(newItem).hasMetadata(itemsFactory.dailyMetadataType.rds_amount))
            newItem.setAmount((Integer) new itemsFactory.Builder(newItem).
                    getMetadata(itemsFactory.dailyMetadataType.rds_amount));

        inv.setItem(4, newItem);
        inv.setItem(8, changeRarity);
        inv.setItem(19, rename);
        inv.setItem(20, changeMaterial);
        inv.setItem(28, changeLore);
        inv.setItem(29, editEnchantments);
        inv.setItem(22, setAmount);
        inv.setItem(23, makeCommand);
        if (new itemsFactory.Builder(newItem).hasMetadata(dailyMetadataType.rds_commands))
            inv.setItem(32, addRemoveCommands);
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
            String uuid = new itemsFactory.Builder(newItem, false).getUUID();
            ItemStack aux = utils.getItemByUuid(uuid, dbManager.listDailyItems);
            if (aux != null && !utils.isEmpty(aux)) {
                utils.translateAllItemData(newItem, aux);
                dailyGuiSettings.openInventory(p);
            }
            else dbManager.listDailyItems.put(new itemsFactory.Builder(newItem, true)
                    .craft(), 500D);
            dailyGuiSettings.openInventory(p);
            buyGui.getInstance().updateItem(uuid, buyGui.updateAction.update);
        }


         else if(e.getSlot() == 8) {
             new itemsFactory.Builder(newItem).addNbt(dailyMetadataType.rds_rarity, "").getItem();
             openInventory(p, newItem);
        }
        else if (e.getSlot() == 19) { // Boton de cambiar nombre
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        utils.runTaskLater(() -> openInventory(player, newItem), 1L);
                    })
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

        else if (e.getSlot() == 20) { // Boton de cambiar material
            changeMaterialGui.openInventory(p, newItem);
        }

        else if (e.getSlot() == 28) { // Boton de cambiar lore
            if (e.isRightClick()) {
                utils.removeLore(newItem, 1);
                openInventory(p, newItem);
            } else if (e.isLeftClick())
                new AnvilGUI.Builder()
                        .onClose(player -> utils.runTaskLater(() ->
                                openInventory(player, newItem), 1L))
                        .onComplete((player, text) -> {
                            utils.setLore(newItem, Arrays.asList(text));
                            return AnvilGUI.Response.close();
                        })
                        .text(conf_msg.CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT)
                        .itemLeft(newItem.clone())
                        .title(conf_msg.CUSTOMIZE_RENAME_ANVIL_TITLE)
                        .plugin(main)
                        .open(p);
        }

        else if (e.getSlot() == 29) { // Boton de cambiar enchants
            if (e.isLeftClick()) changeEnchantments.openInventory(p, newItem);
            else if (e.isRightClick() && !newItem.getEnchantments().isEmpty())
                changeEnchantments.openInventory(p, newItem, newItem.getEnchantments());
        }

        else if (e.getSlot() == 22) { // Boton de cambiar amount
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
                            new itemsFactory.Builder(newItem)
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
                new itemsFactory.Builder(newItem).removeNbt(dailyMetadataType.rds_amount).getItem();
                newItem.setAmount(1);
                openInventory(p, newItem);
            }
        }

        else if (e.getSlot() == 23) { // Boton de cambiar commands

            dailyMetadataType type = dailyMetadataType.rds_commands;

            if(new itemsFactory.Builder(newItem).hasMetadata(type))
                new itemsFactory.Builder(newItem).removeNbt(type).getItem();
            else new itemsFactory.Builder(newItem).addNbt(type, "").getItem();

            customizerMainGuiIH.openInventory(p, newItem);
        }

        else if (e.getSlot() == 32 && e.getCurrentItem() != null) { // Boton de añadir/quitar commands
            if(e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> utils.runTaskLater(() ->
                                openInventory(player, newItem), 1L))
                        .onComplete((player, text) -> {
                            if (text.isEmpty()) return AnvilGUI.Response.text("cannot be empty");
                            new itemsFactory.Builder(newItem).addNbt(dailyMetadataType.rds_commands, text).getItem();
                            return AnvilGUI.Response.close();
                        })
                        .text(conf_msg.CUSTOMIZE_ADD_COMMANDS_TITLE)
                        .itemLeft(newItem.clone())
                        .title(conf_msg.CUSTOMIZE_ADD_COMMANDS_TITLE)
                        .plugin(main)
                        .open(p);
            }
            else if (e.isRightClick()) {
                List<String> commands = (List<String>) new itemsFactory.Builder(newItem)
                        .getMetadata(dailyMetadataType.rds_commands);

                new itemsFactory.Builder(newItem).removeAllMetadata().getItem();
                commands.remove(commands.size() - 1);
                commands.stream().forEach(s -> new itemsFactory.Builder(newItem)
                        .addNbt(dailyMetadataType.rds_commands, s));
            }
        }

        else if (e.getSlot() == 25) { // Boton de hide enchants
            ItemFlag f = ItemFlag.HIDE_ENCHANTS;
            if (utils.hasFlag(newItem, f)) utils.removeFlag(newItem, f);
            else utils.addFlag(newItem, f);
            openInventory(p, newItem);
        }

        else if (e.getSlot() == 26) { // Boton de hide attributes
            ItemFlag f = ItemFlag.HIDE_ATTRIBUTES;
            if (utils.hasFlag(newItem, f)) utils.removeFlag(newItem, f);
            else utils.addFlag(newItem, f);
            openInventory(p, newItem);
        }

        else if (e.getSlot() == 35 && e.getCurrentItem() != null) { // Boton de hide potion effects
            ItemFlag f = ItemFlag.HIDE_POTION_EFFECTS;
            if (utils.hasFlag(newItem, f)) utils.removeFlag(newItem, f);
            else utils.addFlag(newItem, f);
            openInventory(p, newItem);
        }

    }

}
