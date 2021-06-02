package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.settings.shopGui;
import io.github.divios.dailyrandomshop.listeners.dynamicChatListener;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.managers.shopsManager;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class customizerMainGuiIH implements InventoryHolder, Listener {

    private static final DRShop main = DRShop.getInstance();
    private static customizerMainGuiIH instance = null;
    private dItem ditem;
    private dShop shop;

    private customizerMainGuiIH() {}

    public static void openInventory(Player p, dItem ditem, String shopName) {
        if (instance == null) {
            instance = new customizerMainGuiIH();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        instance.ditem = ditem.clone();
        instance.shop = shopsManager.getInstance().getShop(shopName);
        p.openInventory(instance.createInventory());
    }

    public static void openInventory(Player p, dItem ditem, dShop shop) {
        if (instance == null) {
            instance = new customizerMainGuiIH();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        instance.ditem = ditem.clone();
        instance.shop = shop;
        p.openInventory(instance.createInventory());
    }

    private Inventory createInventory() {

        Inventory inv = Bukkit.createInventory(instance, 54, conf_msg.CUSTOMIZE_GUI_TITLE);

        ItemStack barrier = XMaterial.BARRIER.parseItem();
        utils.setDisplayName(barrier, conf_msg.CUSTOMIZE_UNAVAILABLE);

        ItemStack changeEcon = XMaterial.PLAYER_HEAD.parseItem();      // Change econ
        utils.applyTexture(changeEcon, "e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852");
        utils.setDisplayName(changeEcon, conf_msg.CUSTOMIZE_CHANGE_ECON);
        utils.setLore(changeEcon, conf_msg.CUSTOMIZE_CHANGE_ECON_LORE);
        utils.setLore(changeEcon, Arrays.asList("", "&7Current: " + ditem.getEconomy().getClass().getName()
                .replace("io.github.divios.dailyrandomshop.economies.", "&6&l")));

        ItemStack changeRarity = ditem.getRarity().getAsItem();         // Change rarity
        utils.setLore(changeRarity, conf_msg.CUSTOMIZE_CHANGE_RARITY_LORE);

        ItemStack changeConfirmGui = XMaterial.LEVER.parseItem();
        utils.setDisplayName(changeConfirmGui, conf_msg.CUSTOMIZE_CHANGE_CONFIRM_GUI);
        utils.setLore(changeConfirmGui, utils.replaceOnLore(conf_msg.CUSTOMIZE_CHANGE_CONFIRM_GUI_LORE
                , "\\{status}", "" +
                        ditem.getConfirm_gui()));

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

        ItemStack changeLore = XMaterial.PLAYER_HEAD.parseItem();    //Change lore
        utils.applyTexture(changeLore, "c6692f99cc6d78242304110553589484298b2e4a0233b76753f888e207ef5");
        utils.setDisplayName(changeLore, conf_msg.CUSTOMIZE_LORE);
        utils.setLore(changeLore, conf_msg.CUSTOMIZE_LORE_LORE);
        utils.setLore(changeLore, Arrays.asList(""));
        utils.setLore(changeLore, utils.getItemLore(ditem.getItem().getItemMeta()));

        ItemStack editEnchantments = XMaterial.ENCHANTING_TABLE.parseItem();   //Change enchants
        utils.setDisplayName(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS);
        utils.setLore(editEnchantments, conf_msg.CUSTOMIZE_ENCHANTS_LORE);
        utils.setLore(editEnchantments, ditem.getItem().getEnchantments().entrySet().stream()
                .map(entry -> "&f&l" + entry.getKey()
                        .getName() + ":" + entry.getValue()).collect(Collectors.toList()));

        ItemStack setAmount = XMaterial.STONE_BUTTON.parseItem();    //Change amount
        utils.setDisplayName(setAmount, conf_msg.CUSTOMIZE_AMOUNT);
        if (ditem.getStock() != null)
            utils.setLore(setAmount, utils.replaceOnLore(conf_msg.CUSTOMIZE_AMOUNT_LORE,
                    "\\{amount}"
                    , "" + ditem.getStock()));
        else
            utils.setLore(setAmount, conf_msg.CUSTOMIZE_AMOUNT_ENABLE_LORE);


        ItemStack addRemoveCommands = XMaterial.COMMAND_BLOCK.parseItem();    //Change command item
        if (ditem.getCommands() == null) {
            utils.setDisplayName(addRemoveCommands, conf_msg.CUSTOMIZE_ENABLE_COMMANDS);
            utils.setLore(addRemoveCommands, utils.replaceOnLore(
                    conf_msg.CUSTOMIZE_ENABLE_COMMANDS_LORE, "\\{status}",
                    "false"));
        } else {                                                                //add/remove commands
            utils.setDisplayName(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS);
            utils.setLore(addRemoveCommands, conf_msg.CUSTOMIZE_CHANGE_COMMANDS_LORE);
            utils.setLore(addRemoveCommands, Arrays.asList(""));
            utils.setLore(addRemoveCommands, ditem.getCommands()
                    .stream().map(s -> utils.formatString("&f&l" + s)).collect(Collectors.toList()));
        }


        ItemStack perms = XMaterial.PLAYER_HEAD.parseItem();   //add remove perms
        utils.applyTexture(perms, "4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b");
        utils.setDisplayName(perms, conf_msg.CUSTOMIZE_PERMS);
        if (ditem.getPerms() == null) {
            utils.setLore(perms, conf_msg.CUSTOMIZE_ENABLE_PERMS_LORE);
        }
        else {
            utils.setLore(perms, conf_msg.CUSTOMIZE_CHANGE_PERMS_LORE);
            utils.setLore(perms, Collections.singletonList(""));
            utils.setLore(perms, ditem.getPerms()
                    .stream().map(s -> utils.formatString("&f&l" + s)).collect(Collectors.toList()));
        }

        ItemStack setOfItems = XMaterial.CHEST_MINECART.parseItem();    //set of items
        utils.setDisplayName(setOfItems, conf_msg.CUSTOMIZE_SET);
        if (ditem.getSetItems() != null) {
            utils.setLore(setOfItems, utils.replaceOnLore(
                    conf_msg.CUSTOMIZE_CHANGE_SET_LORE, "\\{amount}",
                    "" + ditem.getSetItems()));
        } else {
            utils.setLore(setOfItems, conf_msg.CUSTOMIZE_ENABLE_SET_LORE);
        }

        ItemStack bundle = XMaterial.ITEM_FRAME.parseItem();                         //bundle
        utils.setDisplayName(bundle, "&f&lChange bundle items");
        utils.setLore(bundle, Arrays.asList("&6Right Click > &7To change items on the bundle"));

        ItemStack durability = XMaterial.DAMAGED_ANVIL.parseItem();
        utils.setDisplayName(durability, conf_msg.CUSTOMIZE_DURABILITY);
        utils.setLore(durability, conf_msg.CUSTOMIZE_DURABILITY_LORE);

        ItemFlag f = ItemFlag.HIDE_ENCHANTS;

        ItemStack hideEnchants = XMaterial.BLACK_BANNER.parseItem();    //add/remove enchants visible
        utils.setDisplayName(hideEnchants, conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS);
        utils.setLore(hideEnchants, utils.replaceOnLore(
                conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS_LORE, "\\{status}",
                "" + ditem.hasFlag(f)));

        f = ItemFlag.HIDE_ATTRIBUTES;
        ItemStack hideAtibutes = XMaterial.BOOKSHELF.parseItem();    //add/remove attributes
        utils.setDisplayName(hideAtibutes, conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES);
        utils.setLore(hideAtibutes, utils.replaceOnLore(
                conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE, "\\{status}",
                "" + ditem.hasFlag(f)));

        f = ItemFlag.HIDE_POTION_EFFECTS;
        ItemStack hideEffects = XMaterial.CAULDRON.parseItem(); //add/remove effects
        utils.setDisplayName(hideEffects, conf_msg.CUSTOMIZE_TOGGLE_EFFECTS);
        utils.setLore(hideEffects, utils.replaceOnLore(
                conf_msg.CUSTOMIZE_TOGGLE_EFFECTS_LORE, "\\{status}",
                "" + ditem.hasFlag(f)));

        Integer[] auxList = {3, 5, 13};
        ItemStack item = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();             //fill black panes
        for (int j : auxList) {
            utils.setDisplayName(item, "&6");
            inv.setItem(j, item);
        }

        if (ditem.getStock() != null) {
            ditem.setAmount(ditem.getStock());
        }

        inv.setItem(4, ditem.getItem());
        inv.setItem(0, changeEcon);
        inv.setItem(8, conf_msg.ENABLE_RARITY ? changeRarity:null);
        inv.setItem(7, changeConfirmGui);
        inv.setItem(18, rename);
        inv.setItem(19, changeMaterial);
        inv.setItem(27, changeLore);
        inv.setItem(28, editEnchantments);
        inv.setItem(21, ditem.getSetItems() == null ? setAmount: barrier);
        inv.setItem(22, addRemoveCommands);
        inv.setItem(23, ditem.getItem().getType().getMaxDurability() != 0 ? durability:null);
        inv.setItem(30, perms);
        inv.setItem(31, ditem.getStock() == null ? setOfItems : barrier);
        inv.setItem(32, ditem.getBundle() != null ? bundle:null);
        inv.setItem(25, hideEnchants);
        inv.setItem(26, hideAtibutes);
        inv.setItem(35, utils.isPotion(ditem.getItem()) ? hideEffects:null);
        inv.setItem(47, returnItem);
        inv.setItem(49, customizerItem);

        return inv;
    }

    private void refresh (Player p) {
        openInventory(p, ditem, shop.getName());
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
            shopGui.open(p, shop.getName());
        }

        else if (e.getSlot() == 49) { //Boton de craft
            if (shop.hasItem(ditem.getUid())) {
                shop.updateItem(ditem.getUid(), ditem);
            } else {
                shop.addItem(ditem);
            }
            shopGui.open(p, shop.getName());
        }


        else if (e.getSlot() == 0) {        /* Boton de cambiar economia */
            changeEcon.open(p, ditem, dItem -> refresh(p));
        }

        else if (e.getSlot() == 7) {        /* Boton de cambiar confirm Gui */
            ditem.toggleConfirm_gui();
            refresh(p);
        }

        else if(e.getSlot() == 8 && !utils.isEmpty(e.getCurrentItem())) {    /* Boton de cambiar rarity */
            ditem.nextRarity();
            refresh(p);
        }
        else if (e.getSlot() == 18) { // Boton de cambiar nombre
            new AnvilGUI.Builder()
                    .onClose(player ->
                            utils.runTaskLater(() -> refresh(player), 1L))
                    .onComplete((player, text) -> {
                        ditem.setDisplayName(text);
                        return AnvilGUI.Response.close();
                    })
                    .text(conf_msg.CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT)
                    .itemLeft(ditem.getItem().clone())
                    .title(conf_msg.CUSTOMIZE_RENAME_ANVIL_TITLE)
                    .plugin(main)
                    .open(p);
        }

        else if (e.getSlot() == 19) { // Boton de cambiar material
            changeMaterialGui.openInventory(p, (aBoolean, material) -> {
                if (aBoolean)
                    ditem.setMaterial(material);
                refresh(p);
            });
        }

        else if (e.getSlot() == 27) { // Boton de cambiar lore
            if (e.isRightClick()) {
                List<String> lore = ditem.getLore();
                if (lore.isEmpty()) return;
                lore.remove(lore.size() - 1);
                ditem.setLore(lore);
                refresh(p);
            } else if (e.isLeftClick())
                new dynamicChatListener(p, s -> {
                    if (!s.isEmpty()) {
                        List<String> lore = ditem.getLore();
                        lore.add(s);
                        ditem.setLore(lore);
                    }
                    refresh(p);
                }, conf_msg.CUSTOMIZE_RENAME_ANVIL_TITLE, "");
        }

        else if (e.getSlot() == 28) { // Boton de cambiar enchants
            if (e.isLeftClick()) changeEnchantments.openInventory(p, ditem, shop);
            else if (e.isRightClick() && !ditem.getEnchantments().isEmpty())
                changeEnchantments.openInventory(p, ditem, ditem.getEnchantments(), shop);
        }

        else if (e.getSlot() == 21 && ditem.getStock() != null) { // Boton de cambiar amount
            if(e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> utils.runTaskLater(() ->
                                refresh(player), 1L))
                        .onComplete((player, text) -> {
                            try {
                                Integer.parseInt(text);
                            } catch (NumberFormatException err) {return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);}
                            int i = Integer.parseInt(text);
                            if(i < 1 || i > 64) return AnvilGUI.Response.text("invalid amount");
                            ditem.setStock(Integer.parseInt(text));
                            return AnvilGUI.Response.close();
                        })
                        .text("Change amount")
                        .itemLeft(ditem.getItem().clone())
                        .title("&6Change amount")
                        .plugin(main)
                        .open(p);
            }
            else if (e.isRightClick()) {
                ditem.setStock(null);
                ditem.setAmount(1);
                refresh(p);
            }
        }

        else if (e.getSlot() == 21 && ditem.getStock() == null && e.isLeftClick()) { // Boton de cambiar amount
            if (ditem.getSetItems() != null) {
                p.sendMessage(conf_msg.PREFIX + utils.formatString("&7You can't enable this when " +
                        "the set feature is enable"));
                return;
            }
            ditem.setStock(1);
            refresh(p);

        }

        else if (e.getSlot() == 22 && ditem.getCommands() == null) { // Boton de cambiar commands
            ditem.setCommands(new ArrayList<>());
            refresh(p);
        }

        else if (e.getSlot() == 22 && ditem.getCommands() != null) { // Boton de añadir/quitar commands
            if (e.isLeftClick()) {
                new dynamicChatListener(p, s -> {
                    if (!s.isEmpty()) {
                        List<String> cmds = ditem.getCommands();
                        cmds.add(s);
                        ditem.setCommands(cmds);
                    }
                    refresh(p);
                }, utils.formatString("&7Input new command"), "");

            } else if (e.isRightClick() && !e.isShiftClick()) {
                List<String> cmds = ditem.getCommands();

                if (!cmds.isEmpty()) {
                    cmds.remove(cmds.size() - 1);
                    ditem.setCommands(cmds);
                }
                refresh(p);
            } else if (e.isShiftClick() && e.isRightClick()) {
                ditem.setCommands(null);
                refresh(p);
            }
        }

        else if (e.getSlot() == 23) {               //durability

            new AnvilGUI.Builder()
                    .onClose(player -> utils.runTaskLater(() ->
                            refresh(player), 1L))
                    .onComplete((player, text) -> {
                        try {
                            Short.parseShort(text);
                        } catch (NumberFormatException err) {return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);}
                        ditem.setDurability(Short.parseShort(text));
                        return AnvilGUI.Response.close();
                    })
                    .text("Change durability")
                    .itemLeft(ditem.getItem().clone())
                    .title("&6Change durability")
                    .plugin(main)
                    .open(p);

        }

        else if (e.getSlot() == 32) {  //boton de bundle
            /*new changeBundleItem(p, newItem,
                    (player, itemStack) -> {
                        newItem = itemStack;
                        refresh(p);
                    }, player -> refresh(p)); */
        }

        else if (e.getSlot() == 25) { // Boton de hide enchants
            ditem.toggleFlag(ItemFlag.HIDE_ENCHANTS);
            refresh(p);
        }

        else if (e.getSlot() == 26) { // Boton de hide attributes
            ditem.toggleFlag(ItemFlag.HIDE_ATTRIBUTES);
            refresh(p);
        }

        else if (e.getSlot() == 30 && ditem.getPerms() == null) { // Boton de habilitar perms
            ditem.setPerms(new ArrayList<>());
            refresh(p);
        }

        else if (e.getSlot() == 30 && ditem.getPerms() != null) {  // Boton de añadir/quitar perms
            if (e.isLeftClick()) {
                new dynamicChatListener(p, s -> {
                    if (!s.isEmpty()) {
                        List<String> perms = ditem.getPerms();
                        perms.add(s);
                        ditem.setPerms(perms);
                    }
                    refresh(p);
                }, utils.formatString("&7Input permission"), "");

            } else if (e.isRightClick() && !e.isShiftClick()) {
                List<String> s = ditem.getPerms();

                if (!s.isEmpty()) {
                    s.remove(s.size() - 1);
                    ditem.setPerms(s);
                }
                refresh(p);
            } else if (e.isShiftClick() && e.isRightClick()) {
                ditem.setPerms(null);
                refresh(p);
            }

        }

        else if (e.getSlot() == 31 && ditem.getSetItems() == null && e.isLeftClick()) {  // Boton de edit set
            if (ditem.getStock() != null) {
                p.sendMessage(conf_msg.PREFIX + utils.formatString("&7You can't enable this when " +
                        "the stock feature is enable"));
                return;
            }
            ditem.setSetItems(1);
            refresh(p);
        }

        else if (e.getSlot() == 31 && ditem.getSetItems() != null) {
            if (e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> utils.runTaskLater(() ->
                                refresh(player), 1L))
                        .onComplete((player, text) -> {
                            try {
                                Integer.parseInt(text);
                            } catch (NumberFormatException err) {return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);}
                            int i = Integer.parseInt(text);
                            if(i < 1 || i > 64) return AnvilGUI.Response.text("invalid amount");
                            ditem.setSetItems(Integer.parseInt(text));
                            ditem.setAmount(Integer.parseInt(text));
                            return AnvilGUI.Response.close();
                        })
                        .text("Change amount")
                        .itemLeft(ditem.getItem().clone())
                        .title("&6Change amount")
                        .plugin(main)
                        .open(p);
            }
            else if (e.isRightClick()) {
                ditem.setSetItems(null);
                ditem.setAmount(1);
                refresh(p);
            }
        }

        else if (e.getSlot() == 35 && e.getCurrentItem() != null) { // Boton de hide potion effects
            ditem.toggleFlag(ItemFlag.HIDE_POTION_EFFECTS);
            refresh(p);
        }

    }

}