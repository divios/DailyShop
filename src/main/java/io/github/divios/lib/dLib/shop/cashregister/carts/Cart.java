package io.github.divios.lib.dLib.shop.cashregister.carts;

import com.cryptomorin.xseries.ReflectionUtils;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.utils.TranslationApi;
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

    protected void sendSuccessMsg(int amount, String price, String action) {
        String rawMsg = Utils.JTEXT_PARSER
                .withTemplate(
                        Template.of("action", action),
                        Template.of("amount", amount),
                        Template.of("price", price),
                        Template.of("currency", item.getEcon().getName())
                )
                .parse(Messages.MSG_BUY_ITEM.getValue());

        String msg;
        if (!ItemUtils.getMetadata(item.getItem()).hasDisplayName() && ReflectionUtils.VER >= 12
                && TranslationApi.isOperative())
            msg = TranslationApi.translate(item.getItem().getType(), p.getLocale());
        else
            msg = ItemUtils.getName(item.getItem());

        p.sendMessage(Settings.PREFIX +
                Utils.JTEXT_PARSER
                        .withTemplate(Template.of("item", msg + "&7"))
                        .parse(rawMsg)
        );

    }

}
