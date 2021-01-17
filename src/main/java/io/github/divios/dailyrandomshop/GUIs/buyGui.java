package io.github.divios.dailyrandomshop.GUIs;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.Utils.ConfigUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class buyGui implements InventoryHolder, Listener {

    private Inventory shop;
    private final DailyRandomShop main;

    public buyGui(DailyRandomShop main) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
        inicializeGui(false);
    }

    public void firstRow() {
        ItemStack item;
        ItemMeta meta;

        item = main.utils.setItemAsFill(new ItemStack(Material.PAINTING));
        meta = item.getItemMeta();
        meta.setDisplayName(main.config.BUY_GUI_PAINTING_NAME);
        List<String> lore = new ArrayList<>();
        for (String s : main.config.BUY_GUI_PAINTING_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        shop.setItem(4, item);

        if(main.getConfig().getBoolean("enable-sell-gui")) {
            ItemStack item2 = XMaterial.OAK_FENCE_GATE.parseItem();
            meta = item2.getItemMeta();
            meta.setDisplayName(main.config.BUY_GUI_ARROW_NAME);
            lore = new ArrayList<>();
            for (String s : main.config.BUY_GUI_ARROW_LORE) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(lore);
            item2.setItemMeta(meta);
            shop.setItem(8, item2);
        }
    }

    public void secondRow() {
        ItemStack item;
        ItemMeta meta;
        for (int j = 0; j < 9; j++) {

            item = main.utils.setItemAsFill(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            item.setItemMeta(meta);
            shop.setItem(9 + j, item);
        }
    }

    public void inicializeGui(boolean timer) {

        double dailyRows = main.config.N_DAILY_ITEMS / 9F;
        int rows = (int) Math.ceil(dailyRows + 2);
        if (rows <= 2) rows = 3;

        shop = Bukkit.createInventory(this, (rows * 9), main.config.BUY_GUI_TITLE);

        firstRow();
        secondRow();

        if (timer) createRandomItems();
        else {
            getDailyItems();
        }
    }

    public void createRandomItems() {
        HashMap<ItemStack, Double> listOfMaterials = main.listDailyItems;
        ArrayList<Integer> inserted = new ArrayList<>();

        int j=18;
        while(true) {

            if(shop.firstEmpty() == -1) break;

            if(j >= (18 + main.config.N_DAILY_ITEMS - 1)) {
                break;
            }

            if (listOfMaterials.size() == inserted.size()) {
                break; //make sure to break infinite loop if happens
            }

            int ran = main.utils.randomValue(0, listOfMaterials.size() - 1);

            if (!inserted.isEmpty() && inserted.contains(ran)) {
                continue;
            }

            ItemStack randomItem = main.utils.getEntry(main.listDailyItems, ran);

            if(Math.random() > main.utils.getRarity(randomItem)/100F) continue;

            inserted.add(ran);

            ItemMeta meta = randomItem.getItemMeta();
            List<String> lore;
            if(meta.hasLore()) lore = meta.getLore();
            else lore = new ArrayList<>();

            lore.add(main.config.BUY_GUI_ITEMS_LORE_PRICE.replaceAll("\\{price}", String.format("%,.2f", main.listDailyItems.get(randomItem))));
            meta.setLore(lore);

            randomItem.setItemMeta(meta);
            main.utils.setRarityLore(randomItem, main.utils.getRarity(randomItem));

            randomItem = main.utils.setItemAsDaily(randomItem);

            shop.setItem(j, randomItem);
            j++;
        }
        saveDailyItems();

        main.getServer().broadcastMessage(main.config.PREFIX + main.config.MSG_NEW_DAILY_ITEMS);
        ConfigUtils.resetTime(main);

    }

    public void saveDailyItems() {
        ArrayList<ItemStack> dailyItems = new ArrayList<>();

        for (ItemStack item : shop.getContents()) {

            if (item == null || !main.utils.isDailyItem(item)) {
                continue;
            }
            ItemStack itemCloned = item.clone();
            ItemMeta meta = itemCloned.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(lore.size() - 1);
            lore.remove(lore.size() - 1);
            meta.setLore(lore);
            itemCloned.setItemMeta(meta);

            dailyItems.add(itemCloned);
        }
        if (!dailyItems.isEmpty()) {
            main.dbManager.updateCurrentItems(dailyItems);
        }
    }

    public void getDailyItems() {
        try {
            ArrayList<ItemStack> dailyItem = main.dbManager.getCurrentItems();
            if (dailyItem.isEmpty()) {
                createRandomItems();
                //main.getLogger().severe("Hubo un error al recuperar los items diarios, generando items aleatorios");
                return;
            }

            int n = 18;
            for (ItemStack item : dailyItem) {
                if(n >= (18 + main.config.N_DAILY_ITEMS -1)) break;
                if (shop.firstEmpty() == -1) break;
                ItemMeta meta = item.getItemMeta();
                List<String> lore;
                if(meta.hasLore()) lore = meta.getLore();
                else lore = new ArrayList<>();

                lore.add(main.config.BUY_GUI_ITEMS_LORE_PRICE.replaceAll("\\{price}", String.format("%,.2f", main.listDailyItems.get(item))));
                meta.setLore(lore);

                item.setItemMeta(meta);
                main.utils.setRarityLore(item, main.utils.getRarity(item));
                shop.setItem(n, main.utils.setItemAsDaily(item));
                n++;
            }

        } catch (Exception e) {
            main.getLogger().severe("Hubo un error al recuperar los items diarios, generando items aleatorios");
            createRandomItems();
            e.printStackTrace();
        }

    }

    @Override
    public Inventory getInventory() {
        return shop;
    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTopInventory().getHolder() != this) return;

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
        item = main.utils.removeItemAsDaily(item);

        //item.setAmount(1);

        Double priceaux = main.utils.getItemPrice(main.listDailyItems, item, true);

        if(priceaux <= 0) {
            p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_IN_STOCK);
            p.closeInventory();
            return;
        }

        if (main.utils.isCommandItem(item)) {                                                                   /* if is command item */

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

        if (main.getConfig().getBoolean("enable-confirm-gui") && !main.utils.isItemAmount(item)) {          /* If the confirm gui is enabled */
            try {
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
            } catch (NoSuchFieldError ignored) {
            }

            new confirmGui(main, item, p, (aBoolean, itemStack) -> {

                if (aBoolean) {
                    Double price = main.utils.getItemPrice(main.listDailyItems, itemStack, true) * itemStack.getAmount();

                    if(main.utils.isItemScracth(itemStack)) {
                        int amount = itemStack.getAmount();
                        String[] constructor = main.utils.getMMOItemConstruct(itemStack);
                        itemStack = MMOItems.plugin.getItem(Type.get(constructor[0]), constructor[1]);
                        itemStack.setAmount(amount);
                    }
                    main.utils.giveItem(p, price, itemStack);
                    p.closeInventory();

                } else{
                    p.openInventory(main.BuyGui.getInventory());
                    try {
                        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
                    } catch (NoSuchFieldError ignored) {}
                }

            }, main.config.CONFIRM_GUI_NAME, true);



        } else {                                                                                                /* If the confirm gui is not enabled */

            Double price = main.utils.getItemPrice(main.listDailyItems, item, true);

            if(price <= 0) {
                p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_IN_STOCK);
                p.closeInventory();
                return;
            }

            if (main.econ.getBalance(p) < price) {
                p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_ENOUGH_MONEY);
                return;
            }

            if (main.utils.isItemAmount(item) && main.utils.inventoryFull(p) != -1) {
                main.utils.processItemAmount(e.getView().getTopInventory().getItem(e.getSlot()), e.getSlot());
            }

            item.setAmount(1);

            if(main.utils.isItemScracth(item)) {
                String[] constructor = main.utils.getMMOItemConstruct(item);
                item = MMOItems.plugin.getItem(Type.get(constructor[0]), constructor[1]);
            }

            main.utils.giveItem(p, price, item);

        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() == this) {
            e.setCancelled(true);
        }
    }



}
