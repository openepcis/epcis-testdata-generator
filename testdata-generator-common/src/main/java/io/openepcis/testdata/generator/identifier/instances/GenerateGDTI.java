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
  public final List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    return generateIdentifiers(syntax, count, dlURL, seed);
  }

  private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    try {
      final List<String> formattedGDTI = new ArrayList<>();
      final String prefix = syntax.equals(IdentifierVocabularyType.URN) ? GDTI_URN_PART : dlURL + GDTI_URI_PART;
      final String modifiedGDTI = prepareModifiedGDTI(syntax);
      final String delimiter = syntax.equals(IdentifierVocabularyType.URN) ? "." : "";

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          formattedGDTI.add(prefix + modifiedGDTI + delimiter + rangeID);
        }
        rangeFrom = BigInteger.valueOf(rangeFrom.longValue() + count);
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        randomMinLength = Math.max(1, Math.min(17, randomMinLength));
        randomMaxLength = Math.max(1, Math.min(17, randomMaxLength));
        final List<String> randomSerialNumbers = RandomSerialNumberGenerator.getInstance(seed).randomGenerator(randomType, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedGDTI.add(prefix + modifiedGDTI + delimiter + randomID);
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        formattedGDTI.add(prefix + modifiedGDTI + delimiter + serialNumber);
      }

      return formattedGDTI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of GDTI instance identifiers in WebURI format : " + ex.getMessage(), ex);
    }
  }


  //Function to format the GDTI based on URN or WebURI format
  private String prepareModifiedGDTI(final IdentifierVocabularyType syntax) {
    if (syntax.equals(IdentifierVocabularyType.URN)) {
      return CompanyPrefixFormatter.gcpFormatterNormal(gdti, gcpLength).toString();
    } else {
      return gdti.substring(0, 11) + UPCEANLogicImpl.calcChecksum(gdti.substring(0, 11)) + gdti.charAt(12);
    }
  }
}
