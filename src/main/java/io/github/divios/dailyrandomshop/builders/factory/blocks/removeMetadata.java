package io.github.divios.dailyrandomshop.builders.factory.blocks;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.factory.itemsFactory.dailyMetadataType;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

public class removeMetadata implements runnableBlocks{

    private final dailyMetadataType type;

    public removeMetadata(dailyMetadataType type) {
        this.type = type;
    }

    /**
     *
     *  remove they key of the give item
     */

    @Override
    public void run(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey(type.name());

        utils.translateAllItemData(nbtItem.getItem(), item);

    }
}
