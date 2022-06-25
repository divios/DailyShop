package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public enum Messages {

    MSG_BUY_ITEM("lang.messages.buy_item"),
    MSG_NOT_MONEY("lang.messages.not_enough_money"),
    MSG_INV_FULL("lang.messages.inventory_full"),
    MSG_NOT_ITEMS("lang.messages.not_enough_items"),
    MSG_NOT_STOCK("lang.messages.not_stock"),
    MSG_RESTOCK("lang.messages.restock"),
    MSG_NOT_PERMS("lang.messages.not_perms"),
    MSG_NOT_PERMS_ITEM("lang.messages.not_perms_item"),
    MSG_FULL_STOCK("lang.messages.stock-full"),
    MSG_OUT_STOCK("lang.messages.out_of_stock"),
    MSG_INVALID_BUY("lang.messages.invalid_buy"),
    MSG_INVALID_SELL("lang.messages.invalid_sell"),
    MSG_INVALIDATE_BUY("lang.messages.invalidate_buy"),
    MSG_INVALIDATE_SELL("lang.messages.invalidate_sell"),
    MSG_PERMS_OPEN_SHOP("lang.messages.perms_shop"),
    MSG_CURRENCY_ERROR("lang.messages.currency_error"),
    MSG_INVALID_OPERATION("lang.messages.invalid_operation"),
    MSG_NOT_INTEGER("lang.messages.not_integer"),
    MSG_BALANCE_MAX_LIMIT("lang.messages.shop_balance_max_error"),
    MSG_BALANCE_MIN_LIMIT("lang.messages.shop_balance_min_error"),
    MSG_LIMIT("lang.messages.player_limit");

    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

   public String getValue() {
        return DailyShop.get().getResources().getLangYml().getString(path);
    }

    public void send(final CommandSender sender) {
        sender.sendMessage(Utils.JTEXT_PARSER.parse(prefix() + getValue()));
    }

    public void send(final CommandSender sender, Template... template) {
        sender.sendMessage(
                Utils.JTEXT_PARSER
                        .withTemplate(template)
                        .parse(prefix() + getValue(), sender instanceof Player ? (Player) sender : null)
        );
    }

    public void broadcast() {
        broadcast(new Template[0]);
    }

    public void broadcast(Template... templates) {
        String msg = Utils.JTEXT_PARSER
                .withTemplate(templates)
                .parse(prefix() + getValue());

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(msg));
        Log.info(msg);
    }

    private String prefix() {
        return Settings.PREFIX.getValue().getAsString();
    }

}
