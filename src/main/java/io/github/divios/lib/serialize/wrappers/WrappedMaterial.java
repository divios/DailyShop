package io.github.divios.lib.serialize.wrappers;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class WrappedMaterial {

    private final String material;

    public static WrappedMaterial of(String material) {
        return new WrappedMaterial(material);
    }

    public static String getMaterial(ItemStack item) {
        String material;
        if (ItemUtils.getMaterial(item) == XMaterial.PLAYER_HEAD.parseMaterial()
                && SkullUtils.getSkinValue(ItemUtils.getMetadata(item)) != null)
            material = "base64:" + SkullUtils.getSkinValue(ItemUtils.getMetadata(item));
        else
            material = ItemUtils.getMaterial(item).name();

        return material;
    }

    public WrappedMaterial(String material) {
        this.material = material;
    }

    public ItemStack parseItem() {
        XMaterial item =  XMaterial.matchXMaterial(material)
                .orElseThrow(() -> new RuntimeException("Material not supported in this version?"));
        return Objects.requireNonNull(item.parseItem(), "Material not supported in this version?");
    }

}
