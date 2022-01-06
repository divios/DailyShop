package io.github.divios.lib.dLib;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.dailyShop.files.Lang;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class dRarity implements Cloneable {

    private transient static final List<ItemStack> items = new ArrayList<ItemStack>() {
        {
            add(XMaterial.GRAY_DYE.parseItem());
            add(XMaterial.PINK_DYE.parseItem());
            add(XMaterial.MAGENTA_DYE.parseItem());
            add(XMaterial.PURPLE_DYE.parseItem());
            add(XMaterial.CYAN_DYE.parseItem());
            add(XMaterial.ORANGE_DYE.parseItem());
            add(XMaterial.YELLOW_DYE.parseItem());
            add(XMaterial.BARRIER.parseItem());

        }
    };

    private rarityT rarity = rarityT.Common;

    public dRarity() {
    }

    public static dRarity fromKey(String key) {
        dRarity dRarity = new dRarity();
        for (rarityT value : rarityT.values()) {
            if (value.name().equalsIgnoreCase(key)) {
                dRarity.rarity = value;
            }
        }
        return dRarity;
    }

    /**
     * Gets the rarity as an item
     */
    public ItemStack getAsItem() {
        return ItemBuilder.of(items.get(rarity.ordinal()).clone())
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
     */
    public int getWeight() {
        return rarity.getWeight();
    }

    /**
     * Gets the rarity lore
     */
    private String getRarityLore() {
        try {
            return Lang.CUSTOMIZE_RARITY_TYPES.getAsListString().get(rarity.ordinal());
        } catch (Exception | Error e) {
            return rarity.name();
        }
    }

    public String getKey() {
        return rarity.name();
    }

    @Override
    public String toString() {
        return getRarityLore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dRarity dRarity = (dRarity) o;
        return rarity == dRarity.rarity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rarity);
    }

    @Override
    public dRarity clone() {
        try {
            return (dRarity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private enum rarityT {
        Common,
        UnCommon,
        Rare,
        Epic,
        Ancient,
        Legendary,
        Mythic,
        Unavailable;

        private static final List<Integer> weights = Arrays.asList(100, 80, 60, 40, 20, 10, 5, 0);
        private static final rarityT[] vals = values();

        rarityT() {
        }

        private int getWeight() {
            return weights.get(this.ordinal());
        }

        private rarityT next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }


    }


}
