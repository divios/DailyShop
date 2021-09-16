package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class confirmGuiSell extends abstractConfirmGui {

    private boolean removed = false;

    private confirmGuiSell(
            dShop shop,
            Player p,
            BiConsumer<ItemStack, Integer> accept,
            Consumer<Player> back,
            dItem item,
            dShop.dShopT type,
            String title,
            String acceptLore,
            String backLore
    ) {
        super(shop, p, accept, back, item, type, title, acceptLore, backLore);

    }

    public static void open(
            dShop shop,
            Player player,
            dItem item,
            dShop.dShopT type,
            BiConsumer<ItemStack, Integer> accept,
            Consumer<Player> back,
            String title,
            String acceptLore,
            String backLore
    ) {

        new confirmGuiSell(shop, player, accept, back, item, type, title, acceptLore, backLore);
    }

    protected void update() {

        sellCache.put(p.getUniqueId(), new cacheEntry(item.clone(), added));
        Inventory inv = gui.getInventory();

        inv.setItem(20, added >= 1 ? rem1 : XMaterial.AIR.parseItem());
        inv.setItem(19, added >= 10 ? rem5 : XMaterial.AIR.parseItem());
        inv.setItem(18, added >= 64 ? rem10 : XMaterial.AIR.parseItem());
        inv.setItem(24, ItemUtils.count(p.getInventory(), item) >= 1 ? add1 : XMaterial.AIR.parseItem());
        inv.setItem(25, ItemUtils.count(p.getInventory(), item) >= 10 ? add5 : XMaterial.AIR.parseItem());
        inv.setItem(26, ItemUtils.count(p.getInventory(), item) >= 64 ? add10 : XMaterial.AIR.parseItem());

        gui.getInventory().setItem(39, ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS)
                .setName(confirmLore)
                .addLore(
                        Msg.singletonMsg(main.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                                .add("\\{price}",
                                        PriceWrapper.format(added * dItem.getBuyPrice().get().getPrice()))
                                .add("\\{quantity}", String.valueOf(added))
                                .build()
                )
        );

    }

    @Override
    protected void addItem(int amount) {
        if (ItemUtils.count(p.getInventory(), item) < amount) return;
        added += amount;
        ItemUtils.remove(p.getInventory(), item, amount);
    }

    @Override
    protected void remItem(int amount) {
        if (added < amount) return;
        added -= amount;
        ItemUtils.give(p, item, amount);
    }

    @Override
    protected void addMaximum() {
        added += ItemUtils.countAndRemove(p.getInventory(), item);
    }

    @Override
    protected void removeAllItems() {
        if (removed | added == 0) return;
        ItemUtils.give(p, item, added);
        removed = true;
        sellCache.remove(p.getUniqueId());
    }

    private boolean overflowsInv(ItemStack item, int amount) {
        Inventory inv = Bukkit.createInventory(null, 36, "");
        IntStream.range(0, 36).forEach(value -> inv.setItem(value, p.getInventory().getItem(value)));
        return !inv.addItem(ItemBuilder.of(item.clone()).setCount(amount)).isEmpty();
    }

}
