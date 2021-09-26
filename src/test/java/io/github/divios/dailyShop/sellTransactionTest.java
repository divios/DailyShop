package io.github.divios.dailyShop;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.divios.dailyShop.transaction.sellTransaction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class sellTransactionTest {

    private ServerMock server;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void checkEnoughItemsCheck() throws Exception {
        PlayerMock player = server.addPlayer();
        ItemStack item = new ItemStack(Material.ACACIA_LOG);




    }

}