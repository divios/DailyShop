package io.github.divios.dailyrandomshop.guis;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.builders.lorestategy.currentItemsLore;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.events.expiredTimerEvent;
import io.github.divios.dailyrandomshop.tasks.taskManager;
import io.github.divios.dailyrandomshop.utils.transaction;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
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

    public void openInventory(Player p) {
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

            ItemStack randomItem = new dailyItem(
                    utils.getEntry(listOfMaterials, ran), true)
                    .addLoreStrategy(new currentItemsLore())
                    .getItem();

            /* if(Math.random() > main.utils.getRarity(randomItem)/100F
                    && main.getConfig().getBoolean("enable-rarity")) continue; */

            inserted.add(ran);

            inv.setItem(j, randomItem);
            j++;
        }
        dbManager.currentItems = getCurrentItems();
    }

    /**
     * Update the gui with the currentItem
     * on database
     */

    private void updateCurrentItems() {
        clearDailySlots();
        int j = 18;
        for (String text : dbManager.currentItems) {
            String uuid = text.split("\\\\")[0];

            int amount = -1;
            try { amount = Integer.parseInt(text.split("\\\\")[1]); }
            catch (IndexOutOfBoundsException ignored) {};

            if(j >= (18 + conf_msg.N_DAILY_ITEMS -1)) break;
            if (j == inv.getSize()) return;

            ItemStack item = dailyItem.getRawItem(uuid);
            if(utils.isEmpty(item)) continue;

            ItemStack newItem = new dailyItem(item, true)
                    .addLoreStrategy(new currentItemsLore()).getItem();

            if(amount != -1 &&
                    new dailyItem(newItem).hasMetadata(dailyItem.dailyMetadataType.rds_amount)) {
                newItem.setAmount(amount);
                new dailyItem(newItem)
                        .addNbt(dailyItem.dailyMetadataType.rds_amount, "" + amount);
            }

            inv.setItem(j, newItem);
            j++;
        }
    }

    public void updateItem(String uuid, updateAction a) {
        utils.async(() -> {
            for (int i = 18; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item == null) continue;
                ItemStack newItem;
                if (dailyItem.getUuid(item).equals(uuid)) {
                    if (a == updateAction.update) {
                        newItem =
                                new dailyItem(uuid, true)
                                        .addLoreStrategy(new currentItemsLore()).getItem();
                    }
                    else {
                        newItem = utils.getRedPane();
                    }
                    utils.translateAllItemData(newItem, item);
                    return;
                }
            }
        });
    }

    public void processNextAmount(String uuid) {

        ItemStack item = null;

        for (int i = 18; i < inv.getSize(); i++) {
            if (dailyItem.getUuid(inv.getItem(i)).equals(uuid)) {
                item = inv.getItem(i);
                break;
            }
        }
        if(item == null) return;

        if ( new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount)) {
            Integer amount = (Integer) new dailyItem(item).getMetadata(dailyItem.dailyMetadataType.rds_amount);
            if (amount == 1) utils.translateAllItemData(utils.getRedPane(), item);
            else {
                item.setAmount(item.getAmount() - 1);
                new dailyItem(item).addNbt(dailyItem.dailyMetadataType.rds_amount, "" + (item.getAmount())).getItem();
            }
        }
    }

    public List<String> getCurrentItems() {
        List<String> list = new ArrayList<>();
        for (int i = 18; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if(utils.isEmpty(item)) break;

            String uuid = dailyItem.getUuid(item);
            if(utils.isEmpty(uuid)) continue;

            if(new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount))
                uuid = uuid.concat("\\" + new dailyItem(item).
                        getMetadata(dailyItem.dailyMetadataType.rds_amount));

            list.add(uuid);
        }
        return list;
    }

    private void clearDailySlots() {
        for (int i = 18; i < inv.getSize(); i++) {
            inv.setItem(i, null);
        }
    }

    public void reload() {
        inv.getViewers().forEach(HumanEntity::closeInventory);
        instance = null;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);

        if(utils.isEmpty(e.getCurrentItem())) return;

        if (utils.isEmpty(dailyItem.getUuid(e.getCurrentItem()))) return;

        Player p = (Player) e.getWhoClicked();
        if (e.getSlot() == 9) {} //TODO

        else if (e.getSlot() >= 18 && e.getSlot() < inv.getSize()
                && !utils.isEmpty(e.getCurrentItem())) {
            transaction.initTransaction(p, dailyItem.getRawItem(e.getCurrentItem()));
        }
    }

    @EventHandler
    public void onTimerExpired(expiredTimerEvent e) {
        main.getServer().broadcastMessage(conf_msg.PREFIX + conf_msg.MSG_NEW_DAILY_ITEMS);
        taskManager.getInstance().resetTimer();
        createRandomItems();
    }

    public enum updateAction {
        update,
        delete
    }

}
