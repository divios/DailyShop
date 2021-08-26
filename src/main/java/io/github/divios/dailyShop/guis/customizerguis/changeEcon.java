package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.economies.*;
import io.github.divios.dailyShop.hooks.hooksManager;
import io.github.divios.lib.dLib.dItem;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;
import me.realized.tokenmanager.api.TokenManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;
import me.yic.mpoints.MPointsAPI;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class changeEcon{

    private static final DailyShop plugin = DailyShop.getInstance();

    private static final GemsEconomyAPI gemsApi = hooksManager.getInstance().getGemsEcon();
    private static final TokenEnchantAPI tokenEnchantsApi = hooksManager.getInstance().getTokenEnchantApi();
    private static final TokenManager tokenManagerApi = hooksManager.getInstance().getTokenManagerApi();
    private static final MPointsAPI mPointsAPI = hooksManager.getInstance().getMPointsApi();
    private static final PlayerPointsAPI pPointsApi = hooksManager.getInstance().getPlayerPointsApi();
    private static final UltraEconomyAPI uEconApi = hooksManager.getInstance().getUltraEconomyApi();

    private final dItem item;
    private final Player p;
    private final Consumer<dItem> consumer;

    private changeEcon(Player p, dItem item, Consumer<dItem> consumer) {
        this.p = p;
        this.item = item;
        this.consumer = consumer;

        init();
    }

    @Deprecated
    public static void open(Player p, dItem item, Consumer<dItem> consumer) {
        new changeEcon(p, item, consumer);
    }

    private void init() {


        InventoryGUI gui = new InventoryGUI(plugin, 54, plugin.configM.getLangYml().CUSTOMIZE_ECON_NAME);

        IntStream.of(0,1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53)
                .forEach(value ->
                        gui.addButton(ItemButton.create(ItemBuilder.of(
                                XMaterial.BLUE_STAINED_GLASS_PANE).setName("&c"), e -> {}), value));

        IntStream.of(2, 3, 4, 5, 6, 18, 26, 27, 35, 47, 48, 49, 50, 51)
                .forEach(value ->
                        gui.addButton(ItemButton.create(ItemBuilder.of(
                                XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE).setName("&c"), e -> {}), value));


        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                .setName("&c&lReturn").applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                , e -> consumer.accept(item)), 8);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.CHEST)
                .setName("&f&lVault").addLore("&7Click to change"), e -> {

                item.setEconomy(new vault());
                consumer.accept(item);
        }), inventoryUtils.getFirstEmpty(gui.getInventory()));


        if (gemsApi != null)
            for (String s : gemsApi.plugin.getCurrencyManager().getCurrencies().stream()
                    .map(Currency::getPlural).collect(Collectors.toList())) {

                gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.EMERALD)
                        .setName("&f&l" + s).addLore("&7Click to change"), e -> {
                    item.setEconomy(new gemEcon(FormatUtils.stripColor(ItemUtils.getName(e.getCurrentItem()))));
                    consumer.accept(item);
                }), inventoryUtils.getFirstEmpty(gui.getInventory()));
            }

        if (tokenEnchantsApi != null) {
            gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.ENCHANTED_BOOK)
                    .setName("&d&lTokenEnchants").addLore("&7Click to change"), e -> {
                item.setEconomy(new tokenEnchantsE());
                consumer.accept(item);
            }), inventoryUtils.getFirstEmpty(gui.getInventory()));
        }


        if (tokenManagerApi != null) {
            gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.DIAMOND)
                    .setName("&b&lTokenManager").addLore("&7Click to change"), e -> {
                item.setEconomy(new tokenManagerE());
                consumer.accept(item);
            }), inventoryUtils.getFirstEmpty(gui.getInventory()));
        }

        if (mPointsAPI != null) {
            for (String s : mPointsAPI.getpointslist()) {
                gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.SUNFLOWER)
                        .setName("&f&l" + s).addLore("&7Click to change"), e -> {
                    item.setEconomy(new MPointsE(FormatUtils.stripColor(ItemUtils.getName(e.getCurrentItem()))));
                    consumer.accept(item);
                }), inventoryUtils.getFirstEmpty(gui.getInventory()));
            }
        }

        if (pPointsApi != null) {
            gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                    .setName("&f&lPlayerPoints").addLore("&7Click to change"), e -> {
                item.setEconomy(new playerPointsE());
                consumer.accept(item);
            }), inventoryUtils.getFirstEmpty(gui.getInventory()));
        }

        if (uEconApi != null) {
            uEconApi.getCurrencies().forEach(currency -> {
                gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.TRIPWIRE_HOOK)
                        .setName("&f&l" + currency.getName()).addLore("&7Click to change"), e -> {
                    item.setEconomy(new ultraEconomyE(currency.getName()));
                    consumer.accept(item);
                }), inventoryUtils.getFirstEmpty(gui.getInventory()));
            });
        }

        gui.destroysOnClose();
        gui.open(p);

    }

    public static changeEconBuilder builder() {
        return new changeEconBuilder();
    }

    public static final class changeEconBuilder {
        private dItem item;
        private Player p;
        private Consumer<dItem> consumer;

        private changeEconBuilder() {}

        public changeEconBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public changeEconBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public changeEconBuilder withConsumer(Consumer<dItem> consumer) {
            this.consumer = consumer;
            return this;
        }

        public changeEcon prompt() {
            return new changeEcon(p, item, consumer);
        }
    }
}
