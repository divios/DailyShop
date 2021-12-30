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
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.guis.settings.shopGui;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import io.github.divios.lib.serialize.serializerApi;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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


        inv.getInventory().setItem(5, item.getRealItem());             // The item itself

        inv.addButton(                                                  // Craft button
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.CUSTOMIZE_CRAFT.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_CRAFT_LORE.getAsListString(p))
                                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f")
                        , e -> {

                            // Preconditions of some features

                            if (item.getCommands().isPresent() && item.getCommands().get().isEmpty())
                                item.setCommands(null);

                            if (item.getPermsBuy().isPresent() && item.getPermsBuy().get().isEmpty())
                                item.setPermsBuy(null);

                            if (item.getPermsSell().isPresent() && item.getPermsSell().get().isEmpty())
                                item.setPermsSell(null);

                            if (item.getBundle().isPresent() && item.getBundle().get().isEmpty())
                                item.setBundle(null);

                            // Check to update or add item
                            if (shop.hasItem(item.getUid())) {
                                shop.updateItem(item);
                            } else {
                                shop.addItem(item);
                            }
                            shopGui.open(p, shop.getName());
                            serializerApi.saveShopToFileAsync(shop);
                        }),
                7
        );

        inv.addButton(                                                  // Return button
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.CUSTOMIZE_RETURN.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_RETURN_LORE.getAsListString(p))
                                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                        , e -> shopGui.open(p, shop.getName())),
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
                                            item.setDisplayName(s);
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
                                    if (aBoolean)
                                        item.setMaterial(material);
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
                                .addLore(ItemUtils.getLore(item.getDailyItem()))
                                .applyTexture("c6692f99cc6d78242304110553589484298b2e4a0233b76753f888e207ef5")
                        , e -> {

                            if (e.isRightClick()) {
                                List<String> lore = item.getLore();
                                if (lore.isEmpty()) return;
                                lore.remove(lore.size() - 1);
                                item.setLore(lore);
                                refresh();

                            } else if (e.isLeftClick())

                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {
                                            if (!s.isEmpty()) {
                                                List<String> lore = item.getLore();
                                                lore.add(s);
                                                item.setLore(lore);
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
                                        .withItem(item)
                                        .withBack((dItem) -> new CustomizerMenu(p, dItem, shop))
                                        .open()), 19);

        inv.addButton(                                                  // Enchantments
                ItemButton.create(
                        ItemBuilder.of(XMaterial.ENCHANTING_TABLE)
                                .setName(Lang.CUSTOMIZE_ENCHANTS_NAME.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_ENCHANTS_LORE.getAsListString(p))
                                .addLore(item.getDailyItem().getEnchantments().entrySet().stream()
                                        .map(entry -> "&f&l" + entry.getKey()
                                                .getName() + ":" + entry.getValue()).collect(Collectors.toList()))
                        , e -> {

                            if (e.isLeftClick())

                                changeEnchantments.builder()
                                        .withPlayer(p)
                                        .withDitem(item)
                                        .withShop(shop)
                                        .prompt();

                            else if (e.isRightClick() && !item.getEnchantments().isEmpty())

                                changeEnchantments.builder()
                                        .withPlayer(p)
                                        .withDitem(item)
                                        .withShop(shop)
                                        .withEnchants(item.getEnchantments())
                                        .prompt();

                        }),
                21
        );

        inv.addButton(                                                  // Econ
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.CUSTOMIZE_ECON_NAME.getAsString(p))
                                .addLore(Lang.CUSTOMIZE_ECON_LORE.getAsListString(p))
                                .addLore("", "&7Current: &e" + item.getEconomy().getName())
                                .applyTexture("e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852")
                        , e ->
                                changeEcon.builder()
                                        .withPlayer(p)
                                        .withItem(item)
                                        .withConsumer(dItem -> refresh())
                                        .prompt()
                ), 23
        );

        inv.addButton(                                                  // Price
                ItemButton.create(
                        ItemBuilder.of(XMaterial.EMERALD)
                                .setName(Lang.CUSTOMIZE_PRICE_NAME.getAsString(p))
                                .addLore(
                                        Lang.CUSTOMIZE_PRICE_LORE.getAsListString(p,
                                                Template.of("buy_price", item.getBuyPrice().orElse(dPrice.EMPTY()).getVisualPrice()),
                                                Template.of("sell_price", item.getSellPrice().orElse(dPrice.EMPTY()).getVisualPrice())
                                        )
                                )
                        , e ->
                                changePrice.builder()
                                        .withPlayer(p)
                                        .withType(e.isLeftClick() ? changePrice.Type.BUY : changePrice.Type.SELL)
                                        .withItem(item)
                                        .withAccept(dItem -> new CustomizerMenu(p, dItem, shop))
                                        .withBack(this::refresh)
                                        .prompt()
                ), 24
        );

        inv.addButton(                                                  // Rarity
                ItemButton.create(
                        ItemBuilder.of(item.getRarity().getAsItem())
                                .setName(item.getRarity().toString())
                                .addLore(Lang.CUSTOMIZE_RARITY_NAME.getAsListString(p))

                        , e -> {
                            item.nextRarity();
                            refresh();
                        }
                ), 25
        );

        inv.addButton(                                                  // Durability
                ItemButton.create(
                        item.getDailyItem().getType().getMaxDurability() != 0 ?
                                ItemBuilder.of(XMaterial.DAMAGED_ANVIL)
                                        .setName(Lang.CUSTOMIZE_DURABILITY_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_DURABILITY_LORE.getAsListString(p))
                                :
                                ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")

                        , e ->
                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {
                                            if (Utils.isShort(s)) item.setDurability(Short.parseShort(s), false);
                                            else Messages.MSG_NOT_INTEGER.send(p);
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
                                                Template.of("status", item.hasFlag(ItemFlag.HIDE_ATTRIBUTES)
                                                )
                                        )
                                )

                        , e -> {
                            item.toggleFlag(ItemFlag.HIDE_ATTRIBUTES);
                            refresh();
                        }
                ), 32
        );

        inv.addButton(                                                  // Confirm Gui
                ItemButton.create(
                        ItemBuilder.of(XMaterial.LEVER)
                                .setName(Lang.CUSTOMIZE_CONFIRM_GUI_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_CONFIRM_GUI_LORE.getAsListString(p,
                                                Template.of("status", item.isConfirmGuiEnabled())
                                        )
                                )
                        , e -> {
                            item.toggleConfirm_gui();
                            refresh();
                        }
                ), 34
        );

        inv.addButton(                                                  // Hide Effects
                ItemButton.create(
                        Utils.isPotion(item.getDailyItem()) ?
                                ItemBuilder.of(XMaterial.CAULDRON)
                                        .setName(Lang.CUSTOMIZE_TOGGLE_EFFECTS_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_TOGGLE_EFFECTS_LORE.getAsListString(p,
                                                        Template.of("status", item.hasFlag(ItemFlag.HIDE_POTION_EFFECTS))
                                                )
                                        )
                                :
                                ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")

                        , e -> {
                            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
                            item.toggleFlag(ItemFlag.HIDE_POTION_EFFECTS);
                            refresh();
                        }
                ), 41
        );

        inv.addButton(                                                  // Hide Enchantments
                ItemButton.create(
                        ItemBuilder.of(XMaterial.BOOKSHELF)
                                .setName(Lang.CUSTOMIZE_TOGGLE_ENCHANTS_NAME.getAsString(p))
                                .setLore(Lang.CUSTOMIZE_TOGGLE_ENCHANTS_LORE.getAsListString(p,
                                                Template.of("status", item.hasFlag(ItemFlag.HIDE_ENCHANTS))
                                        )
                                )

                        , e -> {
                            item.toggleFlag(ItemFlag.HIDE_ENCHANTS);
                            refresh();
                        }
                ), 43
        );

        inv.addButton(                                                  // Set
                ItemButton.create(
                        item.hasStock() ?
                                ItemBuilder.of(XMaterial.BARRIER).setName(Lang.CUSTOMIZE_UNAVAILABLE.getAsString(p))
                                :
                                item.getSetItems().isPresent() ?
                                        ItemBuilder.of(XMaterial.CHEST_MINECART)    //set of items
                                                .setName(Lang.CUSTOMIZE_SET_NAME.getAsString(p))
                                                .addLore(Lang.CUSTOMIZE_SET_LORE_ON.getAsListString(p,
                                                                Template.of("amount", item.getSetItems().get())
                                                        )
                                                )
                                        :
                                        ItemBuilder.of(XMaterial.CHEST_MINECART)    //set of items
                                                .setName(Lang.CUSTOMIZE_SET_NAME.getAsString(p))
                                                .addLore(Lang.CUSTOMIZE_SET_LORE.getAsListString(p))

                        , e -> {

                            if (item.hasStock()) return;

                            if (!item.getSetItems().isPresent() && e.isLeftClick()) {  // Boton de edit set
                                if (item.hasStock()) {
                                    Utils.sendRawMsg(p, "&7You can't enable this when the stock feature is enable");
                                    return;
                                }
                                item.setSetItems(1);
                                item.setQuantity(1);
                                refresh();
                            } else if (item.getSetItems().isPresent()) {
                                if (e.isLeftClick()) {

                                    ChatPrompt.builder()
                                            .withPlayer(p)
                                            .withResponse(s -> {
                                                if (!Utils.isInteger(s))
                                                    Messages.MSG_NOT_INTEGER.send(p);
                                                int i = Integer.parseInt(s);
                                                if (i < 1 || i > 64) Utils.sendRawMsg(p, "&7Invalid amount");
                                                item.setSetItems(i);
                                                item.setQuantity(i);
                                                Schedulers.sync().run(this::refresh);
                                            })
                                            .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                            .withTitle("&e&lInput Set Amount")
                                            .prompt();

                                } else if (e.isRightClick()) {
                                    item.setSetItems(null);
                                    item.setQuantity(1);
                                    refresh();
                                }
                            }
                        }
                ), 45
        );

        inv.addButton(                                                  // Stock
                ItemButton.create(
                        item.getSetItems().isPresent() ?
                                ItemBuilder.of(XMaterial.BARRIER).setName(Lang.CUSTOMIZE_UNAVAILABLE.getAsString(p))
                                :
                                item.hasStock() ?
                                        ItemBuilder.of(XMaterial.STONE_BUTTON)  //Change stock
                                                .setName(Lang.CUSTOMIZE_STOCK_NAME.getAsString(p))
                                                .setLore(Lang.CUSTOMIZE_STOCK_LORE_ON.getAsListString(p,
                                                                Template.of("amount", item.getStock().getDefault()),
                                                                Template.of("stock_type", item.getStock().getName())
                                                        )
                                                )
                                        :
                                        ItemBuilder.of(XMaterial.STONE_BUTTON)  //Change stock
                                                .setName(Lang.CUSTOMIZE_STOCK_NAME.getAsString(p))
                                                .setLore(Lang.CUSTOMIZE_STOCK_LORE.getAsListString(p))

                        , e -> {

                            if (item.getSetItems().isPresent()) return;

                            if (e.getClick().equals(ClickType.DROP)) {
                                int defaultStock = item.getStock().getDefault();
                                item.setStock(
                                        item.getStock().getName().equals("INDIVIDUAL") ?
                                                dStockFactory.GLOBAL(defaultStock) : dStockFactory.INDIVIDUAL(defaultStock)
                                );
                                refresh();
                            } else if (e.isLeftClick()) {

                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {
                                            if (Utils.isInteger(s))
                                                item.setStock(dStockFactory.INDIVIDUAL(Integer.parseInt(s)));
                                            else Messages.MSG_NOT_INTEGER.send(p);

                                            Schedulers.sync().run(this::refresh);
                                        })
                                        .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                        .withTitle("&c&lInput Stock number")
                                        .prompt();

                            } else if (e.isRightClick()) {
                                item.setStock(null);
                                refresh();
                            }
                        }
                ), 46
        );

        inv.addButton(                                                  // Commands
                ItemButton.create(
                        item.getCommands().isPresent() ?
                                ItemBuilder.of(XMaterial.COMMAND_BLOCK)  //Change commands
                                        .setName(Lang.CUSTOMIZE_COMMANDS_NAME_ON.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_COMMANDS_LORE_ON.getAsListString(p))
                                        .addLore("")
                                        .addLore(item.getCommands().get()
                                                .stream().map(s -> FormatUtils.color("&f&l" + s)).collect(Collectors.toList()))
                                :
                                ItemBuilder.of(XMaterial.COMMAND_BLOCK)  //set commands
                                        .setName(Lang.CUSTOMIZE_COMMANDS_NAME.getAsString(p))
                                        .setLore(Lang.CUSTOMIZE_COMMANDS_LORE.getAsListString(p,
                                                        Template.of("status", false)
                                                )
                                        )

                        , e -> {

                            if (!item.getCommands().isPresent()) { // Boton de cambiar commands
                                item.setCommands(new ArrayList<>());
                                refresh();

                            } else if (item.getCommands().isPresent()) { // Boton de añadir/quitar commands
                                if (e.isLeftClick()) {

                                    ChatPrompt.builder()
                                            .withPlayer(p)
                                            .withResponse(s -> {
                                                List<String> cmds = item.getCommands().get();
                                                cmds.add(s);
                                                item.setCommands(cmds);
                                                refresh();
                                            })
                                            .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                            .withTitle("&7&lInput new command")
                                            .prompt();

                                } else if (e.isRightClick() && !e.isShiftClick()) {

                                    List<String> cmds = item.getCommands().get();

                                    if (!cmds.isEmpty()) {
                                        cmds.remove(cmds.size() - 1);
                                        item.setCommands(cmds);
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

        inv.addButton(                                                  // Bundle
                ItemButton.create(
                        item.getBundle().isPresent() ?
                                ItemBuilder.of(XMaterial.ITEM_FRAME)  //Change stock
                                        .setName("&f&lChange bundle items")
                                        .setLore("&6Right Click > &7To change items on the bundle")
                                        .addLore("")
                                        .addLore(item.getBundle().orElse(Collections.emptyList()))
                                :
                                ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")

                        , e -> {

                            if (!item.getBundle().isPresent()) return;

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
        new CustomizerMenu(p, item, shop);
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
