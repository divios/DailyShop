package io.github.divios.shop.generators;

import io.github.divios.lib.dLib.shop.util.generators.ValueGenerator;
import io.github.divios.lib.dLib.shop.util.generators.FixedValueGenerator;
import io.github.divios.lib.dLib.shop.util.generators.RandomIntervalGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Predicate;

public class RandomIntervalGeneratorTest {

    @Test
    public void testCreation() {
        new RandomIntervalGenerator(30, 80);
    }

    @Test
    public void testGeneration() {
        Predicate<Double> test = aDouble -> (aDouble >= 30) && (aDouble <= 10000);
        ValueGenerator generator = new RandomIntervalGenerator(30, 10000);

        for (int i = 0; i < 100; i++)
            Assert.assertTrue(test.test(generator.generate()));
    }

    @Test
    public void testIsSimilar() {
        ValueGenerator generator = new RandomIntervalGenerator(30, 80);

        Assert.assertFalse(new RandomIntervalGenerator(3, 40).isSimilar(generator));
        Assert.assertTrue(new RandomIntervalGenerator(30, 80).isSimilar(generator));
        Assert.assertFalse(new FixedValueGenerator(40).isSimilar(generator));
    }

}
