package io.github.divios.dailyShop.hooks;

import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dShop;
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
        if (Utils.isOperative("PlaceholderAPI")) {
            Log.info("Hooked to PlaceholderAPI");
            this.register();
            isHook = true;
        }
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

        Optional<dShop> shop = DailyShop.get().getShopsManager().getShop(identifier.replace("time_", ""));
        if (shop.isPresent() && shop.get().getTimer() == -1) return XSymbols.TIMES_3.parseSymbol();
        return shop.map(Utils::getDiffActualTimer).orElse(null);

    }

}
