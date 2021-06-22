package io.github.divios.dailyShop.builders.itemBuildersHooks;

import io.github.divios.dailyShop.utils.utils;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;

public class MMOItems implements itemsBuilder{

    @Override
    public boolean isItem(ItemStack item) {
        try {
            net.mmogroup.mmolib.api.item.NBTItem NBTItem = net.mmogroup.mmolib.api.item.NBTItem.get(item);
            return NBTItem.hasType();
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            return false;
        }
    }

    @Override
    public ItemStack getItem(ItemStack item) {
        if (!isItem(item)) return null;
        String[] constructor = getMMOItemConstruct(item.clone());
        return net.Indyuce.mmoitems.MMOItems.plugin.getItem(Type.get(constructor[0]), constructor[1]);
    }

    @Override
    public String getUuid(ItemStack item) {
        net.mmogroup.mmolib.api.item.NBTItem NBTItem = net.mmogroup.mmolib.api.item.NBTItem.get(item.clone());
        return NBTItem.getString("MMOITEMS_ITEM_ID");
    }

    @Override
    public boolean updateItem(ItemStack toUpdate) {
        if (!isItem(toUpdate)) return false;
        ItemStack auxitem = getItem(toUpdate);
        //dailyItem.transferDailyMetadata(toUpdate, auxitem);
        utils.translateAllItemData(auxitem, toUpdate);
        return true;
    }

    private String[] getMMOItemConstruct(ItemStack item) {

        net.mmogroup.mmolib.api.item.NBTItem NBTItem = net.mmogroup.mmolib.api.item.NBTItem.get(item.clone());
        String type = NBTItem.getType();
        String id = NBTItem.getString("MMOITEMS_ITEM_ID");

        return new String[]{type, id};
    }

}
