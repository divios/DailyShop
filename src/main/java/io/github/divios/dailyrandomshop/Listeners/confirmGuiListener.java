package io.github.divios.dailyrandomshop.Listeners;

import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class confirmGuiListener implements Listener {

    private final DailyRandomShop main;
    private final List<Integer> interactSlots = new ArrayList<>();
    int[] aux = {1, 5, 10, 1, 5, 10};

    public confirmGuiListener(DailyRandomShop main) {
        this.main = main;
        interactSlots.add(9);
        interactSlots.add(10);
        interactSlots.add(11);
        interactSlots.add(15);
        interactSlots.add(16);
        interactSlots.add(17);

        interactSlots.add(18);
        interactSlots.add(22);

    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {


        if (!(e.getView().getTitle().equals(main.config.CONFIRM_GUI_NAME))) {
            return;
        }

        e.setCancelled(true);

        if (e.getRawSlot() != e.getSlot() || !(interactSlots.contains(e.getSlot()))) {
            return;
        }

        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == 18) {
            p.openInventory(main.BuyGui.getGui());
            p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
            return;
        }

        ItemStack item = e.getView().getTopInventory().getItem(13);

        if (e.getSlot() == 22) {
            Double price = main.listMaterials.get(item.getType().toString())[0] * item.getAmount();

            main.utils.giveItem(p, price, e.getView().getBottomInventory(), item);
            return;
            /*if (main.utils.inventoryFull(e.getView().getBottomInventory().getContents())) {
                p.sendMessage(main.config.PREFIX + main.config.MSG_INVENTORY_FULL);
                return;
            }

            if (main.econ.getBalance(p) < price) {
                p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_ENOUGHT_MONEY);
                return;
            }
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(lore.size() - 1);
            meta.setLore(lore);
            item.setItemMeta(meta);


            p.getInventory().addItem(item);
            main.econ.withdrawPlayer(p, price);
            p.sendMessage(main.config.PREFIX + main.config.MSG_BUY_ITEM.replace("{price}", "" + price).replace("{item}", item.getType().toString()));
            p.openInventory(main.BuyGui.getGui());
            return; */
        }

        if (e.getView().getTopInventory().getItem(e.getSlot()) == null) return;


        if (interactSlots.indexOf(e.getSlot()) > 2)
            item.setAmount(item.getAmount() + aux[interactSlots.indexOf(e.getSlot())]);
        else item.setAmount(item.getAmount() - aux[interactSlots.indexOf(e.getSlot())]);

        e.getView().getTopInventory().setItem(13, item);
        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
        main.ConfirmGui.updateGui(e.getView().getTopInventory());
        p.updateInventory();


    }

}
