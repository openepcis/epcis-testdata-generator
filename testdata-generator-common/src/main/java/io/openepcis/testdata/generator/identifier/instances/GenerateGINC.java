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
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;

@Setter
@JsonTypeName("ginc")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGINC extends GenerateEPCType2 {

  private static final String GINC_URN_PART = "urn:epc:id:ginc:";
  private static final String GINC_URI_PART = "/401/";

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count, final String dlURL) {
    if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
      // For WebURI syntax call the generateWebURI, pass the required identifiers count to create
      // Instance Identifiers
      return generateWebURI(count, dlURL);
    } else {
      // For URN syntax call the generateURN, pass the required identifiers count to create Instance
      // Identifiers
      return generateURN(count);
    }
  }

  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedGINC = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        // Return the list of GINC for RANGE calculation
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedGINC.add(GINC_URN_PART + gcp + "." + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of GINC for RANDOM calculation
        final int requiredMaxLength = 29 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.getInstance().randomGenerator(
                RandomizationType.NUMERIC, 1, requiredMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGINC.add(GINC_URN_PART + gcp + "." + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        // Return the single GINC values for None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGINC.add(GINC_URN_PART + gcp + "." + serialNumber);
        }
      }
      return formattedGINC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GINC instance identifiers in URN format, Please check the values provided for GINC instance identifiers : "
              + ex.getMessage(), ex);
    }
  }

  private List<String> generateWebURI(Integer count, final String dlURL) {
    try {
      final List<String> formattedGINC = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        // RANGE calculation
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedGINC.add(dlURL + GINC_URI_PART + gcp + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // RANDOM calculation
        final int requiredMaxLength = 29 - gcp.length();
        final List<String> randomSerialNumbers =
                RandomValueGenerator.getInstance().randomGenerator(
                RandomizationType.NUMERIC, 1, requiredMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGINC.add(dlURL + GINC_URI_PART + gcp + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        // None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGINC.add(dlURL + GINC_URI_PART + gcp + serialNumber);
        }
      }
      return formattedGINC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GINC instance identifiers in WebURI format, Please check the values provided for GINC instance identifiers : "
              + ex.getMessage(), ex);
    }
  }
}
