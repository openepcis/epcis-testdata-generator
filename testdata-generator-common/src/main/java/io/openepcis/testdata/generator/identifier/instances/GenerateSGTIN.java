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
@JsonTypeName("sgtin")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateSGTIN extends GenerateEPC {

  @Pattern(regexp = "^(\\s*|\\d{14})$", message = "SGTIN should be of 14 digits.")
  @NotNull(message = "SGTIN value cannot be Null. It should consist of 14 digits.")
  @Schema(type = SchemaType.STRING, description = "SGTIN value consisting of 14 digits.", required = true)
  private String sgtin;

  private static final String SGTIN_URN_PART = "urn:epc:id:sgtin:";
  private static final String SGTIN_URI_PART = "/01/";
  private static final String SGTIN_URI_SERIAL_PART = "/21/";

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
    try {
      final List<String> formattedSGTIN = new ArrayList<>();
      sgtin = this.sgtin.substring(0, 13) + UPCEANLogicImpl.calcChecksum(this.sgtin.substring(0, 13));
      final String identifierPrefix = syntax == IdentifierVocabularyType.WEBURI ? dlURL + SGTIN_URI_PART : SGTIN_URN_PART;
      final String identifierSuffix = syntax == IdentifierVocabularyType.WEBURI ? SGTIN_URI_SERIAL_PART : ".";
      final String modifiedSgtin = syntax == IdentifierVocabularyType.WEBURI ? sgtin : CompanyPrefixFormatter.gcpFormatterWithReplace(sgtin, gcpLength).toString();

      if (SerialTypeChecker.isRangeType(this.serialType, count, this.rangeFrom)) {
        //For range generate sequential identifiers
        generateSequentialIdentifiers(formattedSGTIN, identifierPrefix, modifiedSgtin, identifierSuffix, rangeFrom, count);
      } else if (SerialTypeChecker.isRandomType(this.serialType, count)) {
        //For random generate random identifiers or based on seed
        generateRandomIdentifiers(formattedSGTIN, identifierPrefix, modifiedSgtin, identifierSuffix, seed, count);
      } else if (SerialTypeChecker.isNoneType(this.serialType, count, this.serialNumber)) {
        //For none generate static identifier
        generateStaticIdentifiers(formattedSGTIN, identifierPrefix, modifiedSgtin, identifierSuffix, serialNumber, count);
      }

      return formattedSGTIN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of SGTIN instance identifiers : " + ex.getMessage(), ex);
    }


  }

  // Generate SEQUENTIAL/RANGE SGTIN identifiers in URN/WEBURI format based on from value and count
  private void generateSequentialIdentifiers(List<String> formattedSGTIN, String identifierPrefix, String sgtin, String identifierSuffix, BigInteger rangeFrom, Integer count) {
    for (long rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
      formattedSGTIN.add(identifierPrefix + sgtin + identifierSuffix + rangeID);
    } this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
  }

  // Generate RANDOM SGTIN identifiers in URN/WEBURI format based on count
  private void generateRandomIdentifiers(List<String> formattedSGTIN, String identifierPrefix, String sgtin, String identifierSuffix, Long seed, Integer count) {
    this.randomMinLength = Math.max(1, Math.min(20, this.randomMinLength)); this.randomMaxLength = Math.max(1, Math.min(20, this.randomMaxLength));

    final List<String> randomSerialNumbers = RandomSerialNumberGenerator.getInstance(seed).randomGenerator(this.randomType, this.randomMinLength, this.randomMaxLength, count);

    for (String randomID : randomSerialNumbers) {
      formattedSGTIN.add(identifierPrefix + sgtin + identifierSuffix + randomID);
    }
  }

  // Generate STATIC SGTIN identifiers in URN/WEB URI format based on count
  private void generateStaticIdentifiers(List<String> formattedSGTIN, String identifierPrefix, String sgtin, String identifierSuffix, String serialNumber, Integer count) {
    for (int noneCounter = 0; noneCounter < count; noneCounter++) {
      formattedSGTIN.add(identifierPrefix + sgtin + identifierSuffix + serialNumber);
    }
  }
}
