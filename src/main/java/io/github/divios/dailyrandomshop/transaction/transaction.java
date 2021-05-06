package io.github.divios.dailyrandomshop.transaction;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.economies.*;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.IntStream;

public class transaction {

    private final static io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

    public static void initTransaction(Player p, ItemStack item) throws transactionExc {

        summary s = printSummary(p, item);

        // TODO: comprobar dinero y huecos, si es asi, run todos los runnables

        if (!s.getEcon().hasMoney(p, s.getPrice()))
            throw new transactionExc(transactionExc.err.noMoney);

        if (utils.inventoryFull(p.getInventory()) < s.getSlots())
            throw new transactionExc(transactionExc.err.noSpace);

        s.getEcon().witchDrawMoney(p, s.getPrice());

        s.getRunnables().forEach(Runnable::run);

        p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_BUY_ITEM
                .replaceAll("\\{amount}", "" + item.getAmount())
                .replaceAll("\\{price}", "" + s.getPrice())
                .replaceAll("\\{item}", utils.getDisplayName(item) + utils.formatString("&7"))
                .replaceAll("\\{currency}", getEconName(item)));

    }


    private static summary printSummary(Player p, ItemStack item) throws transactionExc {

        if (utils.isEmpty(item))
            return new summary();

        summary s = new summary();
        dailyItem dItem = new dailyItem(item);

        s.setEcon(getEconomyStrategy(p, item)); // get item economy

        if (dItem.hasMetadata(dailyItem.dailyMetadataType.rds_permissions)) { //checks permissions
            for (String _s: (List<String>)dItem.getMetadata(dailyItem.dailyMetadataType.rds_permissions))
                if (!p.hasPermission(_s))
                    throw new transactionExc(transactionExc.err.noPerms);
        }


        if (dItem.hasMetadata(dailyItem.dailyMetadataType.rds_amount)) {// if is amount
            s.addRunnable(() -> buyGui.getInstance()
                    .processNextAmount(dailyItem.getUuid(item)));
            item.setAmount(1);
        }

        if (dItem.hasMetadata(dailyItem.dailyMetadataType.rds_bundle)) { // checks if bundle
            for (String _s : (List<String>) dItem.getMetadata(dailyItem.dailyMetadataType.rds_bundle))
                for (int i = 0; i < item.getAmount(); i++)
                    s.concat(printSummary(p, dailyItem.getRawItem(_s)));
            s.setPrice(dailyItem.getPrice(item)); // sets price as the bundle
        }

        if (dItem.hasMetadata(dailyItem.dailyMetadataType.rds_commands))   // adds commands to run
            IntStream.range(0, item.getAmount()).forEach(value -> s.addRunnable(() ->
                    ((List<String>) dItem.getMetadata(dailyItem.dailyMetadataType.rds_commands))
                        .forEach(_s -> main.getServer().dispatchCommand(main.getServer().getConsoleSender(),
                            _s.replaceAll("%player%", p.getName())))));


        /// A PARTIR DE AQUI YA SOLO COMPROBAR SLOTS Y PRICE ///

        if (dItem.hasMetadata(dailyItem.dailyMetadataType.rds_setItems))
            s.setPrice(s.getPrice() + dailyItem.getPrice(item));
        else
            s.setPrice(s.getPrice() + dailyItem.getPrice(item) * item.getAmount());


        if (item.getMaxStackSize() == 1)
            s.setSlots(s.getSlots() + item.getAmount());
        else
            s.setSlots(s.getSlots() + 1);

        if (!dItem.hasMetadata(dailyItem.dailyMetadataType.rds_commands) &&
                !dItem.hasMetadata(dailyItem.dailyMetadataType.rds_bundle))
            s.addRunnable(() -> {
                ItemStack aux = item.clone();
                aux.setAmount(1);
                IntStream.range(0, item.getAmount()).
                        forEach(i -> p.getInventory().addItem(aux)); });

        return s;
    }


    private static economy getEconomyStrategy(Player p, ItemStack item) {
        AbstractMap.SimpleEntry<String, String> e =
                (AbstractMap.SimpleEntry<String, String>) new dailyItem(item).
                        getMetadata(dailyItem.dailyMetadataType.rds_econ);

        economy econ = new vault();

        if (e == null) return econ;

        else if (e.getKey().equals(econTypes.gemsEconomy.name())) {
            if (hooksManager.getInstance().getGemsEcon() != null &&
                    hooksManager.getInstance().getGemsEcon().plugin
                            .getCurrencyManager().currencyExist(e.getValue()))
                econ = new gemEcon(e.getValue());
        }
        else if (e.getKey().equals(econTypes.tokenEnchants.name())) {
            econ = new tokenEnchantsE();
        }
        else if (e.getKey().equals(econTypes.tokenManager.name())) {
            econ = new tokenManagerE();
        }

        else if (e.getKey().equals(econTypes.MPoints.name())) {
            econ = new MPointsE(e.getValue());
        }

        try {
            econ.hasMoney(p, 0D);
        } catch (Exception err) { econ = new vault(); }

        return econ;
    }

    private static String getEconName(ItemStack item) {
        String currency;
        try {
            currency = ((AbstractMap.SimpleEntry<String, String>) new dailyItem(item).
                    getMetadata(dailyItem.dailyMetadataType.rds_econ)).getKey();
        } catch (NullPointerException e) { currency = conf_msg.VAULT_CUSTOM_NAME; }

        return currency;
    }

}
