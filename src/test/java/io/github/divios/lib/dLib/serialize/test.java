package io.github.divios.lib.dLib.serialize;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.serialize.serializerApi;
import io.github.divios.testUtils;
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

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
public class test {

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
    public void testId() {
        File idTest = testUtils.getResource("tests/idTest.yml");
        Assert.assertThrows(IllegalArgumentException.class, () -> serializerApi.getShopFromFile(idTest));
        try {
            serializerApi.getShopFromFile(idTest);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("A shop needs an ID"));
        }
    }

    @Test
    public void testItems() {
        File noItemsTest = testUtils.getResource("tests/noItemsTest.yml");
        Assert.assertThrows(IllegalArgumentException.class, () -> serializerApi.getShopFromFile(noItemsTest));
        try {
            serializerApi.getShopFromFile(noItemsTest);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("A shop needs items"));
        }
    }

    @Test
    public void testTimerInt() {
        File noIntTest = testUtils.getResource("tests/timerIntTest.yml");
        Assert.assertThrows(IllegalArgumentException.class, () -> serializerApi.getShopFromFile(noIntTest));
        try {
            serializerApi.getShopFromFile(noIntTest);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("Timer needs to be an integer"));
        }
    }

    @Test
    public void testSuccess() {
        File successTest = testUtils.getResource("tests/successTest.yml");
        Assert.assertThrows(NullPointerException.class, () -> serializerApi.getShopFromFile(successTest));
    }

}
