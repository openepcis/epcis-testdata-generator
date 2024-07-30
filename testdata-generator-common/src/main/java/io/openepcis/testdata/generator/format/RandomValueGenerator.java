package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.template.RandomGenerators;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.List;

public class RandomValueGenerator {
    private final TriangularDistribution triangularDistribution;

    //Method to generate the Mersenne Twister instance for each of the RandomGenerators to get subsequent numbers when used multiple times
    public static void generateInstance(final List<RandomGenerators> randomGenerators) {
        if (randomGenerators != null) {
            randomGenerators.forEach(r -> {
                // Initialize Mersenne Twister with the seed
                final RandomGenerator random = new MersenneTwister(r.getSeedValue());
                r.setRandomGenerator(random);
            });
        }
    }

    // Constructor to generate the MersenneTwister instance base on the seed
    public RandomValueGenerator(final RandomGenerators randomGenerators) {
        // Create a TriangularDistribution instance
        triangularDistribution = new TriangularDistribution(randomGenerators.getRandomGenerator(), randomGenerators.getMinValue(), randomGenerators.getMaxValue(), randomGenerators.getMeanValue());
    }


    //Method to generate the sample based on the seed and distribution information
    public double sample() {
        return triangularDistribution.sample();
    }
}
