package io.github.divios.dailyShop.commands;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.economies.Economies;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.jcommands.JCommand;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.newDItem;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class testNewStockCommand {

    private Player p;

    public JCommand getCommand() {
        return JCommand.create("testNewStock")
                .assertRequirements(sender -> Settings.DEBUG.getValue().getAsBoolean())
                .executesPlayer((sender, valueMap) -> {
                    this.p = sender;

                    testStockClone();
                    testStockSimilar();
                    testStockSimilar2();
                    testPricesClone();
                    testPricesSimilar();
                    testPricesEquals1();
                    testPricesEquals2();
                    testEconomyEquals();
                    testEconomyEquals2();
                    testRarityClone();
                    testArrayClone();

                    testInitializer();
                    testSimilar();
                    testDItemClone();
                    testDItemClone2();
                    testDItemCopy();
                    testSerializeToJson();
                    testDeSerializeToJson();
                    testDeSerializeToJson();
                    testDeSerializeToJson2();
                    testClonePerformance();
                    testUnmodifiableItem();
                    testUnmodifiableStock();
                    testUnmodifiablePrice();
                });
    }

    private void testStockClone() {
        dStock stock1 = dStockFactory.INDIVIDUAL(4);
        dStock stock2 = stock1.clone();

        stock1.decrement(p, 1);

        if (stock1.get(p) == 3 && stock2.get(p) == 4) {
            Log.info("Test successfully");
        } else {
            Log.severe("Test Unsuccessfully");
        }
    }

    private void testStockSimilar() {
        dStock stock1 = dStockFactory.INDIVIDUAL(4);
        dStock stock2 = stock1.clone();

        stock1.decrement(p, 1);

        if (newDItem.DailyObject.isSimilar(stock1, stock2)) {
            Log.info("Test successfully");
        } else {
            Log.severe("Test Unsuccessfully");
        }
    }

    private void testStockSimilar2() {
        dStock stock1 = dStockFactory.INDIVIDUAL(4);
        dStock stock2 = dStockFactory.INDIVIDUAL(3);

        if (!newDItem.DailyObject.isSimilar(stock1, stock2)) {
            Log.info("Test successfully");
        } else {
            Log.severe("Test Unsuccessfully");
        }
    }

    private void testPricesClone() {
        dPrice price1 = new dPrice(1, 1500);
        dPrice price2 = price1.clone();

        price1.generateNewPrice();

        if (price1.getPrice() != price2.getPrice())
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testPricesSimilar() {
        dPrice price1 = new dPrice(1, 1500);
        dPrice price2 = price1.clone();

        price1.generateNewPrice();

        if (newDItem.DailyObject.isSimilar(price1, price2))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testPricesEquals1() {
        dPrice price1 = new dPrice(1, 1500);
        dPrice price2 = price1.clone();

        price1.generateNewPrice();

        if (!Objects.equals(price1, price2))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testPricesEquals2() {
        dPrice price1 = new dPrice(1, 1500);
        dPrice price2 = price1.clone();

        if (Objects.equals(price1, price2))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testEconomyEquals() {
        economy econ1 = Economies.MPoints.getEconomy("test");
        economy econ2 = Economies.MPoints.getEconomy("test");

        if (Objects.equals(econ1, econ2))
            Log.info("Test successfully");
        else
            Log.severe("Test unsuccessfully");
    }

    private void testEconomyEquals2() {
        economy econ1 = Economies.MPoints.getEconomy("test");
        economy econ2 = Economies.MPoints.getEconomy("test2");

        if (!Objects.equals(econ1, econ2))
            Log.info("Test successfully");
        else
            Log.severe("Test unsuccessfully");
    }

    private void testRarityClone() {
        dRarity rarity1 = new dRarity();
        dRarity rarity2 = rarity1.clone();

        rarity1.next();

        if (!Objects.equals(rarity1, rarity2))
            Log.info("Test successfully");
        else
            Log.severe("Test unsuccessfully");
    }

    private void testArrayClone() {
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>(list1);

        list1.add("test");

        if (!Objects.equals(list1, list2))
            Log.info("Test successfully");
        else
            Log.severe("Test unsuccessfully");
    }

    private void testInitializer() {
        newDItem.from(XMaterial.DIRT, "dirt");
        Log.info("Test successfully");
    }

    private void testSimilar() {
        newDItem item = newDItem.from(XMaterial.DIRT, "dirt");
        newDItem item2 = newDItem.from(XMaterial.DIRT, "dirt");

        Preconditions.checkArgument(item.isSimilar(item2), "test unsuccessful");
        Log.info("Test successfully");
    }

    private void testDItemClone() {
        newDItem.of(XMaterial.POTION).clone();
        Log.info("Test successfully");
    }

    private void testDItemClone2() {
        newDItem item = newDItem.of(XMaterial.POTION);

        if (Objects.equals(item, item.clone()))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testDItemCopy() {
        newDItem a = newDItem.from(XMaterial.DIRT, "dirt");
        newDItem b = a.copy();

        if (!Objects.equals(a.getID(), b.getID()))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testSerializeToJson() {
        Log.info(newDItem.from(XMaterial.DIRT, "dirt").toJson().toString());
        Log.info("Test successfully");
    }

    private void testDeSerializeToJson() {
        newDItem.fromJson(newDItem.from(XMaterial.DIRT, "dirt").toJson());
        Log.info("Test successfully");
    }

    private void testDeSerializeToJson2() {
        newDItem a = newDItem.from(XMaterial.DIRT, "dirt");
        newDItem b = newDItem.fromJson(a.toJson());

        if (a.isSimilar(b))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testClonePerformance() {
        newDItem a = newDItem.from(XMaterial.DIRT, "dirt");
        a = a.setBundle(Arrays.asList("aaa", "bbb", "ccc", "ddd"));
        a = a.setCommands(Arrays.asList("aaa", "bbb", "ccc"));

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        a.clone();
        Log.info("Clone time: " + (new Timestamp(System.currentTimeMillis()).getNanos() - timestamp.getNanos()) + " ns");

        timestamp = new Timestamp(System.currentTimeMillis());
        newDItem.fromJson(a.toJson());
        Log.info("From/to json time: " + (new Timestamp(System.currentTimeMillis()).getNanos() - timestamp.getNanos()) + " ns");
    }

    private void testUnmodifiableItem() {
        newDItem a = newDItem.from(XMaterial.DIRT, "dirt");

        ItemStack item = a.getItem();
        item.setAmount(3);

        if (item.getAmount() == 3 && a.getItem().getAmount() == 1)
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testUnmodifiableStock() {
        newDItem a = newDItem.from(XMaterial.DIRT, "dirt")
                .setStock(dStockFactory.INDIVIDUAL(3));

        dStock stock = a.getDStock();
        a.decrementStock(p, 1);
        assert stock != null;
        stock.decrement(p, 2);

        if (a.getPlayerStock(p) == 2 && stock.get(p) == 1)
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testUnmodifiablePrice() {
        newDItem a = newDItem.from(XMaterial.DIRT, "dirt")
                .setBuyPrice(30, 50);

        dPrice price = a.getDBuyPrice();
        assert price != null;
        price.generateNewPrice();

        if (price.getPrice() != a.getBuyPrice())
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }


}
