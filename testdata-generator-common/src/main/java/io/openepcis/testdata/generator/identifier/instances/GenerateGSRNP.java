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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Setter
@JsonTypeName("gsrnp")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGSRNP extends GenerateEPCType2 {

  private static final String GSRNP_URN_PART = "urn:epc:id:gsrnp:";
  private static final String GSRNP_URI_PART = "/8017/";

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    if (IdentifierVocabularyType.WEBURI == syntax) {
      return generateWebURI(count);
    } else {
      return generateURN(count);
    }
  }

  // Generate GSRNP based on the URN Format
  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedGSRNP = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          String append = gcp + rangeID;
          append = StringUtils.repeat("0", 17 - append.length()) + rangeID;
          final String gsrnpID = GSRNP_URN_PART + gcp + "." + append;
          formattedGSRNP.add(gsrnpID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final int requiredLength = 17 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, requiredLength, requiredLength, count);

        for (var randomID : randomSerialNumbers) {
          final String gsrnpID = GSRNP_URN_PART + gcp + "." + randomID;
          formattedGSRNP.add(gsrnpID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          String append = gcp + serialNumber;
          append = StringUtils.repeat("0", 17 - append.length()) + serialNumber;
          final String gsrnpID = GSRNP_URN_PART + gcp + "." + append;
          formattedGSRNP.add(gsrnpID);
        }
      }
      return formattedGSRNP;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GSRNP instance identifiers in URN format, Please check the values provided for GSRNP instance identifiers : "
              + ex.getMessage());
    }
  }

  // Generate GSRN based on the WebURI Format
  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedGSRNP = new ArrayList<>();
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          String append = gcp + rangeID;
          append = StringUtils.repeat("0", 18 - append.length()) + rangeID;
          final String gsrnpID = DomainName.IDENTIFIER_DOMAIN + GSRNP_URI_PART + gcp + append;
          formattedGSRNP.add(gsrnpID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final int requiredLength = 18 - gcp.length();
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, requiredLength, requiredLength, randomCount);
        for (var randomID : randomSerialNumbers) {
          final String gsrnpID = DomainName.IDENTIFIER_DOMAIN + GSRNP_URI_PART + gcp + randomID;
          formattedGSRNP.add(gsrnpID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          String append = gcp + serialNumber;
          append = StringUtils.repeat("0", 18 - append.length()) + serialNumber;
          final String gsrnpID = DomainName.IDENTIFIER_DOMAIN + GSRNP_URI_PART + gcp + append;
          formattedGSRNP.add(gsrnpID);
        }
      }
      return formattedGSRNP;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GSRNP instance identifiers in URN format, Please check the values provided for GSRNP instance identifiers : "
              + ex.getMessage());
    }
  }
}
