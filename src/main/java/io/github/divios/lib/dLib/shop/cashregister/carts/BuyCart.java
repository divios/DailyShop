package io.github.divios.lib.dLib.shop.cashregister.carts;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.events.checkoutEvent;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.confirmMenu.BuyConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.cashregister.ItemGenerators.GeneratorFactory;
import io.github.divios.lib.dLib.shop.cashregister.ItemGenerators.ItemGenerator;
import io.github.divios.lib.dLib.shop.cashregister.MultiplePreconditions.BuyPostconditions;
import io.github.divios.lib.dLib.shop.cashregister.MultiplePreconditions.BuyPreconditions;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BuyCart extends Cart {

    private static final BuyPreconditions preconditions = new BuyPreconditions();
    private static final BuyPostconditions postConditions = new BuyPostconditions();

    private double price;
    private int amount;

    public BuyCart(dShop shop, Player p, dItem item) {
        super(shop, p, item);
    }

    @Override
    public void addToCart() {
        preconditions.validate(shop, p, item, item.getItem().getAmount());
    }

    @Override
    public void confirmOperation() {
        if (!item.isConfirmGui()) {
            checkOut(item.getItem().getAmount());
            return;
        }

        BuyConfirmMenu.builder()
                .withPlayer(p)
                .withShop(shop)
                .withItem(item)
                .withOnCompleteAction(this::checkOut)
                .withFallback(() -> shop.openShop(p))
                .prompt();
    }

    @Override
    public void checkOut(int amount) {
        long start = System.currentTimeMillis();
        this.amount = amount;

        if (!validatePostConditions())
            return;

        price = item.getPlayerFloorBuyPrice(p, shop) * amount;

        item.getEcon().witchDrawMoney(p, price);
        executeAction();

        DebugLog.info("Buy transaction finished on : " + (System.currentTimeMillis() - start) + " ms");
        sendSuccessMsg(amount, item.getEcon().formatPrice(price), Lang.BUY_ACTION_NAME.getAsString(p));

        Events.callEvent(new checkoutEvent(shop, Transactions.Type.BUY, p, item, amount, price));
        shop.openShop(p);
    }

    private boolean validatePostConditions() {
        try {
            postConditions.validate(shop, p, item, amount);
        } catch (IllegalPrecondition err) {
            err.sendErrMsg(p);
            p.closeInventory();
            return false;
        }
        return true;
    }

    private void executeAction() {
        if (item.hasCommands())
            runCommands();
        else
            giveItem();
    }

    private void runCommands() {
        item.getCommands()
                .forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                Utils.JTEXT_PARSER
                                        .withTag("%", "%")
                                        .withTemplate("player", p.getName())
                                        .withTemplate("amount", String.valueOf(amount))
                                        .withTemplate("price", String.valueOf(price))
                                        .parse(s)
                        )
                );
    }

    private void giveItem() {
        List<String> bundle;
        if ((bundle = item.getBundle()) != null) {
            bundle.stream()
                    .map(shop::getItem)
                    .filter(Objects::nonNull)
                    .forEach(newDItem -> ItemUtils.give(p, newDItem.getItem()));
        } else
            give(amount, GeneratorFactory.getGenerator(item.getItem()));

    }

    private void give(int amount, Supplier<ItemStack> generator) {
        ItemStack clone;
        for(int stackSize = item.getItem().getType().getMaxStackSize(); amount > stackSize; amount -= stackSize) {
            clone = generator.get();
            clone.setAmount(stackSize);
            ItemUtils.give(p, clone);
        }

        clone = generator.get();
        clone.setAmount(amount);
        ItemUtils.give(p, clone);
    }

}
