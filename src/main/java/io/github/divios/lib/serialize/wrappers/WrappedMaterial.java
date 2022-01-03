package io.github.divios.lib.serialize.wrappers;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class WrappedMaterial {

    private final String material;

    public static WrappedMaterial of(String material) {
        return new WrappedMaterial(material);
    }

    public static String getMaterial(ItemStack item) {
        String material;
        //if (!item.isCustomHead())
        material = ItemUtils.getMaterial(item).name();
        //else  // TODO
        //    material = "base64:" + item.getCustomHeadUrl();

        return material;
    }

    public WrappedMaterial(String material) {
        this.material = material;
    }

    public ItemStack parseItem() {
        //dItem ditem = dItem.of(XMaterial.DIRT.parseItem());

        //if (material.startsWith("base64:")) ditem.setCustomPlayerHead(material.replace("base64:", ""));
        //else ditem.setMaterial(XMaterial.valueOf(material));

        return XMaterial.valueOf(material).parseItem();
    }

}
