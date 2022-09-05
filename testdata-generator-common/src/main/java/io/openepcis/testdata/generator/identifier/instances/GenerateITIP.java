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
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("itip")
@ToString(callSuper = true)
public class GenerateITIP extends GenerateEPC {

  @Pattern(regexp = "^(\\s*|\\d{18})$", message = "ITIP should be of 18 digit")
  @NotNull(message = "ITIP value cannot be Null, should consist of 18 digits")
  @Schema(type = SchemaType.STRING, description = "ITIP consisting of 18 digits", required = true)
  private String itip;

  private static final String ITIP_URN_PART = "urn:epc:id:itip:";
  private static final String ITIP_URI_PART = "/8006/";
  private static final String ITIP_SERIAL_PART = "/21/";

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    if (IdentifierVocabularyType.WEBURI == syntax) {
      return generateWebURI(count);
    } else {
      return generateURN(count);
    }
  }

  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedITIP = new ArrayList<>();
      var modifiedITIP = itip.substring(0, 18);
      modifiedITIP =
          modifiedITIP.substring(1, gcpLength + 1)
              + "."
              + modifiedITIP.charAt(0)
              + modifiedITIP.substring(gcpLength + 1);
      modifiedITIP =
          modifiedITIP.substring(0, 14)
              + "."
              + modifiedITIP.substring(15, 17)
              + "."
              + modifiedITIP.substring(17);

      // RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          formattedITIP.add(ITIP_URN_PART + modifiedITIP + "." + rangeID);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        randomMinLength = randomMinLength < 1 || randomMinLength > 20 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 20 ? 20 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var rangeID : randomSerialNumbers) {
          formattedITIP.add(ITIP_URN_PART + modifiedITIP + "." + rangeID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedITIP.add(ITIP_URN_PART + modifiedITIP + "." + serialNumber);
        }
      }
      return formattedITIP;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of ITIP instance identifiers in URN format, Please check the values provided for ITIP instance identifiers : "
              + ex.getMessage());
    }
  }

  private List<String> generateWebURI(Integer count) {
    try {
      final List<String> formattedITIP = new ArrayList<>();
      var modifiedITIP = itip.substring(0, 18);

      // RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          formattedITIP.add(
              DomainName.IDENTIFIER_DOMAIN
                  + ITIP_URI_PART
                  + modifiedITIP
                  + ITIP_SERIAL_PART
                  + rangeID);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        randomMinLength = randomMinLength < 1 || randomMinLength > 20 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 20 ? 20 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedITIP.add(
              DomainName.IDENTIFIER_DOMAIN
                  + ITIP_URI_PART
                  + modifiedITIP
                  + ITIP_SERIAL_PART
                  + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        for (var noneCounter = 0; noneCounter < count; noneCounter++) {
          formattedITIP.add(
              DomainName.IDENTIFIER_DOMAIN
                  + ITIP_URI_PART
                  + modifiedITIP
                  + ITIP_SERIAL_PART
                  + serialNumber);
        }
      }
      return formattedITIP;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of ITIP instance identifiers in WebURI format, Please check the values provided for ITIP instance identifiers : "
              + ex.getMessage());
    }
  }
}
