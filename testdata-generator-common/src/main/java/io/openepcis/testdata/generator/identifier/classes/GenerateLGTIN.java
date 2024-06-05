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
package io.openepcis.testdata.generator.identifier.classes;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.identifier.util.SerialTypeChecker;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("lgtin")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateLGTIN extends GenerateQuantity {

  @Pattern(regexp = "^(\\s*|\\d{14})$", message = "LGTIN should be of 14 digit")
  @NotNull(message = "LGTIN cannot be null, It should consist of 14 digits")
  @Schema(type = SchemaType.STRING, description = "LGTIN consisting of 14 digits", required = true)
  private String lgtin;

  @Schema(type = SchemaType.STRING, description = "Type of serial identifier generation", enumeration = {"range", "random", "none"}, required = true)
  private String serialType;

  @Schema(type = SchemaType.STRING, description = "Serial number for none based identifier generation")
  private String serialNumber;

  @Min(value = 0, message = "Range start value cannot be less than 0")
  @Schema(type = SchemaType.NUMBER, description = "Starting value for range based identifier generation")
  private BigInteger rangeFrom;

  private static final String LGTIN_URN_PART = "urn:epc:class:lgtin:";
  private static final String LGTIN_URI_PART = "/01/";
  private static final String LGTIN_SERIAL_PART = "/10/";

  @Override
  public List<QuantityList> format(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL, final Long seed) {
    return generateIdentifiers(syntax, count, refQuantity, dlURL, seed);
  }

  private List<QuantityList> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL, final Long seed) {
    try {
      final List<QuantityList> formattedLGTIN = new ArrayList<>();
      lgtin = lgtin.substring(0, 13) + UPCEANLogicImpl.calcChecksum(lgtin.substring(0, 13));
      final String prefix = syntax == IdentifierVocabularyType.URN ? LGTIN_URN_PART : dlURL + LGTIN_URI_PART;
      final String suffix = syntax == IdentifierVocabularyType.URN ? "." : LGTIN_SERIAL_PART;
      final String modifiedSgtin = syntax == IdentifierVocabularyType.URN ? CompanyPrefixFormatter.gcpFormatterWithReplace(lgtin, gcpLength).toString() : lgtin;
      quantity = refQuantity != null && refQuantity != 0 ? refQuantity : quantity;

      if (SerialTypeChecker.isRangeType(serialType, count, rangeFrom)) {
        //For range generate sequential identifiers
        for (var rangeID = rangeFrom.longValue(); rangeID < rangeFrom.longValue() + count; rangeID++) {
          formattedLGTIN.add(quantityUrnCreator(prefix + modifiedSgtin + suffix + rangeID));
        }
      } else if (SerialTypeChecker.isRandomType(serialType, count)) {
        //For random generate random identifiers or based on seed
        final List<String> randomSerialNumbers = RandomSerialNumberGenerator.getInstance(seed).randomGenerator(RandomizationType.NUMERIC, 1, 20, count);

        for (var randomId : randomSerialNumbers) {
          formattedLGTIN.add(quantityUrnCreator(prefix + modifiedSgtin + suffix + randomId));
        }
      } else if (SerialTypeChecker.isNoneType(serialType, count, serialNumber)) {
        //For none generate static identifier
        formattedLGTIN.add(quantityUrnCreator(prefix + modifiedSgtin + suffix + serialNumber));
      }

      return formattedLGTIN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of LGTIN class identifiers : " + ex.getMessage(), ex);
    }
  }

  // Method to create and generate the Quantity object in URN format
  private QuantityList quantityUrnCreator(final String epcClass) {
    var quantityFormatted = new QuantityList();

    quantityFormatted.setEpcClass(epcClass);
    if (!StringUtils.isBlank(quantityType)) {
      quantityFormatted.setQuantity(quantity);
      quantityFormatted.setUom(uom);
    }
    return quantityFormatted;
  }
}
