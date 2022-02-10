package io.github.divios.shop;

import io.github.divios.lib.dLib.shop.ShopAccount;
import io.github.divios.lib.dLib.shop.util.generators.FixedValueGenerator;
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

}
