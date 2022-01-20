package io.github.divios.lib.serialize.wrappers;

import com.google.gson.JsonElement;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class WrappedNBT {

    private static final List<String> itemFlags =
            Arrays.asList("HideFlags", "HideAttributes", "HideDestroys", "HideDye", "HideEnchants",
                    "HidePlacedOn", "HidePotionEffects", "HideUnbreakable");

    private final NBTItem nbt;

    public static ItemStack mergeNBT(ItemStack item, JsonElement nbt) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.mergeCompound(new NBTContainer(
                nbt.isJsonObject()
                        ? nbt.getAsJsonObject().toString()
                        : nbt.getAsString()
        ));

        return nbtItem.getItem();
    }

    public static WrappedNBT valueOf(ItemStack item) {
        return new WrappedNBT(new NBTItem(item));
    }

    public WrappedNBT(NBTItem nbt) {
        this.nbt = nbt;
        removeDailyNBT();
    }

    private void removeDailyNBT() {
        nbt.removeKey("rds_id");
        nbt.removeKey("display");
        nbt.removeKey("Enchantments");
        nbt.removeKey("Potion");
        nbt.removeKey("SkullOwner");
        nbt.removeKey("BlockEntityTag");
        nbt.removeKey("ms_mob");
        nbt.removeKey("SilkSpawners");
        itemFlags.forEach(nbt::removeKey);
    }

    public String getNbt() {
        return nbt.toString();
    }

    public boolean isEmpty() {
        return nbt == null || nbt.getKeys().isEmpty();
    }

}
