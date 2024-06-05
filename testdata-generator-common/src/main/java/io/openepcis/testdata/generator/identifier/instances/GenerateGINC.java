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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("ginc")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGINC extends GenerateEPCType2 {

  private static final String GINC_URN_PART = "urn:epc:id:ginc:";
  private static final String GINC_URI_PART = "/401/";

  /**
   * Method to generate identifiers based on URN/WebURI format by manipulating the provided values.
   *
   * @param syntax syntax in which identifiers need to be generated URN/WebURI
   * @param count  count of instance identifiers need to be generated
   * @param dlURL  if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
   * @param seed   seed for random mersenne generator to generate same random numbers if same seed is provided
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    return generateIdentifiers(syntax, count, dlURL, seed);
  }

  private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    try {
      final List<String> formattedGINC = new ArrayList<>();
      final String prefix = syntax == IdentifierVocabularyType.URN ? GINC_URN_PART : dlURL + GINC_URI_PART;
      final String suffix = syntax == IdentifierVocabularyType.URN ? "." : "";

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          formattedGINC.add(prefix + gcp + suffix + rangeID);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count.longValue());
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        final int requiredMaxLength = 29 - gcp.length();
        final List<String> randomSerialNumbers = RandomSerialNumberGenerator.getInstance(seed).randomGenerator(RandomizationType.NUMERIC, 1, requiredMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGINC.add(prefix + gcp + suffix + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        formattedGINC.add(prefix + gcp + suffix + serialNumber);
      }
      return formattedGINC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of GINC instance identifiers : " + ex.getMessage(), ex);
    }
  }
}
