/*
 * Copyright 2022 benelog GmbH & Co. KG
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

import io.openepcis.testdata.generator.constants.BusinessStep;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessStepFormatter {

  public static String format(final BusinessStep businessStep) {
    try {
      return businessStep.toString();
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of BusinessStep, Please check the values provided for BusinessStep :  "
              + ex.getMessage());
    }
  }
}
