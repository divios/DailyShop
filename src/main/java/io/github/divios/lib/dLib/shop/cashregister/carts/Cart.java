package io.github.divios.lib.dLib.shop.cashregister.carts;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public abstract class Cart {

    protected final dShop shop;
    protected final Player p;
    protected final dItem item;

    public Cart(dShop shop, Player p, dItem item) {
        this.shop = shop;
        this.p = p;
        this.item = item;

        addToCart();
        confirmOperation();
    }

    public abstract void addToCart();

    public abstract void confirmOperation();

    public abstract void checkOut(int amount);

    protected void sendSuccessMsg(int amount, double price, String action) {
        String rawMsg = Utils.JTEXT_PARSER
                .withTemplate(
                        Template.of("action", action),
                        Template.of("amount", amount),
                        Template.of("price", PrettyPrice.pretty(price)),
                        Template.of("currency", item.getEcon().getName())
                )
                .parse(Messages.MSG_BUY_ITEM.getValue());

        if (ItemUtils.getMetadata(item.getItem()).hasDisplayName())
            p.sendMessage(
                    Utils.JTEXT_PARSER
                            .withTemplate(Template.of("item", ItemUtils.getName(item.getItem())))
                            .parse(rawMsg)
            );

        else        // If no custom name, send translated item type
            DailyShop.get().getLocaleLib()
                    .sendMessage(p, rawMsg.replace("{item}", "<item>"), // LocaleLib placeholder is <item>
                            item.getItem().getType(), (short) 0, null);

    }

}
