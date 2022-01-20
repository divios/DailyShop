package io.github.divios.lib.serialize.wrappers;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class WrappedMaterial {

    private static final UUID skullUUID = UUID.nameUUIDFromBytes("randomSkull".getBytes());

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
        if (material.startsWith("base64:")) {
            return ItemUtils.applyTexture(SkullUtils.getSkull(skullUUID), material.substring(7));
        } else {
            XMaterial item = XMaterial.matchXMaterial(material)
                    .orElseThrow(() -> new RuntimeException("Material not supported in this version?"));
            return Objects.requireNonNull(item.parseItem(), "Material not supported in this version?");
        }
    }

}
