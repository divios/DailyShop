package io.github.divios.dailyrandomshop.utils;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.economy;
import io.github.divios.dailyrandomshop.economies.vault;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.confirmGui;
import io.github.divios.dailyrandomshop.main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class transaction {

    private final static main main = io.github.divios.dailyrandomshop.main.getInstance();

    private boolean commandFlag = false;
    private boolean amountFlag = false;
    private Player p;
    private ItemStack item;
    private economy econStrategy;

    public static void initTransaction(Player p, ItemStack item) {
        transaction instance = new transaction();
        instance.p = p;
        instance.item = item.clone();
        instance.econStrategy = new vault();

        if (conf_msg.ENABLE_SELL_GUI && !new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount)
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

    public void secondPhase() {
        double nD = item.getAmount() / 64.0;
        int n = (int) Math.ceil(nD);

        if (!hasEnoughMoreAndSpace(n ,dailyItem.getPrice(item) * item.getAmount())) return;

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
            processEcon(p, item);
        }

    }

    public boolean hasEnoughMoreAndSpace(int amount, double price) {
        if (!econStrategy.hasMoney(p, price)) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_ENOUGH_MONEY);
            return false;
        }
        if(utils.inventoryFull(p.getInventory()) < amount) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_INVENTORY_FULL);
            return false;
        }
        return true;
    }

    public boolean processEcon(Player p, ItemStack item) {
        Double price = dailyItem.getPrice(item);
        econStrategy.waitchDrawMoney(p, price);
        p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_BUY_ITEM
                .replaceAll("\\{price}", "" + price)
                .replaceAll("\\{item}", item.getType().toString()));
        p.getInventory().addItem(item.clone());
        return true;
    }

}
