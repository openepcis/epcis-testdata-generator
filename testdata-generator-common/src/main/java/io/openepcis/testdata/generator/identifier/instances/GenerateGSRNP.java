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
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("gsrnp")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGSRNP extends GenerateEPCType2 {

  private static final String GSRNP_URN_PART = "urn:epc:id:gsrnp:";
  private static final String GSRNP_URI_PART = "/8017/";

  /**
   * Method to generate SSCC identifiers based on URN/WebURI format by manipulating the provided SSCC values.
   *
   * @param syntax                syntax in which identifiers need to be generated URN/WebURI
   * @param count                 count of SSCC instance identifiers need to be generated
   * @param dlURL                 if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
   * @param serialNumberGenerator instance of the RandomSerialNumberGenerator to generate random serial number
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateIdentifiers(syntax, count, dlURL, serialNumberGenerator);
  }

  //Function to check which type of instance identifiers need to be generated Range/Random/Static
  private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedGSRNP = new ArrayList<>();
      final String prefix = syntax.equals(IdentifierVocabularyType.URN) ? GSRNP_URN_PART : dlURL + GSRNP_URI_PART;
      final String delimiter = syntax.equals(IdentifierVocabularyType.URN) ? "." : "";
      final int maxAppendLength = syntax.equals(IdentifierVocabularyType.URN) ? 17 : 18;

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        // Generate SEQUENTIAL/RANGE identifiers in URN/WEBURI format based on from value and count
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          var append = gcp + rangeID;
          append = StringUtils.repeat("0", maxAppendLength - append.length()) + rangeID;
          formattedGSRNP.add(prefix + gcp + delimiter + append);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        // Generate RANDOM identifiers in URN/WEBURI format based on count
        final int requiredLength = maxAppendLength - gcp.length();
        final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, requiredLength, requiredLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGSRNP.add(prefix + gcp + delimiter + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        // Generate STATIC identifiers in URN/WEB URI
        var append = gcp + serialNumber;
        append = StringUtils.repeat("0", maxAppendLength - append.length()) + serialNumber;
        formattedGSRNP.add(prefix + gcp + delimiter + append);
      }
      return formattedGSRNP;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of GSRNP instance identifiers : " + ex.getMessage(), ex);
    }
  }
}
