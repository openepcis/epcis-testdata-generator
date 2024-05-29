package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to generate the random numbers using the Mersenne Twister (MT) algorithm where \ if the seed is provided then Random numbers are generated based on the seed. \ If no seed is provided then truly random numbers are generated.
 */
public class RandomMersenneValueGenerator {

    private static RandomMersenneValueGenerator randomMersenneValueGenerator;
    private final RandomGenerator random;

    private RandomMersenneValueGenerator(Long seed) {
        seed = seed != null ? seed : System.currentTimeMillis();
        this.random = new MersenneTwister(seed);
    }

    public static synchronized RandomMersenneValueGenerator getInstance(final Long seed) {
        randomMersenneValueGenerator = new RandomMersenneValueGenerator(seed);
        return randomMersenneValueGenerator;
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


    static final String ALPHA_NUMERIC_RANDOM_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    static final String URL_SAFE_RANDOM_SET = "abcdefghijklmnopqrstuvwxyz0123456789-_";
    static final String NUMERIC_RANDOM_SET = "1234567890";

    /**
     * Method to generate random Alpha numeric numbers.
     *
     * @param minLength   Minimum length of the random number
     * @param maxLength   Maximum length of the random number
     * @param randomCount Count of the random number
     * @return returns the List<String> with integer formatted random numbers
     */
    public List<String> alphaNumericGenerator(final int minLength, final int maxLength, final int randomCount) {
        System.out.println("ALPHA NUMERIC GENERATOR");
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
            throw new TestDataGeneratorException(
                    "Exception occurred during creation of seral numbers in Random Alphanumeric format, Please check the values provided values for Identifiers random values : "
                            + ex.getMessage(), ex);
        }
    }

    /**
     * Method to generate random URL Safe numbers.
     *
     * @param minLength   Minimum length of the random number
     * @param maxLength   Maximum length of the random number
     * @param randomCount Count of the random number
     * @return returns the List<String> with integer formatted random numbers
     */
    public List<String> urlSafeGenerator(final int minLength, int maxLength, int randomCount) {
        System.out.println("URL GENERATOR");
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
            throw new TestDataGeneratorException("Exception occurred during creation of seral numbers in Random Alphanumeric with special characters, Please check the values provided values for Identifiers random values : " + ex.getMessage(), ex);
        }
    }

    /**
     * Method to generate random integer numbers.
     *
     * @param minLength   Minimum length of the random number
     * @param maxLength   Maximum length of the random number
     * @param randomCount Count of the random number
     * @return returns the List<String> with integer formatted random numbers
     */
    public List<String> numericGenerator(final int minLength, final int maxLength, final int randomCount) {
        System.out.println("NUMERIC GENERATOR");
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
            throw new TestDataGeneratorException("Exception occurred during creation of seral numbers in Random Numeric format, Please check the values provided values for Identifiers random values : " + ex.getMessage(), ex);
        }
    }


}
