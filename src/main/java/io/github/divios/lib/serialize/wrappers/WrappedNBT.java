package io.github.divios.lib.serialize.wrappers;

import com.google.gson.JsonObject;

public class WrappedNBT {

    private final JsonObject nbt;

    public static WrappedNBT valueOf(JsonObject object) {
        return new WrappedNBT(object);
    }

    public WrappedNBT(JsonObject nbt) {
        this.nbt = nbt;
        removeDailyNBT();
    }

    private void removeDailyNBT() {
        nbt.remove("rds_UUID");
        nbt.remove("rds_rarity");
        nbt.remove("rds_sellPrice");
        nbt.remove("dailySlots");
        nbt.remove("rds_buyPrice");
        nbt.remove("rds_stock");
        nbt.remove("rds_cmds");
        nbt.remove("rds_AIR");
        nbt.remove("rds_action");
        nbt.remove("dailySlots");
        nbt.remove("rds_setItems");
        nbt.remove("rds_perms_buy");
        nbt.remove("rds_perms_sell");
        nbt.remove("rds_confirm_gui");
        nbt.remove("rds_bundle");
        nbt.remove("rds_econ");
        nbt.remove("rds_rawItem");
        nbt.remove("rds_econ");
        nbt.remove("display");
        nbt.remove("lore");
        nbt.remove("Enchantments");
    }

    public JsonObject getNbt() {
        return nbt;
    }

    public boolean isEmpty() {
        return nbt.size() == 0;
    }

}
