package io.openepcis.testdata.generator.identifier.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialTypeChecker {

    /**
     * Method to check if the required identifiers need to be generated in RANGE type
     */
    public static boolean isRangeType(final String serialType, final Integer count, final BigInteger rangeFrom) {
        return serialType.equalsIgnoreCase("range") && NumericUtils.isPositive(count) && NumericUtils.isPositive(rangeFrom);
    }

    /**
     * Method to check if the required identifiers need to be generated in RANDOM type
     */
    public static boolean isRandomType(final String serialType, final Integer count) {
        return serialType.equalsIgnoreCase("random") && NumericUtils.isPositive(count);
    }

    /**
     * Method to check if the required identifiers need to be generated in STATIC type
     */
    public static boolean isNoneType(final String serialType, final Integer count, final String serialNumber) {
        return serialType.equalsIgnoreCase("none") && NumericUtils.isPositive(count) && serialNumber != null;
    }
}
