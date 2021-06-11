package io.github.divios.lib.itemHolder;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.dailyrandomshop.conf_msg;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
        Common(() -> 100),
        UnCommon(() -> 80),
        Rare(() -> 60),
        Epic(() -> 40),
        Ancient(() -> 20),
        Legendary(() -> 10),
        Mythic(() -> 5),
        Unavailable(() -> 0);

        private static final rarityT[] vals = values();
        private Supplier<Integer> supplier;

        rarityT(Supplier<Integer> supplier) {
            this.supplier = supplier;
        }

        private int getWeight() { return supplier.get(); }

        private rarityT next() {
            return vals[(this.ordinal()+1) % vals.length];
        }


    }


}
