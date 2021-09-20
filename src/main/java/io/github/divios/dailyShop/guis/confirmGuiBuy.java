package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.utils.FutureUtils;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class confirmGuiBuy extends abstractConfirmGui {

    private confirmGuiBuy(
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

        int amount = 0;
        for (int slot = 0; slot < 27; slot++) {
            if (ItemUtils.isEmpty(p.getInventory().getItem(slot))) continue;

            NBTItem i = new NBTItem(p.getInventory().getItem(slot));
            if (!i.getItem().isSimilar(item.getRawItem())) continue;

            amount += i.getItem().getAmount();
            i.setBoolean("rds_temp_item", true);
            p.getInventory().setItem(slot, i.getItem());
        }


        if (amount != 0) buyCache.put(p.getUniqueId(), new cacheEntry(item.getRawItem().clone(), amount));


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
        new confirmGuiBuy(shop, player, accept, back, item, type, title, acceptLore, backLore);
    }


    private int getMinLimit() {
        int stockLimit = dItem.hasStock() ? FutureUtils.waitFor(dStock.searchStock(p, shop, dItem.getUid())) : MAX_ITEMS_AMOUNT;
        int balanceLimit = (int) Math.floor(dItem.getEconomy().getBalance(p) / dItem.getBuyPrice().get().getPrice());
        int inventoryLimit = added + getInventoryLimit();

        int minLimit = Math.min(inventoryLimit, Math.min(balanceLimit, stockLimit));

        return minLimit;

    }

    private int getInventoryLimit() {
        int limit = 0;
        Inventory playerMockInventory = Bukkit.createInventory(null, 36);

        for (int i = 0; i < 36; i++) {
            playerMockInventory.setItem(i, p.getInventory().getItem(i));
        }

        while (playerMockInventory.addItem(item).isEmpty()) {
            limit++;
        }
        return limit;
    }

    @Override
    protected void update() {

        Inventory inv = gui.getInventory();
        int limit = getMinLimit();

        inv.setItem(20, added >= 1 ? rem1 : XMaterial.AIR.parseItem());
        inv.setItem(19, added >= 10 ? rem5 : XMaterial.AIR.parseItem());
        inv.setItem(18, added >= 64 ? rem10 : XMaterial.AIR.parseItem());
        inv.setItem(24, added <= limit - 1 ? add1 : XMaterial.AIR.parseItem());
        inv.setItem(25, added <= limit - 10 ? add5 : XMaterial.AIR.parseItem());
        inv.setItem(26, added <= limit - 64 ? add10 : XMaterial.AIR.parseItem());

        gui.getInventory().setItem(39, ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS)
                .setName(confirmLore)
                .addLore(
                        Msg.msgList(main.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                                .add("\\{price}", PriceWrapper.format(added * dItem.getBuyPrice().get().getPrice()))
                                .add("\\{quantity}", String.valueOf(added))
                                .build()
                )
        );

    }

    @Override
    protected void addItem(int amount) {
        NBTItem toAdd = new NBTItem(item.clone());
        toAdd.setBoolean("rds_temp_item", true);
        if (overflowsInv(toAdd.getItem(), amount)) return;
        added += amount;
        ItemUtils.give(p, toAdd.getItem(), amount);

    }

    @Override
    protected void remItem(int amount) {
        if (added < amount) return;
        NBTItem toRemove = new NBTItem(item.clone());
        toRemove.setBoolean("rds_temp_item", true);
        added -= amount;
        ItemUtils.remove(p.getInventory(), toRemove.getItem(), amount);
    }


    @Override
    protected void addMaximum() {
        int limit = getMinLimit();
        if (added >= limit) return;
        NBTItem toAdd = new NBTItem(item.clone());
        toAdd.setBoolean("rds_temp_item", true);

        while(p.getInventory().addItem(toAdd.getItem()).isEmpty()) {
            added += 1;
            if (added >= limit) break;
        }

    }

    @Override
    protected void removeAllItems() {
        if (Bukkit.getPlayer(p.getUniqueId()) == null) return;
        ItemStack[] items = p.getInventory().getContents();
        for (ItemStack item : items) {
            if (item == null) continue;
            if (new NBTItem(item).hasKey("rds_temp_item")) item.setAmount(0);
        }
        cacheEntry entry = buyCache.get(p.getUniqueId());
        if (entry != null && !ItemUtils.isEmpty(entry.getItem())) {
            entry.restore(p);
            buyCache.remove(p.getUniqueId());
        }
    }

    private boolean overflowsInv(ItemStack item, int amount) {
        Inventory inv = Bukkit.createInventory(null, 36, "");
        IntStream.range(0, 36).forEach(value -> inv.setItem(value, p.getInventory().getItem(value)));
        return !inv.addItem(ItemBuilder.of(item.clone()).setCount(amount)).isEmpty();
    }


}