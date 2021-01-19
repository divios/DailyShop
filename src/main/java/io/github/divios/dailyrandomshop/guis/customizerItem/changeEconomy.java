package io.github.divios.dailyrandomshop.guis.customizerItem;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.utils.economyTypes;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class changeEconomy implements Listener, InventoryHolder {

    private final DailyRandomShop main;
    private final Player p;
    private final ItemStack item;
    private final BiConsumer<ItemStack, Boolean> bi;

    public changeEconomy(DailyRandomShop main, Player p, ItemStack item, BiConsumer<ItemStack, Boolean> bi) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
        this.p = p;
        this.item = item;
        this.bi = bi;

        p.openInventory(getInventory());
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, ChatColor.translateAlternateColorCodes('&', "&c&lChange Currency"));

        ItemStack vaultItem = getVaultItem();
        ArrayList<ItemStack> gemEconomies = getGemsEconomyItems();

        ItemStack returnSign = getReturnSignItem();

        int slot = 1;
        inv.setItem(0, vaultItem);
        for(ItemStack item: gemEconomies) {
            inv.setItem(slot, item);
            slot++;
        }
        inv.setItem(22, returnSign);

        return inv;
    }


    public ItemStack getVaultItem() {
        ItemStack vaultItem = new ItemStack(Material.CHEST);
        ItemMeta meta = vaultItem.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lVault"));

        vaultItem.setItemMeta(meta);
        return vaultItem;
    }

    public ArrayList<ItemStack> getGemsEconomyItems() {
        ArrayList<ItemStack> gemsItems = new ArrayList<>();

        if(main.gemsApi == null ) return gemsItems;

        List<Currency> currencies = main.gemsApi.plugin.getCurrencyManager().getCurrencies();

        if(currencies.isEmpty()) return gemsItems;

        for(Currency c: currencies) {
            ItemStack gemsItem = new ItemStack(Material.EMERALD);
            ItemMeta meta = gemsItem.getItemMeta();

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l" + c.getPlural()));
            gemsItem.setItemMeta(meta);
            gemsItems.add(gemsItem);
        }

        return gemsItems;
    }

    public ItemStack getReturnSignItem() {
        ItemStack returnSign = XMaterial.OAK_SIGN.parseItem();
        ItemMeta meta = returnSign.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lReturn"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to return"));
        meta.setLore(lore);
        returnSign.setItemMeta(meta);

        return returnSign;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);

        if(e.getSlot() != e.getRawSlot()) return;

        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)

        if(e.getSlot() == 0) {
            bi.accept(main.utils.setEconomyType(item, economyTypes.VAULT), true);
        }

        if(e.getCurrentItem().getType() == Material.EMERALD) {

            String currencyName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            bi.accept(main.utils.setEconomyType(item, economyTypes.GEMSECONOMY, currencyName), true);
        }

        if(e.getSlot() == 22) {
            bi.accept(null, false);
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if(e.getView().getTopInventory().getHolder() == this) e.setCancelled(true);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getView().getTopInventory().getHolder() == this) {

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }
    }

}
