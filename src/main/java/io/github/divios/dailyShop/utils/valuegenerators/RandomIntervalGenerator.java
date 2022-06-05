package io.github.divios.dailyShop.utils.valuegenerators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.dailyShop.utils.valuegenerators.util.Rounder;
import org.apache.commons.lang.Validate;

public class RandomIntervalGenerator implements ValueGenerator {

    static JsonSerializerStrategy getJsonStrategy() {
        return element -> {
            JsonObject object = element.getAsJsonObject();

            Preconditions.checkArgument(object.has("min"), "No min value");
            Preconditions.checkArgument(object.has("max"), "No max value");

            double min = object.get("min").getAsDouble();
            double max = object.get("max").getAsDouble();
            Rounder rounder = object.has("round")
                    ? Rounder.getByName(object.get("round").getAsString()).orElse(Rounder.NONE)
                    : Rounder.NONE;

            Validate.isTrue(min >= 0, "Min value cannot be less than 0");
            Validate.isTrue(max >= 0, "Max value cannot be less than 0");
            Validate.isTrue(min < max, "Min value has to be lower than max");

            return new RandomIntervalGenerator(min, max, rounder);
        };
    }

    private final double min;
    private final double max;
    private final Rounder rounder;

    public RandomIntervalGenerator(double min, double max) {
        this(min, max, Rounder.NONE);
    }

    public RandomIntervalGenerator(double min, double max, Rounder rounder) {
        this.min = min;
        this.max = max;
        this.rounder = rounder;
    }

    @Override
    public double generate() {
        double value = min + (Math.random() * (max - min));
        return rounder.transform(value);
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("min", min);
        json.addProperty("max", max);
        if (!rounder.name().equals("NONE")) json.addProperty("round", rounder.name());

        return json;
    }

    @Override
    public String toString() {
        return PrettyPrice.pretty(min) + " : " + PrettyPrice.pretty(max);
    }

    @Override
    public boolean isSimilar(ValueGenerator o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        RandomIntervalGenerator that = (RandomIntervalGenerator) o;
        return Double.compare(min, that.min) == 0
                && Double.compare(max, that.max) == 0
                && rounder.equals(that.rounder);
    }
}
