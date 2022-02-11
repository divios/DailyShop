package io.github.divios.shop;

import io.github.divios.lib.dLib.shop.ShopAccount;
import io.github.divios.dailyShop.utils.valuegenerators.FixedValueGenerator;
import org.junit.Assert;
import org.junit.Test;

public class ShopAccountTest {

    @Test
    public void testJson() {
        ShopAccount account = new ShopAccount(500, new FixedValueGenerator(300), 200);
        ShopAccount fromJson = ShopAccount.fromJson(account.toJson());

        Assert.assertEquals(account, fromJson);
    }

    @Test
    public void testIsSimilar() {
        ShopAccount account = new ShopAccount(500, new FixedValueGenerator(300), 200);
        ShopAccount account1 = new ShopAccount(500, new FixedValueGenerator(300), 100);

        Assert.assertTrue(account.isSimilar(account1));
    }

    @Test
    public void testDeposit() {
        ShopAccount account = new ShopAccount(Double.MAX_VALUE, new FixedValueGenerator(300));
        account.deposit(300);

        Assert.assertEquals(600, account.getBalance(), 0.0);
    }

    @Test
    public void testWithdraw() {
        ShopAccount account = new ShopAccount(Double.MAX_VALUE, new FixedValueGenerator(300));
        account.withdraw(100);

        Assert.assertEquals(200, account.getBalance(), 0.0);
    }

    @Test
    public void testDepositLimit() {
        ShopAccount account = new ShopAccount(400, new FixedValueGenerator(300));
        account.deposit(200);

        Assert.assertEquals(400, account.getBalance(), 0.0);
    }

    @Test
    public void testWithdrawLimit() {
        ShopAccount account = new ShopAccount(400, new FixedValueGenerator(300));
        account.withdraw(500);

        Assert.assertEquals(0, account.getBalance(), 0.0);
    }

}
