package io.github.divios.dailyShop.utils.RandomSelector;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

final class RandomSelectorImpl<E> implements RandomSelector<E> {

    static <E> RandomSelector<E> uniform(Collection<E> elements) {
        Objects.requireNonNull(elements, "elements must not be null");

        int size = elements.size();

        //noinspection unchecked
        E[] array = elements.toArray((E[]) new Object[size]);
        return new RandomSelectorImpl<>(array, new BoundedRandomSelector(size));
    }

    static <E> RandomSelector<E> weighted(Collection<E> elements, Weigher<? super E> weigher) {
        Objects.requireNonNull(elements, "elements must not be null");
        Objects.requireNonNull(weigher, "weigher must not be null");

        int size = elements.size();

        //noinspection unchecked
        E[] elementArray = elements.toArray((E[]) new Object[size]);

        double totalWeight = 0d;
        double[] probabilities = new double[size];

        for (int i = 0; i < size; i++) {
            double weight = weigher.weigh(elementArray[i]);

            probabilities[i] = weight;
            totalWeight += weight;
        }

        for (int i = 0; i < size; i++) {
            probabilities[i] /= totalWeight;
        }

        return new RandomSelectorImpl<>(elementArray, new WeightedSelector(probabilities));
    }

    private final E[] elements;
    private final IndexSelector selection;

    private RandomSelectorImpl(E[] elements, IndexSelector selection) {
        this.elements = elements;
        this.selection = selection;
    }

    @Override
    public E pick(Random random) {
        return this.elements[this.selection.pickIndex(random)];
    }

    @Override
    public Stream<E> stream(Random random) {
        Objects.requireNonNull(random, "random must not be null");
        return Stream.generate(() -> pick(random));
    }

    private interface IndexSelector {

        int pickIndex(Random random);

    }

    private static final class BoundedRandomSelector implements IndexSelector {
        private final int bound;

        private BoundedRandomSelector(int bound) {
            this.bound = bound;
        }

        @Override
        public int pickIndex(Random random) {
            return random.nextInt(this.bound);
        }
    }

    /**
     * Alias method implementation O(1) using Vose's algorithm to initialize O(n)
     *
     * @author Olivier Grégoire
     */
    private static final class WeightedSelector implements IndexSelector {

        private final double[] probabilities;
        private final int[] alias;

        WeightedSelector(final double[] probabilities) {
            final int size = probabilities.length;

            final double average = 1d / size;
            final int[] small = new int[size];
            int smallSize = 0;
            final int[] large = new int[size];
            int largeSize = 0;

            for (int i = 0; i < size; i++) {
                if (probabilities[i] < average) {
                    small[smallSize++] = i;
                } else {
                    large[largeSize++] = i;
                }
            }

            final double[] pr = new double[size];
            final int[] al = new int[size];
            this.probabilities = pr;
            this.alias = al;

            while (largeSize != 0 && smallSize != 0) {
                final int less = small[--smallSize];
                final int more = large[--largeSize];
                pr[less] = probabilities[less] * size;
                al[less] = more;
                probabilities[more] += probabilities[less] - average;
                if (probabilities[more] < average) {
                    small[smallSize++] = more;
                } else {
                    large[largeSize++] = more;
                }
            }
            while (smallSize != 0) {
                pr[small[--smallSize]] = 1d;
            }
            while (largeSize != 0) {
                pr[large[--largeSize]] = 1d;
            }
        }

        @Override
        public int pickIndex(final Random random) {
            final int column = random.nextInt(this.probabilities.length);
            return random.nextDouble() < this.probabilities[column] ? column : this.alias[column];
        }
    }
}