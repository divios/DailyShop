package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.lib.itemHolder.dAction;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.managers.shopsManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.BiConsumer;

public class customizeAction {

    private final static DRShop plugin = DRShop.getInstance();

    private final Player p;
    private dShop shop;
    private final InventoryGUI inv;
    private final BiConsumer<dAction, String> onComplete;

    private boolean flagPass = false;

    private final EventListener<InventoryCloseEvent> preventClose;

    private customizeAction(Player p, dShop shop,
                            BiConsumer<dAction, String> onComplete) {
        this.p = p;
        this.shop = shop;
        this.inv = new InventoryGUI(plugin, 27, "&6&lManage Actions");
        inv.setDestroyOnClose(false);
        this.onComplete = onComplete;

        initialize();
        inv.open(p);

        preventClose = new EventListener<>(plugin, InventoryCloseEvent.class,
                e -> {
                    if (!e.getPlayer().getUniqueId().equals(p.getUniqueId())) return;

                    if (flagPass) return;

                    Task.syncDelayed(plugin, () -> inv.open(p), 1L);
                });
    }

    public static void open(Player p, dShop shop,
                            BiConsumer<dAction, String> onComplete) {
        new customizeAction(p, shop, onComplete);
    }

    private void initialize() {

        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.BARRIER)
                        .setName("&aNo action").setLore("&7Do nothing when this", "&7item is clicked"),
                e -> {
                    preventClose.unregister();
                    onComplete.accept(dAction.EMPTY, "");
                }), 0);

        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
                                .setName("&aOpen shop").setLore("&7Opens shop when this", "&7item is clicked")
                                .applyTexture("7e3deb57eaa2f4d403ad57283ce8b41805ee5b6de912ee2b4ea736a9d1f465a7"),
                e -> {
                    final boolean[] exit = {false};
                    flagPass = true;
                    new AnvilGUI.Builder()
                            .onClose(player ->
                                {
                                if (!exit[0]) Task.syncDelayed(plugin, () -> {
                                    inv.open(player);
                                    flagPass = true;
                                }, 1L);
                            })
                            .onComplete((player, s) -> {
                                if (!shopsManager.getInstance().getShop(s).isPresent())
                                    return AnvilGUI.Response.text("That shop doesnt exit");

                                preventClose.unregister();
                                inv.destroy();
                                exit[0] = true;
                                Task.syncDelayed(plugin, () ->
                                        onComplete.accept(dAction.OPEN_SHOP, s), 1L);
                                return AnvilGUI.Response.close();

                            })
                            .title(FormatUtils.color("&a&lInput shop name"))
                            .text("Input")
                            .itemLeft(e.getCurrentItem().clone())
                            .plugin(plugin)
                            .open(p);
                }), 1);

        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.COMMAND_BLOCK)
                        .setName("&aRun command").setLore("&7Runs command when this", "&7item is clicked"),
                e -> {
                    final boolean[] exit = {false};
                    flagPass = true;
                    new AnvilGUI.Builder()
                            .onClose(player ->
                            {
                                if (!exit[0]) Task.syncDelayed(plugin, () -> {
                                    inv.open(player);
                                    flagPass = true;
                                }, 1L);
                            })
                            .onComplete((player, s) -> {
                                preventClose.unregister();
                                inv.destroy();
                                exit[0] = true;
                                Task.syncDelayed(plugin, () ->
                                        onComplete.accept(dAction.RUN_CMD, s), 1L);
                                return AnvilGUI.Response.close();

                            })
                            .title(FormatUtils.color("&a&lInput command"))
                            .text("Input")
                            .itemLeft(e.getCurrentItem().clone())
                            .plugin(plugin)
                            .open(p);
                }), 2);

        inv.addButton(ItemButton.create(new ItemBuilder(XMaterial.BOOKSHELF)
                        .setName("&6Show Avariable Items")
                        .addLore("&7Shows all the items", "&7available on this shop"),
                e -> {
                    preventClose.unregister();
                    inv.destroy();
                    onComplete.accept(dAction.SHOW_ALL_ITEMS, shop.getName());
                }), 3);


    }


}
