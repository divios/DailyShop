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

    private final Player player;
    private final dItem item;
    private final dShop shop;
    private int quantity;

    public static sellTransaction create(Player p, dItem item, dShop shop) {
        return new sellTransaction(p, item, shop);
    }

    private sellTransaction(Player p, dItem item, dShop shop) {
        this.player = p;
        this.item = item;
        this.shop = shop;

        initTransaction();
    }

    private void initTransaction() {
        try {
           checkPriceAndPermsConditions();
        } catch (Exception errorMsg) {
           Msg.sendMsg(player, errorMsg.getMessage());
           shop.openShop(player);
           return;
        }

        if (item.isConfirmGuiEnabled()) {
            if (!item.getSetItems().isPresent())
                openConfirmMenu();
            else
                openSingleConfirmMenu();

        } else runTransaction(item.getQuantity());
    }

    private void runTransaction(int quantity) {
        this.quantity = quantity;
        try {
            checkItemsPermsAndAmountConditions();
        } catch (Exception errorMsg) {
            Msg.sendMsg(player, errorMsg.getMessage());
            return;
        }
        removeItemsFromPlayer();
        depositMoney();
        sendMessage();
        shop.openShop(player);
    }


    private void checkPriceAndPermsConditions() throws Exception {
        if (hasNegatePermission() && !player.isOp()) {
            throw new Exception(plugin.configM.getLangYml().MSG_INVALIDATE_SELL);
        }

        if (invalidSellPrice()) {
            throw new Exception(plugin.configM.getLangYml().MSG_INVALID_SELL);
        }
    }

    private void openConfirmMenu() {
        sellConfirmMenu.builder()
                .withShop(shop)
                .withPlayer(player)
                .withItem(item)
                .withOnCompleteAction(this::runTransaction)
                .withFallback(() -> shop.openShop(player))
                .build();
    }

    private void openSingleConfirmMenu() {
        confirmIH.builder()
                .withPlayer(player)
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
                Msg.sendMsg(player, plugin.configM.getLangYml().MSG_INVALID_OPERATION);
                return;
            }
            runTransaction(quantity);
        } else
            shop.openShop(player);
    }

    private void checkItemsPermsAndAmountConditions() throws Exception {
        hasNecessaryPermissions();
        hasEnoughItems();
    }

    private void removeItemsFromPlayer() {
        ItemUtils.remove(player.getInventory(), item.getRawItem(), quantity, CompareItemUtils::compareItems);
    }

    private void depositMoney() {
        item.getEconomy().depositMoney(player, getItemPrice());
    }

    private void sendMessage() {
        List<String> msg = createMsg();

        if (msg.size() == 1) {      // if {item} is included
            Msg.sendMsg(player, msg.get(0));
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
        return player.hasPermission("dailyrandomshop." + shop.getName() + ".negate.sell");
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
        return item.getSellPrice().orElse(null).getPrice() * (item.getSetItems().isPresent() ? 1 : quantity);
    }

    private void hasNecessaryPermissions() throws Exception {
        for (String perm : item.getPermsSell().orElse(Collections.emptyList())) {
            if (!player.hasPermission(perm))
                throw new Exception(plugin.configM.getLangYml().MSG_NOT_PERMS_ITEM);
        }
    }

    private void hasEnoughItems() throws Exception {
        int maxItemsToRemove = ItemUtils.count(player.getInventory(), item.getRawItem(), CompareItemUtils::compareItems);
        if (maxItemsToRemove < quantity)
            throw new Exception(plugin.configM.getLangYml().MSG_NOT_ITEMS);
    }

    @NotNull
    private List<String> createMsg() {
        return Arrays.asList(Msg.singletonMsg(plugin.configM.getLangYml().MSG_BUY_ITEM)
                .add("\\{action}", plugin.configM.getLangYml().MSG_SELL_ACTION)
                .add("\\{amount}", "" + quantity)
                .add("\\{price}", getItemPriceFormatted())
                .add("\\{currency}", item.getEconomy().getName())
                .build().split("\\{item}"));
    }

    private boolean itemWithCustomName() {
        return item.getItem().getItemMeta().getDisplayName().isEmpty();
    }


    private void sendNormalMessage(List<String> msg) {
        Msg.sendMsg(player, msg.get(0) + item.getDisplayName() + "&7" + msg.get(1));
    }

    private void sendTranslatedMaterialMessage(List<String> msg) {
         sendTranslatedMaterialMessageToPlayer(getFormattedMessageForMaterialTranslation(msg), item.getItem().getType());
    }

    private List<String> getItemLore() {
        return Msg.msgList(plugin.configM.getLangYml().CONFIRM_GUI_SELL_ITEM)
                .add("\\{price}", getItemPriceFormatted())
                .build();
    }

    private void sendTranslatedMaterialMessageToPlayer(String formattedMsg, Material material) {
        DailyShop.getInstance().getLocaleManager().sendMessage(player, formattedMsg, material, (short) 0, null);
    }

    private String getFormattedMessageForMaterialTranslation(List<String> msg) {
        return FormatUtils.color(DailyShop.getInstance().configM.getSettingsYml().PREFIX +
                msg.get(0) + "<item>" + "&7" + msg.get(1));
    }

}
