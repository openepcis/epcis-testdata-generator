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
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
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

@Setter
@JsonTypeName("cpi")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateCPI extends GenerateEPC {

  @Pattern(regexp = "[A-Z0-9]{7,30}", message = "Invalid characters in CPI")
  @NotNull(message = "CPI value for the instance identifier cannot be Null")
  @Schema(type = SchemaType.STRING, description = "CPI between 7 and 30 digits", required = true)
  private String cpi;

  private static final String CPI_URN_PART = "urn:epc:id:cpi:";
  private static final String CPI_URI_PART = "/8010/";
  private static final String SERIAL_URI_PART = "/8011/";

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

  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedCPI = new ArrayList<>();
      final String modifiedCPI = cpi.substring(0, gcpLength) + "." + cpi.substring(gcpLength);

      // Return the list of CPI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count.longValue();
            rangeID++) {
          formattedCPI.add(CPI_URN_PART + modifiedCPI + "." + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count.longValue());
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of CPI for RANDOM calculation
        randomMinLength = randomMinLength < 1 || randomMinLength > 12 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 12 ? 12 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, randomMinLength, randomMaxLength, count);

        for (String randomID : randomSerialNumbers) {
          formattedCPI.add(CPI_URN_PART + modifiedCPI + "." + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        // Return the single CPI values for None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedCPI.add(CPI_URN_PART + modifiedCPI + "." + serialNumber);
        }
      }
      return formattedCPI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of CPI instance identifiers in URN format, Please check the values provided for CPI instance identifiers : "
              + ex.getMessage(), ex);
    }
  }

  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedCPI = new ArrayList<>();

      // Return the list of CPI for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count.longValue();
            rangeID++) {
          formattedCPI.add(
              DomainName.IDENTIFIER_DOMAIN + CPI_URI_PART + cpi + SERIAL_URI_PART + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count.longValue());
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of CPI for RANDOM calculation
        randomMinLength = randomMinLength < 1 || randomMinLength > 12 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 12 ? 12 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedCPI.add(
              DomainName.IDENTIFIER_DOMAIN + CPI_URI_PART + cpi + SERIAL_URI_PART + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null && count != null) {
        // Return the single CPI values for None selection
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedCPI.add(
              DomainName.IDENTIFIER_DOMAIN + CPI_URI_PART + cpi + SERIAL_URI_PART + serialNumber);
        }
      }
      return formattedCPI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of CPI instance identifiers in WebURI format, Please check the values provided for CPI instance identifiers : "
              + ex.getMessage(), ex);
    }
  }
}
