package io.github.divios.lib.dLib.priceModifiers;

import org.bukkit.entity.Player;

import java.util.Objects;

public class modifierContext {

    private final Player player;
    private final String shop;
    private final String itemID;
    private final priceModifier.type type;

    modifierContext(Player player, String shop, String itemID, priceModifier.type type) {
        this.player = player;
        this.shop = shop;
        this.itemID = itemID;
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public String getShopName() {
        return shop;
    }

    public String getItemID() {
        return itemID;
    }

    public priceModifier.type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        modifierContext situation = (modifierContext) o;
        return Objects.equals(player, situation.player) && Objects.equals(shop, situation.shop) && Objects.equals(itemID, situation.itemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, shop, itemID);
    }

    @Override
    public String toString() {
        return "situation{" +
                "player=" + player.getDisplayName() +
                ", shop=" + shop +
                ", item=" + itemID +
                '}';
    }
}
