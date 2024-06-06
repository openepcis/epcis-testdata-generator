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
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("gdti")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGDTI extends GenerateQuantity {

  @Pattern(regexp = "^(\\s*|\\d{13})$", message = "GDTI should be of 13 digit")
  @NotNull(message = "GDTI cannot be null, it should consist of 13 digits")
  @Schema(type = SchemaType.STRING, description = "GDTI consisting of 13 digits", required = true)
  private String gdti;

  private static final String GDTI_URN_PART = "urn:epc:idpat:gdti:";
  private static final String GDTI_URI_PART = "/253/";

  @Override
  public List<QuantityList> format(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL, final RandomSerialNumberGenerator randomSerialNumberGenerator) {
    return generateIdentifiers(syntax, count, refQuantity, dlURL);
  }

  // Method to generate GDTI Class identifiers in URN/WebURI format based on information
  private List<QuantityList> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();
      final var quantityFormatted = new QuantityList();
      final var modifiedUrnGDTI = syntax.equals(IdentifierVocabularyType.URN) ? CompanyPrefixFormatter.gcpFormatterNormal(gdti, gcpLength).toString() : "";
      final var modifiedUriGDTI = syntax.equals(IdentifierVocabularyType.WEBURI) ? gdti.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gdti.substring(0, 12)) : "";

      // Loop until the count and create the Class identifiers based on it
      if (count != null && count > 0) {
        //Based on syntax generate EPC class
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          if (syntax.equals(IdentifierVocabularyType.URN)) {
            quantityFormatted.setEpcClass(GDTI_URN_PART + modifiedUrnGDTI + ".*");
          } else if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
            quantityFormatted.setEpcClass(dlURL + GDTI_URI_PART + modifiedUriGDTI);
          }

          quantityFormatted.setQuantity(refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
          quantityFormatted.setUom(uom);
          returnQuantityFormatted.add(quantityFormatted);
        }
      }

      return returnQuantityFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of GDTI class identifiers : " + ex.getMessage(), ex);
    }
  }
}
