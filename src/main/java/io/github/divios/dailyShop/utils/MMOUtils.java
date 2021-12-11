package io.github.divios.dailyShop.utils;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.inventory.ItemStack;

public class MMOUtils {

    public static boolean isMMOItemsOn() {
        return Utils.isOperative("MMOItems");
    }

    public static boolean isMMOItem(ItemStack item) {
        return io.lumine.mythic.lib.api.item.NBTItem.get(item).hasType();
    }

    public static boolean compareMMOItems(ItemStack ItemCompared, ItemStack ItemToCompare) {
        MMOItemData mmoitem = MMOItemData.create(ItemCompared);
        MMOItemData mmoitem2 = MMOItemData.create(ItemToCompare);

        if (mmoitem.getId() == null || mmoitem.getType() == null || mmoitem2.getId() == null || mmoitem2.getType() == null)
            return false;

        return mmoitem.getType().equals(mmoitem2.getType()) && mmoitem.getId().equals(mmoitem2.getId());
    }

    public static ItemStack createNewMMOItem(ItemStack item) {
        if (!isMMOItem(item)) return item;

        io.lumine.mythic.lib.api.item.NBTItem mmoitem = io.lumine.mythic.lib.api.item.NBTItem.get(item);
        String type = mmoitem.getType();
        String id = mmoitem.getString("MMOITEMS_ITEM_ID");

        return MMOItems.plugin.getItem(net.Indyuce.mmoitems.api.Type.get(type), id);

    }

    private static class MMOItemData {

        private final String id;
        private final String type;

        public static MMOItemData create(ItemStack item) {
            io.lumine.mythic.lib.api.item.NBTItem mmoitem = io.lumine.mythic.lib.api.item.NBTItem.get(item);
            return new MMOItemData(mmoitem.getString("MMOITEMS_ITEM_ID"), mmoitem.getType());
        }

        private MMOItemData(String id, String type) {
            this.id = id;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }
    }

}
