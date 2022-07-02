package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.builder.inventoryPopulator;
import io.github.divios.core_lib.inventory.materialsPrompt;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.guis.settings.shopsItemsManagerGui;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jcommands.util.Primitives;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class CustomizerMenu {

    private final static DailyShop plugin = DailyShop.get();

    private final Player p;
    private final dItem item;
    private final dShop shop;

    private InventoryGUI inv;

    private CustomizerMenu(
            Player p,
            dItem item,
            dShop shop
    ) {
        this.p = p;
        this.item = item.clone();
        this.shop = shop;

        build();
        inv.open(p);
    }

    public static void open(@NotNull Player p, @NotNull dItem item, @NotNull dShop shop) {
        new CustomizerMenu(p, item, shop);
    }

    public static newCustomizerMenuBuilder builder() {
        return new newCustomizerMenuBuilder();
    }

    private void build() {

        inv = new InventoryGUI(plugin, 54, Utils.JTEXT_PARSER.parse(Lang.CUSTOMIZE_TITLE.getAsString(p)));

        inventoryPopulator.builder()
                .ofGlass()
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .scheme(3, 3, 3, 11, 11, 7, 7, 7, 7)
                .scheme(3, 7, 7, 7, 11, 7, 11, 11, 11)
                .scheme(3, 7, 7, 7, 3, 7, 7, 7, 11)
                .scheme(11, 7, 7, 7, 3, 7, 7, 7, 3)
                .scheme(11, 11, 3, 11, 11, 7, 7, 7, 3)
                .scheme(7, 7, 7, 7, 11, 11, 3, 3, 3)
                .apply(inv.getInventory());


        inv.getInventory().setItem(5, item.getItem());             // The item itself

        inv.addButton(                                                  // Craft button
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.CUSTOMIZE_CRAFT.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_CRAFT_LORE.getAsListString(p))
                                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f")
                        , e -> {

                            // Check to update or add item
                            if (shop.hasItem(item.getID())) {
                                DebugLog.info("Update item from customizer menu of ID: " + item.getID());
                                shop.updateItem(item);
                            } else {
                                DebugLog.info("Added new item from customizer menu with ID: " + item.getID());
                                shop.addItem(item);
                            }
                            shopsItemsManagerGui.open(p, shop.getName());
                        }),
                7
        );

        inv.addButton(                                                  // Return button
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.CUSTOMIZE_RETURN.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_RETURN_LORE.getAsListString(p))
                                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                        , e -> shopsItemsManagerGui.open(p, shop.getName())),
                8
        );

        inv.addButton(                                                  // Rename
                ItemButton.create(
                        ItemBuilder.of(XMaterial.NAME_TAG)
                                .setName(Lang.CUSTOMIZE_RENAME_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_RENAME_LORE.getAsListString(p))
                        , e ->
                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {
                                            ItemStack toRename = item.getItem();
                                            item.setItem(ItemUtils.rename(toRename, Utils.JTEXT_PARSER.parse(s)));
                                            Schedulers.sync().run(this::refresh);
                                        })
                                        .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                        .withTitle("&a&lInput New Name")
                                        .prompt()),
                10
        );

        inv.addButton(                                                  // Material
                ItemButton.create(
                        ItemBuilder.of(XMaterial.SLIME_BALL)
                                .setName(Lang.CUSTOMIZE_MATERIAL_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_MATERIAL_LORE.getAsListString(p))
                        , e ->

                                materialsPrompt.open(plugin, p, (aBoolean, material) -> {
                                    if (aBoolean) {
                                        ItemStack toChange = item.getItem();
                                        item.setItem(ItemUtils.setMaterial(toChange, material));
                                    }
                                    refresh();
                                })),
                11
        );

        inv.addButton(                                                  // Lore
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.CUSTOMIZE_LORE_NAME.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_LORE_LORE.getAsListString(p))
                                .addLore("")
                                .addLore(ItemUtils.getLore(item.getItem()))
                                .applyTexture("c6692f99cc6d78242304110553589484298b2e4a0233b76753f888e207ef5")
                        , e -> {

                            if (e.isRightClick()) {
                                ItemStack toChange = item.getItem();
                                List<String> lore = ItemUtils.getLore(toChange);
                                if (lore.isEmpty()) return;
                                lore.remove(lore.size() - 1);
                                item.setItem(ItemUtils.setLore(toChange, lore));
                                refresh();

                            } else if (e.isLeftClick())

                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {
                                            if (!s.isEmpty()) {
                                                ItemStack toChange = item.getItem();
                                                List<String> lore = ItemUtils.getLore(toChange);
                                                lore.add(s);
                                                item.setItem(ItemUtils.setLore(toChange, Utils.JTEXT_PARSER.parse(lore)));
                                            }
                                            Schedulers.sync().run(this::refresh);
                                        })
                                        .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                        .withTitle(Lang.CUSTOMIZE_RENAME_TITLE.getAsString(p))
                                        .prompt();

                        }),
                12
        );

        inv.addButton(                                                  // Perms
                ItemButton.create(
                        ItemBuilder.of(XMaterial.BOOKSHELF)
                                .setName(Lang.CUSTOMIZE_PERMS_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_PERMS_LORE_DEFAULT.getAsListString(p))
                        , e ->
                                customizePerms.builder()
                                        .withPlayer(p)
                                        .withBack((strings, strings2) -> {
                                            item.setBuyPerms(strings);
                                            item.setSellPerms(strings2);
                                            refresh();
                                        })
                                        .open()), 19);

        inv.addButton(                                                  // Enchantments
                ItemButton.create(
                        ItemBuilder.of(XMaterial.ENCHANTING_TABLE)
                                .setName(Lang.CUSTOMIZE_ENCHANTS_NAME.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_ENCHANTS_LORE.getAsListString(p))
                                .addLore(item.getItem().getEnchantments().entrySet().stream()
                                        .map(entry -> "&f&l" + entry.getKey()
                                                .getName() + ":" + entry.getValue()).collect(Collectors.toList()))
                        , e -> {

                            if (e.isLeftClick())

                                changeEnchantments.builder()
                                        .withPlayer(p)
                                        .withItem(item.getItem())
                                        .withAccept(itemStack -> {
                                            item.setItem(itemStack);
                                            refresh();
                                        })
                                        .withFallback(this::refresh)
                                        .prompt();

                            else if (e.isRightClick() && !item.getItem().getEnchantments().isEmpty())

                                changeEnchantments.builder()
                                        .withPlayer(p)
                                        .withItem(item.getItem())
                                        .withEnchants(item.getItem().getEnchantments())
                                        .withAccept(itemStack -> {
                                            item.setItem(itemStack);
                                            refresh();
                                        })
                                        .withFallback(this::refresh)
                                        .prompt();

                        }),
                21
        );

        inv.addButton(                                                  // Econ
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.CUSTOMIZE_ECON_NAME.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_ECON_LORE.getAsListString(p))
                                .addLore("", "&7Current: &e" + item.getEcon().getName())
                                .applyTexture("e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852")
                        , e ->
                                changeEcon.builder()
                                        .withPlayer(p)
                                        .withConsumer(economy -> {
                                            if (economy != null)        // If null no economy
                                                item.setEcon(economy);
                                            refresh();
                                        })
                                        .prompt()
                ), 23
        );

        inv.addButton(                                                  // Price
                ItemButton.create(
                        ItemBuilder.of(XMaterial.EMERALD)
                                .setName(Lang.CUSTOMIZE_PRICE_NAME.getAsString(p))
                                .addLore(
                                        Lang.CUSTOMIZE_PRICE_LORE.getAsListString(p,
                                                Template.of("buy_price", item.getDBuyPrice() == null
                                                        ? "&c" + XSymbols.TIMES_3.parseSymbol()
                                                        : item.getDBuyPrice().getGenerator().toString()
                                                ),
                                                Template.of("sell_price", item.getDSellPrice() == null
                                                        ? "&c" + XSymbols.TIMES_3.parseSymbol()
                                                        : item.getDSellPrice().getGenerator().toString()
                                                )
                                        )
                                )
                        , e ->
                                changePrice.builder()
                                        .withPlayer(p)
                                        .withAccept(price -> {
                                            if (e.isLeftClick()) item.setBuyPrice(price);
                                            else item.setSellPrice(price);
                                            refresh();
                                        })
                                        .withBack(this::refresh)
                                        .prompt()
                ), 24
        );

        inv.addButton(                                                  // Rarity
                ItemButton.create(
                        ItemBuilder.of(item.getRarity().getItem())
                                .setName(item.getRarity().getName())
                                .addLore(Lang.CUSTOMIZE_RARITY_NAME.getAsListString(p))

                        , e -> {
                            item.nextRarity();
                            refresh();
                        }
                ), 25
        );

        inv.addButton(                                                  // Durability
                ItemButton.create(
                        item.getItem().getType().getMaxDurability() != 0 ?
                                ItemBuilder.of(XMaterial.DAMAGED_ANVIL)
                                        .setName(Lang.CUSTOMIZE_DURABILITY_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_DURABILITY_LORE.getAsListString(p))
                                :
                                ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")

                        , e ->
                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {
                                            if (Primitives.isShort(s)) {
                                                ItemStack toChange = item.getItem();
                                                item.setItem(ItemUtils.setDurability(toChange, Primitives.getAsShort(s)));
                                                refresh();
                                            } else {
                                                Messages.MSG_NOT_INTEGER.send(p);
                                            }
                                        })
                                        .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                        .withTitle("&c&lInput Durability")
                                        .prompt()
                ), 28
        );

        inv.addButton(                                                  // Hide Attributes
                ItemButton.create(
                        ItemBuilder.of(XMaterial.BLACK_BANNER)   //add/remove enchants visible
                                .setName(Lang.CUSTOMIZE_TOGGLE_ATTRIBUTES_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE.getAsListString(p,
                                                Template.of("status",
                                                        ItemUtils.hasItemFlags(item.getItem(), ItemFlag.HIDE_ATTRIBUTES)
                                                )
                                        )
                                )

                        , e -> {
                            ItemStack toChange = item.getItem();
                            if (ItemUtils.hasItemFlags(toChange, ItemFlag.HIDE_ATTRIBUTES))
                                item.setItem(ItemUtils.removeItemFlags(toChange, ItemFlag.HIDE_ATTRIBUTES));
                            else
                                item.setItem(ItemUtils.addItemFlags(toChange, ItemFlag.HIDE_ATTRIBUTES));
                            refresh();
                        }
                ), 32
        );

        inv.addButton(                                                  // Confirm Gui
                ItemButton.create(
                        ItemBuilder.of(XMaterial.LEVER)
                                .setName(Lang.CUSTOMIZE_CONFIRM_GUI_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_CONFIRM_GUI_LORE.getAsListString(p,
                                                Template.of("status", item.isConfirmGui())
                                        )
                                )
                        , e -> {
                            item.setConfirmGui(!item.isConfirmGui());
                            refresh();
                        }
                ), 34
        );

        inv.addButton(                                                  // Hide Effects
                ItemButton.create(
                        Utils.isPotion(item.getItem()) ?
                                ItemBuilder.of(XMaterial.CAULDRON)
                                        .setName(Lang.CUSTOMIZE_TOGGLE_EFFECTS_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_TOGGLE_EFFECTS_LORE.getAsListString(p,
                                                        Template.of("status",
                                                                ItemUtils.hasItemFlags(item.getItem(), ItemFlag.HIDE_POTION_EFFECTS))
                                                )
                                        )
                                :
                                ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")

                        , e -> {
                            ItemStack toChange = item.getItem();
                            if (ItemUtils.hasItemFlags(toChange, ItemFlag.HIDE_POTION_EFFECTS))
                                item.setItem(ItemUtils.removeItemFlags(toChange, ItemFlag.HIDE_POTION_EFFECTS));
                            else
                                item.setItem(ItemUtils.addItemFlags(toChange, ItemFlag.HIDE_POTION_EFFECTS));
                            refresh();
                        }
                ), 41
        );

        inv.addButton(                                                  // Hide Enchantments
                ItemButton.create(
                        ItemBuilder.of(XMaterial.BOOKSHELF)
                                .setName(Lang.CUSTOMIZE_TOGGLE_ENCHANTS_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_TOGGLE_ENCHANTS_LORE.getAsListString(p,
                                                Template.of("status", ItemUtils.hasItemFlags(
                                                        item.getItem(), ItemFlag.HIDE_ENCHANTS
                                                ))
                                        )
                                )

                        , e -> {
                            ItemStack toChange = item.getItem();
                            if (ItemUtils.hasItemFlags(toChange, ItemFlag.HIDE_ENCHANTS))
                                item.setItem(ItemUtils.removeItemFlags(toChange, ItemFlag.HIDE_ENCHANTS));
                            else
                                item.setItem(ItemUtils.addItemFlags(toChange, ItemFlag.HIDE_ENCHANTS));
                            refresh();
                        }
                ), 43
        );


        inv.addButton(                                                  // QUANTITY
                ItemButton.create(
                        ItemBuilder.of(XMaterial.CHEST_MINECART)    //set of items
                                .setName(Lang.CUSTOMIZE_QUANTITY.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_QUANTITY_LORE.getAsListString(p,
                                                Template.of("amount", item.getItem().getAmount())
                                        )
                                )
                        , e -> {
                            ChatPrompt.builder()
                                    .withPlayer(p)
                                    .withResponse(s -> {
                                        if (!Primitives.isInteger(s)) {
                                            Messages.MSG_NOT_INTEGER.send(p);
                                            return;
                                        }
                                        int i = Primitives.getAsInteger(s);
                                        if (i < 1 || i > 64) Utils.sendRawMsg(p, "&7Invalid amount");
                                        ItemStack toChange = item.getItem();
                                        toChange.setAmount(i);
                                        item.setItem(toChange);
                                        Schedulers.sync().run(this::refresh);
                                    })
                                    .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                    .withTitle("&e&lInput Set Amount")
                                    .prompt();
                        }
                ), 45
        );

        dStock stock = item.getDStock();
        inv.addButton(                                                  // Stock
                ItemButton.create(
                        item.hasStock() ?
                                ItemBuilder.of(XMaterial.STONE_BUTTON)  //Change stock
                                        .setName(Lang.CUSTOMIZE_STOCK_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_STOCK_LORE_ON.getAsListString(p,
                                                        Template.of("amount", stock.getDefault()),
                                                        Template.of("stock_type", stock.getName())
                                                )
                                        )
                                :
                                ItemBuilder.of(XMaterial.STONE_BUTTON)  //Change stock
                                        .setName(Lang.CUSTOMIZE_STOCK_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_STOCK_LORE.getAsListString(p))

                        , e -> {

                            if (e.getClick().equals(ClickType.DROP) && stock != null) {
                                int defaultStock = item.getDStock().getDefault();
                                item.setStock(
                                        stock.getName().equals("INDIVIDUAL") ?
                                                dStockFactory.GLOBAL(defaultStock) : dStockFactory.INDIVIDUAL(defaultStock)
                                );
                                refresh();
                            } else if (e.isLeftClick()) {

                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {
                                            if (Primitives.isInteger(s))
                                                item.setStock(
                                                        !item.hasStock()
                                                                ? dStockFactory.INDIVIDUAL(Integer.parseInt(s))
                                                                : item.getDStock().getName().equals("GLOBAL")
                                                                ? dStockFactory.GLOBAL(Primitives.getAsInteger(s))
                                                                : dStockFactory.INDIVIDUAL(Primitives.getAsInteger(s))
                                                );
                                            else Messages.MSG_NOT_INTEGER.send(p);

                                            Schedulers.sync().run(this::refresh);
                                        })
                                        .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                        .withTitle("&c&lInput Stock number")
                                        .prompt();

                            } else if (e.isRightClick()) {
                                item.setStock(dStockFactory.INFINITE());
                                refresh();
                            }
                        }
                ), 46
        );

        LinkedList<String> commands = item.getCommands();
        inv.addButton(                                                  // Commands
                ItemButton.create(
                        commands != null ?
                                ItemBuilder.of(XMaterial.COMMAND_BLOCK)  //Change commands
                                        .setName(Lang.CUSTOMIZE_COMMANDS_NAME_ON.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_COMMANDS_LORE_ON.getAsListString(p))
                                        .addLore("")
                                        .addLore(commands
                                                .stream().map(s -> FormatUtils.color("&f&l" + s)).collect(Collectors.toList()))
                                :
                                ItemBuilder.of(XMaterial.COMMAND_BLOCK)  //set commands
                                        .setName(Lang.CUSTOMIZE_COMMANDS_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_COMMANDS_LORE.getAsListString(p,
                                                        Template.of("status", false)
                                                )
                                        )

                        , e -> {

                            if (commands == null) { // Boton de cambiar commands
                                item.setCommands(new ArrayList<>());
                                refresh();

                            } else { // Boton de añadir/quitar commands
                                if (e.isLeftClick()) {

                                    ChatPrompt.builder()
                                            .withPlayer(p)
                                            .withResponse(s -> {
                                                commands.addLast(s);
                                                item.setCommands(commands);
                                                refresh();
                                            })
                                            .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                            .withTitle("&7&lInput new command")
                                            .prompt();

                                } else if (e.isRightClick() && !e.isShiftClick()) {

                                    if (!commands.isEmpty()) {
                                        commands.pollLast();
                                        item.setCommands(commands);
                                    }
                                    refresh();

                                } else if (e.isShiftClick() && e.isRightClick()) {
                                    item.setCommands(null);
                                    refresh();
                                }
                            }
                        }
                ), 47
        );

        List<String> bundle = item.getBundle();
        inv.addButton(                                                  // Bundle
                ItemButton.create(
                        bundle != null ?
                                ItemBuilder.of(XMaterial.ITEM_FRAME)  //Change stock
                                        .setName("&f&lChange bundle items")
                                        .setLore("&6Right Click > &7To change items on the bundle")
                                        .addLore("")
                                        .addLore(bundle)
                                :
                                ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")

                        , e -> {

                            if (bundle == null) return;

                            changeBundleItem.builder()
                                    .withPlayer(p)
                                    .withItem(item)
                                    .withShop(shop)
                                    .withConfirm(uuids -> {
                                        item.setBundle(uuids);
                                        refresh();
                                    })
                                    .withBack(this::refresh)
                                    .prompt();

                        }
                ), 48
        );


    }

    private void refresh() {
        inv.destroy();
        build();
        inv.open(p);
    }

    public static final class newCustomizerMenuBuilder {
        private Player p;
        private dItem item;
        private dShop shop;

        private newCustomizerMenuBuilder() {
        }

        public newCustomizerMenuBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public newCustomizerMenuBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public newCustomizerMenuBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public newCustomizerMenuBuilder withShop(String shopS) {
            this.shop = DailyShop.get().getShopsManager().getShop(shopS).orElse(null);
            return this;
        }

        public CustomizerMenu prompt() {

            Preconditions.checkNotNull(p, "player null");
            Preconditions.checkNotNull(item, "item null");
            Preconditions.checkNotNull(shop, "shop null");

            return new CustomizerMenu(p, item, shop);
        }
    }
}
