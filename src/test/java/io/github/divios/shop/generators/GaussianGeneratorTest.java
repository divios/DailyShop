package io.github.divios.shop.generators;

import io.github.divios.lib.dLib.shop.util.generators.ValueGenerator;
import io.github.divios.lib.dLib.shop.util.generators.FixedValueGenerator;
import io.github.divios.lib.dLib.shop.util.generators.GaussianGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Predicate;

public class GaussianGeneratorTest {

    @Test
    public void testCreation() {
        new GaussianGenerator(100, 2);
    }

    @Test
    public void testGeneration() {
        Predicate<Double> test = aDouble -> (aDouble >= 5300 - 200) && (aDouble <= 5300 + 200);
        ValueGenerator generator = new GaussianGenerator(5300, 200);

        for (int i = 0; i < 100; i++) {
            double value = generator.generate();
            System.out.println(value);
        }

    }

    @Test
    public void testIsSimilar() {
        ValueGenerator generator = new GaussianGenerator(5300, 200);

        Assert.assertFalse(new GaussianGenerator(3, 40).isSimilar(generator));
        Assert.assertTrue(new GaussianGenerator(5300, 200).isSimilar(generator));
        Assert.assertFalse(new FixedValueGenerator(40).isSimilar(generator));
    }

}
