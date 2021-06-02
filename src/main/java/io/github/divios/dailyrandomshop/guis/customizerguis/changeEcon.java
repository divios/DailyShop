package io.github.divios.dailyrandomshop.guis.customizerguis;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.*;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
import io.github.divios.dailyrandomshop.redLib.inventorygui.InventoryGUI;
import io.github.divios.dailyrandomshop.redLib.inventorygui.ItemButton;
import io.github.divios.dailyrandomshop.redLib.itemutils.ItemBuilder;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dItem;
import me.realized.tokenmanager.api.TokenManager;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;
import me.yic.mpoints.MPointsAPI;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class changeEcon{

    private static final DRShop plugin = DRShop.getInstance();

    private static final GemsEconomyAPI gemsApi = hooksManager.getInstance().getGemsEcon();
    private static final TokenEnchantAPI tokenEnchantsApi = hooksManager.getInstance().getTokenEnchantApi();
    private static final TokenManager tokenManagerApi = hooksManager.getInstance().getTokenManagerApi();
    private static final MPointsAPI mPointsAPI = hooksManager.getInstance().getMPointsApi();
    private static final PlayerPointsAPI pPointsApi = hooksManager.getInstance().getPlayerPointsApi();

    private final dItem item;
    private final Player p;
    private final Consumer<dItem> consumer;

    private changeEcon(Player p, dItem item, Consumer<dItem> consumer) {
        this.p = p;
        this.item = item;
        this.consumer = consumer;
    }

    public static void open(Player p, dItem item, Consumer<dItem> consumer) {
        changeEcon instance = new changeEcon(p, item, consumer);
        instance.init();
    }

    private void init() {


        InventoryGUI gui = new InventoryGUI(27, conf_msg.CUSTOMIZE_CHANGE_ECON);

        int slot = 0;

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.OAK_SIGN)
                .setName("&c&lReturn"), e -> consumer.accept(item)), 22);
        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.CHEST)
                .setName("&f&lVault").addLore("&7Click to change"), e -> consumer.accept(item)), slot);
        slot++;

        if (gemsApi != null)
            for (String s : gemsApi.plugin.getCurrencyManager().getCurrencies().stream()
                    .map(Currency::getPlural).collect(Collectors.toList())) {
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.EMERALD)
                        .setName("&f&l" + s).addLore("&7Click to change"), e -> consumer.accept(item)), slot);
                slot++;
            }

        if (tokenEnchantsApi != null) {
            gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.ENCHANTED_BOOK)
                    .setName("&d&lTokenEnchants").addLore("&7Click to change"), e -> consumer.accept(item)), slot);
            slot++;
        }


        if (tokenManagerApi != null) {
            gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.DIAMOND)
                    .setName("&b&lTokenManager").addLore("&7Click to change"), e -> consumer.accept(item)), slot);
            slot++;
        }

        if (mPointsAPI != null) {
            for (String s : mPointsAPI.getpointslist()) {
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SUNFLOWER)
                        .setName("&f&l" + s).addLore("&7Click to change"), e -> consumer.accept(item)), slot);
                slot++;
            }
        }

        if (pPointsApi != null) {
            gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
                    .setName("&f&lPlayerPoints").addLore("&7Click to change"), e -> consumer.accept(item)), slot);
            slot++;
        }

        gui.destroysOnClose();
        gui.preventPlayerInvSlots();
        gui.open(p);

    }

}
