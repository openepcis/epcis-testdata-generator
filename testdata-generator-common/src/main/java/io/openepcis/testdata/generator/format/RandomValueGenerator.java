package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.template.RandomGenerators;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomValueGenerator {

    //Method to generate the Mersenne Twister instance for each of the RandomGenerators to get subsequent numbers when used multiple times
    public static void generateInstance(final List<RandomGenerators> randomGenerators) {
        if (randomGenerators != null) {
            randomGenerators.forEach(r -> {
                // Initialize Mersenne Twister with the seed
                final RandomGenerator random = new MersenneTwister(r.getSeedValue());
                final TriangularDistribution triangularDistribution = new TriangularDistribution(random, r.getMinValue(), r.getMeanValue(), r.getMaxValue());
                r.setTriangularDistribution(triangularDistribution);
            });
        }
    }
}
