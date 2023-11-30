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
import org.apache.commons.lang.StringUtils;

@Setter
@JsonTypeName("sscc")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateSSCC extends GenerateEPCType2 {

  private static final String SSCC_URN_PART = "urn:epc:id:sscc:";
  private static final String SSCC_URI_PART = "/00/";

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
      final List<String> formattedSSCC = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          var append = gcp + rangeID;
          append = StringUtils.repeat("0", 17 - append.length()) + rangeID;
          formattedSSCC.add(SSCC_URN_PART + gcp + "." + append);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final int requiredLength = 17 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, requiredLength, requiredLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedSSCC.add(SSCC_URN_PART + gcp + "." + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          var append = gcp + serialNumber;
          append = StringUtils.repeat("0", 17 - append.length()) + serialNumber;
          formattedSSCC.add(SSCC_URN_PART + gcp + "." + append);
        }
      }
      return formattedSSCC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of SSCC instance identifiers in URN format, Please check the values provided for SSCC instance identifiers : "
              + ex.getMessage(), ex);
    }
  }

  private List<String> generateWebURI(Integer count, final String dlURL) {
    try {
      final List<String> formattedSSCC = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          var append = gcp + rangeID;
          append = StringUtils.repeat("0", 18 - append.length()) + rangeID;
          formattedSSCC.add(dlURL + SSCC_URI_PART + gcp + append);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final int requiredLength = 18 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, requiredLength, requiredLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedSSCC.add(dlURL + SSCC_URI_PART + gcp + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          var append = gcp + serialNumber;
          append = StringUtils.repeat("0", 17 - append.length()) + serialNumber;
          formattedSSCC.add(dlURL + SSCC_URI_PART + gcp + append);
        }
      }
      return formattedSSCC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of SSCC instance identifiers in WebURI format, Please check the values provided for SSCC instance identifiers : "
              + ex.getMessage(), ex);
    }
  }
}
