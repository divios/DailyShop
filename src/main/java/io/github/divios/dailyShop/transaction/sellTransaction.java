package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.CompareItemUtils;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.lib.dLib.confirmMenu.sellConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.log.dLog;
import io.github.divios.lib.dLib.log.options.dLogEntry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"ConstantConditions"})
public class sellTransaction {

    private static final DailyShop plugin = DailyShop.getInstance();

    private final Player p;
    private final dItem item;
    private final dShop shop;

    public static sellTransaction create(Player p, dItem item, dShop shop) {
        return new sellTransaction(p, item, shop);
    }

    private sellTransaction(Player p, dItem item, dShop shop) {
        this.p = p;
        this.item = item;
        this.shop = shop;

        initTransaction();
    }

    private void initTransaction() {
        if (checkPriceAndPermsConditions()) return;

        if (item.isConfirmGuiEnabled()) {
            if (!item.getSetItems().isPresent())
                openConfirmMenu();
            else
                openSingleConfirmMenu();

        } else runTransaction();
    }

    private void runTransaction() {
        if (!checkItemsPermsAndAmountConditions()) return;
        removeItemsFromPlayer();
        depositMoney();
        logTransaction();
        sendMessage();
        shop.openShop(p);
    }


    private boolean checkPriceAndPermsConditions() {
        boolean result = true;
        if (hasNegatePermission() && !p.isOp()) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALIDATE_SELL);
            result = false;
        }

        if (invalidSellPrice()) {
            Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_SELL);
            shop.openShop(p);
            result = false;
        }
        return result;
    }

    private void openConfirmMenu() {
        sellConfirmMenu.builder()
                .withShop(shop)
                .withPlayer(p)
                .withItem(item)
                .withOnCompleteAction(quantity -> initTransaction())
                .withFallback(() -> shop.openShop(p))
                .build();
    }

    private void openSingleConfirmMenu() {
        confirmIH.builder()
                .withPlayer(p)
                .withItem(getItem())
                .withAction(this::runSingleConfirmMenuAction)
                .withTitle(plugin.configM.getLangYml().CONFIRM_GUI_SELL_NAME)
                .withConfirmLore(plugin.configM.getLangYml().CONFIRM_GUI_YES, plugin.configM.getLangYml().CONFIRM_GUI_YES_LORE)
                .withCancelLore(plugin.configM.getLangYml().CONFIRM_GUI_NO, plugin.configM.getLangYml().CONFIRM_GUI_NO_LORE)
                .prompt();
    }

    private void runSingleConfirmMenuAction(boolean playerChoice) {
        if (playerChoice) {
            if (!itemExistOnShop()) {
                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INVALID_OPERATION);
                return;
            }
            initTransaction();
        } else
            shop.openShop(p);
    }

    private boolean checkItemsPermsAndAmountConditions() {
        try {
            hasNecessaryPermissions();
            hasEnoughItems();
        } catch (Exception conditionErrorMsg) {
            Msg.sendMsg(p, conditionErrorMsg.getMessage());
            return false;
        }
        return true;
    }

    private void removeItemsFromPlayer() {
        ItemUtils.remove(p.getInventory(), item.getRawItem(), item.getQuantity(), CompareItemUtils::compareItems);
    }

    private void depositMoney() {
        item.getEconomy().depositMoney(p, getItemPrice());
    }

    private void logTransaction() {
        dLog.log(
                dLogEntry.builder()
                        .withPlayer(p)
                        .withShopID(shop.getName())
                        .withItemUUID(item.getUid())
                        .withRawItem(item.getRawItem())
                        .withQuantity(item.getQuantity())
                        .withType(dShop.dShopT.sell)
                        .withPrice(getItemPrice())
                        .build()
        );
    }

    private void sendMessage() {
        List<String> msg = createMsg();

        if (msg.size() == 1) {      // if {item} is included
            Msg.sendMsg(p, msg.get(0));
        } else {
            if (!itemWithCustomName())
                sendNormalMessage(msg);
            else
                sendTranslatedMaterialMessage(msg);
        }
    }

    private ItemStack getItem() {
        return ItemBuilder.of(item.getItem().clone()).
                addLore(getItemLore());
    }

    private boolean hasNegatePermission() {
        return p.hasPermission("dailyrandomshop." + shop.getName() + ".negate.sell");
    }

    private boolean invalidSellPrice() {
        return !item.getSellPrice().isPresent() || item.getSellPrice().get().getPrice() == -1;
    }

    private boolean itemExistOnShop() {
        return shop.getItem(item.getUid()).isPresent();
    }

    private String getItemPriceFormatted() {
        return PriceWrapper.format(getItemPrice());
    }

    private double getItemPrice() {
        return item.getSellPrice().orElse(null).getPrice() * (item.getSetItems().isPresent() ? 1 : item.getQuantity());
    }

    private void hasNecessaryPermissions() throws Exception {
        for (String perm : item.getPermsSell().orElse(Collections.emptyList())) {
            if (!p.hasPermission(perm))
                throw new Exception(plugin.configM.getLangYml().MSG_NOT_PERMS_ITEM);
        }
    }

    private void hasEnoughItems() throws Exception {
        int maxItemsToRemove = ItemUtils.count(p.getInventory(), item.getRawItem(), CompareItemUtils::compareItems);
        if (maxItemsToRemove < item.getQuantity())
            throw new Exception(plugin.configM.getLangYml().MSG_NOT_ITEMS);
    }

    private List<String> getItemLore() {
        return Msg.msgList(plugin.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                .add("\\{price}", getItemPriceFormatted())
                .build();
    }

    @NotNull
    private List<String> createMsg() {
        return Arrays.asList(Msg.singletonMsg(plugin.configM.getLangYml().MSG_BUY_ITEM)
                .add("\\{action}", plugin.configM.getLangYml().MSG_SELL_ACTION)
                .add("\\{amount}", "" + item.getQuantity())
                .add("\\{price}", getItemPriceFormatted())
                .add("\\{currency}", item.getEconomy().getName())
                .build().split("\\{item}"));
    }

    private boolean itemWithCustomName() {
        return item.getItem().getItemMeta().getDisplayName().isEmpty();
    }


    private void sendNormalMessage(List<String> msg) {
        Msg.sendMsg(p, msg.get(0) + item.getDisplayName() + "&7" + msg.get(1));
    }

    private void sendTranslatedMaterialMessage(List<String> msg) {
         sendTranslatedMaterialMessageToPlayer(getFormattedMessageForMaterialTranslation(msg), item.getItem().getType());
    }

    private void sendTranslatedMaterialMessageToPlayer(String formattedMsg, Material material) {
        DailyShop.getInstance().getLocaleManager().sendMessage(p, formattedMsg, material, (short) 0, null);
    }

    private String getFormattedMessageForMaterialTranslation(List<String> msg) {
        return FormatUtils.color(DailyShop.getInstance().configM.getSettingsYml().PREFIX +
                msg.get(0) + "<item>" + "&7" + msg.get(1));
    }

}
