package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.conf_msg;
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
import org.bukkit.inventory.ItemStack;

public class customizerMainGuiIH implements InventoryHolder, Listener {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static Inventory inv = null;
    private ItemStack newItem;
    private ItemStack oldItem;

    private customizerMainGuiIH() {};

    public static void openInventory(Player p, ItemStack newItem, ItemStack oldItem) {
        if(inv == null) {
            createInventory();
        }
        customizerMainGuiIH instance = new customizerMainGuiIH();
        Bukkit.getPluginManager().registerEvents(instance, main);
        instance.newItem = newItem;
        instance.oldItem = oldItem;
        p.openInventory(instance.getInventory());

    }

    private static void createInventory() {
        inv = Bukkit.createInventory(null, 54, conf_msg.CUSTOMIZE_GUI_TITLE);

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

        ItemStack editEnchantments = XMaterial.BOOK.parseItem();    //Change enchants
        utils.setDisplayName(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS);
        utils.setLore(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS_LORE);

        ItemStack setAmount = XMaterial.STONE_BUTTON.parseItem();    //Change amount
        utils.setDisplayName(setAmount, conf_msg.CUSTOMIZE_AMOUNT);
        utils.setLore(setAmount, conf_msg.CUSTOMIZE_AMOUNT_LORE);

        ItemStack makeCommand = XMaterial.COMMAND_BLOCK.parseItem();    //Change command item
        utils.setDisplayName(makeCommand, conf_msg.CUSTOMIZE_ENABLE_COMMANDS);
        utils.setLore(makeCommand, conf_msg.CUSTOMIZE_ENABLE_COMMANDS_LORE);

        ItemStack addRemoveCommands = XMaterial.JUKEBOX.parseItem();    //add/remove commands
        utils.setDisplayName(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS);
        utils.setLore(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS_LORE);

        ItemStack hideEnchants = XMaterial.BLACK_BANNER.parseItem();    //add/remove enchants visible
        utils.setDisplayName(hideEnchants, conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS);
        utils.setLore(hideEnchants, conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS_LORE);

        ItemStack hideAtibutes = XMaterial.BOOKSHELF.parseItem();    //add/remove attributes
        utils.setDisplayName(hideAtibutes, conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES);
        utils.setLore(hideEnchants, conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE);

        ItemStack hideEffects = XMaterial.END_CRYSTAL.parseItem(); //add/remove effects
        utils.setDisplayName(hideEffects, conf_msg.CUSTOMIZE_TOGGLE_EFFECTS);
        utils.setLore(hideEffects, conf_msg.CUSTOMIZE_TOGGLE_EFFECTS_LORE);

        Integer[] auxList = {3, 5, 13};
        ItemStack item = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();             //fill black panes
        for (int j: auxList) {
            utils.setDisplayName(item, "&6");
            inv.setItem(j, item);
        }

        //inv.setItem(4, newItem);
        inv.setItem(19, rename);
        inv.setItem(20, changeMaterial);
        inv.setItem(28, changeLore);
        inv.setItem(29, editEnchantments);
        inv.setItem(22, setAmount);
        inv.setItem(23, makeCommand);
        //if (main.utils.isCommandItem(newItem)) inv.setItem(32, addRemoveCommands);
        inv.setItem(25, hideEnchants);
        inv.setItem(26, hideAtibutes);
        /*if (newItem.getType() == XMaterial.POTION.parseMaterial() ||
                newItem.getType() == XMaterial.SPLASH_POTION.parseMaterial()) {
            inv.setItem(35, hideEffects);
        }
        if (generateMMOItem != null) inv.setItem(40, generateMMOItem); */
        inv.setItem(47, returnItem);
        inv.setItem(49, customizerItem);

    }

    @Override
    public Inventory getInventory() {
        Inventory aux = Bukkit.createInventory(this, 54, conf_msg.CUSTOMIZE_GUI_TITLE);
        aux.setContents(inv.getContents());
        aux.setItem(4, newItem);
        return aux;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();

        if(e.getSlot() != e.getRawSlot()) return;

        if(utils.isEmpty(item)) return;

        if ( e.getSlot() == 47) { //Boton de retornar
            dailyGuiSettings.openInventory(p);
        }

        if ( e.getSlot() == 49) { //Boton de craft
            
        }

        if ( e.getSlot() == 19) { // Boton de cambiar nombre
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        utils.runTaskLater(() -> openInventory(player, newItem, oldItem), 1L);
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

        if ( e.getSlot() == 20) { // Boton de cambiar material
        }

        if ( e.getSlot() == 0) { // Boton de cambiar lore
        }

        if ( e.getSlot() == 28) { // Boton de cambiar lore
        }

        if ( e.getSlot() == 29) { // Boton de cambiar enchants
        }

        if ( e.getSlot() == 22) { // Boton de cambiar amount

        }

        if ( e.getSlot() == 23) { // Boton de cambiar commands

        }

        if ( e.getSlot() == 32 && e.getCurrentItem() != null ) { // Boton de cambiar commands

        }

        if ( e.getSlot() == 25) { // Boton de hide enchants

        }

        if ( e.getSlot() == 26) { // Boton de hide enchants
        }

        if ( e.getSlot() == 35 && e.getCurrentItem() != null ) { // Boton de hide potion effects
        }

        if ( e.getSlot() == 40 && e.getCurrentItem() != null ) { // Boton de scrath MMOItem

        }

        if ( e.getSlot() == 8 && e.getCurrentItem() != null ) { // Boton de scrath MMOItem

        }

    }

}