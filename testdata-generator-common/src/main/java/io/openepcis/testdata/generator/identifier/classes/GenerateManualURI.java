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
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;
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
      final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL) {
    return generateManualURI(refQuantity, count, dlURL);
  }

  private List<QuantityList> generateManualURI(final Float refQuantity, final Integer count, final String dlURL) {
    try {
      final List<QuantityList> formattedURI = new ArrayList<>();

      // If the requested manualURI is of Static type then just add the BaseManualURI
      if (manualUriType.equalsIgnoreCase("static")) {
        final var quantityFormatted = new QuantityList();
        quantityFormatted.setEpcClass(baseManualUri);
        quantityFormatted.setQuantity(
            refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
        quantityFormatted.setUom(uom);
        formattedURI.add(quantityFormatted);
      } else if (manualUriType.equalsIgnoreCase("dynamic")
          && manualUriRangeFrom != null
          && count != null
          && count > 0
          && manualUriRangeFrom >= 0) {
        // If the requested manualURI is of Dynamic type then add the BaseManualURI with serial
        // number
        for (var rangeID = manualUriRangeFrom; rangeID < manualUriRangeFrom + count; rangeID++) {
          final var quantityFormatted = new QuantityList();
          quantityFormatted.setEpcClass(baseManualUri + rangeID);
          quantityFormatted.setQuantity(
              refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
          quantityFormatted.setUom(uom);
          formattedURI.add(quantityFormatted);
        }
        this.manualUriRangeFrom = this.manualUriRangeFrom + count;
      }
      return formattedURI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of Manual class identifiers, Please check the values provided for Manual class identifiers : "
              + ex.getMessage(), ex);
    }
  }
}
