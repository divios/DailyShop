package io.github.divios.dailyrandomshop.guis.customizerguis;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.*;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
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

public class changeEcon implements Listener, InventoryHolder {

    private static final DRShop plugin = DRShop.getInstance();
    private Inventory inv = null;
    private final dItem item;
    private final Player p;
    private final Consumer<dItem> consumer;

    private changeEcon(Player p, dItem item, Consumer<dItem> consumer) {
        this.p = p;
        this.item = item;
        this.consumer = consumer;
    }

    public static void openInventory(Player p, dItem item, Consumer<dItem> consumer) {
        changeEcon instance = new changeEcon(p, item, consumer);
        p.openInventory(instance.getInventory());
    }

    private void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        GemsEconomyAPI gemsApi = hooksManager.getInstance().getGemsEcon();
        TokenEnchantAPI tokenEnchantsApi = hooksManager.getInstance().getTokenEnchantApi();
        TokenManager tokenManagerApi = hooksManager.getInstance().getTokenManagerApi();
        MPointsAPI mPointsAPI = hooksManager.getInstance().getMPointsApi();
        PlayerPointsAPI pPointsApi = hooksManager.getInstance().getPlayerPointsApi();

        inv = Bukkit.createInventory(this, 27, conf_msg.CUSTOMIZE_CHANGE_ECON);

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();
        utils.setDisplayName(returnItem, "&c&lReturn");

        ItemStack Vault = XMaterial.CHEST.parseItem();
        utils.setDisplayName(Vault, "&f&lVault");
        utils.setLore(Vault, Arrays.asList("&7Click to change"));

        List<String> gems = new ArrayList<>();
        if (gemsApi != null) {
            gems = gemsApi.plugin.getCurrencyManager().getCurrencies().stream()
                    .map(Currency::getPlural).collect(Collectors.toList());
        }

        inv.setItem(22, returnItem);
        inv.addItem(Vault);
        for (String s: gems) {
            ItemStack gemItem = XMaterial.EMERALD.parseItem();
            utils.setDisplayName(gemItem, "&f&l" + s);
            utils.setLore(Vault, Arrays.asList("&7Click to change"));
            inv.addItem(gemItem);
        }

        if (tokenEnchantsApi != null) {
            ItemStack tokenEnchantsItem = XMaterial.ENCHANTED_BOOK.parseItem();
            utils.setDisplayName(tokenEnchantsItem, "&d&lTokenEnchants");
            utils.setLore(tokenEnchantsItem, Arrays.asList("&7Click to change"));
            inv.addItem(tokenEnchantsItem);
        }

        if (tokenManagerApi != null) {
            ItemStack tokenManagerItem = XMaterial.DIAMOND.parseItem();
            utils.setDisplayName(tokenManagerItem, "&b&lTokenManager");
            utils.setLore(tokenManagerItem, Arrays.asList("&7Click to change"));
            inv.addItem(tokenManagerItem);
        }

        Set<String> points = new HashSet<>();
        if (mPointsAPI != null) {
            points = mPointsAPI.getpointslist();
        }

        points.forEach(s -> {
            ItemStack pointsItem = XMaterial.SUNFLOWER.parseItem();
            utils.setDisplayName(pointsItem, "&f&l" + s);
            utils.setLore(Vault, Arrays.asList("&7Click to change"));
            inv.addItem(pointsItem);
        });

        if (pPointsApi != null) {
            ItemStack playerPointsItem = XMaterial.PLAYER_HEAD.parseItem();
            utils.setDisplayName(playerPointsItem, "&f&lPlayerPoints");
            utils.setLore(playerPointsItem, Arrays.asList("&7Click to change"));
            inv.addItem(playerPointsItem);
        }

    }

    @Override
    public Inventory getInventory() {
        init();
        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);

        if (utils.isEmpty(e.getCurrentItem())) return;
        if (e.getSlot() != e.getRawSlot()) return;

        if (e.getCurrentItem().getType().equals(XMaterial.CHEST.parseMaterial())) {
            item.setEconomy(new vault());
        }

        else if (e.getCurrentItem().getType().equals(XMaterial.EMERALD.parseMaterial())) {
            String name = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName());
            item.setEconomy(new gemEcon(name));
        }

        else if (e.getCurrentItem().getType().equals(XMaterial.ENCHANTED_BOOK.parseMaterial())) {
            item.setEconomy(new tokenManagerE());
        }

        else if (e.getCurrentItem().getType().equals(XMaterial.DIAMOND.parseMaterial())) {
           item.setEconomy(new tokenManagerE());
        }

        else if (e.getCurrentItem().getType().equals(XMaterial.SUNFLOWER.parseMaterial())) {
            String name = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName());
            item.setEconomy(new MPointsE(name));
        }

        else if(e.getCurrentItem().getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
            item.setEconomy(new playerPointsE());
        }

        consumer.accept(item);

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        InventoryDragEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

}
