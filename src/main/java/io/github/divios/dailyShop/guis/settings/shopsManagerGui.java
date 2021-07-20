package io.github.divios.dailyShop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.builder.inventoryPopulator;
import io.github.divios.core_lib.inventory.builder.paginatedGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.guis.customizerguis.customizeGui;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopsManagerLore;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.dataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class shopsManagerGui {

    private static final DRShop plugin = DRShop.getInstance();
    private static final shopsManager sManager = shopsManager.getInstance();
    private static final dataManager dManager = dataManager.getInstance();

    private static final loreStrategy strategy = new shopsManagerLore();
    private paginatedGui inv;

    private final Player p;

    private shopsManagerGui(Player p) {
        this.p = p;

        createInvs();
    }

    public static void open(Player p) {
        new shopsManagerGui(p);
    }

    private void createInvs() {

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
                                .scheme(11, 11, 3, 0, 0, 0, 3, 11, 11)
                                .scheme(11, 11)
                                .scheme(0)
                                .scheme(0)
                                .scheme(11, 11)
                                .scheme(11, 11, 3, 0, 0, 0, 3, 11, 11)

                )

                .withItems(
                        shopsManager.getInstance().getShops().stream()
                                .map(dShop -> ItemButton.create(
                                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                                .setName("&f&l" + dShop.getName())
                                                .applyTexture("7e3deb57eaa2f4d403ad57283ce8b41805ee5b6de912ee2b4ea736a9d1f465a7"),
                                        this::contentAction))
                                .peek(itemButton -> strategy.setLore(itemButton.getItem()))
                )

                .withNextButton(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName("&1&lNext").applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                        , 51)

                .withBackButton(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName("&1&lPrevious").applyTexture("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9")
                        , 47)

                .withButtons((inventoryGUI, integer) -> {

                    inventoryGUI.addButton(new ItemButton(new ItemBuilder(XMaterial.PLAYER_HEAD)
                            .setName(conf_msg.SHOPS_MANAGER_CREATE)
                            .applyTexture("9b425aa3d94618a87dac9c94f377af6ca4984c07579674fad917f602b7bf235")
                            , e -> nonContentAction()), 53);
                })

                .withExitButton(
                        new ItemButton(new ItemBuilder(XMaterial.PLAYER_HEAD)
                                .setName("&cReturn").setLore("&7Click to return")
                                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                                , e -> {
                            Task.syncDelayed(plugin, () -> inv.destroy() , 3L);
                            p.closeInventory();
                        })
                        , 8
                )

                .withTitle(conf_msg.SHOPS_MANAGER_TITLE)

                .build();

        inv.open(p);

    }

    private void refresh(Player p) {
        inv.destroy();
        open(p);
    }

    private void contentAction(InventoryClickEvent e) {

        ItemStack selected = e.getCurrentItem();
        dShop shop = sManager.getShop(FormatUtils.stripColor(utils.getDisplayName(selected))).get();
        Player p = (Player) e.getWhoClicked();

        if (e.isShiftClick() && e.isLeftClick()) {
            inv.destroy();
            customizeGui.open(p, shop);

        } else if (e.getClick().equals(ClickType.MIDDLE)) {   // rename

            ChatPrompt.builder()
                    .withPlayer(p)
                    .withResponse(s -> {

                        if (s.isEmpty()) {
                            utils.sendMsg(p, "&7Cant be empty");
                            Task.syncDelayed(plugin, () -> refresh(p));
                            return;
                        }

                        if (s.split("\\s+").length > 1) {
                            utils.sendMsg(p, "&7Name cannot have white spaces");
                            Task.syncDelayed(plugin, () -> refresh(p));
                            return;
                        }

                        if (sManager.getShop(s).isPresent()) {
                            utils.sendMsg(p, "&7Already Exist");
                            Task.syncDelayed(plugin, () -> refresh(p));
                            return;
                        }

                        dManager.renameShop(shop.getName(), s);
                        shop.setName(s);
                        Task.syncDelayed(plugin, () -> refresh(p));
                    })
                    .withCancel(cancelReason -> Task.syncDelayed(plugin, () -> refresh(p)))
                    .withTitle("&b&lInput new Shop name")
                    .prompt();

        } else if (e.getClick().equals(ClickType.DROP)) {       // change timer

            ChatPrompt.builder()
                    .withPlayer(p)
                    .withResponse(s -> {

                        if (!utils.isInteger(s)) {
                            utils.sendMsg(p, conf_msg.MSG_NOT_INTEGER);
                            Task.syncDelayed(plugin, () -> refresh(p));
                            return;
                        }

                        if (Integer.parseInt(s) < 50) {
                            utils.sendMsg(p, "&7Time cannot be less than 50");
                            Task.syncDelayed(plugin, () -> refresh(p));
                            return;
                        }
                        shop.setTimer(Integer.parseInt(s));
                        Task.syncDelayed(plugin, () -> refresh(p));

                    })
                    .withCancel(cancelReason -> Task.syncDelayed(plugin, () -> refresh(p)))
                    .withTitle("&e&lInput new Timer")
                    .prompt();

        } else if (e.isRightClick()) {

            confirmIH.builder()
                    .withPlayer(p)
                    .withAction(aBoolean -> {
                        if (aBoolean)
                            shopsManager.getInstance().deleteShop(shop.getName());
                        refresh(p);
                    })
                    .withItem(selected)
                    .withTitle(conf_msg.CONFIRM_GUI_ACTION_NAME)
                    .withConfirmLore(conf_msg.CONFIRM_MENU_YES)
                    .withCancelLore(conf_msg.CONFIRM_MENU_NO)
                    .prompt();

        } else shopGui.open(p, shop.getName());
    }

    private void nonContentAction() {

        ChatPrompt.builder()
                .withPlayer(p)
                .withResponse(s -> {

                    if (s.isEmpty()) {
                        utils.sendMsg(p, "&7Cant be empty");
                        Task.syncDelayed(plugin, () -> refresh(p));
                        return;
                    }

                    if (s.split("\\s+").length > 1) {
                        utils.sendMsg(p, "&7Name cannot have white spaces");
                        Task.syncDelayed(plugin, () -> refresh(p));
                        return;
                    }

                    if (sManager.getShop(s).isPresent()) {
                        utils.sendMsg(p, "&7Already Exist");
                        Task.syncDelayed(plugin, () -> refresh(p));
                        return;
                    }

                    shopsManager.getInstance().createShop(s, dShop.dShopT.buy);
                    Task.syncDelayed(plugin, () -> refresh(p));
                })
                .withCancel(cancelReason -> Task.syncDelayed(plugin, () -> refresh(p)))
                .withTitle("&a&lInput New Shop Name")
                .prompt();

    }


}