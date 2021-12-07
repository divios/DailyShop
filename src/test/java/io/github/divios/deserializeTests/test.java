package io.github.divios.deserializeTests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.serialize.serializerApi;
import io.github.divios.testUtils;
import org.bukkit.Bukkit;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
public class test {

    private ServerMock server;
    private DailyShop plugin;
    private serializerApi api;

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(DailyShop.class);

        when(DailyShop.getInstance()).thenReturn(plugin);
        when(plugin.getDataFolder()).thenReturn(new File(""));

        api = (serializerApi) mockStatic(serializerApi.class);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
    }


    @Test
    public void testId() {
        File idTest = testUtils.getResource("tests/idTest.yml");
        Assertions.assertThrows(IllegalArgumentException.class, () -> api.getShopFromFile(idTest));
        try {
            api.getShopFromFile(idTest);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().equals("A shop needs an ID"));
        }
    }

    @Test
    public void testItems() {
        File noItemsTest = testUtils.getResource("tests/noItemsTest.yml");
        Assertions.assertThrows(IllegalArgumentException.class, () -> api.getShopFromFile(noItemsTest));
        try {
            api.getShopFromFile(noItemsTest);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().equals("A shop needs items"));
        }
    }

    @Test
    public void testTimerInt() {
        File noIntTest = testUtils.getResource("tests/timerIntTest.yml");
        Assertions.assertThrows(IllegalArgumentException.class, () -> api.getShopFromFile(noIntTest));
        try {
            api.getShopFromFile(noIntTest);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().equals("Timer needs to be an integer"));
        }
    }

    @Test
    public void testSuccess() {
        File successTest = testUtils.getResource("tests/successTest.yml");
        Assertions.assertThrows(ExceptionInInitializerError.class, () -> api.getShopFromFile(successTest));
    }

}
