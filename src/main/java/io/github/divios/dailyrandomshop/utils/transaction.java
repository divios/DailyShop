package io.github.divios.dailyrandomshop.utils;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.*;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.confirmGui;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
import io.github.divios.dailyrandomshop.builders.itemBuildersHooks.itemsBuilderManager;
import io.github.divios.dailyrandomshop.main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class transaction {

    private final static main main = io.github.divios.dailyrandomshop.main.getInstance();

    private boolean commandFlag = false;
    private boolean amountFlag = false;
    private boolean oneSlotFlag = false;
    private boolean confirmGuiFlag = false;
    private boolean setFlag = false;
    private boolean setOfItemsFlag = false;
    private Player p;
    private ItemStack item;
    private economy econStrategy = null;
    private int n;

    public static void initTransaction(Player p, ItemStack item) {
        transaction instance = new transaction();
        instance.p = p;
        instance.item = item.clone();   /* gets raw item */
        instance.getEconomyStrategy();
        instance.commandFlag = new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_commands);
        instance.amountFlag = new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount);
        instance.setOfItemsFlag = new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_setItems);
        instance.confirmGuiFlag = (boolean) new dailyItem(item).getMetadata(dailyItem.dailyMetadataType.rds_confirm_gui);
        instance.oneSlotFlag = item.getMaxStackSize() == 1;
        instance.setFlag = new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_setItems);

        if (instance.econStrategy == null)  {       /* if no economyStrategy was found, */
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_IN_STOCK);   /* either currency doesn't exist or plugin is not on */
            return;
        }

        if (!instance.checkPerms()) {
            p.closeInventory();
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_PERMS_ITEM);
            return;
        }

        if (instance.confirmGuiFlag && !instance.amountFlag
                && !instance.setFlag && !instance.setOfItemsFlag)  {
            confirmGui.openInventory(p,
                    dailyItem.getRawItem(item).clone(),
                    (player, itemStack) -> {
                        instance.item = itemStack;
                        player.closeInventory();
                        instance.secondPhase();
                    },
                    player -> buyGui.getInstance().openInventory(player));
        }
        else instance.secondPhase();

    }

    private void secondPhase() {
        double nD;
        if (!oneSlotFlag) nD = getAmount(item) / 64.0;
        else nD = getAmount(item);

        n = (int) Math.ceil(nD);

        double price = dailyItem.getPrice(item);
        if (!setOfItemsFlag) price *= getAmount(item);

        if (!hasEnoughMoneyAndSpace(price)) return;

        if (amountFlag) {
            buyGui.getInstance().processNextAmount(dailyItem.getUuid(item));
            item.setAmount(1);
        }

        if (setOfItemsFlag)
            item.setAmount((Integer) new dailyItem(item).
                    getMetadata(dailyItem.dailyMetadataType.rds_setItems));

        new confirmIH(p, (player, aBoolean) -> {
            if (aBoolean) {
                thirdPhase();
                player.closeInventory();
            } else
                buyGui.getInstance().openInventory(player);
        }, player -> buyGui.getInstance().openInventory(player), item,
                conf_msg.CONFIRM_GUI_NAME, "&a&lConfirm purchase",
                "&7&lCancel purchase");
    }

    private void thirdPhase() {
        if (commandFlag) {
            IntStream.range(0, getAmount(item)).forEach(value -> {
                ((List<String>) new dailyItem(item).getMetadata(dailyItem.dailyMetadataType.rds_commands))
                        .forEach(s -> main.getServer().dispatchCommand(main.getServer().getConsoleSender(),
                                s.replaceAll("%player%", p.getName())));
                econStrategy.witchDrawMoney(p, dailyItem.getPrice(item));
            });
            sendBuyMessage(dailyItem.getPrice(item) * item.getAmount());
        }

        if (!commandFlag || setOfItemsFlag)
            processEcon(item);
    }

    /**
     *
     * @param item
     * @return gets the amount of the item with rds_amount awareness
     */
    private static int getAmount(ItemStack item) {
        int amount = item.getAmount();

        if (new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount)) {
            amount = 1;
        }
        return amount;
    }


    private boolean hasEnoughMoneyAndSpace(double price) {
        if (!econStrategy.hasMoney(p, price)) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_ENOUGH_MONEY);
            utils.sendSound(p, Sound.ENTITY_VILLAGER_NO);
            return false;
        }
        if( (oneSlotFlag && utils.inventoryFull(p.getInventory()) < n) ||
                ((amountFlag && utils.inventoryFull(p.getInventory()) < 1)) &&
                !commandFlag) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_INVENTORY_FULL);
            utils.sendSound(p, Sound.ENTITY_VILLAGER_NO);
            return false;
        }

        return true;
    }

    private boolean processEcon(ItemStack item) {
        double price = dailyItem.getPrice(item);
        if (!setOfItemsFlag)
            price *= item.getAmount();

        econStrategy.witchDrawMoney(p, price);
        sendBuyMessage(price);

        HashMap<Integer, ItemStack> remaining = giveItem(item);     /* Returns null if oneSlotFlag */

        if(remaining != null && !remaining.isEmpty()) {     /* If items are lost due to space and is not oneSlotFlag */
            for(Map.Entry<Integer, ItemStack> e: remaining.entrySet()) {
                econStrategy.depositMoney(p, dailyItem.getPrice(item)
                        * e.getValue().getAmount());
                p.sendMessage(conf_msg.PREFIX + utils.formatString("&7You dont have enough space, " +
                        e.getValue().getAmount() + " " + e.getValue().getType().toString() +
                        " &7was lost and " + dailyItem.getPrice(item) * e.getValue().getAmount() +
                        " &7$ was returned to you"));
            }
        }

        return true;
    }

    private void sendBuyMessage(double price) {
        String currency;
        try {
            currency = ((AbstractMap.SimpleEntry<String, String>) new dailyItem(item).
                    getMetadata(dailyItem.dailyMetadataType.rds_econ)).getKey();
        } catch (NullPointerException e) { currency = conf_msg.VAULT_CUSTOM_NAME; }

        p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_BUY_ITEM
                .replaceAll("\\{amount}", "" + getAmount(item))
                .replaceAll("\\{price}", "" + price)
                .replaceAll("\\{item}", utils.getDisplayName(item) + utils.formatString("&7"))
                .replaceAll("\\{currency}", currency));
    }

    private HashMap<Integer, ItemStack> giveItem(ItemStack item) {
        ItemStack itemToGive;
        if (itemsBuilderManager.isUpdateItem(item)) {
            itemToGive = itemsBuilderManager.getItem(item);
        }
        else itemToGive = new dailyItem(item, true).removeAllMetadata().getItem();
        Inventory inv = p.getInventory();

        if (oneSlotFlag) {
            int n = item.getAmount();
            item.setAmount(1);
            IntStream.range(0, n).forEach(value ->
                    inv.setItem(inv.firstEmpty(), item));

            return null;
        }

        return inv.addItem(itemToGive);
    }

    private void getEconomyStrategy() {
        AbstractMap.SimpleEntry<String, String> e =
                (AbstractMap.SimpleEntry<String, String>) new dailyItem(item).
                        getMetadata(dailyItem.dailyMetadataType.rds_econ);

        if (e == null) econStrategy = new vault();
        else if (e.getKey().equals(econTypes.gemsEconomy.name())) {
            if (hooksManager.getInstance().getGemsEcon() != null &&
                    hooksManager.getInstance().getGemsEcon().plugin
                            .getCurrencyManager().currencyExist(e.getValue()))
                econStrategy = new gemEcon(e.getValue());
        }
        else if (e.getKey().equals(econTypes.tokenEnchants.name())) {
            econStrategy = new tokenEnchantsE();
        }
        else if (e.getKey().equals(econTypes.tokenManager.name())) {
            econStrategy = new tokenManagerE();
        }
    }

    private boolean checkPerms() {
        if (!new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_permissions))
            return true;
        for (String s: (List<String>) new dailyItem(item).
                getMetadata(dailyItem.dailyMetadataType.rds_permissions)) {
            if (!p.hasPermission(s)) return false;
        }
        return true;
    }

}
