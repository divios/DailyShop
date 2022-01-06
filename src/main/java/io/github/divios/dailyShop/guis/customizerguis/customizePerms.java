package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.files.Lang;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class customizePerms {

    private final Player p;
    private LinkedList<String> buyPerms;
    private LinkedList<String> sellPerms;
    private final BiConsumer<List<String>, List<String>> back;

    public customizePerms(Player p,
                          @Nullable List<String> buyPerms,
                          @Nullable List<String> sellPerms,
                          BiConsumer<List<String>, List<String>> back) {
        this.p = p;
        this.buyPerms = buyPerms == null ? null : new LinkedList<>(buyPerms);
        this.sellPerms = sellPerms == null ? null : new LinkedList<>(sellPerms);
        this.back = back;

        init();
    }

    private void init() {

        InventoryGUI gui = new InventoryGUI(27, "&8Customize perms");

        gui.addButton(                                                  // Perms Buy
                ItemButton.create(
                        buyPerms != null ?
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(Lang.CUSTOMIZE_PERMS_NAME_BUY.getAsString(p))
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(Lang.CUSTOMIZE_PERMS_LORE_ON.getAsListString(p))
                                        .addLore("")
                                        .addLore(buyPerms.stream()
                                                .map(s -> FormatUtils.color("&f&l" + s))
                                                .collect(Collectors.toList()))
                                :
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(Lang.CUSTOMIZE_PERMS_NAME_BUY.getAsString(p))
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(Lang.CUSTOMIZE_PERMS_LORE.getAsListString(p))

                        , e -> {

                            if (buyPerms == null) { // Boton de habilitar perms
                                buyPerms = new LinkedList<>();
                                refresh();

                            } else {
                                if (e.isLeftClick())

                                    ChatPrompt.builder()
                                            .withPlayer(p)
                                            .withResponse(s -> {
                                                if (!s.isEmpty()) {
                                                    buyPerms.add(s);
                                                }
                                                Schedulers.sync().run(this::refresh);
                                            })
                                            .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                            .withTitle("&a&lInput permission")
                                            .prompt();

                                else if (e.isRightClick() && !e.isShiftClick()) {
                                    buyPerms.pollLast();
                                    refresh();
                                } else if (e.isShiftClick() && e.isRightClick()) {
                                    buyPerms = null;
                                    refresh();
                                }

                            }
                        }),
                11);

        gui.addButton(                                                  // Perms Sell
                ItemButton.create(
                        sellPerms != null ?
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(Lang.CUSTOMIZE_PERMS_NAME_SELL.getAsString(p))
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(Lang.CUSTOMIZE_PERMS_LORE_ON.getAsListString(p))
                                        .addLore("")
                                        .addLore(sellPerms.stream()
                                                .map(s -> FormatUtils.color("&f&l" + s))
                                                .collect(Collectors.toList()))
                                :
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(Lang.CUSTOMIZE_PERMS_NAME_SELL.getAsString(p))
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(Lang.CUSTOMIZE_PERMS_LORE.getAsString(p))

                        , e -> {

                            if (sellPerms == null) { // Boton de habilitar perms
                                sellPerms = new LinkedList<>();
                                refresh();

                            } else {  // Boton de añadir/quitar perms
                                if (e.isLeftClick())

                                    ChatPrompt.builder()
                                            .withPlayer(p)
                                            .withResponse(s -> {
                                                if (!s.isEmpty()) {
                                                    sellPerms.add(s);
                                                }
                                                Schedulers.sync().run(this::refresh);
                                            })
                                            .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                            .withTitle("&a&lInput permission")
                                            .prompt();

                                else if (e.isRightClick() && !e.isShiftClick()) {
                                    sellPerms.pollLast();
                                    refresh();
                                } else if (e.isShiftClick() && e.isRightClick()) {
                                    sellPerms = null;
                                    refresh();
                                }

                            }
                        }),
                15);

        gui.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.OAK_DOOR)
                                .setName(Lang.CONFIRM_GUI_RETURN_NAME.getAsString(p))
                                .setLore(Lang.CONFIRM_GUI_RETURN_PANE_LORE.getAsListString(p))
                        , e -> back.accept(buyPerms, sellPerms)),
                22);

        gui.open(p);

    }

    private void refresh() {
        init();
    }

    public static customizePermsBuilder builder() {
        return new customizePermsBuilder();
    }


    public static final class customizePermsBuilder {
        private Player p;
        private List<String> buyPerms;
        private List<String> sellPerms;
        private BiConsumer<List<String>, List<String>> back;

        private customizePermsBuilder() {
        }

        public customizePermsBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public customizePermsBuilder withBuyPerms(@Nullable List<String> buyPerms) {
            this.buyPerms = buyPerms;
            return this;
        }

        public customizePermsBuilder withSellPerms(@Nullable List<String> sellPerms) {
            this.sellPerms = sellPerms;
            return this;
        }

        public customizePermsBuilder withBack(BiConsumer<List<String>, List<String>> back) {
            this.back = back;
            return this;
        }

        public customizePerms open() {
            return new customizePerms(p, buyPerms, sellPerms, back);
        }
    }
}
