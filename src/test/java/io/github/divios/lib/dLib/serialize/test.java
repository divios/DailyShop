package io.github.divios.lib.dLib.serialize;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.serialize.serializerApi;
import io.github.divios.testUtils;
import org.bukkit.Bukkit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/*@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
@SuppressStaticInitializationFor("com.example.package.util.ClassUnderTest") */
public class test {
/*
    private ServerMock server;
    private DailyShop plugin;
    private serializerApi api;

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(DailyShop.class);

        Whitebox.setInternalState(DailyShop.class, "INSTANCE", plugin);

        when(DailyShop.getInstance()).thenReturn(plugin);
        when(plugin.getDataFolder()).thenReturn(new File(""));

       mockStatic(serializerApi.class);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
    }


    @Test
    public void testId() {
        File idTest = testUtils.getResource("tests/idTest.yml");
        Assert.assertThrows(IllegalArgumentException.class, () -> api.getShopFromFile(idTest));
        try {
            api.getShopFromFile(idTest);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("A shop needs an ID"));
        }
    }

    @Test
    public void testItems() {
        File noItemsTest = testUtils.getResource("tests/noItemsTest.yml");
        Assert.assertThrows(IllegalArgumentException.class, () -> api.getShopFromFile(noItemsTest));
        try {
            api.getShopFromFile(noItemsTest);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("A shop needs items"));
        }
    }

    @Test
    public void testTimerInt() {
        File noIntTest = testUtils.getResource("tests/timerIntTest.yml");
        Assert.assertThrows(IllegalArgumentException.class, () -> api.getShopFromFile(noIntTest));
        try {
            api.getShopFromFile(noIntTest);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("Timer needs to be an integer"));
        }
    }

    @Test
    public void testSuccess() {
        File successTest = testUtils.getResource("tests/successTest.yml");
        Assert.assertThrows(ExceptionInInitializerError.class, () -> api.getShopFromFile(successTest));
    } */

}
