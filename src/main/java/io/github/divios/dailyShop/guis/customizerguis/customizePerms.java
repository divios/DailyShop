package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class customizePerms {

    private static final DailyShop plugin = DailyShop.get();

    private final Player p;
    private final dItem item;
    private final Consumer<dItem> back;

    public customizePerms(Player p, dItem item, Consumer<dItem> back) {
        this.p = p;
        this.item = item;
        this.back = back;

        init();
    }

    private void init() {

        InventoryGUI gui = new InventoryGUI(27, "&8Customize perms");

        gui.addButton(                                                  // Perms Buy
                ItemButton.create(
                        item.getPermsBuy().isPresent() ?
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(plugin.configM.getLangYml().CUSTOMIZE_PERMS_NAME_BUY)
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(plugin.configM.getLangYml().CUSTOMIZE_PERMS_LORE_ON)
                                        .addLore("")
                                        .addLore(item.getPermsBuy().get()
                                                .stream().map(s -> FormatUtils.color("&f&l" + s))
                                                .collect(Collectors.toList()))
                                :
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(plugin.configM.getLangYml().CUSTOMIZE_PERMS_NAME_BUY)
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(plugin.configM.getLangYml().CUSTOMIZE_PERMS_LORE)

                        , e -> {

                            if (!item.getPermsBuy().isPresent()) { // Boton de habilitar perms
                                item.setPermsBuy(new ArrayList<>());
                                refresh();

                            } else if (item.getPermsBuy().isPresent()) {  // Boton de añadir/quitar perms
                                if (e.isLeftClick())

                                    ChatPrompt.builder()
                                            .withPlayer(p)
                                            .withResponse(s -> {
                                                if (!s.isEmpty()) {
                                                    List<String> perms = item.getPermsBuy().get();
                                                    perms.add(s);
                                                    item.setPermsBuy(perms);
                                                }
                                                Schedulers.sync().run(this::refresh);
                                            })
                                            .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                            .withTitle("&a&lInput permission")
                                            .prompt();

                                else if (e.isRightClick() && !e.isShiftClick()) {
                                    List<String> s = item.getPermsBuy().get();

                                    if (!s.isEmpty()) {
                                        s.remove(s.size() - 1);
                                        item.setPermsBuy(s);
                                    }
                                    refresh();
                                } else if (e.isShiftClick() && e.isRightClick()) {
                                    item.setPermsBuy(null);
                                    refresh();
                                }

                            }
                        }),
                11);

        gui.addButton(                                                  // Perms Sell
                ItemButton.create(
                        item.getPermsSell().isPresent() ?
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(plugin.configM.getLangYml().CUSTOMIZE_PERMS_NAME_SELL)
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(plugin.configM.getLangYml().CUSTOMIZE_PERMS_LORE_ON)
                                        .addLore("")
                                        .addLore(item.getPermsSell().get()
                                                .stream().map(s -> FormatUtils.color("&f&l" + s))
                                                .collect(Collectors.toList()))
                                :
                                ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                        .setName(plugin.configM.getLangYml().CUSTOMIZE_PERMS_NAME_SELL)
                                        .applyTexture("4e68435e9dd05dbe2e7bb45c5d3c95d0c9d8cb4c062d30e9b4aed1ccfa65a49b")
                                        .addLore(plugin.configM.getLangYml().CUSTOMIZE_PERMS_LORE)

                        , e -> {

                            if (!item.getPermsSell().isPresent()) { // Boton de habilitar perms
                                item.setPermsSell(new ArrayList<>());
                                refresh();

                            } else if (item.getPermsSell().isPresent()) {  // Boton de añadir/quitar perms
                                if (e.isLeftClick())

                                    ChatPrompt.builder()
                                            .withPlayer(p)
                                            .withResponse(s -> {
                                                if (!s.isEmpty()) {
                                                    List<String> perms = item.getPermsSell().get();
                                                    perms.add(s);
                                                    item.setPermsSell(perms);
                                                }
                                                Schedulers.sync().run(this::refresh);
                                            })
                                            .withCancel(cancelReason -> Schedulers.sync().run(this::refresh))
                                            .withTitle("&a&lInput permission")
                                            .prompt();

                                else if (e.isRightClick() && !e.isShiftClick()) {
                                    List<String> s = item.getPermsSell().get();

                                    if (!s.isEmpty()) {
                                        s.remove(s.size() - 1);
                                        item.setPermsSell(s);
                                    }
                                    refresh();
                                } else if (e.isShiftClick() && e.isRightClick()) {
                                    item.setPermsSell(null);
                                    refresh();
                                }

                            }
                        }),
                15);

        gui.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.OAK_DOOR)
                            .setName(plugin.configM.getLangYml().CONFIRM_GUI_RETURN_NAME)
                            .setLore(plugin.configM.getLangYml().CONFIRM_GUI_RETURN_PANE_LORE)
                        , e -> back.accept(item)),
                22);

        gui.open(p);

    }

    private void refresh() {
        new customizePerms(p, item, back);
    }

    public static customizePermsBuilder builder() {
        return new customizePermsBuilder();
    }


    public static final class customizePermsBuilder {
        private Player p;
        private dItem item;
        private Consumer<dItem> back;

        private customizePermsBuilder() {
        }

        public customizePermsBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public customizePermsBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public customizePermsBuilder withBack(Consumer<dItem> back) {
            this.back = back;
            return this;
        }

        public customizePerms open() {
            return new customizePerms(p, item, back);
        }
    }
}
