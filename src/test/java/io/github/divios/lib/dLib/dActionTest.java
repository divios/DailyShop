package io.github.divios.lib.dLib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.divios.dailyShop.DailyShop;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
public class dActionTest {

    private ServerMock server;
    private DailyShop plugin;
    @Mock
    private Inventory inv;
    @Mock
    private Player player;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        plugin = MockBukkit.load(DailyShop.class);

        Whitebox.setInternalState(DailyShop.class, "INSTANCE", plugin);

        PowerMockito.mockStatic(Bukkit.class);

        Server server = mock(Server.class);
        when(server.getVersion()).thenReturn("git-Paper-386 (MC: 1.17.1)");
        when(Bukkit.getServer()).thenReturn(server);
        when(Bukkit.getVersion()).thenReturn("git-Paper-386 (MC: 1.17.1)");

        ItemFactory itemFac = mock(ItemFactory.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFac);
        // Panel inventory
        when(Bukkit.createInventory(any(), Mockito.anyInt(), any())).thenReturn(inv);

        player = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);

    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    public void testEmpty() {
        dAction action = dAction.EMPTY;
        action.run(player, "");
    }

    @Test
    public void testOpenShop() {

    }

}
