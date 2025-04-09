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
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("manualURI")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateManualURI implements QuantityStatergy {

  @NotNull(message = "Manual URI cannot be Null")
  @Schema(
      type = SchemaType.STRING,
      description = "Valid URI for the Manual URI identifier.",
      required = true)
  private String baseManualUri;

  @NotNull(message = "Manual URI type cannot be Null")
  @Schema(
      type = SchemaType.STRING,
      enumeration = {"static", "dynamic"},
      description = "Valid URI for the Manual URI identifier.",
      required = true)
  private String manualUriType;

  private Integer manualUriRangeFrom;

  @Schema(type = SchemaType.NUMBER, description = "Quantity value for the identifier.")
  private Float quantity;

  @Schema(type = SchemaType.STRING, description = "Quantity measurement value for the identifier.")
  private String uom;

  @Override
  public List<QuantityList> format(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final Float refQuantity,
      final String dlURL,
      final RandomSerialNumberGenerator randomSerialNumberGenerator) {
    return generateIdentifier(refQuantity, count);
  }

  private List<QuantityList> generateIdentifier(final Float refQuantity, final Integer count) {
    try {
      final List<QuantityList> formattedURI = new ArrayList<>();

      // If the requested manualURI is of Static type then just add the BaseManualURI
      if (manualUriType.equalsIgnoreCase("static")) {
        addQuantityToList(formattedURI, baseManualUri, refQuantity, uom);
        return formattedURI;
      }

      // If the requested manualURI is of Dynamic type then add the BaseManualURI with serial number
      if (manualUriType.equalsIgnoreCase("dynamic")
          && manualUriRangeFrom != null
          && count != null
          && count > 0
          && manualUriRangeFrom >= 0) {
        for (var rangeID = manualUriRangeFrom; rangeID < manualUriRangeFrom + count; rangeID++) {
          addQuantityToList(formattedURI, baseManualUri + rangeID, refQuantity, uom);
        }
        manualUriRangeFrom = manualUriRangeFrom + count;
      }

      return formattedURI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of Manual class identifiers : " + ex.getMessage(),
          ex);
    }
  }

  private void addQuantityToList(
      final List<QuantityList> formattedURI,
      final String epcClass,
      final Float refQuantity,
      final String uom) {
    final var quantityFormatted = new QuantityList();
    quantityFormatted.setEpcClass(epcClass);
    quantityFormatted.setQuantity(refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
    quantityFormatted.setUom(uom);
    formattedURI.add(quantityFormatted);
  }
}
