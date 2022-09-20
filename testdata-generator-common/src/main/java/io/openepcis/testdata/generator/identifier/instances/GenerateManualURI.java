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
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
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
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    return generateURI(count);
  }

  private List<String> generateURI(Integer count) {
    try {
      final List<String> formattedURI = new ArrayList<>();

      // If the requested manualURI is of Static type then just add the BaseManualURI
      if (manualUriType.equalsIgnoreCase("static")) {
        formattedURI.add(baseManualUri);
      } else if (manualUriType.equalsIgnoreCase("dynamic")
          && manualUriRangeFrom != null
          && count != null
          && count > 0
          && manualUriRangeFrom >= 0) {
        // If the requested manualURI is of Dynamic type then add the BaseManualURI with serial
        // number
        for (var rangeID = manualUriRangeFrom; rangeID < manualUriRangeFrom + count; rangeID++) {
          formattedURI.add(baseManualUri + rangeID);
        }
        this.manualUriRangeFrom = this.manualUriRangeFrom + count;
      }
      return formattedURI;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during addition of Manual instance identifiers, Please check the values provided for Manual instance identifiers : "
              + ex.getMessage());
    }
  }
}
