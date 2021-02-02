package io.github.divios.dailyrandomshop.builders.factory;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.factory.blocks.*;
import io.github.divios.dailyrandomshop.builders.lorestategy.loreStrategy;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class itemsFactory {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

    private final ItemStack item;
    private final List<runnableBlocks> runnableBlocks;

    private itemsFactory(ItemStack item,
                         List<runnableBlocks> runnableBlocks
    ) {

        this.item = item;
        this.runnableBlocks = runnableBlocks;

        constructItem();
    }

    private void constructItem() {
        runnableBlocks.forEach(r -> r.run(item));
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
        rds_mmoitem,
        rds_commands
    }

    public static class Builder {

        private ItemStack item = null;
        List<runnableBlocks> runnableBlocks = new ArrayList<>();

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

        public Builder addNbt(dailyMetadataType key, String value) {
            runnableBlocks.add(new addMetadata(key, value));
            return this;
        }

        public Builder removeNbt(dailyMetadataType key) {
            runnableBlocks.add(new removeMetadata(key));
            return this;
        }

        public boolean hasMetadata(dailyMetadataType key) {
            NBTItem nbtItem = new NBTItem(item);
            return nbtItem.hasKey(key.name());
        }

        public Object getMetadata(dailyMetadataType key) {
            NBTItem nbtItem = new NBTItem(item);
            Object obj;
            switch (key) {
                case rds_amount:
                    obj = nbtItem.getInteger(key.name()); break;
                case rds_commands:
                    obj = nbtItem.getObject(key.name(), List.class);
                    if(obj == null) obj = new ArrayList<>();
                    break;
                case rds_rarity:
                   return nbtItem.getInteger(key.name());
                default: obj = nbtItem.getString(key.name()); break;
            }
            return obj;
        }

        public Builder removeAllMetadata() {
            for(dailyMetadataType s: dailyMetadataType.values()) {
                runnableBlocks.add(new removeMetadata(s));
            }
            return this;
        }

        public Builder addLoreStrategy(loreStrategy s) {
            runnableBlocks.add(new addLoreStrategy(s));
            return this;
        }

        @Deprecated
        public Builder removeLoreStrategy(loreStrategy s) {
            runnableBlocks.add(new removeLoreStrategy(s));
            return this;
        }

        public ItemStack craft() {
            return new itemsFactory(item, runnableBlocks).craft(true);
        }

        public ItemStack getItem() {
            return new itemsFactory(item, runnableBlocks).craft(false);
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

}
