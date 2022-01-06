package io.github.divios.lib.serialize.wrappers;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class WrapperSpawnerItem {

    public static boolean isSpawner(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey("BlockEntityTag")
                && nbtItem.getCompound("BlockEntityTag").hasKey("EntityId");
    }

    public static String getSpawnerName(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        return EntityType.fromName(nbtItem.getCompound("BlockEntityTag").getString("EntityId")).name();
    }

    public static ItemStack setSpawnerMeta(ItemStack item, EntityType type) {

        NBTItem nbtItem = new NBTItem(item);

        nbtItem.setString("ms_mob", type.getName());  // MineableSpawner

        NBTCompound compound = nbtItem.getOrCreateCompound("SilkSpawners");  // SilkSpawner
        compound.setString("entity", type.getName());

        compound = nbtItem.getOrCreateCompound("BlockEntityTag");  // Vanilla
        compound.setString("EntityId", type.getName());

        NBTCompound subCompound = compound.getOrCreateCompound("EntityTag");
        subCompound.setString("id", "minecraft:" + type.getName());

        subCompound = compound.getOrCreateCompound("SpawnData");
        subCompound.setString("id", "minecraft:" + type.getName());

        compound.getOrCreateCompound("SpawnPotentials");

        compound.setString("id", "mob_spawner");

        compound = nbtItem.getOrCreateCompound("SilkSpawners");  // SilkSpawner
        compound.setString("entity", type.getName());

        compound = nbtItem.getOrCreateCompound("BlockEntityTag");  // Vanilla
        compound.setString("EntityId", type.getName());

        subCompound = compound.getOrCreateCompound("EntityTag");
        subCompound.setString("id", "minecraft:" + type.getName());

        subCompound = compound.getOrCreateCompound("SpawnData");
        subCompound.setString("id", "minecraft:" + type.getName());

        compound.getOrCreateCompound("SpawnPotentials");

        compound.setString("id", "mob_spawner");

        return nbtItem.getItem();
    }

}
