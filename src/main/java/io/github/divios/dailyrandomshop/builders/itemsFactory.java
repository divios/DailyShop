package io.github.divios.dailyrandomshop.builders;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.lorestategy.loreStategy;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class itemsFactory {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

    private List<dailyMetadataType> metadataToRemove;
    private HashMap<dailyMetadataType, String> metadataToAdd;
    private boolean removeAllMetadata = false;
    private ItemStack item = null;
    private List<Enchantment> enchantments = new ArrayList<>();
    private List<ItemFlag> flags = new ArrayList<>();
    private loreStategy LoreStategy = null;
    private strategy loreStrategyOption;

    private itemsFactory(ItemStack item,
                         HashMap<dailyMetadataType, String> metadataToAdd,
                         List<dailyMetadataType> metadataToRemove,
                         boolean removeAllMetadata,
                         List<Enchantment> enchantments,
                         List<ItemFlag> flags,
                         loreStategy LoreStategy,
                         strategy loreStrategyOption
    ) {

        this.item = item;
        this.metadataToAdd = metadataToAdd;
        this.metadataToRemove = metadataToRemove;
        this.removeAllMetadata = removeAllMetadata;
        this.enchantments = enchantments;
        this.flags = flags;
        this.LoreStategy = LoreStategy;
        this.loreStrategyOption = loreStrategyOption;

        constructItem();
    }

    private void constructItem() {
        if (!removeAllMetadata) {
            if (!metadataToAdd.isEmpty()) applyMetadata();
            if (!metadataToRemove.isEmpty()) removeMetadata();
        } else removeAllMetadata();
        if (LoreStategy != null) {
            if(loreStrategyOption == strategy.add) LoreStategy.setLore(item);
            else if (loreStrategyOption == strategy.remove) LoreStategy.removeLore(item);
        }
    }

    private void applyMetadata() {
        NBTItem nbtItem = new NBTItem(item);

        for (Map.Entry<dailyMetadataType, String> entry : metadataToAdd.entrySet()) {
            nbtItem.setString(entry.getKey().name(), entry.getValue());
        }
    }

    private void removeMetadata() {
        NBTItem nbtItem = new NBTItem(item);

        for (dailyMetadataType s : metadataToRemove) {
            nbtItem.removeKey(s.name());
        }
    }

    public void removeAllMetadata() {
        NBTItem nbtItem = new NBTItem(item);
        for (dailyMetadataType m : dailyMetadataType.values()) {
            nbtItem.removeKey(m.name());
        }
    }

    public ItemStack craft(boolean uuid) {
        if (!uuid) return item;

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(dailyMetadataType.rds_UUID.name(), UUID.randomUUID().toString());

        return nbtItem.getItem();
    }

    public enum dailyMetadataType {
        DailyItem,
        sellItem,
        rds_UUID,
        rds_amount,
        rds_rarity,
        rds_econ,
        rds_mmoitem
    }

    public static class Builder {

        private List<dailyMetadataType> metadataToRemove = new ArrayList<>();
        private HashMap<dailyMetadataType, String> metadataToAdd = new HashMap<>();
        private boolean removeAllMetadata = false;
        private ItemStack item = null;
        private List<Enchantment> enchantments = new ArrayList<>();
        private List<ItemFlag> flags = new ArrayList<>();
        private loreStategy LoreStategy;
        private strategy loreStategyOption;

        public Builder(Material material) {
            this.item = new ItemStack(material);
        }

        public Builder(ItemStack item, boolean clone) {
            if(clone) this.item = item.clone();
            else this.item = item;
        }

        public Builder(ItemStack item) {
            this.item = item;
        }

        public Builder setMaterial(Material m) {
            item.setType(m);
            return this;
        }

        public Builder setNbtToAdd(dailyMetadataType key, String value) {
            metadataToAdd.put(key, value);
            return this;
        }

        public Builder setNbtToRemove(dailyMetadataType key) {
            metadataToRemove.add(key);
            return this;
        }

        public boolean hasMetadata(dailyMetadataType key) {
            NBTItem nbtItem = new NBTItem(item);
            return nbtItem.hasKey(key.name());
        }

        public Builder removeAllMetadata() {
            removeAllMetadata = true;
            return this;
        }

        public Builder setEnchantments(Enchantment e) {
            enchantments.add(e);
            return this;
        }

        public Builder setEnchantments(List<Enchantment> e) {
            enchantments.addAll(e);
            return this;
        }

        public Builder setFlags(ItemFlag f) {
            flags.add(f);
            return this;
        }

        public Builder setFlags(List<ItemFlag> f) {
            flags.addAll(f);
            return this;
        }

        public Builder setLoreStrategy(loreStategy strategy, strategy str) {
            LoreStategy = strategy;
            loreStategyOption = str;
            return this;
        }

        public ItemStack craft() {
            return new itemsFactory(item, metadataToAdd, metadataToRemove, removeAllMetadata,
                    enchantments, flags, LoreStategy, loreStategyOption).craft(true);
        }

        public ItemStack getItem() {
            return new itemsFactory(item, metadataToAdd, metadataToRemove, removeAllMetadata,
                    enchantments, flags, LoreStategy, loreStategyOption).craft(false);
        }

        public String getUUID() {
            NBTItem nbtItem = new NBTItem(item);
            return nbtItem.getString(dailyMetadataType.rds_UUID.name());
        }

        public Double getPrice(Map<ItemStack, Double> list) {
            String uuid = getUUID();

            for (Map.Entry<ItemStack, Double> entry : list.entrySet()) {
                NBTItem nbtItem = new NBTItem(entry.getKey());
                if (nbtItem.getString(dailyMetadataType.rds_UUID.name()).equals(uuid)) return entry.getValue();
            }
            return null;
        }
    }

    public enum strategy {
        add,
        remove
    }
}
