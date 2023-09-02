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
import io.openepcis.testdata.generator.constants.DomainName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@Setter
@JsonTypeName("lgtin")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateLGTIN extends GenerateQuantity {

  @Pattern(regexp = "^(\\s*|\\d{14})$", message = "LGTIN should be of 14 digit")
  @NotNull(message = "LGTIN cannot be null, It should consist of 14 digits")
  @Schema(type = SchemaType.STRING, description = "LGTIN consisting of 14 digits", required = true)
  private String lgtin;

  @Schema(
      type = SchemaType.STRING,
      description = "Type of serial identifier generation",
      enumeration = {"range", "random", "none"},
      required = true)
  private String serialType;

  @Schema(
      type = SchemaType.NUMBER,
      description = "Serial number for none based identifier generation")
  private BigInteger serialNumber;

  @Min(value = 0, message = "Range start value cannot be less than 0")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Starting value for range based identifier generation")
  private BigInteger rangeFrom;

  private static final String LGTIN_URN_PART = "urn:epc:class:lgtin:";
  private static final String LGTIN_URI_PART = "/01/";
  private static final String LGTIN_SERIAL_PART = "/10/";

  @Override
  public List<QuantityList> format(
      final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity) {
    if (IdentifierVocabularyType.WEBURI == syntax) {
      // For WebURI syntax call the generateWebURI, pass the required identifiers count to create
      // Class Identifiers
      return generateWebURI(count, refQuantity);
    } else {
      // For URN syntax call the generateURN, pass the required identifiers count to create Class
      // Identifiers
      return generateURN(count, refQuantity);
    }
  }

  public List<QuantityList> generateURN(Integer count, final Float refQuantity) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();

      var formattedLgtin =
          this.lgtin.substring(0, 13) + UPCEANLogicImpl.calcChecksum(this.lgtin.substring(0, 13));
      formattedLgtin =
          CompanyPrefixFormatter.gcpFormatterWithReplace(formattedLgtin, gcpLength).toString();
      quantity = refQuantity != null && refQuantity != 0 ? refQuantity : quantity;

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.doubleValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          // Call the method to create and add the Quantity information for every range option
          returnQuantityFormatted.add(
              quantityUrnCreator(
                  IdentifierVocabularyType.URN,
                  formattedLgtin,
                  Long.valueOf(rangeID).toString(),
                  quantity,
                  uom));
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(RandomizationType.NUMERIC, 1, 20, count);

        for (var randomId : randomSerialNumbers) {
          // Call the method to create and add the Quantity information for every random option
          returnQuantityFormatted.add(
              quantityUrnCreator(
                  IdentifierVocabularyType.URN, formattedLgtin, randomId, quantity, uom));
        }
      } else if (serialType.equalsIgnoreCase("none")
          && serialNumber != null
          && count != null
          && count > 0) {
        // Return the single SGTIN values for None selection
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          // Call the method to create and add the Quantity information for every serial option
          returnQuantityFormatted.add(
              quantityUrnCreator(
                  IdentifierVocabularyType.URN,
                  formattedLgtin,
                  serialNumber.toString(),
                  quantity,
                  uom));
        }
      }
      return returnQuantityFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of LGTIN class identifiers in URN format, Please check the values provided for LGTIN class identifiers : "
              + ex.getMessage());
    }
  }

  public List<QuantityList> generateWebURI(Integer count, final Float refQuantity) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();
      final String formattedLgtin =
          this.lgtin.substring(0, 13) + UPCEANLogicImpl.calcChecksum(this.lgtin.substring(0, 13));
      final String lgtinWebURI =
          DomainName.IDENTIFIER_DOMAIN + LGTIN_URI_PART + formattedLgtin + LGTIN_SERIAL_PART;
      quantity = refQuantity != null && refQuantity != 0 ? refQuantity : quantity;

      // Return the list of SGTIN for RANGE calculation
      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom.doubleValue() >= 0) {
        for (var rangeID = rangeFrom.longValue();
            rangeID < rangeFrom.longValue() + count;
            rangeID++) {
          // Call the method to create and add the Quantity information for every range option
          returnQuantityFormatted.add(
              quantityUrnCreator(
                  IdentifierVocabularyType.WEBURI,
                  lgtinWebURI,
                  Long.valueOf(rangeID).toString(),
                  quantity,
                  uom));
        }
        this.rangeFrom = BigInteger.valueOf(this.rangeFrom.longValue() + count);
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        // Return the list of SGTIN for RANDOM calculation
        List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(RandomizationType.NUMERIC, 1, 20, count);

        for (var randomId : randomSerialNumbers) {
          // Call the method to create and add the Quantity information for every random option
          returnQuantityFormatted.add(
              quantityUrnCreator(
                  IdentifierVocabularyType.WEBURI, lgtinWebURI, randomId, quantity, uom));
        }
      } else if (serialType.equalsIgnoreCase("none")
          && serialNumber != null
          && count != null
          && count > 0) {
        // Return the single SGTIN values for None selection
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          // Call the method to create and add the Quantity information for every Serial number
          // option
          returnQuantityFormatted.add(
              quantityUrnCreator(
                  IdentifierVocabularyType.WEBURI,
                  lgtinWebURI,
                  serialNumber.toString(),
                  quantity,
                  uom));
        }
      }
      return returnQuantityFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of LGTIN class identifiers in WebURI format, Please check the values provided for LGTIN class identifiers : "
              + ex.getMessage());
    }
  }

  // Method to create and generate the Quantity object in URN format
  private QuantityList quantityUrnCreator(
      final IdentifierVocabularyType syntax,
      final String formattedLgtin,
      final String serialId,
      final Float quantity,
      final String uom) {
    var quantityFormatted = new QuantityList();
    // Based on type specified format the LGTIN accordingly
    quantityFormatted.setEpcClass(
        syntax.equals(IdentifierVocabularyType.URN)
            ? LGTIN_URN_PART + formattedLgtin + "." + serialId
            : formattedLgtin + serialId);
    quantityFormatted.setQuantity(quantity);
    quantityFormatted.setUom(uom);
    return quantityFormatted;
  }
}
