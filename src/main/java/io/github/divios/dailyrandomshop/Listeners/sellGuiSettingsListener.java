package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.sellGuiSettings;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sellGuiSettingsListener implements Listener {

    private final DailyRandomShop main;
    private final String name;
    private final ArrayList<Integer> reservedSlots = new ArrayList<>();

    public sellGuiSettingsListener(DailyRandomShop main) {
        this.main = main;
        name = main.config.SETTINGS_GUI_TITLE;
        reservedSlots.add(45);
        reservedSlots.add(46);
        reservedSlots.add(47);
        reservedSlots.add(48);
        reservedSlots.add(49);
        reservedSlots.add(50);
        reservedSlots.add(51);
        reservedSlots.add(52);
        reservedSlots.add(53);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        if (!e.getView().getTitle().equals(name + ChatColor.BOLD)) {
            return;
        }

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        if(e.getSlot() == e.getRawSlot() && e.getSlot() == 49) {
            p.closeInventory();
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 45 &&
            e.getCurrentItem() != null) {
            p.openInventory(main.settings.processNextGui(e.getView().getTopInventory(), -1));
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 53 &&
                e.getCurrentItem() != null) {
            p.openInventory(main.settings.processNextGui(e.getView().getTopInventory(), 1));
        }

        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR ||
            e.getSlot() != e.getRawSlot() || reservedSlots.contains(e.getSlot())) {
            return;
        }

        ItemStack item = removeLore(e.getCurrentItem().clone());
        if(e.isRightClick()) {
            ItemStack rightItem = item.clone();
            rightItem.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            ItemMeta meta = rightItem.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            rightItem.setItemMeta(meta);
            new AnvilGUI.Builder()
                    .onClose(player -> {                                        //called when the inventory is closing
                        player.openInventory(e.getInventory());
                    })
                    .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                        try {
                            Double price = Double.parseDouble(text);
                            main.listSellItems.replace(item, price);
                            main.dbManager.updateSellItems();
                            main.settings = new sellGuiSettings(main);
                            p.openInventory(main.settings.invs.get(0));
                            return AnvilGUI.Response.close();
                        }catch (Exception err) {
                            return AnvilGUI.Response.text("Error");
                        }

                    })
                    .text("Price")
                    .itemLeft(new ItemStack(item))
                    .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Set price")
                    .plugin(main)
                    .open(p);
        }
        else if (e.isLeftClick()) {
            main.listSellItems.remove(item);
            p.sendMessage(main.config.PREFIX + ChatColor.GRAY + "Removed item successfully");
            main.settings = new sellGuiSettings(main);
            if(main.listSellItems.isEmpty()) p.closeInventory();
            else p.openInventory(main.settings.invs.get(0));
            try {
                main.dbManager.updateSellItems();
            } catch (Exception ignored) { }

        }

    }

    public ItemStack removeLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

}
