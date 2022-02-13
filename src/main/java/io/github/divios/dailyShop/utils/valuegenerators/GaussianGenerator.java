package io.github.divios.dailyShop.utils.valuegenerators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import org.apache.commons.lang.Validate;

import java.util.Random;

public class GaussianGenerator implements ValueGenerator {

    static JsonSerializerStrategy getJsonStrategy() {
        return element -> {
            JsonObject object = element.getAsJsonObject();

            Preconditions.checkArgument(object.has("mean"), "No mean value");
            Preconditions.checkArgument(object.has("var"), "No var value");

            double mean = object.get("mean").getAsDouble();
            double var = object.get("var").getAsDouble();

            Validate.isTrue(mean >= 0, "Mean has to be greater than 0");
            Validate.isTrue(var >= 0, "Var has to be greater than 0");

            return new GaussianGenerator(mean, var);
        };
    }

    private final double mean;
    private final double var;

    private final Random generator;

    public GaussianGenerator(double mean, double var) {
        this.mean = mean;
        this.var = var;

        generator = new Random();
    }

    @Override
    public double generate() {
        double value = Math.max(0, (generator.nextGaussian() * var) + mean);
        return Utils.round(value, 2);
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("mean", mean);
        json.addProperty("var", var);

        return json;
    }

    @Override
    public String toString() {
        return PrettyPrice.pretty(mean) + "~" + PrettyPrice.pretty(var);
    }

    @Override
    public boolean isSimilar(ValueGenerator o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        GaussianGenerator that = (GaussianGenerator) o;
        return Double.compare(mean, that.mean) == 0
                && Double.compare(var, that.var) == 0;
    }
}
