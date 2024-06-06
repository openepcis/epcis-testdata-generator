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
import io.openepcis.testdata.generator.constants.RandomizationType;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

  /**
   * Method to generate identifiers based on URN/WebURI format by manipulating the provided values.
   *
   * @param syntax                syntax in which identifiers need to be generated URN/WebURI
   * @param count                 count of instance identifiers need to be generated
   * @param dlURL                 if provided use the provided dlURI to format WebURI identifiers else use default ref.gs1.org
   * @param serialNumberGenerator instance of the RandomSerialNumberGenerator to generate random serial number
   * @return returns list of identifiers in string format
   */
  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateIdentifiers(syntax, count, dlURL, serialNumberGenerator);
  }

  private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedCPI = new ArrayList<>();
      final String prefix = syntax == IdentifierVocabularyType.URN ? CPI_URN_PART : dlURL + CPI_URI_PART;
      final String suffix = syntax == IdentifierVocabularyType.URN ? "." : SERIAL_URI_PART;
      final String modifiedCPI = syntax == IdentifierVocabularyType.URN ? CompanyPrefixFormatter.gcpFormatterWithReplace(cpi, gcpLength).toString() : cpi;

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count.longValue(); rangeID++) {
          formattedCPI.add(prefix + modifiedCPI + suffix + rangeID);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count.longValue());
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        randomMinLength = Math.max(1, Math.min(12, randomMinLength));
        randomMaxLength = Math.max(1, Math.min(12, randomMaxLength));

        final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, randomMinLength, randomMaxLength, count);

        for (String randomID : randomSerialNumbers) {
          formattedCPI.add(prefix + modifiedCPI + suffix + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        formattedCPI.add(prefix + modifiedCPI + suffix + serialNumber);
      }
      return formattedCPI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of CPI instance identifiers : " + ex.getMessage(), ex);
    }
  }
}
