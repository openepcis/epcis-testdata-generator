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
package io.openepcis.testdata.generator.identifier.classes;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Setter
@JsonTypeName("itip")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateITIP extends GenerateQuantity {

  @Pattern(regexp = "^(\\s*|\\d{18})$", message = "ITIP should be of 18 digit")
  @NotNull(message = "ITIP value cannot be Null, should consist of 18 digits")
  @Schema(type = SchemaType.STRING, description = "ITIP consisting of 18 digits", required = true)
  private String itip;

  private static final String ITIP_URN_PART = "urn:epc:idpat:itip:";
  private static final String ITIP_URI_PART = "/8006/";

  @Override
  public List<QuantityList> format(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL, final RandomSerialNumberGenerator randomSerialNumberGenerator) {
    return generateIdentifiers(syntax, count, refQuantity, dlURL);
  }

  // Method to generate ITIP Class identifiers in URN/WebURI format based on information
  private List<QuantityList> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();
      final var quantityFormatted = new QuantityList();
      var modifiedUrnITIP = "";

      if (syntax.equals(IdentifierVocabularyType.URN)) {
        modifiedUrnITIP = itip.substring(0, 18);
        modifiedUrnITIP = modifiedUrnITIP.substring(1, gcpLength + 1) + "." + modifiedUrnITIP.charAt(0) + modifiedUrnITIP.substring(gcpLength + 1);
        modifiedUrnITIP = modifiedUrnITIP.substring(0, 14) + "." + modifiedUrnITIP.substring(15, 17) + "." + modifiedUrnITIP.substring(17);
      }

      var modifiedUriITIP = syntax.equals(IdentifierVocabularyType.WEBURI) ? itip.substring(0, 18) : "";

      // Loop until the count and create the Class identifiers based on it
      if (count != null && count > 0) {
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          // Based on syntax generate EPC class
          if (syntax.equals(IdentifierVocabularyType.URN)) {
            quantityFormatted.setEpcClass(ITIP_URN_PART + modifiedUrnITIP + ".*");
          } else if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
            quantityFormatted.setEpcClass(dlURL + ITIP_URI_PART + modifiedUriITIP);
          }

          quantityFormatted.setQuantity(refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
          quantityFormatted.setUom(quantityType != null && quantityType.equals("Variable Measure Quantity") ? uom : null);
          returnQuantityFormatted.add(quantityFormatted);
        }
      }

      return returnQuantityFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of ITIP class identifiers : " + ex.getMessage(), ex);
    }
  }
}
