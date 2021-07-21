package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class customizeAction {

    private final static DailyShop plugin = DailyShop.getInstance();

    private final Player p;
    private final dShop shop;
    private final InventoryGUI inv;
    private final BiConsumer<dAction, String> onComplete;
    private final Consumer<Player> back;

    private boolean flagPass = false;

    private final EventListener<InventoryCloseEvent> preventClose;

    private customizeAction(Player p, dShop shop,
                            BiConsumer<dAction, String> onComplete,
                            Consumer<Player> back) {
        this.p = p;
        this.shop = shop;
        this.inv = new InventoryGUI(plugin, 27, "&6&lManage Actions");
        inv.setDestroyOnClose(false);
        this.onComplete = onComplete;
        this.back = back;

        initialize();
        inv.open(p);

        preventClose = new EventListener<>(InventoryCloseEvent.class,
                e -> {
                    if (!e.getInventory().equals(inv.getInventory())) return;

                    if (flagPass) return;

                    Task.syncDelayed(plugin, () -> inv.open(p), 1L);
                });
    }

    public static void open(Player p, dShop shop,
                            BiConsumer<dAction, String> onComplete, Consumer<Player> back) {
        new customizeAction(p, shop, onComplete, back);
    }

    private void initialize() {

        IntStream.of(0, 1, 9, 18, 19, 7, 17, 25, 26)
                .forEach(value -> inv.addButton(value, new ItemButton(new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE)
                        .setName("&c"), e -> {})));

        IntStream.of(2, 3, 4, 5, 6, 10, 16, 20, 21, 22, 23, 24)
                .forEach(value -> inv.addButton(value, new ItemButton(new ItemBuilder(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .setName("&c"), e -> {})));

        inv.addButton(8, new ItemButton(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                .setName("&cClick to Return"), e -> {
            preventClose.unregister();
            back.accept(p);
        }));


        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.BARRIER)
                        .setName("&aNo action").setLore("&7Do nothing when this", "&7item is clicked"),
                e -> {
                    preventClose.unregister();
                    onComplete.accept(dAction.EMPTY, "");
                }), inventoryUtils.getFirstEmpty(inv.getInventory()));

        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
                                .setName("&aOpen shop").setLore("&7Opens shop when this", "&7item is clicked")
                                .applyTexture("7e3deb57eaa2f4d403ad57283ce8b41805ee5b6de912ee2b4ea736a9d1f465a7"),
                e -> {
                    flagPass = true;
                    ChatPrompt.prompt(plugin, p, (s) -> {
                        if (!shopsManager.getInstance().getShop(s).isPresent()) {
                            utils.sendMsg(p, "&7That shop doesnt exist");
                            Task.syncDelayed(plugin, () -> inv.open(p));
                            flagPass = true;
                            return;
                        }
                        preventClose.unregister();
                        inv.destroy();
                        Task.syncDelayed(plugin, () ->
                                onComplete.accept(dAction.OPEN_SHOP, s), 1L);
                    }, (cause) -> {
                        Task.syncDelayed(plugin, () -> inv.open(p));
                        flagPass = true;
                    }, "&a&lInput shop name", "");
                }), inventoryUtils.getFirstEmpty(inv.getInventory()));

        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.COMMAND_BLOCK)
                        .setName("&aRun command").setLore("&7Runs command when this", "&7item is clicked"),
                e -> {
                    flagPass = true;
                    ChatPrompt.prompt(plugin, p , (s) -> {
                        preventClose.unregister();
                        inv.destroy();
                        Task.syncDelayed(plugin, () -> onComplete.accept(dAction.RUN_CMD, s));
                    }, (player -> {
                        Task.syncDelayed(plugin, () -> inv.open(p));
                        flagPass = true;
                    }), "", "");
                }), inventoryUtils.getFirstEmpty(inv.getInventory()));

        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.BOOKSHELF)
                        .setName("&6Show Avariable Items")
                        .addLore("&7Shows all the items", "&7available on this shop"),
                e -> {
                    preventClose.unregister();
                    inv.destroy();
                    onComplete.accept(dAction.SHOW_ALL_ITEMS, shop.getName());
                }), inventoryUtils.getFirstEmpty(inv.getInventory()));


    }


}
