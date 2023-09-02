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
import io.openepcis.testdata.generator.constants.DomainName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@Setter
@JsonTypeName("gdti")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGDTI extends GenerateEPC {

  private static final String GDTI_URN_PART = "urn:epc:id:gdti:";
  private static final String GDTI_URI_PART = "/253/";

  @Pattern(regexp = "^(\\s*|\\d{13})$", message = "GDTI should be of 13 digit")
  @NotNull(message = "GDTI cannot be null, it should consist of 13 digits")
  @Schema(type = SchemaType.STRING, description = "GDTI consisting of 13 digits", required = true)
  private String gdti;

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    if (IdentifierVocabularyType.WEBURI == syntax) {
      // For WebURI syntax call the generateWebURI, pass the required identifiers count to create
      // Instance Identifiers
      return generateWebURI(count);
    } else {
      // For URN syntax call the generateURN, pass the required identifiers count to create Instance
      // Identifiers
      return generateURN(count);
    }
  }

  // Generate URN formatted GDTI
  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedGDTI = new ArrayList<>();
      final String modifiedGDTI = CompanyPrefixFormatter.gcpFormatterNormal(gdti, gcpLength) + ".";

      // Return the list of GDTI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedGDTI.add(GDTI_URN_PART + modifiedGDTI + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of GDTI for RANDOM calculation
        randomMinLength = randomMinLength < 1 || randomMinLength > 17 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 17 ? 17 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGDTI.add(GDTI_URN_PART + modifiedGDTI + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        // Return the single GDTI values for None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGDTI.add(GDTI_URN_PART + modifiedGDTI + serialNumber);
        }
      }
      return formattedGDTI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GDTI instance identifiers in URN format, Please check the values provided for GDTI instance identifiers : "
              + ex.getMessage());
    }
  }

  // Generate URN formatted GDTI
  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedGDTI = new ArrayList<>();
      final String modifiedGDTI =
          gdti.substring(0, 11)
              + UPCEANLogicImpl.calcChecksum(gdti.substring(0, 11))
              + gdti.charAt(12);

      // Return the list of GDTI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedGDTI.add(DomainName.IDENTIFIER_DOMAIN + GDTI_URI_PART + modifiedGDTI + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of GDTI for RANDOM calculation
        randomMinLength = randomMinLength < 1 || randomMinLength > 17 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 17 ? 17 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGDTI.add(DomainName.IDENTIFIER_DOMAIN + GDTI_URI_PART + modifiedGDTI + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        // Return the single GDTI values for None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedGDTI.add(
              DomainName.IDENTIFIER_DOMAIN + GDTI_URI_PART + modifiedGDTI + serialNumber);
        }
      }
      return formattedGDTI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GDTI instance identifiers in WebURI format, Please check the values provided for GDTI instance identifiers : "
              + ex.getMessage());
    }
  }
}
