package io.github.divios.shop.generators;

import com.google.gson.JsonObject;
import io.github.divios.dailyShop.utils.valuegenerators.FixedValueGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.GaussianGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.RandomIntervalGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.ValueGenerator;
import org.junit.Assert;
import org.junit.Test;

public class ValueGeneratorTest {

    @Test
    public void testFromJson1() {
        ValueGenerator generator = new FixedValueGenerator(3);
        ValueGenerator fromJson = ValueGenerator.fromJson(generator.toJson());

        Assert.assertTrue(generator.isSimilar(fromJson));
    }

    @Test
    public void testFromJson2() {
        ValueGenerator generator = new RandomIntervalGenerator(3, 500);
        ValueGenerator fromJson = ValueGenerator.fromJson(generator.toJson());

        Assert.assertTrue(generator.isSimilar(fromJson));
    }

    @Test
    public void testFromJson3() {
        ValueGenerator generator = new GaussianGenerator(50, 20);
        ValueGenerator fromJson = ValueGenerator.fromJson(generator.toJson());

        Assert.assertTrue(generator.isSimilar(fromJson));
    }

    @Test
    public void testFromJsonError() {
        Assert.assertThrows(Exception.class, () -> ValueGenerator.fromJson(new JsonObject()));
    }

}
