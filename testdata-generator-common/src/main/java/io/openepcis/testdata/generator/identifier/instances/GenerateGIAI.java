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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;

@Setter
@JsonTypeName("giai")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGIAI extends GenerateEPCType2 {

  private static final String GIAI_URN_PART = "urn:epc:id:giai:";
  private static final String GIAI_URI_PART = "/8004/";

  /**
   * Method to generate identifiers based on URN/WebURI format by manipulating the provided values.
   *
   * @param syntax syntax in which identifiers need to be generated URN/WebURI.
   * @param count count of instance identifiers need to be generated.
   * @param dlURL if provided use the provided dlURI to format WebURI identifiers else use default
   *     ref.gs1.org.
   * @param serialNumberGenerator instance of the RandomSerialNumberGenerator to generate random
   *     serial number
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final String dlURL,
      final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateIdentifiers(syntax, count, dlURL, serialNumberGenerator);
  }

  // Function to check which type of instance identifiers need to be generated Range/Random/Static
  // and accordingly generate
  private List<String> generateIdentifiers(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final String dlURL,
      final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedGIAI = new ArrayList<>();
      final String prefix =
          syntax.equals(IdentifierVocabularyType.URN) ? GIAI_URN_PART : dlURL + GIAI_URI_PART;
      final String delimiter = syntax.equals(IdentifierVocabularyType.URN) ? "." : "";

      if (SerialTypeChecker.isRangeType(this.serialType, count, this.rangeFrom)) {
        // For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count.longValue();
            rangeID++) {
          formattedGIAI.add(prefix + gcp + delimiter + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count.longValue());
      } else if (SerialTypeChecker.isRandomType(this.serialType, count)) {
        // For random generate random identifiers or based on seed
        final int requiredMaxLength = 29 - gcp.length();
        final List<String> randomSerialNumbers =
            serialNumberGenerator.randomGenerator(
                RandomizationType.NUMERIC, 1, requiredMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGIAI.add(prefix + gcp + delimiter + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(this.serialType, count, this.serialNumber)) {
        // For none generate static identifier
        formattedGIAI.add(prefix + gcp + delimiter + serialNumber);
      }
      return formattedGIAI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GIAI instance identifiers : " + ex.getMessage(),
          ex);
    }
  }
}
