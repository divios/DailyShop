package io.github.divios.dailyShop.economies;

import me.elementalgaming.ElementalGems.GemAPI;
import org.bukkit.entity.Player;

public class ElementalGemsEcon extends economy {

    public ElementalGemsEcon() {
        this("");
    }

    protected ElementalGemsEcon(String name) {
        super("", "elementalGems", econTypes.elementalGems);
    }

    @Override
    public void test() {
        GemAPI.getGemItem();
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        GemAPI.removeGems(p.getUniqueId(), price.longValue());
    }

    @Override
    public void depositMoney(Player p, Double price) {
        GemAPI.addGems(p.getUniqueId(), price.intValue());
    }

    @Override
    public double getBalance(Player p) {
        return GemAPI.getGems(p.getUniqueId());
    }
}
