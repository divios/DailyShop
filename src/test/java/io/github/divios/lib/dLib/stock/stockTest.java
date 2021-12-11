package io.github.divios.lib.dLib.stock;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.databaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
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
        Mockito.framework().clearInlineMocks();
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
