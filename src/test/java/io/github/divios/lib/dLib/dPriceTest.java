package io.github.divios.lib.dLib;

import io.github.divios.dailyShop.DailyShop;
import org.bukkit.Bukkit;
import org.bukkit.Server;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
public class dPriceTest {

    @Mock
    private DailyShop plugin;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Whitebox.setInternalState(DailyShop.class, "INSTANCE", plugin);

        PowerMockito.mockStatic(Bukkit.class);

        Server server = mock(Server.class);
        when(server.getVersion()).thenReturn("git-Paper-386 (MC: 1.17.1)");
        when(Bukkit.getServer()).thenReturn(server);
        when(Bukkit.getVersion()).thenReturn("git-Paper-386 (MC: 1.17.1)");
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    public void testSingularPrice() {
        dPrice price = new dPrice(3.0);
        Assert.assertEquals(3.0, price.getPrice(), 0.0);
    }

    @Test
    public void testRandomPrice() {
        dPrice price = new dPrice(100, 1000);
        double priceD;
        for (int i = 0; i < 100; i++) {
            price.generateNewPrice();
            priceD = price.getPrice();
            Assert.assertTrue(priceD >= 100 && priceD <= 1000);
        }
    }

    @Test
    public void testStringFormat() {
        dPrice price = new dPrice(100.5, 1000);
        Assert.assertEquals(price.getVisualPrice(), "100.5 - 1,000");
    }

}
