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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
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
@JsonTypeName("bic")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateBIC implements EPCStrategy {

  @Pattern(regexp = "^[A-Z]{3}U\\d{7}$", message = "BIC should be 11 characters with first 3 alphabet character, 4th character U and followed by numbers")
  @NotNull(message = "BIC value cannot be Null")
  @Schema(type = SchemaType.STRING, description = "Valid URI for the BIC identifier.", required = true)
  private String bic;

  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL) {
    return format(syntax, count, dlURL, null);
  }

  @Override
  public List<String> format(final IdentifierVocabularyType syntax, final Integer count, final String dlURL, final Long seed) {
    return generateBIC();
  }

  private List<String> generateBIC() {
    try {
      final List<String> formattedBIC = new ArrayList<>();
      formattedBIC.add("urn:epc:id:bic:" + bic);
      return formattedBIC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException("Exception occurred during generation of BIC instance identifiers : " + ex.getMessage(), ex);
    }
  }
}
