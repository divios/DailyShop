package io.github.divios.lib.serialize.wrappers;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class WrappedNBT {

    private static final List<String> itemFlags =
            Arrays.asList("HideFlags", "HideAttributes", "HideDestroys", "HideDye", "HideEnchants",
                    "HidePlacedOn", "HidePotionEffects", "HideUnbreakable");

    private final JsonObject nbt;

    public static WrappedNBT valueOf(JsonObject object) {
        return new WrappedNBT(object);
    }

    public WrappedNBT(JsonObject nbt) {
        this.nbt = nbt;
        removeDailyNBT();
    }

    private void removeDailyNBT() {
        nbt.remove("rds_id");
        nbt.remove("display");
        nbt.remove("SkullOwner");
        nbt.remove("BlockEntityTag");
        nbt.remove("ms_mob");
        nbt.remove("SilkSpawners");
        itemFlags.forEach(nbt::remove);
    }

    public JsonObject getNbt() {
        return nbt;
    }

    public boolean isEmpty() {
        return nbt.size() == 0;
    }

}
