package io.github.divios.dailyShop.commands;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.misc.WeightedRandom;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.economies.Economies;
import io.github.divios.dailyShop.economies.Economy;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.valuegenerators.RandomIntervalGenerator;
import io.github.divios.jcommands.JCommand;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class testNewStockCommand {

    private Player p;

    public JCommand getCommand() {
        return JCommand.create("testNewStock")
                .assertRequirements(sender -> Settings.DEBUG.getValue().getAsBoolean())
                .executesPlayer((sender, valueMap) -> {
                    this.p = sender;

                    testStockClone();
                    testStockClone2();
                    testStockSimilar();
                    testStockSimilar2();
                    testStockClone3();
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
                    testSimilar2();
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

                    testPricePretty();
                    testHashCode();

                    CompletableFuture.runAsync(this::benchMarkRestock);
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

    private void testStockClone2() {
        dStock stock1 = dStockFactory.INDIVIDUAL(4);

        stock1.decrement(p, 1);
        dStock stock2 = stock1.clone();

        if (stock1.get(p) == 3 && stock2.get(p) == 3) {
            Log.info("Test successfully");
        } else {
            Log.severe("Test Unsuccessfully");
        }
    }

    private void testStockClone3() {
        dStock stock1 = dStockFactory.INDIVIDUAL(4);

        stock1.decrement(p, 1);

        stock1 = dStock.fromJson(stock1.toJson());

        if (stock1.get(p) == 3) {
            Log.info("Test successfully");
        } else {
            Log.severe("Test Unsuccessfully");
        }
    }

    private void testStockSimilar() {
        dStock stock1 = dStockFactory.INDIVIDUAL(4);
        dStock stock2 = stock1.clone();

        stock1.decrement(p, 1);

        if (dItem.DailyObject.isSimilar(stock1, stock2)) {
            Log.info("Test successfully");
        } else {
            Log.severe("Test Unsuccessfully");
        }
    }

    private void testStockSimilar2() {
        dStock stock1 = dStockFactory.INDIVIDUAL(4);
        dStock stock2 = dStockFactory.INDIVIDUAL(3);

        if (!dItem.DailyObject.isSimilar(stock1, stock2)) {
            Log.info("Test successfully");
        } else {
            Log.severe("Test Unsuccessfully");
        }
    }

    private void testPricesClone() {
        dPrice price1 = new dPrice(new RandomIntervalGenerator(1, 1500));
        dPrice price2 = price1.clone();

        price1.generateNewPrice();

        if (price1.getPrice() != price2.getPrice())
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testPricesSimilar() {
        dPrice price1 = new dPrice(new RandomIntervalGenerator(1, 1500));
        dPrice price2 = price1.clone();

        price1.generateNewPrice();

        if (dItem.DailyObject.isSimilar(price1, price2))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testPricesEquals1() {
        dPrice price1 = new dPrice(new RandomIntervalGenerator(1, 1500));
        dPrice price2 = price1.clone();

        price1.generateNewPrice();

        if (!Objects.equals(price1, price2))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testPricesEquals2() {
        dPrice price1 = new dPrice(new RandomIntervalGenerator(1, 1500));
        dPrice price2 = price1.clone();

        if (Objects.equals(price1, price2))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testEconomyEquals() {
        Economy econ1 = Economies.MPoints.getEconomy("test");
        Economy econ2 = Economies.MPoints.getEconomy("test");

        if (Objects.equals(econ1, econ2))
            Log.info("Test successfully");
        else
            Log.severe("Test unsuccessfully");
    }

    private void testEconomyEquals2() {
        Economy econ1 = Economies.MPoints.getEconomy("test");
        Economy econ2 = Economies.MPoints.getEconomy("test2");

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
        dItem.from(XMaterial.DIRT, "dirt");
        Log.info("Test successfully");
    }

    private void testSimilar() {
        dItem item = dItem.from(XMaterial.DIRT, "dirt");
        dItem item2 = dItem.from(XMaterial.DIRT, "dirt");

        Preconditions.checkArgument(item.isSimilar(item2), "test unsuccessful");
        Log.info("Test successfully");
    }

    private void testSimilar2() {
        dItem item = dItem.from(XMaterial.DIRT, "dirt");
        item.setAction(dAction.OPEN_SHOP, "prueba");
        item.setBundle(Arrays.asList("wow", "much wow", "Nooo"));
        item.setCommands(Arrays.asList("dogee", "bitcoin"));
        item.setConfirmGui(false);
        item.setEcon(Economies.exp.getEconomy());
        item.setItemQuantity(5);
        item.setRarity("Rare");

        dItem item2 = dItem.from(XMaterial.DIRT, "dirt");
        item2.setAction(dAction.OPEN_SHOP, "prueba");
        item2.setBundle(Arrays.asList("wow", "much wow", "Nooo"));
        item2.setCommands(Arrays.asList("dogee", "bitcoin"));
        item2.setConfirmGui(false);
        item2.setEcon(Economies.exp.getEconomy());
        item2.setItemQuantity(5);
        item2.setRarity("Rare");

        Preconditions.checkArgument(dItem.DailyObject.isSimilar(item, item2), "test unsuccessful");
        Log.info("Test successfully");
    }

    private void testDItemClone() {
        dItem.of(XMaterial.POTION).clone();
        Log.info("Test successfully");
    }

    private void testDItemClone2() {
        dItem item = dItem.of(XMaterial.POTION);

        if (Objects.equals(item, item.clone()))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testDItemCopy() {
        dItem a = dItem.from(XMaterial.DIRT, "dirt");
        dItem b = a.copy();

        if (!Objects.equals(a.getID(), b.getID()))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testSerializeToJson() {
        Log.info(dItem.from(XMaterial.DIRT, "dirt").toJson().toString());
        Log.info("Test successfully");
    }

    private void testDeSerializeToJson() {
        dItem.fromJson(dItem.from(XMaterial.DIRT, "dirt").toJson());
        Log.info("Test successfully");
    }

    private void testDeSerializeToJson2() {
        dItem a = dItem.from(XMaterial.DIRT, "dirt");
        dItem b = dItem.fromJson(a.toJson());

        if (a.isSimilar(b))
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testClonePerformance() {
        dItem a = dItem.from(XMaterial.DIRT, "dirt");
        a = a.setBundle(Arrays.asList("aaa", "bbb", "ccc", "ddd"));
        a = a.setCommands(Arrays.asList("aaa", "bbb", "ccc"));

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        a.clone();
        Log.info("Clone time: " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + " ns");

        timestamp = new Timestamp(System.currentTimeMillis());
        dItem.fromJson(a.toJson());
        Log.info("From/to json time: " + (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + " ns");
    }

    private void testUnmodifiableItem() {
        dItem a = dItem.from(XMaterial.DIRT, "dirt");

        ItemStack item = a.getItem();
        item.setAmount(3);

        if (item.getAmount() == 3 && a.getItem().getAmount() == 1)
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testUnmodifiableStock() {
        dItem a = dItem.from(XMaterial.DIRT, "dirt")
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
        dItem a = dItem.from(XMaterial.DIRT, "dirt")
                .setBuyPrice(new RandomIntervalGenerator(30, 50));

        dPrice price = a.getDBuyPrice();
        assert price != null;
        price.generateNewPrice();

        if (price.getPrice() != a.getBuyPrice())
            Log.info("Test successfully");
        else
            Log.severe("Test Unsuccessfully");
    }

    private void testPricePretty() {
        DebugLog.info(String.valueOf(Settings.INTEGER_VAL.getValue().getAsBoolean()));
        double d = 5000.00;
        String expect = Settings.INTEGER_VAL.getValue().getAsBoolean()
                ? "5.000"
                : "5.000,00";

        Preconditions.checkArgument(expect.equals(PrettyPrice.pretty(d)));
    }

    public void testHashCode() {
        dItem a = dItem.of(XMaterial.DIRT);
        dItem b = dItem.of(XMaterial.DIRT);
        b = b.setID(a.getID());

        Preconditions.checkArgument(a.equals(b));
        Preconditions.checkArgument(a.hashCode() == b.hashCode());
    }

    public void benchMarkRestock() {
        long totalTime = System.currentTimeMillis();

        benchmarkMethod[] methods =  new benchmarkMethod[]{ this::shuffleMethod , this::weightedRandom};
        long[] totalMethods = new long[methods.length];

        StringBuilder completedBar = new StringBuilder();
        for (dShop shop : DailyShop.get().getShopsManager().getShops()) {
            System.out.println(completedBar.append(" ."));
            if (shop.getItems().isEmpty()) continue;
            for (int i = 0; i < 40; i++) {          // nItems to test
                for (int j = 0; j < 1000; j++) {     // test each nItems 500 times

                    for (int i1 = 0; i1 < totalMethods.length; i1++)
                        totalMethods[i1] += testTime(methods[i1], shop, i);
                }

            }
        }

        System.out.println("# Run Complete. Total time: " + timeDifference(System.currentTimeMillis() - totalTime));
        System.out.println();
        System.out.println("benchmark");
        for (int i = 0; i < methods.length; i++)
            System.out.println(methods[i].getClass().getSimpleName() + "     score: " + totalMethods[i] / (System.currentTimeMillis() - totalTime) + "     time: " + timeDifference(totalMethods[i]));

    }

    private long testTime(benchmarkMethod method, dShop shop, int nTimes) {
        long start = System.currentTimeMillis();
        method.run(shop, nTimes);
        return System.currentTimeMillis() - start;
    }

    private static String timeDifference(long timeDifference) {
        timeDifference = timeDifference / 1000;
        int h = (int) (timeDifference / (3600));
        int m = (int) ((timeDifference - (h * 3600)) / 60);
        int s = (int) (timeDifference - (h * 3600) - m * 60);

        return String.format("%02d:%02d:%02d", h, m, s);
    }

    @FunctionalInterface
    private interface benchmarkMethod {
        void run(dShop shop, int nItems);
    }

    private void shuffleMethod(dShop shop, int nItems) {
        List<dItem> itemsPool = new ArrayList<>();
        Set<String> already = new HashSet<>();
        Set<dItem> rolledItems = new HashSet<>();

        for (dItem item : shop.getItems())
            for (int i = 0; i < item.getRarity().getWeight(); i++)
                itemsPool.add(item);

        Collections.shuffle(itemsPool);

        for (int i = 0; i < nItems; i++) {
            if (rolledItems.size() == itemsPool.size()) break;
            dItem rolled = null;
            for (dItem dItem : itemsPool)
                if (!already.contains(dItem.getID())) { rolled = dItem; break; }

            if (rolled == null) break;
            rolledItems.add(rolled);
            already.add(rolled.getID());
        }

    }

    private void weightedRandom(dShop shop, int nTimes) {
        Set<dItem> rolledItems = new HashSet<>();

        WeightedRandom<dItem> random = WeightedRandom
                .fromCollection(shop.getItems(), dItem::clone, value -> value.getRarity().getWeight());

        for (int i = 0; i < nTimes; i++) {
            dItem rolled;
            if ((rolled = random.roll()) == null) break;

            random.remove(rolled);
            rolledItems.add(rolled);
        }

    }


}
