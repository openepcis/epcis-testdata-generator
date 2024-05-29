package io.openepcis.testdata.generator.identifier.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumericUtils {

    /**
     * Check if the provided Integer is not NULL and greater than 0
     */
    public static boolean isPositive(final Integer count) {
        return count != null && count > 0;
    }

    /**
     * Check if the provided BigInteger is not NULL and greater than 0
     */
    public static boolean isPositive(final BigInteger count) {
        return count != null && count.longValue() > 0;
    }
}

