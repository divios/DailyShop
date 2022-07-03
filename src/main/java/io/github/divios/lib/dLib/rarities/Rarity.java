package io.github.divios.lib.dLib.rarities;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@SuppressWarnings("unused")
public class Rarity {

    public static final Rarity UNAVAILABLE;

    static {
        UNAVAILABLE = new Rarity("Unavailable",
                "&cUnavailable",
                ItemBuilder.of(XMaterial.BARRIER).setName("&cUnavailable"),
                0F
        );
    }

    public static RarityBuilder builder() {
        return new RarityBuilder();
    }

    private final String id;
    private final String name;
    private final ItemStack item;
    private final double weight;

    public Rarity(String id, String name, ItemStack item, double weight) {
        this.id = id.toLowerCase();
        this.name = name;
        this.item = item;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rarity rarity = (Rarity) o;
        return Objects.equals(id, rarity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rarity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", item=" + item +
                ", weight=" + weight +
                '}';
    }

    public static final class RarityBuilder {
        public String id;
        public String name;
        public ItemStack item;
        public double weight;

        private RarityBuilder() {
        }

        public static RarityBuilder aRarity() {
            return new RarityBuilder();
        }

        public RarityBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public RarityBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public RarityBuilder withItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public RarityBuilder withWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public Rarity build() {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(item, "item");
            Objects.requireNonNull(weight, "weight");

            return new Rarity(id, name, item, weight);
        }
    }

}
