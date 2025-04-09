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

import java.math.BigInteger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialTypeChecker {

  /** Method to check if the required identifiers need to be generated in RANGE type */
  public static boolean isRangeType(
      final String serialType, final Integer count, final BigInteger rangeFrom) {
    return serialType.equalsIgnoreCase("range")
        && NumericUtils.isPositive(count)
        && NumericUtils.isPositive(rangeFrom);
  }

  /** Method to check if the required identifiers need to be generated in RANDOM type */
  public static boolean isRandomType(final String serialType, final Integer count) {
    return serialType.equalsIgnoreCase("random") && NumericUtils.isPositive(count);
  }

  /** Method to check if the required identifiers need to be generated in STATIC type */
  public static boolean isNoneType(
      final String serialType, final Integer count, final String serialNumber) {
    return serialType.equalsIgnoreCase("none")
        && NumericUtils.isPositive(count)
        && serialNumber != null;
  }

  public static boolean isNoneType(
      final String serialType, final Integer count, final Integer serialNumber) {
    return serialType.equalsIgnoreCase("none")
        && NumericUtils.isPositive(count)
        && NumericUtils.isPositive(serialNumber);
  }
}
