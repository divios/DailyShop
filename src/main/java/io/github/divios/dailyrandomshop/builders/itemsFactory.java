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

    private List<metadataType> metadataToRemove;
    private HashMap<metadataType, String> metadataToAdd;
    private boolean removeAllMetadata = false;
    private ItemStack item = null;
    private List<Enchantment> enchantments = new ArrayList<>();
    private List<ItemFlag> flags = new ArrayList<>();
    private loreStategy LoreStategy = null;

    private itemsFactory(ItemStack item,
                         HashMap<metadataType, String> metadataToAdd,
                         List<metadataType> metadataToRemove,
                         boolean removeAllMetadata,
                         List<Enchantment> enchantments,
                         List<ItemFlag> flags,
                         loreStategy LoreStategy
    ) {

        this.item = item;
        this.metadataToAdd = metadataToAdd;
        this.metadataToRemove = metadataToRemove;
        this.removeAllMetadata = removeAllMetadata;
        this.enchantments = enchantments;
        this.flags = flags;
        this.LoreStategy = LoreStategy;

        constructItem();
    }

    private void constructItem() {
        if (!removeAllMetadata) {
            if (!metadataToAdd.isEmpty()) applyMetadata();
            if (!metadataToRemove.isEmpty()) removeMetadata();
        } else removeAllMetadata();
        if (LoreStategy != null) LoreStategy.setLore(item);
    }

    private void applyMetadata() {
        NBTItem nbtItem = new NBTItem(item);

        for (Map.Entry<metadataType, String> entry : metadataToAdd.entrySet()) {
            nbtItem.setString(entry.getKey().name(), entry.getValue());
        }
    }

    private void removeMetadata() {
        NBTItem nbtItem = new NBTItem(item);

        for (metadataType s : metadataToRemove) {
            nbtItem.removeKey(s.name());
        }
    }

    public void removeAllMetadata() {
        NBTItem nbtItem = new NBTItem(item);
        for (metadataType m : metadataType.values()) {
            nbtItem.removeKey(m.name());
        }
    }

    public ItemStack craft(boolean uuid) {
        NBTItem nbtItem = new NBTItem(item);
        if (!uuid) return nbtItem.getItem();

        String metadataStr = item.getItemMeta().toString();
        nbtItem.setString(metadataType.rds_UUID.name(), UUID.fromString(metadataStr).toString());

        return nbtItem.getItem();
    }

    public enum metadataType {
        DailyItem,
        sellItem,
        rds_UUID,
        rds_amount,
        rds_rarity,
        rds_econ,
        rds_mmoitem
    }

    public static class Builder {

        private List<metadataType> metadataToRemove;
        private HashMap<metadataType, String> metadataToAdd;
        private boolean removeAllMetadata = false;
        private ItemStack item = null;
        private List<Enchantment> enchantments = new ArrayList<>();
        private List<ItemFlag> flags = new ArrayList<>();
        private loreStategy LoreStategy;

        public Builder(Material material) {
            this.item = new ItemStack(material);
        }

        public Builder(ItemStack item, boolean clone) {
            if(clone) this.item = item.clone();
            else this.item = item;
        }

        public Builder setMaterial(Material m) {
            item.setType(m);
            return this;
        }

        public Builder setAmount(int a) {
            item.setAmount(a);
            return this;
        }

        public Builder setNbtToAdd(metadataType key, String value) {
            metadataToAdd.put(key, value);
            return this;
        }

        public Builder setNbtToRemove(metadataType key) {
            metadataToRemove.add(key);
            return this;
        }

        public Builder removeMetadata() {
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

        public Builder setLoreStategy(loreStategy strategy) {
            LoreStategy = strategy;
            return this;
        }

        public ItemStack craft() {
            return new itemsFactory(item, metadataToAdd, metadataToRemove, removeAllMetadata,
                    enchantments, flags, LoreStategy).craft(true);
        }

        public ItemStack getItem() {
            return new itemsFactory(item, metadataToAdd, metadataToRemove, removeAllMetadata,
                    enchantments, flags, LoreStategy).craft(false);
        }

        public Double getPrice(ItemStack item, Map<ItemStack, Double> list) {
            String uuid = UUID.fromString(item.getItemMeta().toString()).toString();

            for (Map.Entry<ItemStack, Double> entry : list.entrySet()) {
                NBTItem nbtItem = new NBTItem(entry.getKey());
                if (nbtItem.getString(metadataType.rds_UUID.name()).equals(uuid)) return entry.getValue();
            }
            return -1D;
        }
    }
}
