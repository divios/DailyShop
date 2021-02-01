package io.github.divios.dailyrandomshop.guis;

import io.github.divios.dailyrandomshop.builders.itemsFactory;
import io.github.divios.dailyrandomshop.builders.lorestategy.currentItemsLore;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.events.expiredTimerEvent;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class buyGui implements Listener, InventoryHolder {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private static buyGui instance = null;
    private static Inventory inv = null;

    private buyGui() { }

    public static buyGui getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public static void openInventory(Player p) {
        if (instance == null) {
            init();
        }
        p.openInventory(instance.getInventory());
    }

    private static void init() {
        instance = new buyGui();
        instance.createInventory();
        Bukkit.getPluginManager().registerEvents(instance, main);
    }

    private void createInventory() {
        double dailyRows = conf_msg.N_DAILY_ITEMS / 9F;
        int rows = (int) Math.ceil(dailyRows + 2);
        if (rows <= 2) rows = 3;

        inv = Bukkit.createInventory(this, (rows * 9), conf_msg.BUY_GUI_TITLE);

        ItemStack painting = XMaterial.PAINTING.parseItem();
        utils.setDisplayName(painting, conf_msg.BUY_GUI_PAINTING_NAME);
        utils.setLore(painting, conf_msg.BUY_GUI_PAINTING_LORE);
        inv.setItem(4, painting);

        if (main.getConfig().getBoolean("enable-sell-gui")) {
            ItemStack fence = XMaterial.OAK_FENCE_GATE.parseItem();
            utils.setDisplayName(fence, conf_msg.BUY_GUI_ARROW_NAME);
            utils.setLore(fence, conf_msg.BUY_GUI_ARROW_LORE);
            inv.setItem(8, fence);
        }

        ItemStack grayPanel = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        utils.setDisplayName(grayPanel, "&7");
        for (int i = 9; i < 18; i++) {     /* Gray panels */
            inv.setItem(i, grayPanel);
        }
        updateCurrentItems();
    }

    private void createRandomItems() {
        clearDailySlots();
        dbManager.currentItems.clear();
        Map<ItemStack, Double> listOfMaterials = dbManager.listDailyItems;
        ArrayList<Integer> inserted = new ArrayList<>();

        int j = 18;
        while (true) {

            if (inv.firstEmpty() == -1) break;

            if (j >= (18 + conf_msg.N_DAILY_ITEMS)) {
                break;
            }

            if (listOfMaterials.size() == inserted.size()) {
                break;              //make sure to break infinite loop if happens
            }

            int ran = utils.randomValue(0, listOfMaterials.size() - 1);

            if (!inserted.isEmpty() && inserted.contains(ran)) {
                continue;
            }

            ItemStack randomItem = new itemsFactory.Builder(utils.getEntry(listOfMaterials, ran), true)
                    .setLoreStrategy(new currentItemsLore(), itemsFactory.strategy.add)
                    .getItem();

            /*if(Math.random() > main.utils.getRarity(randomItem)/100F
                    && main.getConfig().getBoolean("enable-rarity")) continue; */

            inserted.add(ran);

            inv.setItem(j, randomItem);
            dbManager.currentItems.add(new itemsFactory.Builder(randomItem).getUUID());
            j++;
        }
    }

    /**
     * Update the gui with the currentItem
     * on database
     */

    private void updateCurrentItems() {
        clearDailySlots();
        int j = 18;
        for (String uuid : dbManager.currentItems) {
            if(j >= (18 + conf_msg.N_DAILY_ITEMS -1)) break;
            if (j == inv.getSize()) return;

            ItemStack item = utils.getItemByUuid(uuid, dbManager.listDailyItems);
            if(utils.isEmpty(item)) continue;

            inv.setItem(j, new itemsFactory.Builder(item, true)
                    .setLoreStrategy(new currentItemsLore(), itemsFactory.strategy.add).getItem());
            j++;
        }
    }

    public static void updateItem(String uuid, updateAction a) {
        utils.async(() -> {
            for (int i = 18; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item == null) continue;
                ItemStack newItem;
                if (new itemsFactory.Builder(item).getUUID().equals(uuid)) {
                    if (a == updateAction.update)
                        newItem =
                                new itemsFactory.Builder(utils.getItemByUuid(uuid, dbManager.listDailyItems), true)
                                        .setLoreStrategy(new currentItemsLore(), itemsFactory.strategy.add).getItem();
                    else {
                        newItem = utils.getRedPane();
                    }
                    utils.translateAllItemData(newItem, item);
                    return;
                }
            }
        });
    }

    private void clearDailySlots() {
        for (int i = 18; i < inv.getSize(); i++) {
            inv.setItem(i, null);
        }
    }

    public static void reload() {
        if(instance == null) return;
        instance.createInventory();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onTimerExpired(expiredTimerEvent e) {
        main.getServer().broadcastMessage(conf_msg.PREFIX + conf_msg.MSG_NEW_DAILY_ITEMS);
        createRandomItems();
    }

    public enum updateAction {
        update,
        delete
    }

}
