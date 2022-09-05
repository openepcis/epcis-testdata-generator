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
package io.openepcis.testdata.generator.identifier.classes;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.testdata.generator.constants.DomainName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@Setter
@JsonTypeName("gtin")
@ToString(callSuper = true)
public class GenerateGTIN extends GenerateQuantity {

  @Pattern(regexp = "^(\\s*|\\d{14})$", message = "GTIN should be of 14 digit")
  @NotNull(message = "GTIN cannot be null, it should consist of 14 digits")
  @Schema(type = SchemaType.STRING, description = "GTIN consisting of 14 digits", required = true)
  private String gtin;

  private static final String GTIN_URN_PART = "urn:epc:idpat:sgtin:";
  private static final String GTIN_URI_PART = "/01/";

  @Override
  public List<QuantityList> format(
      final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity) {
    return generateGtinIdentifiers(syntax, count, refQuantity);
  }

  // Method to generate GTIN Class identifiers in URN/WebURI format based on information provided by
  // the users.
  private List<QuantityList> generateGtinIdentifiers(
      final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();
      final var quantityFormatted = new QuantityList();
      final var modifiedUrnGRAI =
          syntax.equals(IdentifierVocabularyType.URN)
              ? CompanyPrefixFormatter.gcpFormatterWithReplace(gtin, gcpLength).toString()
              : "";
      final var modifiedUriGRAI =
          syntax.equals(IdentifierVocabularyType.WEBURI)
              ? gtin.substring(0, 13) + UPCEANLogicImpl.calcChecksum(gtin.substring(0, 13))
              : "";

      // Loop until the count and create the Class identifiers based on it
      if (count != null && count > 0) {
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          // For URN syntax create the identifiers based on the URN type
          if (syntax.equals(IdentifierVocabularyType.URN)) {
            quantityFormatted.setEpcClass(GTIN_URN_PART + modifiedUrnGRAI + ".*");
          } else if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
            // For WebURI syntax create the identifiers based on the WebURI type
            quantityFormatted.setEpcClass(
                DomainName.IDENTIFIER_DOMAIN + GTIN_URI_PART + modifiedUriGRAI);
          }
          quantityFormatted.setQuantity(
              refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
          quantityFormatted.setUom(uom);
          returnQuantityFormatted.add(quantityFormatted);
        }
      }
      return returnQuantityFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GTIN class identifiers, Please check the values provided for GTIN class identifiers : "
              + ex.getMessage());
    }
  }
}
