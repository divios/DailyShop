package io.github.divios.dailyShop.economies;

import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class itemEconomy extends economy {

    private final ItemStack item;

    itemEconomy(ItemStack item) {
        this(ItemUtils.serialize(item));
    }

    itemEconomy(String currency) {
        super("item", currency, Economies.exp);
        item = ItemUtils.deserialize(currency);
    }

    @Override
    public void test() {

    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        ItemUtils.remove(p.getInventory(), item, price.intValue());
    }

    @Override
    public void depositMoney(Player p, Double price) {
        ItemUtils.give(p, item, price.intValue());
    }

    @Override
    public double getBalance(Player p) {
        return ItemUtils.count(p.getInventory(), item);
    }
}
