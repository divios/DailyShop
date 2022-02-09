package io.github.divios.lib.dLib.registry.util;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class RecordBookOptionsGui {

    private final Player p;
    private final RecordBookOptions options;
    private final Consumer<RecordBookOptions> fallback;

    private RecordBookOptionsGui(Player p, RecordBookOptions options, Consumer<RecordBookOptions> fallback) {
        this.p = p;
        this.options = options;
        this.fallback = fallback;

        createInv();
    }

    private void createInv() {
        InventoryGUI inv = new InventoryGUI(36, "&8Filters");

        inv.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName("&ePlayer to Filter")
                                .addLore("&7Player: &e" + (options.getfPlayer() == null ? "undefined" : options.getfPlayer()))
                                .addLore("&7Right click to delete")
                        , e -> {

                            if (e.isLeftClick())
                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withSubtitle("&eInput player name to filter")
                                        .withResponse(s -> new RecordBookOptionsGui(p, options.setfPlayer(s), fallback))
                                        .withCancel(cancelReason -> new RecordBookOptionsGui(p, options, fallback))
                                        .prompt();

                            if (e.isRightClick())
                                new RecordBookOptionsGui(p, options.setfPlayer(null), fallback);
                        }

                ), 11
        );

        inv.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.PLAYER_HEAD)
                                .setName("&aShop to filter")
                                .addLore("&7Shop: &a" + (options.getfShopId() == null ? "undefined" : options.getfShopId()))
                                .addLore("&7Right click to delete")
                                .applyTexture("9b425aa3d94618a87dac9c94f377af6ca4984c07579674fad917f602b7bf235")
                        , e -> {

                            if (e.isLeftClick())
                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withSubtitle("&eInput shop name to filter")
                                        .withResponse(s -> new RecordBookOptionsGui(p, options.setfShopId(s), fallback))
                                        .withCancel(cancelReason -> new RecordBookOptionsGui(p, options, fallback))
                                        .prompt();

                            if (e.isRightClick())
                                new RecordBookOptionsGui(p, options.setfShopId(null), fallback);

                        }

                ), 13
        );

        inv.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.REDSTONE_TORCH)
                                .setName("&cChange Type")
                                .addLore("&7Type: &c" + options.getfType())
                                .addLore("&7Right click to delete")
                        , e -> new RecordBookOptionsGui(p, options.setfType(nextType()), fallback)

                ), 15
        );

        /*
        inv.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.SUNFLOWER)
                                .setName("&eChange price filter")
                        , e -> {
                        }
                ), 16
        );

        inv.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.CLOCK)
                                .setName("&eChange time filter")
                        , e -> {
                        }
                ), 31
        ); */

        inv.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.OAK_DOOR)
                                .setName("&eGenerate report")
                        , e -> fallback.accept(options)
                ), 31
        );

        inv.open(p);
    }

    private Transactions.Type nextType() {
        if (options.getfType() == Transactions.Type.BUY)
            return Transactions.Type.SELL;
        else if (options.getfType() == Transactions.Type.SELL)
            return null;
        else return Transactions.Type.BUY;
    }

    public static LogOptionsGuiBuilder builder() {
        return new LogOptionsGuiBuilder();
    }

    public static final class LogOptionsGuiBuilder {
        private Player p;
        private RecordBookOptions options;
        private Consumer<RecordBookOptions> fallback;

        private LogOptionsGuiBuilder() {
        }

        public LogOptionsGuiBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public LogOptionsGuiBuilder withOptions(RecordBookOptions options) {
            this.options = options;
            return this;
        }

        public LogOptionsGuiBuilder withFallback(Consumer<RecordBookOptions> fallback) {
            this.fallback = fallback;
            return this;
        }

        public RecordBookOptionsGui build() {
            return new RecordBookOptionsGui(p, options, fallback);
        }
    }
}
