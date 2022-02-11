package io.github.divios.dailyShop.hooks.util.papiresolvers;

import org.bukkit.entity.Player;

public interface PlaceholderResolver {

    boolean canResolve(String rawPlaceholder);
    String resolve(Player p, String rawPlaceholder);

}
