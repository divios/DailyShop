package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

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

        Inventory inv = gui.getInventory();

        inv.setItem(20, added >= 1 ? rem1 : XMaterial.AIR.parseItem());
        inv.setItem(19, added >= 10 ? rem5 : XMaterial.AIR.parseItem());
        inv.setItem(18, added >= 64 ? rem10 : XMaterial.AIR.parseItem());
        inv.setItem(24, countSimilarItems() >= 1 ? add1 : XMaterial.AIR.parseItem());
        inv.setItem(25, countSimilarItems() >= 10 ? add5 : XMaterial.AIR.parseItem());
        inv.setItem(26, countSimilarItems() >= 64 ? add10 : XMaterial.AIR.parseItem());

        gui.getInventory().setItem(39, ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS)
                .setName(confirmLore)
                .addLore(
                        Msg.msgList(main.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                                .add("\\{price}",
                                        PriceWrapper.format(added * dItem.getSellPrice().get().getPrice()))
                                .add("\\{quantity}", String.valueOf(added))
                                .build()
                )
        );

    }


    public static BiPredicate<ItemStack, ItemStack> getComparison(ItemStack item) {

        return (ItemCompared, ItemToCompare) -> {
            if (utils.isOperative("MMOItems") && io.lumine.mythic.lib.api.item.NBTItem.get(item).hasType()) {
                if (ItemUtils.isEmpty(ItemCompared) || ItemUtils.isEmpty(ItemToCompare)) return false;

                io.lumine.mythic.lib.api.item.NBTItem mmoitem = io.lumine.mythic.lib.api.item.NBTItem.get(ItemCompared);
                String type = mmoitem.getType();
                String id = mmoitem.getString("MMOITEMS_ITEM_ID");

                io.lumine.mythic.lib.api.item.NBTItem mmoitem2 = io.lumine.mythic.lib.api.item.NBTItem.get(ItemToCompare);
                String type2 = mmoitem2.getType();
                String id2 = mmoitem2.getString("MMOITEMS_ITEM_ID");

                if (type == null || type2 == null || id == null || id2 == null) return false;

                return type2.equals(type) && id.equals(id2);
            }
            return ItemCompared.isSimilar(ItemToCompare);
        };

    }

    private int countSimilarItems() {
        return ItemUtils.count(p.getInventory(), item, getComparison(item));
    }

    private List<ItemStack> countAndRemove(int quantity) {

        Log.info("quantity: " + quantity);
        ItemStack[] playerItems = p.getInventory().getContents();
        List<ItemStack> itemsRemoved = new ArrayList<>();

        for (ItemStack item : playerItems) {
            if (quantity <= 0) break;
            if (!getComparison(item).test(this.item, item)) continue;

            if (item.getAmount() <= quantity) {
                quantity -= item.getAmount();
                itemsRemoved.add(item.clone());
                item.setAmount(0);
            } else {
                itemsRemoved.add(ItemBuilder.of(item.clone()).setCount(quantity));
                quantity = 0;
                item.setAmount(item.getAmount() - quantity);
            }

        }

        return itemsRemoved;
    }

    private void giveItemsBack(int quantity) {

        for (Iterator<ItemStack> iter = sellCache.get(p.getUniqueId()).iterator(); iter.hasNext(); ) {
            ItemStack next = iter.next();

            int itemAmount = next.getAmount();
            if (itemAmount < quantity) {
                ItemUtils.give(p, next);
                quantity -= itemAmount;
                iter.remove();
            } else {
                ItemUtils.give(p, ItemBuilder.of(next).setCount(quantity));
                next.setAmount(next.getAmount() - quantity);
                break;
            }
        }
        sellCache.entrySet().removeIf(uuidListEntry -> uuidListEntry.getValue().isEmpty());     // Remove garbage
    }

    private void addToCache(List<ItemStack> items) {
        if (sellCache.containsKey(p.getUniqueId()))
            sellCache.get(p.getUniqueId()).addAll(items);
        else
            sellCache.put(p.getUniqueId(), items);
    }


    @Override
    protected void addItem(int amount) {
        added += amount;
        List<ItemStack> removedItems = countAndRemove(amount);
        addToCache(removedItems);
    }

    @Override
    protected void remItem(int amount) {
        if (added < amount) return;
        added -= amount;
        giveItemsBack(amount);
    }

    @Override
    protected void addMaximum() {
        List<ItemStack> removedItems = countAndRemove(MAX_ITEMS_AMOUNT);
        removedItems.forEach(itemStack -> added += itemStack.getAmount());
        addToCache(removedItems);

    }

    @Override
    protected void removeAllItems() {
        if (removed | added == 0) return;
        giveItemsBack(MAX_ITEMS_AMOUNT);
        removed = true;
        sellCache.remove(p.getUniqueId());
    }

}
