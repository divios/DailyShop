package io.github.divios.shop.generators;

import io.github.divios.dailyShop.utils.valuegenerators.ValueGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.FixedValueGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.RandomIntervalGenerator;
import org.junit.Assert;
import org.junit.Test;

public class FixedGeneratorTest {

    @Test
    public void testCreation() {
        new FixedValueGenerator(50);
    }

    @Test
    public void testGeneration() {
        Assert.assertEquals(50, new FixedValueGenerator(50).generate(), 0);
    }

    @Test
    public void testIsSimilar() {
        ValueGenerator generator = new FixedValueGenerator(50);

        Assert.assertFalse(new RandomIntervalGenerator(3, 40).isSimilar(generator));
        Assert.assertFalse(new FixedValueGenerator(40).isSimilar(generator));
        Assert.assertTrue(new FixedValueGenerator(50).isSimilar(generator));
    }

}
