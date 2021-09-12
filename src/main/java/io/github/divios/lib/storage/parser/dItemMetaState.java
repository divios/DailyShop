package io.github.divios.lib.storage.parser;

import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.economies.vault;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class dItemMetaState {

    private final String buyPrice;
    private final String sellPrice;
    private final String stock;
    private final Integer set;
    private final List<String> buyPerms;
    private final List<String> sellPerms;
    private final List<String> commands;
    private final List<UUID> bundle;
    private final boolean confirm_gui;
    private final String rarity;
    private final String econ;

    public dItemMetaState(dItem item) {
        buyPrice = item.getBuyPrice().get().toString().isEmpty() ? null : item.getBuyPrice().get().toString();
        sellPrice = item.getSellPrice().get().toString().isEmpty() ? null : item.getSellPrice().get().toString();
        stock = item.hasStock() ? item.getStock().getName() + ":" + item.getStock().getDefault() : null;
        set = item.getSetItems().orElse(null);
        buyPerms = item.getPermsBuy().orElse(null);
        sellPerms = item.getPermsSell().orElse(null);
        commands = item.getCommands().orElse(null);
        bundle = item.getBundle().orElse(null);
        confirm_gui = item.getConfirm_gui();
        rarity = item.getRarity().getKey();
        econ = item.getEconomy().getKey() + ":" + item.getEconomy().getCurrency();

    }

    public dItemMetaState(ItemStack item) { this(dItem.of(item)); }

    public static dItemMetaState of(dItem item) { return new dItemMetaState(item); }

    public static dItemMetaState of(ItemStack item) { return new dItemMetaState(item); }

    public String getBuyPrice() {
        return buyPrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public String getStock() {
        return stock;
    }

    public Integer getSet() {
        return set;
    }

    public List<String> getBuyPerms() {
        return buyPerms;
    }

    public List<String> getSellPerms() {
        return sellPerms;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<UUID> getBundle() {
        return bundle;
    }

    public boolean isConfirm_gui() {
        return confirm_gui;
    }

    public String getRarity() {
        return rarity;
    }

    public String getEcon() {
        return econ;
    }

    public void applyValues(dItem item) {

        if (buyPrice != null) {
            String[] prices = buyPrice.split(":");
            if (prices.length == 2) item.setBuyPrice(Double.parseDouble(prices[0]), Double.parseDouble(prices[1]));
            else item.setBuyPrice(Double.parseDouble(prices[0]));
        } else item.setBuyPrice(-1);

        if (sellPrice != null) {
            String[] prices = sellPrice.split(":");
            if (prices.length == 2) item.setSellPrice(Double.parseDouble(prices[0]), Double.parseDouble(prices[1]));
            else item.setSellPrice(Double.parseDouble(prices[0]));
        } else item.setSellPrice(-1);

        if (stock != null) {
            String[] _stock = stock.split(":");
            if (_stock[0].equalsIgnoreCase("GLOBAL"))
                item.setStock(dStockFactory.GLOBAL(Integer.parseInt(_stock[1])));
            else if (_stock[0].equalsIgnoreCase("INDIVIDUAL"))
                item.setStock(dStockFactory.INDIVIDUAL(Integer.parseInt(_stock[1])));
        }

        if (set != null) {
            item.setSetItems(set);
            item.setQuantity(set);
        }

        item.setPermsBuy(buyPerms);
        item.setPermsSell(sellPerms);
        item.setCommands(commands);
        item.setBundle(bundle);
        item.setConfirm_gui(confirm_gui);
        item.setRarity(dRarity.fromKey(rarity));

        try {
            String[] keys = econ.split(":");
            economy econ = economy.getFromKey(keys[0], keys.length == 2 ? keys[1]:"");
            econ.test();
            item.setEconomy(econ);
        } catch (Exception e) {
            item.setEconomy(new vault());
        }


    }
    
}
