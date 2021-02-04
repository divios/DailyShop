package io.github.divios.dailyrandomshop.utils;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.econTypes;
import io.github.divios.dailyrandomshop.economies.economy;
import io.github.divios.dailyrandomshop.economies.gemEcon;
import io.github.divios.dailyrandomshop.economies.vault;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.confirmGui;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
import io.github.divios.dailyrandomshop.main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.List;

public class transaction {

    private final static main main = io.github.divios.dailyrandomshop.main.getInstance();

    private boolean commandFlag = false;
    private boolean amountFlag = false;
    private boolean oneSlotFlag = false;
    private Player p;
    private ItemStack item;
    private economy econStrategy = null;
    private int n;

    public static void initTransaction(Player p, ItemStack item) {
        transaction instance = new transaction();
        instance.p = p;
        instance.item = item.clone();   /* gets raw item */
        instance.getEconomyStrategy();

        if (instance.econStrategy == null)  {       /* if no economyStrategy was found, */
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_IN_STOCK);   /* either currency doesn't exist or plugin is not on */
            return;
        }

        if (item.getMaxStackSize() == 1)
            instance.oneSlotFlag = true;

        if (conf_msg.ENABLE_CONFIRM_GUI && !new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount)
                && !new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_commands)
            )  {
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

        if (!hasEnoughMoreAndSpace(dailyItem.getPrice(item) * getAmount(item))) return;

        if (new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_commands)) {
            commandFlag = true;
            ((List<String>) new dailyItem(item).getMetadata(dailyItem.dailyMetadataType.rds_commands))
                    .forEach(s -> main.getServer().dispatchCommand(main.getServer().getConsoleSender(),
                            s.replaceAll("%player%", p.getName())));
        }

        if (new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount)) {
            amountFlag = true;
            buyGui.getInstance().processNextAmount(dailyItem.getUuid(item));
            item.setAmount(1);
        }

        if (!commandFlag) {
            processEcon(item);
        }
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


    private boolean hasEnoughMoreAndSpace(double price) {
        if (!econStrategy.hasMoney(p, price)) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_ENOUGH_MONEY);
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }
        if(utils.inventoryFull(p.getInventory()) < n) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_INVENTORY_FULL);
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }
        return true;
    }

    private boolean processEcon(ItemStack item) {
        Double price = dailyItem.getPrice(item);
        String currency;
        try {
            currency = ((AbstractMap.SimpleEntry<String, String>) new dailyItem(item).
                    getMetadata(dailyItem.dailyMetadataType.rds_econ)).getKey();
        } catch (NullPointerException e) { currency = conf_msg.VAULT_CUSTOM_NAME; }

        econStrategy.witchDrawMoney(p, price);
        p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_BUY_ITEM
                .replaceAll("\\{price}", "" + price)
                .replaceAll("\\{item}", item.getType().toString())
                .replaceAll("\\{currency}", currency));
        giveItem(item);
        return true;
    }

    private void giveItem(ItemStack item) {
        ItemStack itemToGive = new dailyItem(item, true).removeAllMetadata().getItem();
        Inventory inv = p.getInventory();
        if (!oneSlotFlag) inv.addItem(itemToGive);
        else {
            itemToGive.setAmount(1);
            for(int i= 0; i< n; i++) {
                inv.addItem(itemToGive);
            }
        }
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
    }

}
