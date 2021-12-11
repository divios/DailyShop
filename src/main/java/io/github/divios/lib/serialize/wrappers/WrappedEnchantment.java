package io.github.divios.lib.serialize.wrappers;

import org.bukkit.enchantments.Enchantment;

public class WrappedEnchantment {

    private final Enchantment enchant;
    private final int level;

    public static WrappedEnchantment of(Enchantment enchant, int level) {
        return new WrappedEnchantment(enchant, level);
    }

    public WrappedEnchantment(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }

    public Enchantment getEnchant() {
        return enchant;
    }

    public int getLevel() {
        return level;
    }
}
