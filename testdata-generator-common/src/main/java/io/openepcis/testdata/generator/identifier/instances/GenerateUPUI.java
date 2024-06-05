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
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

  /**
   * Method to generate identifiers based on URN/WebURI format by manipulating the provided values.
   *
   * @param syntax syntax in which identifiers need to be generated URN/WebURI
   * @param count  count of instance identifiers need to be generated
   * @param dlURL  if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
   * @param seed   seed for random mersenne generator to generate same random numbers if same seed is provided
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    return generateIdentifiers(syntax, count, dlURL, seed);
  }

  private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    try {
      final List<String> formattedUPUI = new ArrayList<>();
      final String prefix = syntax == IdentifierVocabularyType.URN ? UPUI_URN_PART : dlURL + UPUI_URI_PART;
      final String suffix = syntax == IdentifierVocabularyType.URN ? "." : UPUI_URI_SERIAL_PART;
      upui = upui.substring(0, 13) + UPCEANLogicImpl.calcChecksum(upui.substring(0, 13));
      upui = syntax == IdentifierVocabularyType.URN ? CompanyPrefixFormatter.gcpFormatterWithReplace(upui, gcpLength).toString() : upui;


      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          formattedUPUI.add(prefix + upui + suffix + rangeID);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        randomMinLength = Math.max(1, Math.min(28, randomMinLength));
        randomMaxLength = Math.max(1, Math.min(28, randomMaxLength));

        final List<String> randomSerialNumbers = RandomSerialNumberGenerator.getInstance(seed).randomGenerator(randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedUPUI.add(prefix + upui + suffix + randomID);
        }

      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        formattedUPUI.add(prefix + upui + suffix + serialNumber);
      }

      return formattedUPUI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of UPUI instance identifiers : " + ex.getMessage(), ex);
    }
  }
}
