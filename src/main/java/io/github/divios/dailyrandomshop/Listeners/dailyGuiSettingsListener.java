package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.customizerItem.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.GUIs.settings.addDailyItemGuiIH;
import io.github.divios.dailyrandomshop.GUIs.settings.confirmIH;
import io.github.divios.dailyrandomshop.GUIs.settings.dailyGuiSettings;
import io.github.divios.dailyrandomshop.GUIs.settings.settingsGuiIH;
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

import java.util.ArrayList;
import java.util.List;

public class dailyGuiSettingsListener implements Listener {

    private final DailyRandomShop main;
    private final ArrayList<Integer> reservedSlots = new ArrayList<>();

    public dailyGuiSettingsListener(DailyRandomShop main) {
        this.main = main;
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

        if (!e.getView().getTitle().equals(main.config.DAILY_SETTINGS_TITLE + ChatColor.AQUA)) {
            return;
        }

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 49) {
            new settingsGuiIH(main, p);
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 45 &&
                e.getCurrentItem() != null) {
            p.openInventory(main.DailyGuiSettings.processNextGui(e.getView().getTopInventory(), -1));
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 53 &&
                e.getCurrentItem() != null) {
            p.openInventory(main.DailyGuiSettings.processNextGui(e.getView().getTopInventory(), 1));
        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 52 &&
                e.getCurrentItem() != null) {
            new addDailyItemGuiIH(main, p, e.getView().getTopInventory());
        }

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR ||
                e.getSlot() != e.getRawSlot() || reservedSlots.contains(e.getSlot())) {
            return;
        }

        ItemStack item = removeLore(e.getCurrentItem().clone());
        if (e.isLeftClick() && !e.isShiftClick()) {
            ItemStack rightItem = item.clone();
            rightItem.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            ItemMeta meta = rightItem.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            rightItem.setItemMeta(meta);
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.closeInventory();
                    })
                    .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                        try {
                            Double price = Double.parseDouble(text);
                            main.listDailyItems.replace(item, price);
                            main.dbManager.updateDailyItems();
                            main.DailyGuiSettings = new dailyGuiSettings(main);
                            p.openInventory(main.DailyGuiSettings.getFirstGui());
                            return AnvilGUI.Response.close();
                        } catch (Exception err) {
                            return AnvilGUI.Response.text("Error");
                        }

                    })
                    .text("Price")
                    .itemLeft(new ItemStack(item))
                    .title(ChatColor.GOLD + "" + ChatColor.BOLD + "Set price")
                    .plugin(main)
                    .open(p);

        } else if (e.isRightClick()) {

            new confirmIH(p,  (p1, bool) -> {

                if (bool) {
                    main.listDailyItems.remove(item);
                    p1.sendMessage(main.config.PREFIX + ChatColor.GRAY + "Removed item successfully");
                    main.DailyGuiSettings = new dailyGuiSettings(main);
                    main.dbManager.updateDailyItems();
                }
                p1.openInventory(main.DailyGuiSettings.getFirstGui());

            }, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Confirm", main);

        } else if (e.isLeftClick() && e.isShiftClick()) {
            new customizerMainGuiIH(main, (Player) e.getWhoClicked(), removeLore(e.getCurrentItem().clone()), removeLore(e.getCurrentItem().clone()));
        }
    }

    public ItemStack removeLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

}