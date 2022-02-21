package io.github.divios.lib.dLib.shop.view.util;

import com.cryptomorin.xseries.ReflectionUtils;
import io.github.divios.dailyShop.utils.NMSUtils.NMSHelper;
import io.github.divios.dailyShop.utils.NMSUtils.NMSObject;
import org.bukkit.entity.Player;

public class NMSContainerID {

    public static int getPlayerInventoryID(Player p) {

        Class<?> craftClass = NMSHelper.getClass(ReflectionUtils.CRAFTBUKKIT + "entity.CraftPlayer")
                .getWrappedClass();

        if (ReflectionUtils.VER >= 17)
            return (int) new NMSObject(craftClass.cast(p))
                    .callMethod("getHandle")
                    .getField(1, "bW")
                    .getField(1, "j")
                    .getObject();
        else
            return (int) new NMSObject(craftClass.cast(p))
                    .callMethod("getHandle")
                    .getField(1, "activeContainer")
                    .getField(1, "windowId")
                    .getObject();
    }

}
