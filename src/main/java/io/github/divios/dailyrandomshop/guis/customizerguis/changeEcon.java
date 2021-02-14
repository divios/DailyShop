package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.econTypes;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import me.xanium.gemseconomy.currency.Currency;
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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class changeEcon implements Listener, InventoryHolder {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private Inventory inv = null;
    private ItemStack item;
    private Player p;

    private changeEcon() {}

    public static void openInventory(Player p, ItemStack item) {
        changeEcon instance = new changeEcon();
        instance.p = p;
        instance.item = item.clone();
        p.openInventory(instance.getInventory());
    }

    private void init() {
        Bukkit.getPluginManager().registerEvents(this, main);
        GemsEconomyAPI gemsApi = hooksManager.getInstance().getGemsEcon();
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
        for(String s: gems) {
            ItemStack gemItem = XMaterial.EMERALD.parseItem();
            utils.setDisplayName(gemItem, "&f&l" + s);
            utils.setLore(Vault, Arrays.asList("&7Click to change"));
            inv.addItem(gemItem);
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
            new dailyItem(item)
                .removeNbt(dailyItem.dailyMetadataType.rds_econ).getItem();
        }

        else if (e.getCurrentItem().getType().equals(XMaterial.EMERALD.parseMaterial())) {
            String name = utils.trimString(e.getCurrentItem().getItemMeta().getDisplayName());
            new dailyItem(item)
                    .addNbt(dailyItem.dailyMetadataType.rds_econ,
                            new AbstractMap.SimpleEntry<>(econTypes.gemsEconomy.name(), name)).getItem();
        }
        customizerMainGuiIH.openInventory(p, item);

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