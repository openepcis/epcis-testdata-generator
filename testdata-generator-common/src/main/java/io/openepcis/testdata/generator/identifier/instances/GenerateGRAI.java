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
@JsonTypeName("grai")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGRAI extends GenerateEPC {

  @Pattern(regexp = "^(\\s*|\\d{13})$", message = "GRAI should be of 14 digits")
  @NotNull(message = "GRAI cannot be null, it should consist of 14 digits")
  @Schema(type = SchemaType.STRING, description = "GRAI consisting of 14 digits", required = true)
  private String grai;

  private static final String GRAI_URN_PART = "urn:epc:id:grai:";
  private static final String GRAI_URI_PART = "/8003/";

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

  //Function to check which type of instance identifiers need to be generated Range/Random/Static
  private List<String> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedGRAI = new ArrayList<>();
      final String modifiedGRAI = prepareModifiedGRAI(syntax);
      final String prefix = syntax.equals(IdentifierVocabularyType.URN) ? GRAI_URN_PART : dlURL + GRAI_URI_PART;
      final String delimiter = syntax.equals(IdentifierVocabularyType.URN) ? "." : "";

      if (SerialTypeChecker.isRangeType(this.serialType, count, this.rangeFrom)) {
        //For range generate sequential identifiers
        generateRangeIdentifiers(formattedGRAI, prefix, modifiedGRAI, delimiter, count);
      } else if (SerialTypeChecker.isRandomType(this.serialType, count)) {
        //For random generate random identifiers or based on seed
        generateRandomIdentifiers(formattedGRAI, prefix, modifiedGRAI, delimiter, serialNumberGenerator, count);
      } else if (SerialTypeChecker.isNoneType(this.serialType, count, this.serialNumber)) {
        //For none generate static identifier
        generateStaticIdentifier(formattedGRAI, prefix, modifiedGRAI, delimiter, this.serialNumber);
      }

      return formattedGRAI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of GRAI instance identifiers : " + ex.getMessage(), ex);
    }
  }

  //Function to generate the GRAI range identifiers and return the list of identifiers
  private void generateRangeIdentifiers(final List<String> formattedGRAI, final String prefix, final String modifiedGRAI, final String delimiter, final Integer count) {
    for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
      formattedGRAI.add(prefix + modifiedGRAI + delimiter + rangeID);
    }
    this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
  }

  //Function to generate the GRAI Random identifiers based on seed and return the list of identifiers
  private void generateRandomIdentifiers(final List<String> formattedGRAI, final String prefix, final String modifiedGRAI, final String delimiter, final RandomSerialNumberGenerator serialNumberGenerator, final Integer count) {
    randomMinLength = Math.max(1, Math.min(16, randomMinLength));
    randomMaxLength = Math.max(1, Math.min(16, randomMaxLength));
    final List<String> randomSerialNumbers = serialNumberGenerator.randomGenerator(randomType, randomMinLength, randomMaxLength, count);

    for (var randomID : randomSerialNumbers) {
      formattedGRAI.add(prefix + modifiedGRAI + delimiter + randomID);
    }
  }

  //Function to generate the GRAI static identifiers and return the list of identifiers
  private void generateStaticIdentifier(final List<String> formattedGRAI, final String prefix, final String modifiedGRAI, final String delimiter, final String serialNumber) {
    formattedGRAI.add(prefix + modifiedGRAI + delimiter + serialNumber);
  }


  //Function to format the GRAI based on URN or WebURI format
  private String prepareModifiedGRAI(final IdentifierVocabularyType syntax) {
    if (syntax.equals(IdentifierVocabularyType.URN)) {
      return CompanyPrefixFormatter.gcpFormatterNormal(grai, gcpLength).toString();
    } else {
      return this.grai.substring(0, 12) + UPCEANLogicImpl.calcChecksum(this.grai.substring(0, 12));
    }
  }
}
