package io.github.divios.dailyShop.utils.NMSUtils;

import com.cryptomorin.xseries.ReflectionUtils;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public class SetSlotPacket {

    protected static final NMSClass packetClazz;
    protected static final NMSClass CraftItemClazz;
    protected static final NMSClass NMSItemStackClazz;

    protected static final Constructor<?> packetConstructorPost_1_17;
    protected static final Constructor<?> packetConstructorPre_1_17;

    static {
        packetClazz = NMSHelper.getClass(ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutSetSlot").getName());
        CraftItemClazz = NMSHelper.getClass(ReflectionUtils.CRAFTBUKKIT + "inventory.CraftItemStack");
        NMSItemStackClazz = ReflectionUtils.VER >= 17
                ? NMSHelper.getClass("net.minecraft.world.item.ItemStack")
                : NMSHelper.getClass(ReflectionUtils.getNMSClass("ItemStack").getName());

        packetConstructorPost_1_17 = NMSHelper.getConstructor(packetClazz.getWrappedClass(), int.class, int.class, int.class, NMSItemStackClazz.getWrappedClass());
        packetConstructorPre_1_17 = NMSHelper.getConstructor(packetClazz.getWrappedClass(), int.class, int.class, NMSItemStackClazz.getWrappedClass());
    }

    public static void send(@NotNull Player p, @Nullable ItemStack item, int slot) {
        send(p, item, slot, -2);
    }

    public static void send(@NotNull Player p, @Nullable ItemStack item, int slot, int containerID) {
        try {
            if (item == null) item = new ItemStack(Material.AIR);
            NMSObject craftItem = CraftItemClazz.callStaticMethod("asNMSCopy", item);

            Object packetClass = ReflectionUtils.VER >= 17
                    ? packetConstructorPost_1_17.newInstance(containerID, 1, slot, craftItem.getObject())
                    : packetConstructorPre_1_17.newInstance(-2, slot, craftItem.getObject());
            ReflectionUtils.sendPacket(p, packetClass);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}