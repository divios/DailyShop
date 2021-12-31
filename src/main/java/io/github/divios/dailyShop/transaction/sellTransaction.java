package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.utils.CompareItemUtils;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.confirmMenu.sellConfirmMenu;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.log.dLog;
import io.github.divios.lib.dLib.log.options.dLogEntry;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"ConstantConditions", "unused"})
public class sellTransaction {

    private final Player player;
    private final dItem item;
    private final dShop shop;
    private int quantity = 1;

    public static sellTransaction create(Player p, dItem item, dShop shop) {
        return new sellTransaction(p, item, shop);
    }

    private sellTransaction(Player p, dItem item, dShop shop) {
        this.player = p;
        this.item = item.clone();
        this.shop = shop;

        initTransaction();
    }

    private void initTransaction() {
        try {
            checkPriceAndPermsConditions();
            hasEnoughItems();
        } catch (Exception errorMsg) {
            Messages.valueOf(errorMsg.getMessage()).send(player);
            //shop.openShop(player);
            return;
        }

        if (item.isConfirmGuiEnabled()) {
            if (item.getSetItems().isPresent())
                item.setSetItems(1);
            openConfirmMenu();

        } else runTransaction(item.getQuantity());
    }

    private void runTransaction(int quantity) {
        this.quantity = quantity;
        try {
            checkItemsPermsAndAmountConditions();
        } catch (Exception errorMsg) {
            Messages.valueOf(errorMsg.getMessage()).send(player);
            return;
        }
        removeItemsFromPlayer();
        depositMoney();
        logTransaction();
        sendMessage();
        shop.openShop(player);
    }


    private void checkPriceAndPermsConditions() throws Exception {
        if (hasNegatePermission() && !player.isOp()) {
            throw new Exception("MSG_INVALIDATE_SELL");
        }

        if (invalidSellPrice()) {
            throw new Exception("MSG_INVALID_SELL");
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
                .withTitle(Lang.CONFIRM_GUI_SELL_NAME.getAsString(player))
                .withConfirmLore(Lang.CONFIRM_GUI_YES.getAsString(player), Lang.CONFIRM_GUI_YES_LORE.getAsListString(player))
                .withCancelLore(Lang.CONFIRM_GUI_NO.getAsString(player), Lang.CONFIRM_GUI_NO_LORE.getAsListString(player))
                .prompt();
    }

    private void runSingleConfirmMenuAction(boolean playerChoice) {
        if (playerChoice) {
            if (!itemExistOnShop()) {
                Messages.MSG_INVALID_OPERATION.send(player);
                return;
            }
            runTransaction(item.getQuantity());
        } else
            shop.openShop(player);
    }

    private void checkItemsPermsAndAmountConditions() throws Exception {
        hasNecessaryPermissions();
        hasEnoughItems();
    }

    private void removeItemsFromPlayer() {
        ItemUtils.remove(player.getInventory(), item.getRealItem(), quantity, CompareItemUtils::compareItems);
    }

    private void depositMoney() {
        item.getEconomy().depositMoney(player, getItemPrice());
    }

    private void logTransaction() {
        dLog.log(createLogEntry());
    }

    private void sendMessage() {
        Messages.MSG_BUY_ITEM.send(player,
                Template.of("action", Lang.SELL_ACTION_NAME.getAsString(player)),
                Template.of("item", item.getDisplayName()),
                Template.of("amount", quantity),
                Template.of("price", getItemPriceFormatted()),
                Template.of("currency", item.getEconomy().getName())
        );
    }

    private ItemStack getItem() {
        return ItemBuilder.of(item.getDailyItem().clone()).
                addLore(getItemLore());
    }

    private boolean hasNegatePermission() {
        return player.hasPermission("dailyrandomshop." + shop.getName() + ".negate.sell");
    }

    private boolean invalidSellPrice() {
        return !item.getDSellPrice().isPresent() || item.getDSellPrice().get().getPrice() == -1;
    }

    private boolean itemExistOnShop() {
        return shop.getItem(item.getUid()).isPresent();
    }

    private String getItemPriceFormatted() {
        return PriceWrapper.format(getItemPrice());
    }

    private double getItemPrice() {
        return item.getDSellPrice().orElse(null).getPriceForPlayer(player, shop, item.getID(), priceModifier.type.SELL) * quantity;
    }

    private void hasNecessaryPermissions() throws Exception {
        for (String perm : item.getPermsSell().orElse(Collections.emptyList())) {
            if (!player.hasPermission(perm))
                throw new Exception("MSG_NOT_PERMS_ITEM");
        }
    }

    private void hasEnoughItems() throws Exception {
        int maxItemsToRemove = ItemUtils.count(player.getInventory(), item.getRealItem(), CompareItemUtils::compareItems);
        if (maxItemsToRemove < quantity)
            throw new Exception("MSG_NOT_ITEMS");
    }

    @NotNull
    private dLogEntry createLogEntry() {
        return dLogEntry.builder()
                .withPlayer(player)
                .withShopID(shop.getName())
                .withItemUUID(item.getUid())
                .withRawItem(item.getRealItem())
                .withQuantity(quantity)
                .withType(dLogEntry.Type.SELL)
                .withPrice(getItemPrice())
                .build();
    }

    private boolean itemWithCustomName() {
        return item.getDailyItem().getItemMeta().getDisplayName().isEmpty();
    }


    private void sendNormalMessage(List<String> msg) {
        Utils.sendRawMsg(player, msg.get(0) + item.getDisplayName() + "&7" + msg.get(1));
    }

    private void sendTranslatedMaterialMessage(List<String> msg) {
        sendTranslatedMaterialMessageToPlayer(getFormattedMessageForMaterialTranslation(msg), item.getDailyItem().getType());
    }

    private List<String> getItemLore() {
        return Lang.CONFIRM_GUI_SELL_ITEM.getAsListString(player,
                Template.of("price", getItemPriceFormatted())
        );
    }

    private void sendTranslatedMaterialMessageToPlayer(String formattedMsg, Material material) {
        //DailyShop.get().getLocaleManager().sendMessage(player, formattedMsg, material, (short) 0, null);
    }

    private String getFormattedMessageForMaterialTranslation(List<String> msg) {
        return Utils.JTEXT_PARSER.parse(Settings.PREFIX + msg.get(0) + "<item>" + "&7" + msg.get(1));
    }

}
