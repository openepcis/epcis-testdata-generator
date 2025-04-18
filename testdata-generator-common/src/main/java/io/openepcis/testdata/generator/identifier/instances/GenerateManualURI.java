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
package io.openepcis.testdata.generator.identifier.instances;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.identifier.util.NumericUtils;
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
public class GenerateManualURI implements EPCStrategy {

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
  private Integer manualUriRangeTo;

  @Override
  public List<String> format(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final String dlURL,
      final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateURI(count);
  }

  private List<String> generateURI(final Integer count) {
    try {
      final List<String> formattedURI = new ArrayList<>();

      if (manualUriType.equalsIgnoreCase("static")) {
        // If Static type then just add the BaseManualURI
        formattedURI.add(baseManualUri);
      } else if (manualUriType.equalsIgnoreCase("dynamic")
          && NumericUtils.isPositive(count)
          && NumericUtils.isPositive(manualUriRangeFrom)) {
        // If Dynamic type then add the BaseManualURI with serial number
        for (var rangeID = manualUriRangeFrom; rangeID < manualUriRangeFrom + count; rangeID++) {
          formattedURI.add(baseManualUri + rangeID);
        }
        this.manualUriRangeFrom = this.manualUriRangeFrom + count;
      }

      return formattedURI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during addition of Manual instance identifiers : " + ex.getMessage(),
          ex);
    }
  }
}
