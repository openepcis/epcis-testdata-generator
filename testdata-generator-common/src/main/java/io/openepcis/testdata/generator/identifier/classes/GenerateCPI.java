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
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("cpi")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateCPI extends GenerateQuantity {

  @Pattern(regexp = "^[A-Z0-9]{7,30}$", message = "GCN should be of min 7 and max 30 digit")
  @NotNull(message = "CPI must be between 7 and 30 digits")
  @Schema(type = SchemaType.STRING, description = "CPI between 7 and 30 digits", required = true)
  private String cpi;

  private static final String CPI_URN_PART = "urn:epc:idpat:cpi:";

  @Override
  public List<QuantityList> format(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final Float refQuantity,
      final String dlURL,
      final RandomSerialNumberGenerator randomSerialNumberGenerator) {
    return generateIdentifiers(syntax, count, refQuantity, dlURL);
  }

  // Method to generate CPI Class identifiers in URN/WebURI format based on information
  private List<QuantityList> generateIdentifiers(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final Float refQuantity,
      final String dlURL) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();
      final var quantityFormatted = new QuantityList();
      final var modifiedUrnCPI =
          syntax.equals(IdentifierVocabularyType.URN)
              ? cpi.substring(0, gcpLength) + "." + cpi.substring(gcpLength)
              : "";

      if (count != null && count > 0) {
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          // Set EPC class based on syntax
          if (syntax.equals(IdentifierVocabularyType.URN)) {
            quantityFormatted.setEpcClass(CPI_URN_PART + modifiedUrnCPI + ".*");
          } else if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
            quantityFormatted.setEpcClass(dlURL + "/8010/" + cpi);
          }

          // Add the quantity and UOM information if provided accordingly to the Quantity
          quantityFormatted.setQuantity(
              refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
          quantityFormatted.setUom(
              quantityType != null && quantityType.equals("Variable Measure Quantity")
                  ? uom
                  : null);
          returnQuantityFormatted.add(quantityFormatted);
        }
      }

      return returnQuantityFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of CPI class identifiers : " + ex.getMessage(), ex);
    }
  }
}
