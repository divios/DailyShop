package io.github.divios.lib.dLib.shop.util.generators;

import com.google.gson.JsonElement;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public interface ValueGenerator {

    Set<JsonSerializerStrategy> serializers = new HashSet<>();

    static ValueGenerator fromJson(JsonElement element) {

        if (serializers.isEmpty()) {            // First population
            registerJsonStrategy(FixedValueGenerator.getJsonStrategy());
            registerJsonStrategy(RandomIntervalGenerator.getJsonStrategy());
            registerJsonStrategy(GaussianGenerator.getJsonStrategy());
        }

        ValueGenerator generator = null;
        for (JsonSerializerStrategy serializer : serializers) {
            try {generator = serializer.fromJson(element);
            } catch (Exception ignored) {continue;}
            break;
        }

        return Objects.requireNonNull(generator, "Couldn't find strategy to deserialize to a valid ValueGenerator");
    }

    static void registerJsonStrategy(JsonSerializerStrategy strategy) {
        serializers.add(strategy);
    }

    double generate();

    JsonElement toJson();

    boolean isSimilar(ValueGenerator o);

    @FunctionalInterface
    interface JsonSerializerStrategy {
        ValueGenerator fromJson(JsonElement element);
    }

}
