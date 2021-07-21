package io.github.divios.dailyShop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.Schedulers;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.builder.inventoryPopulator;
import io.github.divios.core_lib.inventory.builder.paginatedGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DailyShop;
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
import java.util.Collections;
import java.util.List;

public class shopsManagerGui {

    private static final DailyShop plugin = DailyShop.getInstance();
    private static final shopsManager sManager = shopsManager.getInstance();
    private static final dataManager dManager = dataManager.getInstance();

    private static final loreStrategy strategy = new shopsManagerLore();
    private paginatedGui inv;

    private final Player p;

    private shopsManagerGui(Player p) {
        this.p = p;

        createInvs();
        updateTask();
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
                                        strategy.applyLore(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                                .setName("&8> &6" + dShop.getName())
                                                .applyTexture("7e3deb57eaa2f4d403ad57283ce8b41805ee5b6de912ee2b4ea736a9d1f465a7")),
                                        this::contentAction))
                )

                .withNextButton(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName("&1&lNext").applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                        , 51)

                .withBackButton(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName("&1&lPrevious").applyTexture("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9")
                        , 47)

                .withButtons((inventoryGUI, integer) -> {

                    inventoryGUI.addButton(new ItemButton(new ItemBuilder(XMaterial.PLAYER_HEAD)
                            .setName(plugin.configM.getLangYml().SHOPS_MANAGER_CREATE)
                            .addLore(plugin.configM.getLangYml().SHOPS_MANAGER_CREATE_LORE)
                            .applyTexture("9b425aa3d94618a87dac9c94f377af6ca4984c07579674fad917f602b7bf235")
                            , e -> nonContentAction()), 53);
                })

                .withExitButton(
                        new ItemButton(new ItemBuilder(XMaterial.PLAYER_HEAD)
                                .setName(plugin.configM.getLangYml().SHOPS_MANAGER_RETURN)
                                .setLore(plugin.configM.getLangYml().SHOPS_MANAGER_RETURN_LORE)
                                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                                , e -> {
                            Task.syncDelayed(plugin, () -> inv.destroy() , 3L);
                            p.closeInventory();
                        })
                        , 8
                )

                .withTitle(plugin.configM.getLangYml().SHOPS_MANAGER_TITLE)

                .build();

        inv.open(p);

    }

    private void refresh(Player p) {
        inv.destroy();
        open(p);
    }

    private void contentAction(InventoryClickEvent e) {

        ItemStack selected = e.getCurrentItem();
        dShop shop = sManager.getShop(FormatUtils.stripColor(utils.getDisplayName(selected).substring(4))).get();
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
                            utils.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_INTEGER);
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
                    .withTitle(plugin.configM.getLangYml().CONFIRM_GUI_ACTION_NAME)
                    .withConfirmLore(plugin.configM.getLangYml().CONFIRM_GUI_YES, plugin.configM.getLangYml().CONFIRM_GUI_YES_LORE)
                    .withCancelLore(plugin.configM.getLangYml().CONFIRM_GUI_NO, plugin.configM.getLangYml().CONFIRM_GUI_NO_LORE)
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

    static List<Integer> itemSlots = null;
    private void updateTask() {

        Schedulers.builder()
                .sync()
                .afterAndEvery(20)
                .consume(task -> {

                    if (inv.getInvs().stream()
                            .allMatch(invI -> invI.getInventory().getViewers().isEmpty())) {
                        task.close();
                        return;
                    }

                    if (itemSlots == null) {            // Populate slots

                        itemSlots = new ArrayList<>();
                        List<List<Integer>> masks = inv.getPopulator().getMasks();
                        for (int i = 0; i < 6; i++)
                            for (int j = 0; j < 9; j++) {
                                int mask = masks.get(i).get(j);
                                if (mask == 1) continue;
                                itemSlots.add(i * 9 + j);
                            }

                    }

                    loreStrategy stategy = new shopsManagerLore();
                    inv.getInvs().forEach(inventoryGUI -> {
                        itemSlots.forEach(slot -> {
                            ItemStack itemToUpdate = inventoryGUI.getInventory().getItem(slot);
                            if (ItemUtils.isEmpty(itemToUpdate)) return;

                            ItemStack newItem = stategy.applyLore(ItemBuilder.of(itemToUpdate.clone()).setLore(Collections.emptyList()));

                            inventoryGUI.getInventory().setItem(slot, newItem);

                        });
                    });

                });

    }


}