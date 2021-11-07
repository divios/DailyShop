package io.github.divios.dailyShop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.builder.inventoryPopulator;
import io.github.divios.core_lib.inventory.builder.paginatedGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.guis.customizerguis.CustomizerMenu;
import io.github.divios.dailyShop.lorestategy.shopItemsManagerLore;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Optional;
import java.util.UUID;

public class shopGui {

    private static final DailyShop plugin = DailyShop.getInstance();
    private static final shopsManager sManager = shopsManager.getInstance();

    private static final BiMap<UUID, Integer> cache = HashBiMap.create();

    private paginatedGui inv;
    private final Player p;
    private final dShop shop;

    private final shopItemsManagerLore strategy;

    private shopGui(Player p, dShop shop) {

        this.p = p;
        this.shop = shop;
        this.strategy = new shopItemsManagerLore(shop.getType());

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
        new shopGui(p, shop);
    }

    private void createGuis() {

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
                        shop.getItems().stream().parallel()
                                .map(dItem ->
                                        ItemButton.create(strategy.applyLore(dItem.getItem().clone())
                                                , this::contentAction))
                )

                .withNextButton(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(plugin.configM.getLangYml().DAILY_ITEMS_NEXT)
                                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                        , 51
                )

                .withBackButton(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName(plugin.configM.getLangYml().DAILY_ITEMS_PREVIOUS)
                                .applyTexture("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9")
                        , 47
                )

                .withExitButton(
                        ItemButton.create(
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(plugin.configM.getLangYml().SHOPS_MANAGER_RETURN)
                                        .setLore("&7Click to return")
                                        .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                                , e -> {
                                    Schedulers.sync().runLater(() -> inv.destroy(), 3L);
                                    cache.remove(p.getUniqueId());
                                    shopsManagerGui.open(p);
                                }), 8
                )

                .withButtons(
                        (inventoryGUI, integer) -> {

                            inventoryGUI.addButton(ItemButton.create(
                                    ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                            .setName(plugin.configM.getLangYml().DAILY_ITEMS_ADD)
                                            .addLore(plugin.configM.getLangYml().DAILY_ITEMS_ADD_LORE)
                                            .applyTexture("9b425aa3d94618a87dac9c94f377af6ca4984c07579674fad917f602b7bf235"),

                                    e -> addDailyGuiIH.open(p, shop, itemStack -> {
                                        shop.addItem(new dItem(itemStack));
                                        refresh();
                                    }, this::refresh)), 53);

                        }
                )

                .withTitle("&8" + shop.getName())

                .build();

    }

    private void contentAction(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        UUID uid = dItem.getUid(e.getCurrentItem());

        if (e.isLeftClick()) {

            Schedulers.sync().runLater(() -> inv.destroy(), 1L);
            CustomizerMenu.builder()
                    .withPlayer(p)
                    .withShop(shop)
                    .withItem(shop.getItem(uid).orElse(null))
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
                    .withTitle(plugin.configM.getLangYml().CONFIRM_GUI_ACTION_NAME)
                    .withConfirmLore(plugin.configM.getLangYml().CONFIRM_GUI_YES, plugin.configM.getLangYml().CONFIRM_GUI_YES_LORE)
                    .withCancelLore(plugin.configM.getLangYml().CONFIRM_GUI_NO, plugin.configM.getLangYml().CONFIRM_GUI_NO_LORE)
                    .withItem(dItem.of(e.getCurrentItem()).getRawItem())
                    .prompt();

    }

    private void refresh() {
        Schedulers.sync().runLater(() -> inv.destroy(), 3L);
        open(p, shop);
    }

}
