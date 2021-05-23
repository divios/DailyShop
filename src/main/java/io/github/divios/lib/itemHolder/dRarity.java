package io.github.divios.lib.itemHolder;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.th0rgal.oraxen.shaded.syntaxapi.utils.java.Arrays;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
        ItemStack item = items.get(rarity.ordinal());
        utils.setDisplayName(item, getRarityLore());
        return item;
    }

    /**
     * Process next rarity
     */
    public dRarity next() {
        rarity = rarity.next();
        return this;
    }

    /**
     * Gets the rarity lore
     * @return
     */
    private String getRarityLore() {

        switch (rarity) {
            case Common:
                try {
                    return conf_msg.RARITY_NAMES.get(0);
                } catch (Exception ignored) {}
                return "Common";
            case UnCommon:
                try {
                    return conf_msg.RARITY_NAMES.get(1);
                } catch (Exception ignored) {}
                return "UnCommon";
            case Rare:
                try {
                    return conf_msg.RARITY_NAMES.get(2);
                } catch (Exception ignored) {}
                return "Rare";
            case Epic:
                try {
                    return conf_msg.RARITY_NAMES.get(3);
                } catch (Exception ignored) {}
                return "Epic";
            case Ancient:
                try {
                    return conf_msg.RARITY_NAMES.get(4);
                } catch (Exception ignored) {}
                return "Ancient";
            case Legendary:
                try {
                    return conf_msg.RARITY_NAMES.get(5);
                } catch (Exception ignored) {}
                return "&6Legendary";
            case Mythic:
                try {
                    return conf_msg.RARITY_NAMES.get(6);
                } catch (Exception ignored) {}
                return "Mythic";
            default:
                try {
                    return conf_msg.RARITY_NAMES.get(7);
                } catch (Exception ignored) {}
                return "Unavailable";
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

        private static final rarityT[] vals = values();

        public rarityT next() {
            return vals[(this.ordinal()+1) % vals.length];
        }

    }


}
