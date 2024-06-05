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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.RandomMersenneValueGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("sscc")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateSSCC extends GenerateEPCType2 {

  private static final String SSCC_URN_PART = "urn:epc:id:sscc:";
  private static final String SSCC_URI_PART = "/00/";

  /**
   * Method to generate SSCC identifiers based on URN/WebURI format by manipulating the provided SSCC values.
   *
   * @param syntax syntax in which identifiers need to be generated URN/WebURI
   * @param count  count of SSCC instance identifiers need to be generated
   * @param dlURL  if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
   * @param seed   seed for random mersenne generator to generate same random numbers if same seed is provided
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
      // For WebURI syntax call the generateWebURI to create Instance Identifiers
      return generateWebURI(count, dlURL, seed);
    } else {
      // For URN syntax call the generateURN to create Instance Identifiers
      return generateURN(count, seed);
    }
  }

  /**
   * Method to generate SSCC identifiers in URN format.
   *
   * @param count count of SSCC instance identifiers need to be generated
   * @param seed  seed for random mersenne generator to generate same random numbers if same seed is provided
   * @return returns list of identifiers in string format
   */
  private List<String> generateURN(final Integer count, final Long seed) {
    return generateIdentifiers(count, seed, SSCC_URN_PART, ".", 17);
  }

  /**
   * Method to generate SSCC identifiers in WebURI format
   *
   * @param count count of SSCC instance identifiers need to be generated
   * @param seed  seed for random mersenne generator to generate same random numbers if same seed is provided
   * @return returns list of identifiers in string format
   */
  private List<String> generateWebURI(final Integer count, final String dlURL, final Long seed) {
    gcp = "0" + gcp; // Prepend '0' to gcp for WebURI format
    return generateIdentifiers(count, seed, dlURL + SSCC_URI_PART, "", 18);
  }

  //Generate the SSCC identifiers based on the serialType and format them according to URN or WebURI format.
  private List<String> generateIdentifiers(final Integer count, final Long seed, final String prefix, final String delimiter, final int totalLength) {
    try {
      final List<String> formattedSSCC = new ArrayList<>();
      String baseGcp = gcp;

      if (SerialTypeChecker.isRangeType(this.serialType, count, this.rangeFrom)) {
        // Generate SEQUENTIAL/RANGE SSCC identifiers in URN/WEBURI format based on from value and count
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          formattedSSCC.add(prefix + baseGcp + delimiter + padAndAppend(baseGcp, rangeID, totalLength));
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(this.serialType, count)) {
        // Generate RANDOM SSCC identifiers in URN/WEBURI format based on count
        final int requiredLength = totalLength - baseGcp.length();
        final List<String> randomSerialNumbers = RandomMersenneValueGenerator.getInstance(seed).randomGenerator(RandomizationType.NUMERIC, requiredLength, requiredLength, count);
        for (var randomID : randomSerialNumbers) {
          formattedSSCC.add(prefix + baseGcp + delimiter + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(this.serialType, count, this.serialNumber)) {
        // Generate STATIC SSCC identifiers in URN/WEB URI format based on count
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedSSCC.add(prefix + baseGcp + delimiter + padAndAppend(baseGcp, serialNumber, totalLength));
        }
      }
      return formattedSSCC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of SSCC instance identifiers : " + ex.getMessage(), ex);
    }
  }

  // Append the 0s to the generated identifiers to match the required SSCC length of 18
  private String padAndAppend(final String baseGcp, final Object id, final int totalLength) {
    return StringUtils.repeat("0", totalLength - (baseGcp.length() + String.valueOf(id).length())) + id;
  }
}
