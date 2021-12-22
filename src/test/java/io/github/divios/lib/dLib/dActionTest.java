package io.github.divios.lib.dLib;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.databaseManager;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
public class dActionTest {

    @Mock
    private DailyShop plugin;

    private databaseManager dManager;

    private shopsManager sManager;

    private dShop shop;

    @Mock
    private Inventory inv;
    @Mock
    private Player player;
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

        dManager = mock(databaseManager.class);
        when(DailyShop.get().getDatabaseManager()).thenReturn(dManager);

        sManager = mock(shopsManager.class);
        when(DailyShop.get().getShopsManager()).thenReturn(sManager);

        shop = mock(dShop.class);
        when(sManager.getShop("asdf")).thenReturn(Optional.of(shop));

        ItemFactory itemFac = mock(ItemFactory.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFac);
        // Panel inventory
        when(Bukkit.createInventory(any(), Mockito.anyInt(), any())).thenReturn(inv);

        player = mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getDisplayName()).thenReturn("Divios");
        when(player.getName()).thenReturn("Divios");

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
        dAction action = dAction.OPEN_SHOP;
        action.run(player, "asdf");
        verify(shop).openShop(any());
    }

    @Test
    public void testRunCmd() {
        dAction action = dAction.RUN_CMD;
        action.run(player, "broadcast hello");

        PowerMockito.verifyStatic(Bukkit.class);
        Bukkit.dispatchCommand(null, "broadcast hello");
    }

    /*TODO SHOW ALL ITEMS */

}
