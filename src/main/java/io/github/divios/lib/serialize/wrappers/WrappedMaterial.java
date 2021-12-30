package io.github.divios.lib.serialize.wrappers;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.lib.dLib.dItem;

public class WrappedMaterial {

    private final String material;

    public static WrappedMaterial of(String material) {
        return new WrappedMaterial(material);
    }

    public static String getMaterial(dItem item) {
        String material;
        if (!item.isCustomHead())
            material = ItemUtils.getMaterial(item.getRealItem()).name();
        else
            material = "base64:" + item.getCustomHeadUrl();

        return material;
    }

    public WrappedMaterial(String material) {
        this.material = material;
    }

    public dItem parseItem() {
        dItem ditem = dItem.of(XMaterial.DIRT.parseItem());

        if (material.startsWith("base64:")) ditem.setCustomPlayerHead(material.replace("base64:", ""));
        else ditem.setMaterial(XMaterial.valueOf(material));

        return ditem;
    }

}
