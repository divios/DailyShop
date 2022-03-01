package io.github.divios.dailyShop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.builder.inventoryPopulator;
import io.github.divios.core_lib.inventory.builder.paginatedGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.guis.customizerguis.CustomizerMenu;
import io.github.divios.dailyShop.lorestategy.shopItemsManagerLore;
import io.github.divios.dailyShop.utils.valuegenerators.FixedValueGenerator;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.*;

public class shopsItemsManagerGui {

    private static final shopsManager sManager = DailyShop.get().getShopsManager();

    private static final BiMap<UUID, Integer> cache = HashBiMap.create();

    private paginatedGui inv;
    private final Player p;
    private final dShop shop;

    private shopsItemsManagerGui(Player p, dShop shop) {

        this.p = p;
        this.shop = shop;

        createGuis();

        if (cache.containsKey(p.getUniqueId())) {
            if (cache.get(p.getUniqueId()) >= inv.getInvs().size())
                inv.open(p);
            else
                inv.open(p, cache.get(p.getUniqueId()));
        } else
            inv.open(p);


        Events.subscribe(InventoryOpenEvent.class)
                .filter(o -> o.getPlayer().equals(p))
                .biHandler((e, o) -> {

                    Optional<InventoryGUI> x = inv.getInvs().stream()
                            .filter(inventoryGUI -> inventoryGUI.getInventory().equals(o.getInventory()))
                            .findFirst();

                    if (x.isPresent())
                        cache.put(p.getUniqueId(), inv.getInvs().indexOf(x.get()));
                    else
                        e.unregister();

                });

    }

    public static void open(Player p, String shop) {
        sManager.getShop(shop).ifPresent(shop1 -> open(p, shop1));
    }

    public static void open(Player p, dShop shop) {
        new shopsItemsManagerGui(p, shop);
    }

    private void createGuis() {

        Deque<dItem> entries = new ArrayDeque<>();
        shop.getItems().forEach(entries::addFirst);

        inv = paginatedGui.Builder()

                .withPopulator(
                        inventoryPopulator.builder()
                                .ofGlass()
                                .mask("111111111")
                                .mask("100000001")
                                .mask("000000000")
                                .mask("000000000")
                                .mask("100000001")
                                .mask("111111111")
                                .scheme(13, 13, 5, 0, 0, 0, 5, 13, 13)
                                .scheme(13, 13)
                                .scheme(0)
                                .scheme(0)
                                .scheme(13, 13)
                                .scheme(13, 13, 5, 0, 0, 0, 5, 13, 13)

                )

                .withItems(
                        entries.stream()
                                .parallel()
                                .sorted(Comparator.comparing(dItem::getID))
                                .map(dItem ->
                                        ItemButton.create(shopItemsManagerLore.applyLore(dItem)
                                                , this::contentAction))
                )

                .withNextButton(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.DAILY_ITEMS_NEXT.getAsString(p))
                                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                        , 51
                )

                .withBackButton(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(Lang.DAILY_ITEMS_PREVIOUS.getAsString(p))
                                .applyTexture("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9")
                        , 47
                )

                .withExitButton(
                        ItemButton.create(
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(Lang.SHOPS_MANAGER_RETURN.getAsString(p))
                                        .setLore("&7Click to return")
                                        .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                                , e -> {
                                    Schedulers.sync().runLater(() -> inv.destroy(), 3L);
                                    cache.remove(p.getUniqueId());
                                    shopsManagerGui.open(p);
                                }), 8
                )

                .withButtons(
                        (inventoryGUI, integer) ->

                                inventoryGUI.addButton(ItemButton.create(
                                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                                .setName(Lang.DAILY_ITEMS_ADD.getAsString(p))
                                                .addLore(Lang.DAILY_ITEMS_ADD_LORE.getAsString(p))
                                                .applyTexture("9b425aa3d94618a87dac9c94f377af6ca4984c07579674fad917f602b7bf235"),

                                        e -> addDailyGuiIH.open(p, shop, itemStack -> {
                                            shop.addItem(dItem.of(itemStack)
                                                    .setBuyPrice(new FixedValueGenerator(Settings.DEFAULT_BUY.getValue().getAsDouble()))
                                                    .setSellPrice(new FixedValueGenerator(Settings.DEFAULT_SELL.getValue().getAsDouble()))
                                            );
                                            refresh();
                                        }, this::refresh)), 53)
                )

                .withTitle("&8" + shop.getName())

                .build();

    }

    private void contentAction(InventoryClickEvent e) {

        if (ItemUtils.isEmpty(e.getCurrentItem())) return;

        Player p = (Player) e.getWhoClicked();

        UUID uid = dItem.getUUIDKey(e.getCurrentItem());
        if (uid == null) {
            refresh();
            return;
        }

        dItem item = shop.getItem(uid);
        if (item == null) {
            refresh();
            return;
        }

        if (e.isLeftClick()) {

            Schedulers.sync().runLater(() -> inv.destroy(), 1L);
            CustomizerMenu.builder()
                    .withPlayer(p)
                    .withShop(shop)
                    .withItem(item)
                    .prompt();

        } else if (e.isRightClick())

            confirmIH.builder()
                    .withPlayer(p)
                    .withAction(aBoolean -> {
                        if (aBoolean)
                            shop.removeItem(uid);

                        Schedulers.sync().runLater(() -> inv.destroy(), 3L);
                        open(p, shop);
                    })
                    .withTitle(Lang.CONFIRM_GUI_ACTION_NAME.getAsString(p))
                    .withConfirmLore(Lang.CONFIRM_GUI_YES.getAsString(p), Lang.CONFIRM_GUI_YES_LORE.getAsListString(p))
                    .withCancelLore(Lang.CONFIRM_GUI_NO.getAsString(p), Lang.CONFIRM_GUI_NO_LORE.getAsListString(p))
                    .withItem(e.getCurrentItem())
                    .prompt();

    }

    private void refresh() {
        inv.destroy();
        createGuis();
        inv.open(p);
    }

}
