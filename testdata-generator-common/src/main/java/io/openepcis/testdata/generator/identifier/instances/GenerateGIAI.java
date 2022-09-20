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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.DomainName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
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

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
      // For WebURI syntax call the generateWebURI, pass the required identifiers count to create
      // Instance Identifiers
      return generateWebURI(count);
    } else {
      // For URN syntax call the generateURN, pass the required identifiers count to create Instance
      // Identifiers
      return generateURN(count);
    }
  }

  // Generate URN formatted GIAI
  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedGIAI = new ArrayList<>();

      // Return the list of GIAI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          formattedGIAI.add(GIAI_URN_PART + gcp + "." + rangeID);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of GIAI for RANDOM calculation
        final int requiredMaxLength = 29 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, 1, requiredMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGIAI.add(GIAI_URN_PART + gcp + "." + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        // Return the single GIAI values for None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGIAI.add(GIAI_URN_PART + gcp + "." + serialNumber);
        }
      }
      return formattedGIAI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GIAI instance identifiers in URN format, Please check the values provided for GIAI instance identifiers : "
              + ex.getMessage());
    }
  }

  // Generate URN formatted GIAI
  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedGIAI = new ArrayList<>();

      // Return the list of GIAI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          formattedGIAI.add(DomainName.IDENTIFIER_DOMAIN + GIAI_URI_PART + gcp + rangeID);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of GIAI for RANDOM calculation
        final int requiredMaxLength = 29 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, 1, requiredMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGIAI.add(DomainName.IDENTIFIER_DOMAIN + GIAI_URI_PART + gcp + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        // Return the single GIAI values for None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGIAI.add(DomainName.IDENTIFIER_DOMAIN + GIAI_URI_PART + gcp + serialNumber);
        }
      }
      return formattedGIAI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GIAI instance identifiers in WebURI format, Please check the values provided for GIAI instance identifiers : "
              + ex.getMessage());
    }
  }
}
