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
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@Setter
@JsonTypeName("grai")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGRAI extends GenerateQuantity {

  @Pattern(regexp = "^(\\s*|\\d{13})$", message = "GRAI should be of 13 digit")
  @NotNull(message = "GRAI cannot be null, it should consist of 13 digits")
  @Schema(type = SchemaType.STRING, description = "GRAI consisting of 13 digits", required = true)
  private String grai;

  private static final String GRAI_URN_PART = "urn:epc:idpat:grai:";
  private static final String GRAI_URI_PART = "/8003/";

  @Override
  public List<QuantityList> format(
      final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity) {
    return generateGraiIdentifiers(syntax, count, refQuantity);
  }

  // Method to generate GRAI Class identifiers in URN/WebURI format based on information provided by
  // the users.
  private List<QuantityList> generateGraiIdentifiers(
      final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();
      final var quantityFormatted = new QuantityList();
      final var modifiedUrnGRAI =
          syntax.equals(IdentifierVocabularyType.URN)
              ? CompanyPrefixFormatter.gcpFormatterNormal(grai, gcpLength).toString()
              : "";
      final var modifiedUriGRAI =
          syntax.equals(IdentifierVocabularyType.WEBURI)
              ? grai.substring(0, 12) + UPCEANLogicImpl.calcChecksum(grai.substring(0, 12))
              : "";

      // Loop until the count and create the Class identifiers based on it
      if (count != null && count > 0) {
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          // For URN syntax create the identifiers based on the URN type
          if (syntax.equals(IdentifierVocabularyType.URN)) {
            quantityFormatted.setEpcClass(GRAI_URN_PART + modifiedUrnGRAI + ".*");
          } else if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
            // For WebURI syntax create the identifiers based on the WebURI type
            quantityFormatted.setEpcClass(
                DomainName.IDENTIFIER_DOMAIN + GRAI_URI_PART + modifiedUriGRAI);
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
          "Exception occurred during generation of GRAI class identifiers, Please check the values provided for GRAI class identifiers : "
              + ex.getMessage(), ex);
    }
  }
}
