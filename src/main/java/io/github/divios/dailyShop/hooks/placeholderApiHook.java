package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.LimitHelper;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.dShop;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

class placeholderApiHook extends PlaceholderExpansion implements Hook<PlaceholderExpansion> {

    private boolean isHook = false;

    placeholderApiHook() {
        tryToHook();
    }

    private void tryToHook() {
        Log.info("Hooked to PlaceholderAPI");
        this.register();
        isHook = true;
    }

    @Override
    public boolean isOn() {
        return isHook;
    }

    @Override
    public PlaceholderExpansion getApi() {
        return isHook ? this : null;
    }


    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @NotNull
    @Override
    public String getAuthor() {
        return DailyShop.get().getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @NotNull
    @Override
    public String getIdentifier() {
        return "DailyShop";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @NotNull
    @Override
    public String getVersion() {
        return DailyShop.get().getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String placeholder = null;

        if (identifier.startsWith("time"))
            placeholder = timerPlaceholder(identifier.replace("time_", ""));
        else if (identifier.startsWith("balance_"))
            placeholder = balancePlaceholder(identifier.replace("balance_", ""));
        else if (identifier.startsWith("limit_"))
            placeholder = limitPlaceholder(player, identifier.replace("limit_", ""));

        return placeholder;
    }

    private String timerPlaceholder(String s) {
        Optional<dShop> shop = DailyShop.get().getShopsManager().getShop(s);
        if (shop.isPresent() && shop.get().getTimer() == -1) return XSymbols.TIMES_3.parseSymbol();
        return shop.map(Utils::getDiffActualTimer).orElse(null);
    }

    private String balancePlaceholder(String s) {
        Optional<dShop> shop = DailyShop.get().getShopsManager().getShop(s);
        if (!shop.isPresent()) return null;
        if (shop.get().getAccount() == null)
            return XSymbols.INFINITY.parseSymbol();

        double balance = shop.get().getAccount().getBalance();
        double maxBalance = shop.get().getAccount().getMaxBalance();

        if (balance == 0 || Double.compare(balance, maxBalance) == 0)
            return "&c" + balance;
        else
            return String.valueOf(balance);
    }

    // %dailyShop_limit_sell_drops%
    // %dailyShop_limit_buy_drops_DIRT%
    private String limitPlaceholder(Player p, String s) {
        String[] strings = s.split("_");
        if (strings.length < 2 || strings.length > 3) return null;

        Transactions.Type type;
        try {type = Transactions.Type.getByKey(strings[0]);}
        catch (Exception e) {return null;}

        dShop shop = DailyShop.get().getShopsManager().getShop(strings[1]).orElse(null);
        if (shop == null) return null;

        if (strings.length == 2) {
            int limit = LimitHelper.getShopLimit(p, shop, type);
            return limit == -1 ? XSymbols.INFINITY.parseSymbol() : String.valueOf(limit);
        }

        int limit = LimitHelper.getItemLimit(p, shop, strings[2], type);
        return limit == -1 ? XSymbols.INFINITY.parseSymbol() : String.valueOf(limit);
    }

}
