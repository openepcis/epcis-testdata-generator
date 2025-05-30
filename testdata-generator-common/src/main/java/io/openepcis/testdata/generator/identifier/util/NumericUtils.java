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
public class NumericUtils {

  /** Check if the provided Integer is not NULL and greater than 0 */
  public static boolean isPositive(final Integer count) {
    return count != null && count > 0;
  }

  /** Check if the provided BigInteger is not NULL and greater than 0 */
  public static boolean isPositive(final BigInteger count) {
    return count != null && count.longValue() > 0;
  }
}
