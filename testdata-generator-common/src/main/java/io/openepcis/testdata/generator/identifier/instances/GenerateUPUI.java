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
@JsonTypeName("upui")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateUPUI extends GenerateEPC {

  @Pattern(regexp = "^(\\s*|\\d{14})$", message = "UPUI should be of 14 digit")
  @NotNull(message = "UPUI cannot be null, it should consist of 14 digits")
  @Schema(type = SchemaType.STRING, description = "UPUI consisting of 14 digits", required = true)
  private String upui;

  private static final String UPUI_URN_PART = "urn:epc:id:upui:";
  private static final String UPUI_URI_PART = "/01/";
  private static final String UPUI_URI_SERIAL_PART = "/235/";

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
      final List<String> formattedUPUI = new ArrayList<>();
      var modifiedUPUI =
          upui.substring(0, 13) + UPCEANLogicImpl.calcChecksum(upui.substring(0, 13));
      modifiedUPUI =
          CompanyPrefixFormatter.gcpFormatterWithReplace(modifiedUPUI, gcpLength).toString();

      // RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedUPUI.add(UPUI_URN_PART + modifiedUPUI + "." + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        randomMinLength = randomMinLength < 1 || randomMinLength > 28 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 28 ? 28 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);
        for (var randomID : randomSerialNumbers) {
          formattedUPUI.add(UPUI_URN_PART + modifiedUPUI + "." + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedUPUI.add(UPUI_URN_PART + modifiedUPUI + "." + serialNumber);
        }
      }
      return formattedUPUI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of UPUI instance identifiers in URN format, Please check the values provided for UPUI instance identifiers : "
              + ex.getMessage(), ex);
    }
  }

  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedUPUI = new ArrayList<>();
      final String modifiedUPUI =
          upui.substring(0, 13) + UPCEANLogicImpl.calcChecksum(upui.substring(0, 13));

      // RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.longValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedUPUI.add(
              DomainName.IDENTIFIER_DOMAIN
                  + UPUI_URI_PART
                  + modifiedUPUI
                  + UPUI_URI_SERIAL_PART
                  + rangeID);
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        randomMinLength = randomMinLength < 1 || randomMinLength > 28 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 28 ? 28 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedUPUI.add(
              DomainName.IDENTIFIER_DOMAIN
                  + UPUI_URI_PART
                  + modifiedUPUI
                  + UPUI_URI_SERIAL_PART
                  + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedUPUI.add(
              DomainName.IDENTIFIER_DOMAIN
                  + UPUI_URI_PART
                  + modifiedUPUI
                  + UPUI_URI_SERIAL_PART
                  + serialNumber);
        }
      }
      return formattedUPUI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of UPUI instance identifiers in WebURI format, Please check the values provided for UPUI instance identifiers : "
              + ex.getMessage(), ex);
    }
  }
}
