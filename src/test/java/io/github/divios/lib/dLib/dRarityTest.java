package io.github.divios.lib.dLib;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.configManager;
import io.github.divios.dailyShop.files.langResource;
import org.bukkit.Bukkit;
import org.bukkit.Server;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DailyShop.class, Bukkit.class})
public class dRarityTest {

    @Mock
    private DailyShop plugin;
    @Mock
    private Inventory inv;

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

        ItemFactory itemFac = mock(ItemFactory.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFac);
        // Panel inventory
        when(Bukkit.createInventory(any(), Mockito.anyInt(), any())).thenReturn(inv);

    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() {
       Mockito.framework().clearInlineMocks();
    }

    @Test
    public void testFromKey() {
        List<String> keys = Arrays.asList("CoMmon", "UncOmMon", "rare", "epic", "ancient", "legendary", "mythic", "unavailable");
        List<String> expected = Arrays.asList("Common", "UnCommon", "Rare", "Epic", "Ancient", "Legendary", "Mythic", "Unavailable");

        for (int i = 0; i < keys.size(); i++) {
            Assert.assertEquals(dRarity.fromKey(keys.get(i)).getKey(), expected.get(i));
        }
    }

    @Test
    public void testNextKey() {
        List<String> keys = Arrays.asList("CoMmon", "UncOmMon", "rare", "epic", "ancient", "legendary", "mythic", "unavailable");
        List<String> expected = Arrays.asList("UnCommon", "Rare", "Epic", "Ancient", "Legendary", "Mythic", "Unavailable", "Common");

        for (int i = 0; i < keys.size(); i++) {
            Assert.assertEquals(dRarity.fromKey(keys.get(i)).next().getKey(), expected.get(i));
        }
    }

    /*@Test
    public void testCustomRarityTypes() {

        configManager manager = mock(configManager.class);
        langResource lang = mock(langResource.class);
        when(manager.getLangYml()).thenReturn(lang);
        Whitebox.setInternalState(DailyShop.class, "configM", manager);

        List<String> customRarities = Arrays.asList("CommonCustom", "UnCommonCustom", "RareCustom", "EpicCustom", "AncientCustom", "LegendaryCustom", "MythicCustom", "UnavailableCustom");
        List<String> keys = Arrays.asList("Common", "UnCommon", "Rare", "Epic", "Ancient", "Legendary", "Mythic", "Unavailable");

        when(lang.CUSTOMIZE_RARITY_TYPES).thenReturn(customRarities);

        for (int i = 0; i < keys.size(); i++) {
            Assert.assertEquals(dRarity.fromKey(keys.get(i)).toString(), customRarities.get(i));
        }
    } */

}
