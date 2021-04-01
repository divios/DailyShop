package io.github.divios.dailyrandomshop.builders.factory.blocks;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem.dailyMetadataType;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class addMetadata implements runnableBlocks {

    private final dailyMetadataType type;
    private final Object value;

    public addMetadata(dailyMetadataType type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void run(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        switch (type) {
            case rds_commands:
                try {
                    List<String> aux = nbtItem.getObject(type.name(), List.class);
                    aux.add((String) value);
                    nbtItem.setObject(type.name(), aux);
                } catch (NullPointerException e) {
                    List<String> aux = new ArrayList<>();
                    aux.add((String) value);
                    nbtItem.setObject(type.name(), aux);
                }
                break;
            case rds_econ:
                nbtItem.setObject(type.name(), value);
                break;
            case rds_amount:
                nbtItem.setInteger(type.name(), Integer.valueOf((String) value));
                break;
            case rds_rarity:
                int i = processNextRarity(nbtItem.getInteger(type.name()));
                if (i == 0) nbtItem.removeKey(type.name());
                else nbtItem.setInteger(type.name(), i);
                break;
            default:
                nbtItem.setString(type.name(), (String) value);
                break;
        }

        utils.translateAllItemData(nbtItem.getItem(), item);
    }

    private int processNextRarity(int s) {
        switch (s) {
            case 80:
                return 60;
            case 60:
                return 40;
            case 40:
                return 20;
            case 20:
                return 10;
            case 10:
                return 5;
            case 5:
                return 0;
            default:
                return 80;
        }

    }
}