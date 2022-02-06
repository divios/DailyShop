package io.github.divios.lib.dLib.nmsInventory.util;

import com.cryptomorin.xseries.ReflectionUtils;
import io.github.divios.dailyShop.utils.NMSUtils.NMSHelper;
import io.github.divios.dailyShop.utils.NMSUtils.NMSObject;
import org.bukkit.entity.Player;

public class NMSContainer {

    public static int getPlayerInventoryID(Player p) {
        Class<?> craftClass = NMSHelper.getClass(ReflectionUtils.CRAFTBUKKIT + "entity.CraftPlayer")
                .getWrappedClass();

       return (int) new NMSObject(craftClass.cast(p))
               .callMethod("getHandle").getField(1, "bW").getField(1, "j").getObject();

    }

}
