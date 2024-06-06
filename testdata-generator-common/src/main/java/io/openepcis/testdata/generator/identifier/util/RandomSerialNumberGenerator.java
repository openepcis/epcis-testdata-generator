/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.testdata.generator.identifier.util;

import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to generate the random numbers using the Mersenne Twister (MT) algorithm where if the seed is provided then Random numbers are generated based on the seed. If no seed is provided then truly random numbers are generated.
 */
public class RandomSerialNumberGenerator {
    private final RandomGenerator random;

    // Public method to get the singleton instance
    public static RandomSerialNumberGenerator getInstance(final Long seed) {
        return new RandomSerialNumberGenerator(seed);
    }

    // Private constructor to prevent direct instantiation.
    private RandomSerialNumberGenerator(Long seed) {
        seed = seed != null ? seed : System.nanoTime();
        this.random = new MersenneTwister(seed);
    }

    public List<String> randomGenerator(RandomizationType type, int minLength, int maxLength, int randomCount) {
        return switch (type) {
            case ALPHA_NUMERIC:
                yield alphaNumericGenerator(minLength, maxLength, randomCount);
            case URL_SAFE_CHARACTERS:
                yield urlSafeGenerator(minLength, maxLength, randomCount);
            default:
                yield numericGenerator(minLength, maxLength, randomCount);
        };
    }

    private static final String ALPHA_NUMERIC_RANDOM_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String URL_SAFE_RANDOM_SET = "abcdefghijklmnopqrstuvwxyz0123456789-_";
    private static final String NUMERIC_RANDOM_SET = "1234567890";

    /**
     * Method to generate random Alpha numeric serial identifiers based on provided max and min length.
     *
     * @param minLength   Minimum length of the random number
     * @param maxLength   Maximum length of the random number
     * @param randomCount Count of the random number
     * @return returns the List<String> with integer formatted random numbers
     */
    public List<String> alphaNumericGenerator(final int minLength, final int maxLength, final int randomCount) {
        try {
            final List<String> randomList = new ArrayList<>();

            for (int id = 0; id < randomCount; id++) {
                final StringBuilder randomID = new StringBuilder();
                final int charPicker = minLength + random.nextInt(maxLength - minLength + 1);

                for (int i = 0; i < charPicker; i++) {
                    randomID.append(ALPHA_NUMERIC_RANDOM_SET.charAt(random.nextInt(ALPHA_NUMERIC_RANDOM_SET.length())));
                }
                randomList.add(randomID.toString());
            }

            return randomList;
        } catch (Exception ex) {
            throw new TestDataGeneratorException("Exception occurred during creation of Random Alphanumeric serial identifiers : " + ex.getMessage(), ex);
        }
    }

    /**
     * Method to generate random URL Safe serial identifier based on provided min and max length.
     *
     * @param minLength   Minimum length of the random number
     * @param maxLength   Maximum length of the random number
     * @param randomCount Count of the random number
     * @return returns the List<String> with integer formatted random numbers
     */
    public List<String> urlSafeGenerator(final int minLength, int maxLength, int randomCount) {
        try {
            final List<String> randomList = new ArrayList<>();

            for (var id = 0; id < randomCount; id++) {
                var randomID = new StringBuilder();
                final int charPicker = minLength + random.nextInt(maxLength - minLength + 1);

                for (var i = 0; i < charPicker; i++) {
                    randomID.append(URL_SAFE_RANDOM_SET.charAt(random.nextInt(URL_SAFE_RANDOM_SET.length())));
                }
                randomList.add(randomID.toString());
            }
            return randomList;
        } catch (Exception ex) {
            throw new TestDataGeneratorException("Exception occurred during creation of Random URL safe special identifiers : " + ex.getMessage(), ex);
        }
    }

    /**
     * Method to generate random integer serial identifiers based on provided min and max length.
     *
     * @param minLength   Minimum length of the random number
     * @param maxLength   Maximum length of the random number
     * @param randomCount Count of the random number
     * @return returns the List<String> with integer formatted random numbers
     */
    public List<String> numericGenerator(final int minLength, final int maxLength, final int randomCount) {
        try {
            final List<String> randomList = new ArrayList<>();

            for (int id = 0; id < randomCount; id++) {
                final StringBuilder randomID = new StringBuilder();
                final int charPicker = minLength + random.nextInt(maxLength - minLength + 1);

                for (int i = 0; i < charPicker; i++) {
                    randomID.append(NUMERIC_RANDOM_SET.charAt(random.nextInt(NUMERIC_RANDOM_SET.length())));
                }
                randomList.add(randomID.toString());
            }
            return randomList;
        } catch (Exception ex) {
            throw new TestDataGeneratorException("Exception occurred during creation of Random Numeric serial identifiers : " + ex.getMessage(), ex);
        }
    }


}
