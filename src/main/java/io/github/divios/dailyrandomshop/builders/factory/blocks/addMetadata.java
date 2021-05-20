package io.github.divios.dailyrandomshop.builders.factory.blocks;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
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

            case rds_bundle:
            case rds_commands:
            case rds_permissions:
                try {
                    List<String> aux = nbtItem.getObject(type.name(), List.class);
                    if ( !((String) value).isEmpty())
                            aux.add((String) value);
                    nbtItem.setObject(type.name(), aux);
                } catch (NullPointerException e) {
                    List<String> aux = new ArrayList<>();
                    aux.add((String) value);
                    nbtItem.setObject(type.name(), aux);
                }
                break;
            case rds_price:
                ((dailyItem.dailyItemPrice) value).generateRandomPrice();
                nbtItem.setObject(type.name(), value);
                break;
            case rds_tEcon:
                nbtItem.setObject(type.name(), value);
                break;
            case rds_setItems:
            case rds_amount:
                nbtItem.setInteger(type.name(), Integer.valueOf((String) value));
                break;
            case rds_rarity:
                int i = processNextRarity(nbtItem.getInteger(type.name()));
                if (i == 0) nbtItem.removeKey(type.name());
                else nbtItem.setInteger(type.name(), i);
                break;
            case rds_confirm_gui:
                nbtItem.setBoolean(type.name(), (Boolean) value);
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
                return -1;
            case -1:
                return 0;
            default:
                return 80;
        }

    }
}