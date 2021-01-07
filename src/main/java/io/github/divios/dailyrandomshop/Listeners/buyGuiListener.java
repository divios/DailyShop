package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import io.github.divios.dailyrandomshop.GUIs.sellGuiIH;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class buyGuiListener implements Listener {

    private DailyRandomShop main;
    private Inventory inv;

    public buyGuiListener(DailyRandomShop main) {
        this.main = main;
        inv = main.BuyGui.getGui();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!(e.getView().getTitle().equals(main.config.BUY_GUI_TITLE + ChatColor.GOLD))) return;

        e.setCancelled(true);

        if (e.getSlot() == 8 &&
                e.getRawSlot() == e.getSlot() && main.getConfig().getBoolean("enable-sell-gui")) {

            if (!p.hasPermission("DailyRandomShop.sell")) {
                try {
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                } catch (NoSuchFieldError ignored) {
                }
                p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_PERMS);
                return;
            }

            try {
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
            } catch (NoSuchFieldError ignored) {}

            new sellGuiIH(main, p);
            return;
        }

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        if (!main.utils.isDailyItem(e.getCurrentItem())) return;

        ItemStack item = e.getView().getTopInventory().getItem(e.getSlot()).clone();
        //item.setAmount(1);

        Double priceaux = main.utils.getItemPrice(main.listDailyItems, item, true);

        if(priceaux <= 0) {
            p.sendMessage(main.config.PREFIX + ChatColor.GRAY + "That item is not in stock anymore, an admin must have take it away");
            p.closeInventory();
            return;
        }

        if (main.utils.isCommandItem(item)) {

            Double price = main.utils.getItemPrice(main.listDailyItems, item, true);

            if (main.econ.getBalance(p) < price) {
                p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_ENOUGH_MONEY);
                try {
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                } catch (NoSuchFieldError ignored) {}
                finally { return; }
            }

            for (String s : main.utils.getItemCommand(item)) {
                main.getServer().dispatchCommand(main.getServer().getConsoleSender(), s.replaceAll("%player%", p.getName()));
            }
            p.closeInventory();
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
            } catch (NoSuchFieldError ignored) {
            }
            main.econ.withdrawPlayer(p, price);
            if (main.utils.isItemAmount(item)) main.utils.processItemAmount(item, e.getSlot());
            return;
        }

        if (main.getConfig().getBoolean("enable-confirm-gui") && !main.utils.isItemAmount(item)) {
            try {
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
            } catch (NoSuchFieldError ignored) {
            }

            new confirmGui(main, item, p, (aBoolean, itemStack) -> {

                if (aBoolean) {
                   Double price = main.utils.getItemPrice(main.listDailyItems, itemStack, true) * itemStack.getAmount();

                    if(main.utils.isItemScracth(itemStack)) {
                        int amount = itemStack.getAmount();
                        String constructor[] = main.utils.getMMOItemConstruct(itemStack);
                        itemStack = MMOItems.plugin.getItem(Type.get(constructor[0]), constructor[1]);
                        itemStack.setAmount(amount);
                    }
                    main.utils.giveItem(p, price, e.getView().getBottomInventory(), itemStack);
                    p.closeInventory();

                } else{
                    p.openInventory(main.BuyGui.getGui());
                    try {
                        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
                    } catch (NoSuchFieldError ignored) {}
                }

            }, main.config.CONFIRM_GUI_NAME, true);



        } else {

            Double price = main.utils.getItemPrice(main.listDailyItems, item, true);

            if(price <= 0) {
                p.sendMessage(main.config.PREFIX + ChatColor.GRAY + "That item is not in stock anymore, an admin must have take it away");
                p.closeInventory();
                return;
            }

            if (main.utils.isItemAmount(item) && main.econ.getBalance(p) >= price &&
                !main.utils.inventoryFull(p)) {
                main.utils.processItemAmount(e.getView().getTopInventory().getItem(e.getSlot()), e.getSlot());
            }
            item.setAmount(1);

            if(main.utils.isItemScracth(item)) {
                String constructor[] = main.utils.getMMOItemConstruct(item);
                item = MMOItems.plugin.getItem(Type.get(constructor[0]), constructor[1]);
            }

            main.utils.giveItem(p, price, e.getView().getBottomInventory(), item);

        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);

        }
    }


}
