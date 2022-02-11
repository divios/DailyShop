package io.github.divios.dailyShop.utils.valuegenerators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;

public class RandomIntervalGenerator implements ValueGenerator {

    static JsonSerializerStrategy getJsonStrategy() {
        return element -> {
            JsonObject object = element.getAsJsonObject();

            Preconditions.checkArgument(object.has("min"), "No min value");
            Preconditions.checkArgument(object.has("max"), "No max value");

            double min = object.get("min").getAsDouble();
            double max = object.get("max").getAsDouble();

            return new RandomIntervalGenerator(min, max);
        };
    }

    private final double min;
    private final double max;

    public RandomIntervalGenerator(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double generate() {
        double value = min + (Math.random() * (max - min));
        return Utils.round(value, 2);
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("min", min);
        json.addProperty("max", max);

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
                && Double.compare(max, that.max) == 0;
    }
}
