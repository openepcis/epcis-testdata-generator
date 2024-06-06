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
import io.openepcis.testdata.generator.constants.DomainName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.CompanyPrefixFormatter;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
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
@JsonTypeName("gcn")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGCN extends GenerateQuantity {

  @Pattern(regexp = "^(\\s*|\\d{13})$", message = "GCN should be of 13 digit")
  @NotNull(message = "GCN cannot be null, it should consist of 13 digits")
  @Schema(type = SchemaType.STRING, description = "GCN consisting of 13 digits", required = true)
  private String gcn;

  private static final String SGCN_URN_PART = "urn:epc:idpat:sgcn:";

  @Override
  public List<QuantityList> format(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL, final RandomSerialNumberGenerator randomSerialNumberGenerator) {
    return generateIdentifiers(syntax, count, refQuantity, dlURL);
  }

  // Method to generate GCN Class identifiers in URN/WebURI format based on information
  private List<QuantityList> generateIdentifiers(final IdentifierVocabularyType syntax, final Integer count, final Float refQuantity, final String dlURL) {
    try {
      final List<QuantityList> returnQuantityFormatted = new ArrayList<>();
      final var quantityFormatted = new QuantityList();

      final var modifiedUrnGCN = syntax.equals(IdentifierVocabularyType.URN) ? CompanyPrefixFormatter.gcpFormatterNormal(gcn, gcpLength).toString() : "";
      final var modifiedUriGCN = syntax.equals(IdentifierVocabularyType.WEBURI) ? gcn.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gcn.substring(0, 12)) : "";

      // Loop until the count and create the Class identifiers based on it
      if (count != null && count > 0) {
        for (var identifierCounter = 0; identifierCounter < count; identifierCounter++) {
          //Based on syntax generate identifier
          if (syntax.equals(IdentifierVocabularyType.URN)) {
            quantityFormatted.setEpcClass(SGCN_URN_PART + modifiedUrnGCN + ".*");
          } else if (syntax.equals(IdentifierVocabularyType.WEBURI)) {
            quantityFormatted.setEpcClass(dlURL + "/255/" + modifiedUriGCN);
          }

          quantityFormatted.setQuantity(refQuantity != null && refQuantity != 0 ? refQuantity : quantity);
          quantityFormatted.setUom(uom);
          returnQuantityFormatted.add(quantityFormatted);
        }
      }

      return returnQuantityFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of GCN class identifiers : " + ex.getMessage(), ex);
    }
  }
}
