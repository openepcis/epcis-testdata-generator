/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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
package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class RandomValueGenerator {

  private static final SecureRandom random = new SecureRandom();

  public static List<String> randomGenerator(
      RandomizationType type, int minLength, int maxLength, int randomCount) {
    return switch (type) {
      case ALPHA_NUMERIC:
        yield alphaNumericGenerator(minLength, maxLength, randomCount);
      case URL_SAFE_CHARACTERS:
        yield urlSafeGenerator(minLength, maxLength, randomCount);
      default:
        yield numericGenerator(minLength, maxLength, randomCount);
    };
  }

  // If Numeric random values needs to be generated
  public static List<String> numericGenerator(int minLength, int maxLength, int randomCount) {
    try {
      final List<String> randomList = new ArrayList<>();
      final var numericRandomSet = "1234567890";

      for (var id = 0; id < randomCount; id++) {
        var randomID = new StringBuilder();
        final int charPicker = minLength + random.nextInt(maxLength - minLength + 1);

        for (var i = 0; i < charPicker; i++) {
          randomID.append(numericRandomSet.charAt(random.nextInt(numericRandomSet.length())));
        }
        randomList.add(randomID.toString());
      }
      return randomList;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during creation of seral numbers in Random Numeric format, Please check the values provided values for Identifiers random values : "
              + ex.getMessage(), ex);
    }
  }

  // If Alphanumeric random values needs to be generated
  public static List<String> alphaNumericGenerator(int minLength, int maxLength, int randomCount) {
    try {
      final var alphaNumericRandomSet =
          "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      final List<String> randomList = new ArrayList<>();

      for (var id = 0; id < randomCount; id++) {
        var randomID = new StringBuilder();
        final int charPicker = minLength + random.nextInt(maxLength - minLength + 1);

        for (var i = 0; i < charPicker; i++) {
          randomID.append(
              alphaNumericRandomSet.charAt(random.nextInt(alphaNumericRandomSet.length())));
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

  // If URL Safe random values needs to be generated
  public static List<String> urlSafeGenerator(int minLength, int maxLength, int randomCount) {
    try {
      final var urlSafeRandomSet = "abcdefghijklmnopqrstuvwxyz0123456789-_";
      final List<String> randomList = new ArrayList<>();

      for (var id = 0; id < randomCount; id++) {
        var randomID = new StringBuilder();
        final int charPicker = minLength + random.nextInt(maxLength - minLength + 1);

        for (var i = 0; i < charPicker; i++) {
          randomID.append(urlSafeRandomSet.charAt(random.nextInt(urlSafeRandomSet.length())));
        }
        randomList.add(randomID.toString());
      }
      return randomList;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during creation of seral numbers in Random Alphanumeric with special characters, Please check the values provided values for Identifiers random values : "
              + ex.getMessage(), ex);
    }
  }
}
