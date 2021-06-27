package io.github.divios.lib.dLib;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.dailyShop.conf_msg;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class dRarity {

    private transient static final List<ItemStack> items = new ArrayList<ItemStack>() {{
        add(XMaterial.GRAY_DYE.parseItem());
        add(XMaterial.PINK_DYE.parseItem());
        add(XMaterial.MAGENTA_DYE.parseItem());
        add(XMaterial.PURPLE_DYE.parseItem());
        add(XMaterial.CYAN_DYE.parseItem());
        add(XMaterial.ORANGE_DYE.parseItem());
        add(XMaterial.YELLOW_DYE.parseItem());
        add(XMaterial.BARRIER.parseItem());

    } };

    private rarityT rarity = rarityT.Common;

    public dRarity() {}

    /**
     * Gets the rarity as an item
     * @return
     */
    public ItemStack getAsItem() {
        return new ItemBuilder(items.get(rarity.ordinal()).clone())
                .setName(getRarityLore());
    }

    /**
     * Process next rarity
     */
    public dRarity next() {
        rarity = rarity.next();
        return this;
    }

    /**
     * Gets the weight of this rarity
     * @return
     */
    public int getWeight() { return rarity.getWeight(); }

    /**
     * Gets the rarity lore
     * @return
     */
    private String getRarityLore() {
        try {
            return conf_msg.RARITY_NAMES.get(rarity.ordinal());
        } catch (Exception e) {
            return rarity.name();
        }
    }

    @Override
    public String toString() {
        return getRarityLore();
    }

    private enum rarityT {
        Common(100),
        UnCommon(80),
        Rare(60),
        Epic(40),
        Ancient(20),
        Legendary(10),
        Mythic(5),
        Unavailable(0);

        private static final rarityT[] vals = values();
        private final int weight;

        rarityT(Integer weight) { this.weight = weight; }

        private int getWeight() { return weight; }

        private rarityT next() {
            return vals[(this.ordinal()+1) % vals.length];
        }


    }


}
