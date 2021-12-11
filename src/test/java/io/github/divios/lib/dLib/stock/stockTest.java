package io.github.divios.lib.dLib.stock;

import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class stockTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testToSting() {
        dStock inv = dStockFactory.INDIVIDUAL(3);
        dStock global = dStockFactory.GLOBAL(4);

        Assert.assertEquals(inv.toString(), "INDIVIDUAL:3");
        Assert.assertEquals(global.toString(), "GLOBAL:4");
    }

    @Test
    public void testDecrementIndividual() {
        dStock inv = dStockFactory.INDIVIDUAL(3);
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        inv.decrement(uuid1, 2);

        Assert.assertEquals(1, inv.get(uuid1).intValue());
        Assert.assertEquals( 3, inv.get(uuid2).intValue());
    }

    @Test
    public void testDecrementGlobal() {
        dStock inv = dStockFactory.GLOBAL(3);
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        inv.decrement(uuid1, 2);

        Assert.assertEquals(1, inv.get(uuid1).intValue());
        Assert.assertEquals( 1, inv.get(uuid2).intValue());
    }

}
