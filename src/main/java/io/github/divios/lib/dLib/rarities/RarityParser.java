package io.github.divios.lib.dLib.rarities;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RarityParser {

    public static Rarity parse(String key, ConfigurationSection section) {
        Rarity.RarityBuilder builder = parseSection(section);
        return builder.withId(key).build();
    }

    private static Rarity.RarityBuilder parseSection(ConfigurationSection section) {
        String name = section.getString("name");
        String itemStr = section.getString("item");
        double weight = section.contains("weight")
                ? Doubles.constrainToRange(section.getDouble("weight", -1), 0, 100) : -1;


        Objects.requireNonNull(name, "No name defined");
        Objects.requireNonNull(itemStr, "No item defined");
        Preconditions.checkArgument(weight >= 0, "No weight defined");

        ItemStack itemStack = XMaterial.matchXMaterial(itemStr)
                .orElseThrow(() -> new IllegalArgumentException("invalid item material")).parseItem();

        return Rarity.builder()
                .withName(name)
                .withItem(itemStack)
                .withWeight(weight);
    }

    public static final class ParserError extends RuntimeException {
        public ParserError(String message) {
            super(message);
        }
    }

}
