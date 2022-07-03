package io.github.divios.dailyShop.utils.valuegenerators;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ValueGenerator {

    List<JsonSerializerStrategy> serializers = Stream.of(
            FixedValueGenerator.getJsonStrategy(),
            RandomIntervalGenerator.getJsonStrategy(),
            GaussianGenerator.getJsonStrategy()
    ).collect(Collectors.toList());

    static ValueGenerator fromJson(JsonElement element) {
        return Objects.requireNonNull(fromJsonOptional(element).orElse(null),
                "Couldn't find strategy to deserialize to a valid ValueGenerator");
    }

    static Optional<ValueGenerator> fromJsonOptional(JsonElement element) {
        ValueGenerator generator = null;
        for (JsonSerializerStrategy serializer : serializers) {
            try {
                generator = serializer.fromJson(element);
            } catch (Exception ignored) {
                continue;
            }
            break;
        }

        return Optional.ofNullable(generator);
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
