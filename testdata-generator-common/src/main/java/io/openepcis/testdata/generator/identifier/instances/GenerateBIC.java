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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("bic")
@ToString(callSuper = true)
public class GenerateBIC implements EPCStrategy {

  @NotNull(message = "BIC value cannot be Null")
  @Schema(
      type = SchemaType.STRING,
      description = "Valid URI for the BIC identifier.",
      required = true)
  private String bic;

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    return generateBIC();
  }

  private List<String> generateBIC() {
    try {
      final List<String> formattedBIC = new ArrayList<>();
      formattedBIC.add(bic);
      return formattedBIC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of BIC instance identifiers, Please check the values provided for BIC instance identifiers : "
              + ex.getMessage());
    }
  }
}
