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
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigInteger;
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
      final List<String> formattedGSIN = new ArrayList<>();
      final String prefix =
          syntax == IdentifierVocabularyType.URN ? GSIN_URN_PART : dlURL + GSIN_URI_PART;
      final String suffix = syntax == IdentifierVocabularyType.URN ? "." : "";
      final int paddingLength = syntax == IdentifierVocabularyType.WEBURI ? 17 : 16;

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        // For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count.longValue();
            rangeID++) {
          appendFormattedGSIN(formattedGSIN, prefix, suffix, rangeID, paddingLength);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count.longValue());
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        // For random generate random identifiers or based on seed
        final List<String> randomSerialNumbers =
            serialNumberGenerator.randomGenerator(
                RandomizationType.NUMERIC, 1, 4, count.intValue());

        for (var randomID : randomSerialNumbers) {
          appendFormattedGSIN(formattedGSIN, prefix, suffix, randomID, paddingLength);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        // For none generate static identifier
        appendFormattedGSIN(formattedGSIN, prefix, suffix, serialNumber, paddingLength);
      }

      return formattedGSIN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GSIN instance identifiers : " + ex.getMessage(),
          ex);
    }
  }

  // Method to append additional characters to match required format
  private void appendFormattedGSIN(
      final List<String> formattedGSIN,
      final String prefix,
      final String suffix,
      final Object serial,
      final int paddingLength) {
    var append = gcp + serial.toString();
    append = StringUtils.repeat("0", paddingLength - append.length()) + serial;
    formattedGSIN.add(prefix + gcp + suffix + append);
  }
}
