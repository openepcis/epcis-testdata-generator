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
import org.apache.commons.lang.StringUtils;

@Setter
@ToString(callSuper = true)
@JsonTypeName("gsin")
@RegisterForReflection
public class GenerateGSIN extends GenerateEPCType2 {

  private static final String GSIN_URN_PART = "urn:epc:id:gsin:";
  private static final String GSIN_URI_PART = "/402/";

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

  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedGSIN = new ArrayList<>();

      // RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          String append = gcp + rangeID;
          append = StringUtils.repeat("0", 16 - append.length()) + rangeID;
          formattedGSIN.add(GSIN_URN_PART + gcp + "." + append);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(RandomizationType.NUMERIC, 1, 4, count);

        for (var randomID : randomSerialNumbers) {
          var append = gcp + randomID;
          append = StringUtils.repeat("0", 16 - append.length()) + randomID;
          formattedGSIN.add(GSIN_URN_PART + gcp + "." + append);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        // NONE selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          String append = gcp + "." + serialNumber;
          append = StringUtils.repeat("0", 16 - append.length()) + serialNumber;
          formattedGSIN.add(GSIN_URN_PART + gcp + "." + append);
        }
      }
      return formattedGSIN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GSIN instance identifiers in URN format, Please check the values provided for GSIN instance identifiers : "
              + ex.getMessage());
    }
  }

  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedGSIN = new ArrayList<>();

      // RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          var append = gcp + rangeID;
          append = StringUtils.repeat("0", 17 - append.length()) + rangeID;
          formattedGSIN.add(DomainName.IDENTIFIER_DOMAIN + GSIN_URI_PART + gcp + append);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // RANDOM calculation
        List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(RandomizationType.NUMERIC, 1, 4, count);

        for (var randomID : randomSerialNumbers) {
          var append = gcp + randomID;
          append = StringUtils.repeat("0", 17 - append.length()) + randomID;
          formattedGSIN.add(DomainName.IDENTIFIER_DOMAIN + GSIN_URI_PART + gcp + append);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        // None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          var append = gcp + serialNumber;
          append = StringUtils.repeat("0", 17 - append.length()) + serialNumber;
          formattedGSIN.add(DomainName.IDENTIFIER_DOMAIN + GSIN_URI_PART + gcp + append);
        }
      }
      return formattedGSIN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GSIN instance identifiers in WebURI format, Please check the values provided for GSIN instance identifiers : "
              + ex.getMessage());
    }
  }
}
