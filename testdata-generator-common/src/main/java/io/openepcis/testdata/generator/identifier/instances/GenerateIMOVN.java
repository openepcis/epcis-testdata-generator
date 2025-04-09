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
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("imovn")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateIMOVN implements EPCStrategy {

  @Pattern(regexp = "^(\\s*|\\d{7})$", message = "IMOVN should be of 7 digit")
  @NotNull(message = "IMOVN cannot be null, it should consist of 7 digits")
  @Schema(type = SchemaType.STRING, description = "IMOVN consisting of 7 digits", required = true)
  private String imovn;

  @Max(value = 1000, message = "Number of required serial numbers cannot be less than 1")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Count value for identifier generation.",
      required = true)
  private Integer imovnRandomCount;

  private static final String IMOVN_URN_PART = "urn:epc:id:imovn:";

  @Override
  public List<String> format(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final String dlURL,
      final RandomSerialNumberGenerator serialNumberGenerator) {
    return generateIMOVN(count, serialNumberGenerator);
  }

  private List<String> generateIMOVN(
      final Integer count, final RandomSerialNumberGenerator serialNumberGenerator) {
    try {
      final List<String> formattedIMOVN = new ArrayList<>();

      if (count != null && count > 0) {
        final List<String> randomSerialNumbers =
            serialNumberGenerator.randomGenerator(RandomizationType.NUMERIC, 7, 7, count);
        for (var rangeID : randomSerialNumbers) {
          formattedIMOVN.add(IMOVN_URN_PART + rangeID);
        }
      } else if (imovn != null) {
        formattedIMOVN.add(IMOVN_URN_PART + imovn);
      }
      return formattedIMOVN;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of IMOVN instance identifiers : " + ex.getMessage(),
          ex);
    }
  }
}
