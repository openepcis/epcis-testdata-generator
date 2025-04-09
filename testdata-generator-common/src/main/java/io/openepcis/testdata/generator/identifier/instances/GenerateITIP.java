/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
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
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("itip")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateITIP extends GenerateEPC {

  @Pattern(regexp = "^(\\s*|\\d{18})$", message = "ITIP should be of 18 digit")
  @NotNull(message = "ITIP value cannot be Null, should consist of 18 digits")
  @Schema(type = SchemaType.STRING, description = "ITIP consisting of 18 digits", required = true)
  private String itip;

  private static final String ITIP_URN_PART = "urn:epc:id:itip:";
  private static final String ITIP_URI_PART = "/8006/";
  private static final String ITIP_SERIAL_PART = "/21/";

  /**
   * Method to generate identifiers based on URN/WebURI format by manipulating the provided values.
   *
   * @param syntax syntax in which identifiers need to be generated URN/WebURI
   * @param count count of instance identifiers need to be generated
   * @param dlURL if provided use the provided dlURI to format WebURI identifiers else use default
   *     ref.gs1.org
   * @param serialNumberGenerator instance of the RandomSerialNumberGenerator to generate random
   *     serial number
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final String dlURL,
      final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateIdentifiers(syntax, count, dlURL, serialNumberGenerator);
  }

  private List<String> generateIdentifiers(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final String dlURL,
      final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedITIP = new ArrayList<>();
      final String prefix =
          syntax == IdentifierVocabularyType.URN ? ITIP_URN_PART : dlURL + ITIP_URI_PART;
      final String suffix = syntax == IdentifierVocabularyType.URN ? "." : ITIP_SERIAL_PART;

      // Modify the ITIP values for the URN formatted identifiers
      String modifiedITIP = itip;
      if (syntax == IdentifierVocabularyType.URN) {
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
      }

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        // For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          formattedITIP.add(prefix + modifiedITIP + suffix + rangeID);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        // For random generate random identifiers or based on seed
        randomMinLength = Math.max(1, Math.min(20, randomMinLength));
        randomMaxLength = Math.max(1, Math.min(20, randomMaxLength));

        final List<String> randomSerialNumbers =
            serialNumberGenerator.randomGenerator(
                randomType, randomMinLength, randomMaxLength, count);
        for (var randomID : randomSerialNumbers) {
          formattedITIP.add(prefix + modifiedITIP + suffix + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        // For none generate static identifier
        formattedITIP.add(prefix + modifiedITIP + suffix + serialNumber);
      }

      return formattedITIP;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of ITIP instance identifiers : " + ex.getMessage(),
          ex);
    }
  }
}
