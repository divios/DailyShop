package io.github.divios.dailyrandomshop.utils;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Utils {
    private final DailyRandomShop main;

    public Utils ( DailyRandomShop main) {
        this.main = main;
    }

    public boolean IntegerListContains(int[] list, int i) {

        for (int j: list) {
            if (j == i) {
                return true;
            }
        }
        return false;
    }

    public int randomValue(int minValue, int maxValue) {

        return minValue + (int)(Math.random() * ((maxValue - minValue) + 1));
    }

    /**
     * Gets the amount of free slots from a player inventory
     *
     * @param p    The player to set the active container of
     * @return     The amount of free slots or -1 if there inventory is full

     */
    public int inventoryFull(Player p) {

        int freeSlots = 0;
        for (int i = 0; i < 36; i++) {

            if (p.getInventory().getItem(i) == null ||
                    p.getInventory().getItem(i).getType() == Material.AIR) {
                freeSlots++;
            }
        }
        if(freeSlots == 0) return -1;

        return freeSlots;
    }

    public ItemStack getEntry(Map<ItemStack, Double> list, int index) {
        int i = 0;
        for (ItemStack item: list.keySet()) {
            if (index == i) return item.clone();
            i++;
        }
        return null;
    }

    /**
     * Gives the daily item to the player, additionally, it 'll check if the player
     * has enough money and space and the item getMaxStackSiz. Also it removes the lore
     * and send messages accordingly
     *
     * @param p             The player to give the item
     * @param price         The price of the item
     * @param item          The item to give
    */

    public void giveItem(Player p, Double price, ItemStack item) {

        int freeSlots = main.utils.inventoryFull(p);

        if (freeSlots == -1 || (item.getMaxStackSize() == 1 && freeSlots < item.getAmount())) {
            p.sendMessage(main.config.PREFIX + main.config.MSG_INVENTORY_FULL);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);

            } catch (NoSuchFieldError ignored) {}
            finally { return; }

        }

        if (!playerHasEnoughMoney(p, getEconomyType(item), price)) {
            p.sendMessage(main.config.PREFIX + main.config.MSG_NOT_ENOUGH_MONEY);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            } catch (NoSuchFieldError ignored) {}
            finally { return; }
        }

        ItemStack aux = item.clone();  /* Just in case */
        ItemMeta meta = aux.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        if (main.getConfig().getBoolean("enable-rarity")) lore.remove(lore.size() - 1);
        meta.setLore(lore);
        aux.setItemMeta(meta);

        try {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } catch (NoSuchFieldError ignored) {}

        AbstractMap.SimpleEntry<economyTypes, String> currencyStr = getEconomyType(aux);
        aux = removeDailyMetadata(aux);

        if (aux.getMaxStackSize() == 1) {
            int amount = aux.getAmount();
            aux.setAmount(1);
            for(int i=0; i< amount; i++){
                p.getInventory().addItem(aux);
            }
        }

        else p.getInventory().addItem(aux);
        withdrawMoneyFromPlayer(p, currencyStr, price);
        p.sendMessage(main.config.PREFIX + main.config.MSG_BUY_ITEM.replace("{price}", String.format("%,.2f", price)).replace("{item}",
                item.getType().toString()).replace("{currency}", currencyStr.getValue()));
        p.openInventory(main.BuyGui.getInventory());

    }

    public ItemStack setItemAsFill(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("FillGui", "isfill");
        return nbtItem.getItem();
    }

    public ItemStack setItemAsAmount(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("setAmount", "amnt");
        return nbtItem.getItem();
    }

    public boolean isItemAmount(ItemStack item) {
        if (item == null) return false;

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey("setAmount");
    }

    public ItemStack removeItemAmount(ItemStack item) {

        item.setAmount(1);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey("setAmount");
        return nbtItem.getItem();
    }

    public void processItemAmount(ItemStack item, int slot) {
        if(item.getAmount() == 1) {
            item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Out of Stock");
            item.setItemMeta(meta);

        } else item.setAmount(item.getAmount() - 1);

        main.BuyGui.getInventory().setItem(slot, item);

    }

    public ItemStack setItemAsDaily(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("DailyItem", "isdaily");
        return nbtItem.getItem();
    }

    public boolean isDailyItem(ItemStack item) {
        if (item == null) return false;

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey("DailyItem");
    }

    public ItemStack removeItemAsDaily(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey("DailyItem");
        return nbtItem.getItem();
    }

    public boolean isMMOItem(ItemStack item) {
        try {
            net.mmogroup.mmolib.api.item.NBTItem NBTItem = net.mmogroup.mmolib.api.item.NBTItem.get(item);
            return NBTItem.hasType();
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            return false;
        }
    }

    public String[] getMMOItemConstruct(ItemStack item) {

        net.mmogroup.mmolib.api.item.NBTItem NBTItem = net.mmogroup.mmolib.api.item.NBTItem.get(item);
        String type = NBTItem.getType();
        String id = NBTItem.getString("MMOITEMS_ITEM_ID");

        return new String[]{type, id};
    }

    public ItemStack setItemAsScracth(ItemStack item) {
        NBTItem NBTItem = new NBTItem(item);
        NBTItem.setString("Scratch", "true");

        return NBTItem.getItem();
    }

    public ItemStack removeItemScracth(ItemStack item) {
        NBTItem NBTItem = new NBTItem(item);
        NBTItem.removeKey("Scratch");

        return NBTItem.getItem();
    }

    public boolean isItemScracth (ItemStack item) {
        NBTItem NBTItem = new NBTItem(item);

        return NBTItem.hasKey("Scratch");
    }

    public ItemStack setItemAsCommand(ItemStack item, List<String> command) {
        NBTItem nbtItem = new NBTItem(item);
        String commands = "";

        for(String s: command) {
            commands = commands.concat(";" + s);
        }

        if(!commands.isEmpty()) commands = commands.substring(1);

        nbtItem.setString("Command", commands);
        return nbtItem.getItem();
    }

    public boolean isCommandItem(ItemStack item) {
        if (item == null) return false;

        NBTItem NBTitem = new NBTItem(item);
        return NBTitem.hasKey("Command");
    }

    public List<String> getItemCommand(ItemStack item) {

        NBTItem NBTitem = new NBTItem(item);
        String rawCommands = NBTitem.getString("Command");

        return new ArrayList<>(Arrays.asList(rawCommands.split(";")));
    }

    public ItemStack removeItemCommand(ItemStack item) {
        NBTItem NBTitem = new NBTItem(item);
        NBTitem.removeKey("Command");

        return NBTitem.getItem();
    }

    public AbstractMap.SimpleEntry<economyTypes, String> getEconomyType(ItemStack item) {

        AbstractMap.SimpleEntry<economyTypes, String> economyType;
        AbstractMap.SimpleEntry<String, String> aux;
        NBTItem NBTitem = new NBTItem(item);

        if(NBTitem.hasKey("dailyEconomyWrapper")) {
            aux = NBTitem.getObject("dailyEconomyWrapper", AbstractMap.SimpleEntry.class);
            economyType = new AbstractMap.SimpleEntry<>(economyTypes.valueOf(aux.getKey()), aux.getValue());
        } else
            economyType =  new AbstractMap.SimpleEntry<>(economyTypes.VAULT, main.config.VAULT_CUSTOM_NAME);

        return economyType;
    }

    public ItemStack setEconomyType(ItemStack item, economyTypes e) {

        NBTItem NBTitem = new NBTItem(item);

        switch (e) {
            case VAULT:
                NBTitem.removeKey("dailyEconomyWrapper");
                break;
            default:
                NBTitem.setObject("dailyEconomyWrapper", new AbstractMap.SimpleEntry<>(e.toString(), ""));
        }

        return NBTitem.getItem();

    }

    public ItemStack setEconomyType(ItemStack item, economyTypes e, String economyStr) {

        NBTItem NBTitem = new NBTItem(item);

        switch (e) {
            case VAULT:
                NBTitem.removeKey("dailyEconomyWrapper");
                break;
            default:
                NBTitem.setObject("dailyEconomyWrapper", new AbstractMap.SimpleEntry<>(e.toString(), economyStr));
        }

        return NBTitem.getItem();

    }

    public ItemStack removeEconomyTypeMetadata(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey("dailyEconomyWrapper");
        return nbtItem.getItem();
    }

    public Double getItemPrice(Map<ItemStack, Double> items, ItemStack toCompare, boolean lore) {
        Double price = 0.0;
        ItemStack toCompare2 = null;
        if (lore) toCompare2 =  removePriceLore(toCompare);
        else toCompare2 = toCompare.clone();

        for (Map.Entry<ItemStack, Double> item: items.entrySet()) {
            //ItemStack item2 = removePriceLore(item.getKey());
            if (item.getKey().isSimilar(toCompare2) ||
                    (isMMOItem(toCompare2) && net.mmogroup.mmolib.api.item.NBTItem.get(toCompare2).getString("MMOITEMS_ITEM_ID").equals(net.mmogroup.mmolib.api.item.NBTItem.get(item.getKey()).getString("MMOITEMS_ITEM_ID")))) return item.getValue();
        }

        return price;
    }

    public Boolean playerHasEnoughMoney(Player p, AbstractMap.SimpleEntry<economyTypes, String> e, Double m) {

        boolean status = false;

        switch (e.getKey()) {
            case VAULT:
                status = main.econ.getBalance(p) >= m;
                break;
            case GEMSECONOMY:
                if(main.gemsApi == null) return false;
                Currency currency = main.gemsApi.plugin.getCurrencyManager().getCurrency(e.getValue());
                if(currency == null) return false;
                status = main.gemsApi.plugin.getAccountManager().getAccount(p).hasEnough(currency, m);
                break;
        }
        return status;

    }

    public void withdrawMoneyFromPlayer(Player p, AbstractMap.SimpleEntry<economyTypes, String> e, Double m) {
        switch (e.getKey()) {
            case VAULT:
                main.econ.withdrawPlayer(p, m);
                break;
            case GEMSECONOMY:
                Currency currency = main.gemsApi.plugin.getCurrencyManager().getCurrency(e.getValue());
                main.gemsApi.withdraw(p.getUniqueId(), m, currency);
                break;
        }

    }

    public ItemStack removePriceLore ( ItemStack item) {
        ItemStack item2 = item.clone();
        ItemMeta meta = null;
        meta = item2.getItemMeta();

        if(meta.hasLore()) {
            List<String> lore = meta.getLore();
            lore.remove(lore.size() - 1);
            lore.remove(lore.size() - 1);
            if(main.getConfig().getBoolean("enable-rarity")) lore.remove(lore.size() - 1);
            meta.setLore(lore);
        }
        item2.setItemMeta(meta);

        return item2;
    }

    public ItemStack getBuyItem(ItemStack item) {
        for( Map.Entry<ItemStack, Double> s: main.listDailyItems.entrySet()) {
            if (s.getKey().isSimilar(item)) return s.getKey().clone();
        }
        return null;
    }

    public void removeItemOnList(Map<ItemStack, Double> list, ItemStack item) {

        for (ItemStack entryItem: list.keySet()) {

            if(entryItem.isSimilar(item) ||
                    (isMMOItem(item) && net.mmogroup.mmolib.api.item.NBTItem.get(entryItem).getString("MMOITEMS_ITEM_ID").equals(net.mmogroup.mmolib.api.item.NBTItem.get(item).getString("MMOITEMS_ITEM_ID")))) {

                list.remove(entryItem);
                return;
            }
        }

    }

    public void replacePriceOnList(Map<ItemStack, Double> list, ItemStack item, Double price) {

        for (ItemStack entryItem: list.keySet()) {
            if(entryItem.isSimilar(item) || (isMMOItem(item) &&
                    net.mmogroup.mmolib.api.item.NBTItem.get(entryItem).getString("MMOITEMS_ITEM_ID").equals(net.mmogroup.mmolib.api.item.NBTItem.get(item).getString("MMOITEMS_ITEM_ID")))) {
                list.replace(entryItem, price);
                return;
            }
        }
    }

    public boolean listContaisItem(Map<ItemStack, Double> list, ItemStack item) {
        for (ItemStack entryItem: list.keySet()) {
            if(entryItem.isSimilar(item) || (isMMOItem(item) &&
                    net.mmogroup.mmolib.api.item.NBTItem.get(entryItem).getString("MMOITEMS_ITEM_ID").equals(net.mmogroup.mmolib.api.item.NBTItem.get(item).getString("MMOITEMS_ITEM_ID"))))
                return true;
        }
        return false;
    }

    public void waitXticks(long ticks) {
        Bukkit.getScheduler().runTaskLater(main, () -> {

        }, ticks);
    }

    public int getRarity(ItemStack item) {
        int rarity = 100;
        NBTItem nbtItem = new NBTItem(item);
        if(nbtItem.hasKey("rarityRdshop")) {
            rarity = nbtItem.getInteger("rarityRdshop");
        }

        return rarity;
    }

    public String getRarityString(ItemStack item) {
        String rarity = "";
        NBTItem nbtItem = new NBTItem(item);
        switch (getRarity(item)) {
            case 100:
                rarity = "Common";
                break;
            case 80:
                rarity = "unCommon";
                break;
            case 60:
                rarity = "Rare";
                break;
            case 40:
                rarity = "Epic";
                break;
            case 20:
                rarity = "Ancient";
                break;
            case 10:
                rarity = "Legendary";
                break;
            case 5:
                rarity = "Mythic";
                break;
        }
        return rarity;
    }

    //common (100), uncommon (80), rare (60), epic (40), ancient (20), legendary (10), mythic (5)
    public ItemStack processNextRarity(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if(!nbtItem.hasKey("rarityRdshop")) {
            nbtItem.setInteger("rarityRdshop", 80);
        }
        else {
            switch (nbtItem.getInteger("rarityRdshop")) {
                case 80: nbtItem.setInteger("rarityRdshop", 60); break;
                case 60: nbtItem.setInteger("rarityRdshop", 40); break;
                case 40: nbtItem.setInteger("rarityRdshop", 20); break;
                case 20: nbtItem.setInteger("rarityRdshop", 10); break;
                case 10: nbtItem.setInteger("rarityRdshop", 5); break;
                case 5: nbtItem.removeKey("rarityRdshop"); break;
            }
        }
        return nbtItem.getItem();
    }

    public ItemStack setRarity(ItemStack item, int rarity) {
        NBTItem nbtItem = new NBTItem(item);
        switch (rarity) {
            case 80: nbtItem.setInteger("rarityRdshop", 80); break;
            case 60: nbtItem.setInteger("rarityRdshop", 60); break;
            case 40: nbtItem.setInteger("rarityRdshop", 40); break;
            case 20: nbtItem.setInteger("rarityRdshop", 20); break;
            case 10: nbtItem.setInteger("rarityRdshop", 10); break;
            case 5: nbtItem.setInteger("rarityRdshop", 5); break;
            default: nbtItem.removeKey("rarityRdshop"); break;
        }
        return nbtItem.getItem();
    }

    public ItemStack removeRarityMeta(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey("rarityRdshop");
        return nbtItem.getItem();
    }

    public void setRarityLore(ItemStack item, int rarity) {

        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if (meta.hasLore()) lore = meta.getLore();
        else lore = new ArrayList<>();
        switch (rarity) {
            case 100:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Common"));
                break;
            case 80:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "UnCommon"));
                break;
            case 60:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Rare"));
                break;
            case 40:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Epic"));
                break;
            case 20:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Ancient"));
                break;
            case 10:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Legendary"));
                break;
            case 5:
                lore.add(main.config.BUY_GUI_ITEMS_LORE_RARITY.replaceAll("\\{rarity}", "Mythic"));
                break;
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public ItemStack transferDailyMetadata(ItemStack oldItem, ItemStack newItem) {

        if(isItemScracth(oldItem)) {
            newItem = setItemAsScracth(newItem);
        }

        if(isDailyItem(oldItem)) {
            newItem = setItemAsDaily(newItem);
        }

        if(isCommandItem(oldItem)) {
            newItem = setItemAsCommand(newItem, getItemCommand(oldItem));
        }

        if(isItemAmount(oldItem)) {
            newItem = setItemAsAmount(oldItem);
            newItem.setAmount(oldItem.getAmount());
        }
        AbstractMap.SimpleEntry<economyTypes, String> entry = getEconomyType(oldItem);
        newItem = setEconomyType(newItem, entry.getKey(), entry.getValue());

        newItem = setRarity(newItem, getRarity(oldItem));

        return newItem;

    }

    public ItemStack removeDailyMetadata(ItemStack item) {

        ItemStack aux = item.clone();

        if(isItemScracth(item)) {
            aux = removeItemScracth(aux);
        }

        if(isDailyItem(item)) {
            aux = removeItemAsDaily(aux);
        }

        if(isCommandItem(item)) {
            aux = removeItemCommand(aux);
        }

        if(isItemAmount(item)) {
            aux = removeItemAmount(aux);
            aux.setAmount(1);
        }

        aux = removeRarityMeta(aux);

        return removeEconomyTypeMetadata(aux);

    }

}
