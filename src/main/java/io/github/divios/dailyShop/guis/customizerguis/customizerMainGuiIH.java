package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.guis.settings.shopGui;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class customizerMainGuiIH implements InventoryHolder, Listener {

    private static final DailyShop main = DailyShop.getInstance();
    private static customizerMainGuiIH instance = null;
    private dItem ditem;
    private dShop shop;

    private customizerMainGuiIH() {}

    public static void open(Player p, dItem ditem, String shopName) {
        if (instance == null) {
            instance = new customizerMainGuiIH();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        instance.ditem = ditem.clone();
        instance.shop = shopsManager.getInstance().getShop(shopName).get();
        p.openInventory(instance.createInventory());
    }

    public static void open(Player p, dItem ditem, dShop shop) {
        if (instance == null) {
            instance = new customizerMainGuiIH();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        instance.ditem = ditem.clone();
        instance.shop = shop;
        p.openInventory(instance.createInventory());
    }

    private Inventory createInventory() {

        Inventory inv = Bukkit.createInventory(instance, 54, FormatUtils.color(main.configM.getLangYml().CUSTOMIZE_TITLE));

        ItemStack barrier = new ItemBuilder(XMaterial.BARRIER.parseItem())
                .setName(main.configM.getLangYml().CUSTOMIZE_UNAVAILABLE);

        ItemStack changeEcon = new ItemBuilder(XMaterial.PLAYER_HEAD)     // Change econ
                .setName(main.configM.getLangYml().CUSTOMIZE_ECON_NAME)
                .addLore(main.configM.getLangYml().CUSTOMIZE_ECON_LORE)
                .addLore("", "&7Current: &e" + ditem.getEconomy().getName())
                .applyTexture("e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852");

        ItemStack changePrice = new ItemBuilder(XMaterial.EMERALD)      // change price
                .setName(main.configM.getLangYml().CUSTOMIZE_PRICE_NAME)
                .addLore(
                        Msg.msgList(main.configM.getLangYml().CUSTOMIZE_PRICE_LORE)
                        .add("\\{buy_price}", ditem.getBuyPrice().get().getVisualPrice())
                        .add("\\{sell_price}", ditem.getSellPrice().get().getVisualPrice())
                        .build()
                );

        ItemStack changeRarity = new ItemBuilder(ditem.getRarity().getAsItem())         // Change rarity
                .addLore(main.configM.getLangYml().CUSTOMIZE_RARITY_NAME);

        ItemStack changeConfirmGui = new ItemBuilder(XMaterial.LEVER.parseItem())
                .setName(main.configM.getLangYml().CUSTOMIZE_CONFIRM_GUI_NAME)
                .setLore(Msg.msgList(main.configM.getLangYml().CUSTOMIZE_CONFIRM_GUI_LORE)
                        .add("\\{status}", "" + ditem.getConfirm_gui()).build());

        ItemStack customizerItem = new ItemBuilder(XMaterial.PLAYER_HEAD)   //Done button (anvil)
                .setName(main.configM.getLangYml().CUSTOMIZE_CRAFT)
                .addLore(main.configM.getLangYml().CUSTOMIZE_CRAFT_LORE)
                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f");

        ItemStack returnItem = new ItemBuilder(XMaterial.PLAYER_HEAD)    //Back sign
                .setName(main.configM.getLangYml().CUSTOMIZE_RETURN)
                .setLore(main.configM.getLangYml().CUSTOMIZE_RETURN_LORE)
                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf");

        ItemStack rename = new ItemBuilder(XMaterial.NAME_TAG)   //Rename item
                .setName(main.configM.getLangYml().CUSTOMIZE_RENAME_NAME)
                .setLore(main.configM.getLangYml().CUSTOMIZE_RENAME_LORE);

        ItemStack changeMaterial = new ItemBuilder(XMaterial.SLIME_BALL)   //Change material
                .setName(main.configM.getLangYml().CUSTOMIZE_MATERIAL_NAME)
                .setLore(main.configM.getLangYml().CUSTOMIZE_MATERIAL_LORE);

        ItemStack changeLore = new ItemBuilder(XMaterial.PLAYER_HEAD)   //Change lore
                .setName(main.configM.getLangYml().CUSTOMIZE_LORE_NAME)
                .addLore(main.configM.getLangYml().CUSTOMIZE_LORE_LORE)
                .addLore("")
                .addLore(ItemUtils.getLore(ditem.getItem()))
                .applyTexture("c6692f99cc6d78242304110553589484298b2e4a0233b76753f888e207ef5");

        ItemStack editEnchantments = new ItemBuilder(XMaterial.ENCHANTING_TABLE)  //Change enchants
                .setName(main.configM.getLangYml().CUSTOMIZE_ENCHANTS_NAME)
                .addLore(main.configM.getLangYml().CUSTOMIZE_ENCHANTS_LORE)
                .addLore(ditem.getItem().getEnchantments().entrySet().stream()
                        .map(entry -> "&f&l" + entry.getKey()
                                .getName() + ":" + entry.getValue()).collect(Collectors.toList()));

        ItemStack setStock = new ItemBuilder(XMaterial.STONE_BUTTON)  //Change stock
                .setName(main.configM.getLangYml().CUSTOMIZE_STOCK_NAME)
                .setLore(ditem.getStock().isPresent() ?
                        Msg.msgList(main.configM.getLangYml().CUSTOMIZE_STOCK_LORE_ON).add("\\{amount}",
                                "" + ditem.getStock().get()).build() :
                        main.configM.getLangYml().CUSTOMIZE_STOCK_LORE);

        ItemStack addRemoveCommands = XMaterial.COMMAND_BLOCK.parseItem();    //Change command item
        if (!ditem.getCommands().isPresent()) {
            addRemoveCommands = new ItemBuilder(addRemoveCommands)
                    .setName(main.configM.getLangYml().CUSTOMIZE_COMMANDS_NAME)
                    .setLore(Msg.msgList(main.configM.getLangYml().CUSTOMIZE_COMMANDS_LORE)
                        .add("\\{status}", "false").build());
        } else {                                                                //add/remove commands
            addRemoveCommands = new ItemBuilder(addRemoveCommands)
                    .setName(main.configM.getLangYml().CUSTOMIZE_COMMANDS_NAME_ON)
                    .setLore(main.configM.getLangYml().CUSTOMIZE_COMMANDS_LORE_ON)
                    .addLore("")
                    .addLore(ditem.getCommands().get()
                            .stream().map(s -> FormatUtils.color("&f&l" + s)).collect(Collectors.toList()));
        }


        ItemStack perms = new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())   //add remove perms
                .setName(main.configM.getLangYml().CUSTOMIZE_PERMS_NAME)
                .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b");

        if (!ditem.getPerms().isPresent()) {
            perms = new ItemBuilder(perms).addLore(main.configM.getLangYml().CUSTOMIZE_PERMS_LORE);
        }
        else {
            perms = new ItemBuilder(perms)
                    .addLore(main.configM.getLangYml().CUSTOMIZE_PERMS_LORE_ON)
                    .addLore("")
                    .addLore(ditem.getPerms().get()
                            .stream().map(s -> FormatUtils.color("&f&l" + s))
                            .collect(Collectors.toList()));
        }

        ItemStack setOfItems = new ItemBuilder(XMaterial.CHEST_MINECART)    //set of items
                .setName(main.configM.getLangYml().CUSTOMIZE_SET_NAME);

        if (ditem.getSetItems().isPresent()) {
            setOfItems = new ItemBuilder(setOfItems).addLore(
                    Msg.msgList(main.configM.getLangYml().CUSTOMIZE_SET_LORE_ON).add("\\{amount}",
                    "" + ditem.getSetItems().get()).build());

        } else {
            setOfItems = new ItemBuilder(setOfItems)
                    .addLore(main.configM.getLangYml().CUSTOMIZE_SET_LORE);
        }

        ItemStack bundle = new ItemBuilder(XMaterial.ITEM_FRAME)                //bundle
                .setName("&f&lChange bundle items")
                .setLore("&6Right Click > &7To change items on the bundle")
                .addLore("")
                .addLore(ditem.getBundle().orElse(Collections.emptyList()).stream()
                        .map(uuid -> shop.getItem(uuid).orElse(dItem.AIR()).getDisplayName())
                        .collect(Collectors.toList()));

        ItemStack durability = new ItemBuilder(XMaterial.DAMAGED_ANVIL)
                .setName(main.configM.getLangYml().CUSTOMIZE_DURABILITY_NAME)
                .setLore(main.configM.getLangYml().CUSTOMIZE_DURABILITY_LORE);

        ItemFlag f = ItemFlag.HIDE_ENCHANTS;

        ItemStack hideEnchants = new ItemBuilder(XMaterial.BLACK_BANNER)   //add/remove enchants visible
                .setName(main.configM.getLangYml().CUSTOMIZE_TOGGLE_ENCHANTS_NAME)
                .setLore(Msg.msgList(main.configM.getLangYml().CUSTOMIZE_TOGGLE_ENCHANTS_LORE)
                .add("\\{status}", "" + ditem.hasFlag(f)).build());

        f = ItemFlag.HIDE_ATTRIBUTES;

        ItemStack hideAtibutes = new ItemBuilder(XMaterial.BOOKSHELF)    //add/remove attributes
                .setName(main.configM.getLangYml().CUSTOMIZE_TOGGLE_ATTIBUTES_NAME)
                .setLore(Msg.msgList(main.configM.getLangYml().CUSTOMIZE_TOGGLE_ATTIBUTES_LORE)
                    .add("\\{status}", "" + ditem.hasFlag(f)).build());

        f = ItemFlag.HIDE_POTION_EFFECTS;

        ItemStack hideEffects = new ItemBuilder(XMaterial.CAULDRON)     //add/remove effects
                .setName(main.configM.getLangYml().CUSTOMIZE_TOGGLE_EFFECTS_NAME)
                .addLore(Msg.msgList(main.configM.getLangYml().CUSTOMIZE_TOGGLE_EFFECTS_LORE)
                    .add("\\{status}", "" + ditem.hasFlag(f)).build());

        Integer[] auxList = {3, 5, 13};
                                    //fill black panes
        for (int j : auxList) {
            ItemStack item = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setName("&6");
            inv.setItem(j, item);
        }

        IntStream.of(0, 1, 2, 9, 18, 38, 22, 31, 14, 51, 52, 53, 44, 35)
            .forEach(value -> inv.setItem(value, new ItemBuilder(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                .setName("&c")));

        IntStream.of(3, 4, 13, 15, 16, 17, 26, 27, 36, 37, 39, 40, 49, 50)
                .forEach(value -> inv.setItem(value, new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE)
                        .setName("&c")));

        IntStream.of(10, 11, 12, 19, 20, 21, 28, 29, 30,
                5, 6, 7, 8, 23, 24, 25, 32, 33, 34, 41, 42, 43, 45, 46, 47, 48)
                .forEach(value -> inv.setItem(value, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        .setName("&c")));

        inv.setItem(5, ditem.getItem());
        inv.setItem(23, changeEcon);
        inv.setItem(24, changePrice);
        inv.setItem(25, changeRarity);
        inv.setItem(34, changeConfirmGui);
        inv.setItem(10, rename);
        inv.setItem(11, changeMaterial);
        inv.setItem(12, changeLore);
        inv.setItem(21, editEnchantments);
        inv.setItem(46, !ditem.getSetItems().isPresent() ? setStock: barrier);
        inv.setItem(47, addRemoveCommands);
        if (ditem.getItem().getType().getMaxDurability() != 0) inv.setItem(28, durability);
        inv.setItem(19, perms);
        inv.setItem(45, !ditem.getStock().isPresent() ? setOfItems : barrier);
        if (ditem.getBundle().isPresent()) inv.setItem(48, bundle);
        inv.setItem(32, hideEnchants);
        inv.setItem(43, hideAtibutes);
        if (utils.isPotion(ditem.getItem())) inv.setItem(41, hideEffects);
        inv.setItem(8, returnItem);
        inv.setItem(7, customizerItem);

        return inv;
    }

    private void refresh (Player p) {
        open(p, ditem, shop.getName());
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

        if (e.getSlot() == 8) { //Boton de retornar
            shopGui.open(p, shop.getName());
        }

        else if (e.getSlot() == 7) { //Boton de craft
            if (shop.hasItem(ditem.getUid())) {
                shop.updateItem(ditem.getUid(), ditem);
            } else {
                shop.addItem(ditem);
            }
            shopGui.open(p, shop.getName());
        }


        else if (e.getSlot() == 23) {        /* Boton de cambiar economia */
            changeEcon.open(p, ditem, dItem -> refresh(p));
        }

        else if (e.getSlot() == 24) {        // boton de cambiar price
            new changePrice(p, ditem, shop, e.isLeftClick() ? dShop.dShopT.buy: dShop.dShopT.sell
                    ,() -> refresh(p), () -> refresh(p));
        }

        else if (e.getSlot() == 34) {        /* Boton de cambiar confirm Gui */
            ditem.toggleConfirm_gui();
            refresh(p);
        }

        else if(e.getSlot() == 25 && !utils.isEmpty(e.getCurrentItem())) {    /* Boton de cambiar rarity */
            ditem.nextRarity();
            refresh(p);
        }
        else if (e.getSlot() == 10) { // Boton de cambiar nombre
            ChatPrompt.prompt(main, p, (s) -> {
                ditem.setDisplayName(s);
                Task.syncDelayed(main, () -> refresh(p));
            },  cause -> Task.syncDelayed(main, () -> refresh(p)), "&a&lInput New Name", "");
        }

        else if (e.getSlot() == 11) { // Boton de cambiar material
            materialsPrompt.open(main, p, (aBoolean, material) -> {
                if (aBoolean)
                    ditem.setMaterial(material);
                refresh(p);
            });
        }

        else if (e.getSlot() == 12) { // Boton de cambiar lore
            if (e.isRightClick()) {
                List<String> lore = ditem.getLore();
                if (lore.isEmpty()) return;
                lore.remove(lore.size() - 1);
                ditem.setLore(lore);
                refresh(p);
            } else if (e.isLeftClick())
                ChatPrompt.prompt(main, p, (s) -> {
                    if (!s.isEmpty()) {
                        List<String> lore = ditem.getLore();
                        lore.add(s);
                        ditem.setLore(lore);
                    }
                    Task.syncDelayed(main, () -> refresh(p));
                },  cause -> Task.syncDelayed(main, () -> refresh(p)),
                        main.configM.getLangYml().CUSTOMIZE_RENAME_TITLE, "");
        }

        else if (e.getSlot() == 21) { // Boton de cambiar enchants
            if (e.isLeftClick()) changeEnchantments.openInventory(p, ditem, shop);
            else if (e.isRightClick() && !ditem.getEnchantments().isEmpty())
                changeEnchantments.openInventory(p, ditem, ditem.getEnchantments(), shop);
        }

        else if (e.getSlot() == 46 && ditem.getStock().isPresent()) { // Boton de cambiar Stock
            if (e.isLeftClick()) {
                ChatPrompt.prompt(main, p, (s) -> {
                    if (utils.isInteger(s)) ditem.setStock(Integer.parseInt(s));
                    else utils.sendMsg(p, main.configM.getLangYml().MSG_NOT_INTEGER);

                    Task.syncDelayed(main, () -> refresh(p));
                },  cause -> Task.syncDelayed(main, () -> refresh(p)), "&c&lInput Stock number", "");
            }
            else if (e.isRightClick()) {
                ditem.setStock(null);
                refresh(p);
            }
        }

        else if (e.getSlot() == 46 && !ditem.getStock().isPresent() && e.isLeftClick()) { // Boton de cambiar Stock
            if (ditem.getSetItems().isPresent()) {
                Msg.sendMsg(p, "&7You can't enable this when " +
                        "the set feature is enable");
                return;
            }
            ditem.setStock(1);
            refresh(p);

        }

        else if (e.getSlot() == 47 && !ditem.getCommands().isPresent()) { // Boton de cambiar commands
            ditem.setCommands(new ArrayList<>());
            refresh(p);
        }

        else if (e.getSlot() == 47 && ditem.getCommands().isPresent()) { // Boton de añadir/quitar commands
            if (e.isLeftClick()) {
                ChatPrompt.prompt(main, p, (s) -> {
                    if (!s.isEmpty()) {
                        List<String> cmds = ditem.getCommands().get();
                        cmds.add(s);
                        ditem.setCommands(cmds);
                    }
                    Task.syncDelayed(main, () -> refresh(p));
                }, cause -> Task.syncDelayed(main, () -> refresh(p)),"&7&lInput new command", "");

            } else if (e.isRightClick() && !e.isShiftClick()) {
                List<String> cmds = ditem.getCommands().get();

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

        else if (e.getSlot() == 28) {               //durability

            ChatPrompt.prompt(main, p, (s) -> {
                if (utils.isShort(s)) ditem.setDurability(Short.parseShort(s), false);
                else Msg.sendMsg(p, main.configM.getLangYml().MSG_NOT_INTEGER);

                Task.syncDelayed(main, () -> refresh(p));
            },  cause -> Task.syncDelayed(main, () -> refresh(p)), "&c&lInput Durability", "");

        }

        else if (e.getSlot() == 48) {  //boton de bundle
            new changeBundleItem(p, ditem, shop,
                    (player, uuids) -> {
                        ditem.setBundle(uuids);
                        refresh(p);
                    }, this::refresh);
        }

        else if (e.getSlot() == 32) { // Boton de hide enchants
            ditem.toggleFlag(ItemFlag.HIDE_ENCHANTS);
            refresh(p);
        }

        else if (e.getSlot() == 43) { // Boton de hide attributes
            ditem.toggleFlag(ItemFlag.HIDE_ATTRIBUTES);
            refresh(p);
        }

        else if (e.getSlot() == 19 && !ditem.getPerms().isPresent()) { // Boton de habilitar perms
            ditem.setPerms(new ArrayList<>());
            refresh(p);
        }

        else if (e.getSlot() == 19 && ditem.getPerms().isPresent()) {  // Boton de añadir/quitar perms
            if (e.isLeftClick()) {
                ChatPrompt.builder()
                        .withPlayer(p)
                        .withResponse(s -> {
                            if (!s.isEmpty()) {
                                List<String> perms = ditem.getPerms().get();
                                perms.add(s);
                                ditem.setPerms(perms);
                            }
                            Task.syncDelayed(main, () -> refresh(p));
                        })
                        .withCancel(cancelReason -> Task.syncDelayed(main, () -> refresh(p)))
                        .withTitle("&a&lInput permission")
                        .prompt();

            } else if (e.isRightClick() && !e.isShiftClick()) {
                List<String> s = ditem.getPerms().get();

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

        else if (e.getSlot() == 45 && !ditem.getSetItems().isPresent() && e.isLeftClick()) {  // Boton de edit set
            if (ditem.getStock().isPresent()) {
                Msg.sendMsg(p, "&7You can't enable this when the stock feature is enable");
                return;
            }
            ditem.setSetItems(1);
            refresh(p);
        }

        else if (e.getSlot() == 45 && ditem.getSetItems().isPresent()) {
            if (e.isLeftClick()) {

                ChatPrompt.builder()
                        .withPlayer(p)
                        .withResponse(s -> {
                            if (!utils.isInteger(s)) Msg.sendMsg(p, main.configM.getLangYml().MSG_NOT_INTEGER);
                            int i = Integer.parseInt(s);
                            if(i < 1 || i > 64) Msg.sendMsg(p, "&7Invalid amount");
                            ditem.setSetItems(i);
                            ditem.setAmount(i);
                            Task.syncDelayed(main, () -> refresh(p));
                        })
                        .withCancel(cancelReason -> Task.syncDelayed(main, () -> refresh(p)))
                        .withTitle("&e&lInput Set Amount")
                        .prompt();

            }
            else if (e.isRightClick()) {
                ditem.setSetItems(null);
                ditem.setAmount(1);
                refresh(p);
            }
        }

        else if (e.getSlot() == 41 && e.getCurrentItem() != null) { // Boton de hide potion effects
            ditem.toggleFlag(ItemFlag.HIDE_POTION_EFFECTS);
            refresh(p);
        }

    }

}