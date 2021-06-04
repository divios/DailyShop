package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.settings.shopGui;
import io.github.divios.dailyrandomshop.utils.utils;
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

        ItemStack barrier = new ItemBuilder(XMaterial.BARRIER.parseItem())
                .setName(conf_msg.CUSTOMIZE_UNAVAILABLE);

        ItemStack changeEcon = new ItemBuilder(XMaterial.PLAYER_HEAD)     // Change econ
                .setName(conf_msg.CUSTOMIZE_CHANGE_ECON).addLore(conf_msg.CUSTOMIZE_CHANGE_ECON_LORE)
                .addLore(Arrays.asList("", "&7Current: " + ditem.getEconomy().getClass().getName()
                        .replace("io.github.divios.dailyrandomshop.economies.", "&6&l")))
                .applyTexture("e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852");

        ItemStack changeRarity = new ItemBuilder(ditem.getRarity().getAsItem())         // Change rarity
                .addLore(conf_msg.CUSTOMIZE_CHANGE_RARITY_LORE);

        ItemStack changeConfirmGui = new ItemBuilder(XMaterial.LEVER.parseItem())
                .setName(conf_msg.CUSTOMIZE_CHANGE_CONFIRM_GUI)
                .setLore(Msg.msgList(conf_msg.CUSTOMIZE_CHANGE_CONFIRM_GUI_LORE)
                        .add("\\{status}", "" + ditem.getConfirm_gui()).build());  //TODO

        ItemStack customizerItem = new ItemBuilder(XMaterial.ANVIL)   //Done button (anvil)
                .setName(conf_msg.CUSTOMIZE_CRAFT).addLore(conf_msg.CUSTOMIZE_CRAFT_LORE);

        ItemStack returnItem = new ItemBuilder(XMaterial.OAK_SIGN)    //Back sign
                .setName(conf_msg.CUSTOMIZE_RETURN).setLore(conf_msg.CUSTOMIZE_RETURN_LORE);

        ItemStack rename = new ItemBuilder(XMaterial.NAME_TAG)   //Rename item
                .setName(conf_msg.CUSTOMIZE_RENAME).setLore(conf_msg.CUSTOMIZE_RENAME_LORE);

        ItemStack changeMaterial = new ItemBuilder(XMaterial.SLIME_BALL)   //Change material
                .setName(conf_msg.CUSTOMIZE_MATERIAL).setLore(conf_msg.CUSTOMIZE_MATERIAL_LORE);

        ItemStack changeLore = new ItemBuilder(XMaterial.PLAYER_HEAD)   //Change lore
                .setName(conf_msg.CUSTOMIZE_LORE)
                .addLore(conf_msg.CUSTOMIZE_LORE_LORE)
                .addLore("")
                .addLore(ItemUtils.getLore(ditem.getItem()))
                .applyTexture("c6692f99cc6d78242304110553589484298b2e4a0233b76753f888e207ef5");

        ItemStack editEnchantments = new ItemBuilder(XMaterial.ENCHANTING_TABLE)  //Change enchants
                .setName(conf_msg.CUSTOMIZE_ENCHANTS)
                .addLore(conf_msg.CUSTOMIZE_ENCHANTS_LORE)
                .addLore(ditem.getItem().getEnchantments().entrySet().stream()
                        .map(entry -> "&f&l" + entry.getKey()
                                .getName() + ":" + entry.getValue()).collect(Collectors.toList()));

        ItemStack setAmount = new ItemBuilder(XMaterial.STONE_BUTTON)  //Change amount
                .setName(conf_msg.CUSTOMIZE_AMOUNT)
                .setLore(ditem.getStock() != null ?
                        Msg.msgList(conf_msg.CUSTOMIZE_AMOUNT_LORE).add("\\{amount}",
                                "" + ditem.getStock()).build() :
                        conf_msg.CUSTOMIZE_AMOUNT_ENABLE_LORE);

        ItemStack addRemoveCommands = XMaterial.COMMAND_BLOCK.parseItem();    //Change command item
        if (ditem.getCommands() == null) {
            addRemoveCommands = new ItemBuilder(addRemoveCommands)
                    .setName(conf_msg.CUSTOMIZE_ENABLE_COMMANDS)
                    .setLore(Msg.msgList(conf_msg.CUSTOMIZE_ENABLE_COMMANDS_LORE)
                        .add("\\{status}", "false").build());
        } else {                                                                //add/remove commands
            addRemoveCommands = new ItemBuilder(addRemoveCommands)
                    .setName(conf_msg.CUSTOMIZE_CHANGE_COMMANDS)
                    .setLore(conf_msg.CUSTOMIZE_CHANGE_COMMANDS_LORE)
                    .addLore("")
                    .addLore(ditem.getCommands()
                            .stream().map(s -> FormatUtils.color("&f&l" + s)).collect(Collectors.toList()));
        }


        ItemStack perms = new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())   //add remove perms
                .setName(conf_msg.CUSTOMIZE_PERMS)
                .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b");

        if (ditem.getPerms() == null) {
            perms = new ItemBuilder(perms).addLore(conf_msg.CUSTOMIZE_ENABLE_PERMS_LORE);
        }
        else {
            perms = new ItemBuilder(perms)
                    .addLore(conf_msg.CUSTOMIZE_CHANGE_PERMS_LORE)
                    .addLore("")
                    .addLore(ditem.getPerms()
                            .stream().map(s -> FormatUtils.color("&f&l" + s))
                            .collect(Collectors.toList()));
        }

        ItemStack setOfItems = new ItemBuilder(XMaterial.CHEST_MINECART)    //set of items
                .setName(conf_msg.CUSTOMIZE_SET);

        if (ditem.getSetItems() != null) {
            setOfItems = new ItemBuilder(setOfItems).addLore(
                    Msg.msgList(conf_msg.CUSTOMIZE_CHANGE_SET_LORE).add("\\{amount}",
                    "" + ditem.getSetItems()).build());

        } else {
            setOfItems = new ItemBuilder(setOfItems)
                    .addLore(conf_msg.CUSTOMIZE_ENABLE_SET_LORE);
        }

        ItemStack bundle = new ItemBuilder(XMaterial.ITEM_FRAME)                //bundle
                .setName("&f&lChange bundle items")
                .setLore("&6Right Click > &7To change items on the bundle");

        ItemStack durability = new ItemBuilder(XMaterial.DAMAGED_ANVIL)
                .setName(conf_msg.CUSTOMIZE_DURABILITY)
                .setLore(conf_msg.CUSTOMIZE_DURABILITY_LORE);

        ItemFlag f = ItemFlag.HIDE_ENCHANTS;

        ItemStack hideEnchants = new ItemBuilder(XMaterial.BLACK_BANNER)   //add/remove enchants visible
                .setName(conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS)
                .setLore(Msg.msgList(conf_msg.CUSTOMIZE_TOGGLE_ENCHANTS_LORE)
                .add("\\{status}", "" + ditem.hasFlag(f)).build());

        f = ItemFlag.HIDE_ATTRIBUTES;

        ItemStack hideAtibutes = new ItemBuilder(XMaterial.BOOKSHELF)    //add/remove attributes
                .setName(conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES)
                .setLore(Msg.msgList(conf_msg.CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE)
                    .add("\\{status}", "" + ditem.hasFlag(f)).build());

        f = ItemFlag.HIDE_POTION_EFFECTS;

        ItemStack hideEffects = new ItemBuilder(XMaterial.CAULDRON)     //add/remove effects
                .setName(conf_msg.CUSTOMIZE_TOGGLE_EFFECTS)
                .addLore(Msg.msgList(conf_msg.CUSTOMIZE_TOGGLE_EFFECTS_LORE)
                    .add("\\{status}", "" + ditem.hasFlag(f)).build());

        Integer[] auxList = {3, 5, 13};
                                    //fill black panes
        for (int j : auxList) {
            ItemStack item = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName("&6");
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
                            Task.syncDelayed(main, () -> refresh(player), 1L))
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
            materialsPrompt.open(main, p, (aBoolean, material) -> {
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
                new ChatPrompt(main, p, (player, s) -> {
                    if (!s.isEmpty()) {
                        List<String> lore = ditem.getLore();
                        lore.add(s);
                        ditem.setLore(lore);
                    }
                    refresh(p);
                }, this::refresh, conf_msg.CUSTOMIZE_RENAME_ANVIL_TITLE, "");
        }

        else if (e.getSlot() == 28) { // Boton de cambiar enchants
            if (e.isLeftClick()) changeEnchantments.openInventory(p, ditem, shop);
            else if (e.isRightClick() && !ditem.getEnchantments().isEmpty())
                changeEnchantments.openInventory(p, ditem, ditem.getEnchantments(), shop);
        }

        else if (e.getSlot() == 21 && ditem.getStock() != null) { // Boton de cambiar amount
            if(e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> Task.syncDelayed(main, () ->
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
                p.sendMessage(conf_msg.PREFIX + FormatUtils.color("&7You can't enable this when " +
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
                new ChatPrompt(main, p, (player, s) -> {
                    if (!s.isEmpty()) {
                        List<String> cmds = ditem.getCommands();
                        cmds.add(s);
                        ditem.setCommands(cmds);
                    }
                    refresh(p);
                }, this::refresh, FormatUtils.color("&7Input new command"), "");

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
                    .onClose(player -> Task.syncDelayed(main, () ->
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
                new ChatPrompt(main, p, (player, s) -> {

                    if (!s.isEmpty()) {
                        List<String> perms = ditem.getPerms();
                        perms.add(s);
                        ditem.setPerms(perms);
                    }
                    refresh(p);
                }, this::refresh, FormatUtils.color("&7Input permission"), "");

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
                p.sendMessage(conf_msg.PREFIX + FormatUtils.color("&7You can't enable this when " +
                        "the stock feature is enable"));
                return;
            }
            ditem.setSetItems(1);
            refresh(p);
        }

        else if (e.getSlot() == 31 && ditem.getSetItems() != null) {
            if (e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(player -> Task.syncDelayed(main, () ->
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