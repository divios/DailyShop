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
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class changeEcon {

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

    public static changeEconBuilder builder() {
        return new changeEconBuilder();
    }

    @Deprecated
    public static void open(Player p, dItem item, Consumer<dItem> consumer) {
        new changeEcon(p, item, consumer);
    }

    private changeEcon(Player p, dItem item, Consumer<dItem> consumer) {
        this.p = p;
        this.item = item;
        this.consumer = consumer;

        InventoryGUI menu = createMenu();
        menu.destroysOnClose();
        menu.open(p);
    }

    private InventoryGUI createMenu() {
        InventoryGUI menu = new InventoryGUI(plugin, 54, plugin.configM.getLangYml().CUSTOMIZE_ECON_NAME);
        createBluePanels(menu);
        createLightBluePanes(menu);
        createReturnButton(menu);
        createVaultButton(menu);
        //createExpButton(menu);
        //createItemButton(menu);
        if (gemsApi != null) createGemsApiButton(menu);
        if (tokenEnchantsApi != null) createTokenEnchantsButton(menu);
        if (tokenManagerApi != null) createTokenManagerButton(menu);
        if (mPointsAPI != null) createMPointsButton(menu);
        if (pPointsApi != null) createPlayerPointsButton(menu);
        if (uEconApi != null) createUltraEconomyButtons(menu);
        return menu;
    }

    private void addButton(InventoryGUI menu, ItemButton itemButtonToAdd) {
        menu.addButton(itemButtonToAdd, inventoryUtils.getFirstEmpty(menu.getInventory()));
    }

    private ItemButton createEconomyButton(XMaterial material, String name, economy econ) {
        return new ItemButton(createEconomyItem(material, name), e -> {
            item.setEconomy(econ);
            consumer.accept(item);
        });
    }

    private ItemStack createEconomyItem(XMaterial material, String name) {
        return ItemBuilder.of(material).setName(name).setLore("&7Click to change");
    }

    private void createExpButton(InventoryGUI menu) {
        addButton(menu, createEconomyButton(XMaterial.EXPERIENCE_BOTTLE, "&f&lExp", new expEconomy()));
    }

    /*private void createItemButton(InventoryGUI menu) {
        addButton(menu, createEconomyButton(XMaterial.DIAMOND_SWORD, "&f&lItem", new itemEconomy(XMaterial.ACACIA_FENCE_GATE.parseItem())));
    } */

    private void createMPointsButton(InventoryGUI menu) {
        for (String s : mPointsAPI.getpointslist()) {
            addButton(menu,
                    ItemButton.create(
                            createEconomyItem(XMaterial.SUNFLOWER, "&f&l" + s),
                            e -> {
                                item.setEconomy(new MPointsE(FormatUtils.stripColor(ItemUtils.getName(e.getCurrentItem()))));
                                consumer.accept(item);
                            }));
        }
    }

    private void createPlayerPointsButton(InventoryGUI menu) {
        addButton(menu, createEconomyButton(XMaterial.PLAYER_HEAD, "&f&lPlayerPoints", new playerPointsE()));
    }

    private void createUltraEconomyButtons(InventoryGUI menu) {
        uEconApi.getCurrencies().forEach(currency ->
                addButton(menu, createEconomyButton(XMaterial.TRIPWIRE_HOOK, "&f&l" + currency.getName(),
                        new ultraEconomyE(currency.getName()))
                )
        );
    }

    private void createTokenManagerButton(InventoryGUI menu) {
        addButton(menu, createEconomyButton(XMaterial.DIAMOND, "&b&lTokenManager", new tokenManagerE()));
    }

    private void createTokenEnchantsButton(InventoryGUI menu) {
        addButton(menu, createEconomyButton(XMaterial.ENCHANTED_BOOK, "&d&lTokenEnchants", new tokenManagerE()));
    }

    private void createGemsApiButton(InventoryGUI menu) {
        for (String s : gemsApi.plugin.getCurrencyManager().getCurrencies().stream()
                .map(Currency::getPlural).collect(Collectors.toList())) {
            addButton(menu,
                    ItemButton.create(
                            createEconomyItem(XMaterial.EMERALD, "&f&l" + s),
                            e -> {
                                item.setEconomy(new gemEcon(FormatUtils.stripColor(ItemUtils.getName(e.getCurrentItem()))));
                                consumer.accept(item);
                            }));
        }
    }

    private void createVaultButton(InventoryGUI menu) {
        addButton(menu, createEconomyButton(XMaterial.CHEST, "&f&lVault", new vault()));
    }

    private void createReturnButton(InventoryGUI menu) {
        menu.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PLAYER_HEAD)
                        .setName("&c&lReturn").applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                , e -> consumer.accept(item)), 8);
    }

    private void createLightBluePanes(InventoryGUI menu) {
        IntStream.of(2, 3, 4, 5, 6, 18, 26, 27, 35, 47, 48, 49, 50, 51)
                .forEach(value ->
                        menu.addButton(ItemButton.create(ItemBuilder.of(
                                XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE).setName("&c"), e -> {
                        }), value));
    }

    private void createBluePanels(InventoryGUI menu) {
        IntStream.of(0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53)
                .forEach(value ->
                        menu.addButton(ItemButton.create(ItemBuilder.of(
                                XMaterial.BLUE_STAINED_GLASS_PANE).setName("&c"), e -> {
                        }), value));
    }

    public static final class changeEconBuilder {
        private dItem item;
        private Player p;
        private Consumer<dItem> consumer;

        private changeEconBuilder() {
        }

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
