package io.github.divios.lib.dLib.priceModifiers;

import org.bukkit.permissions.Permission;

import java.util.Objects;

public abstract class abstractModifier implements priceModifier {

    protected final String id;
    protected final priceModifier.type type;
    protected final double value;

    protected abstractModifier(String id, priceModifier.type type, double value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public abstract scope scope();

    @Override
    public type type() {
        return this.type;
    }

    @Override
    public Permission getPermission() {
        return new Permission("dailyrandomshop.pricemodifiers." + id());
    }

    @Override
    public abstract boolean appliesToContext(modifierContext context);

    @Override
    public double getValue(modifierContext context) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        abstractModifier that = (abstractModifier) o;
        return Double.compare(that.value, value) == 0 && Objects.equals(id, that.id) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, value);
    }

    @Override
    public String toString() {
        return "abstractModifier{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
