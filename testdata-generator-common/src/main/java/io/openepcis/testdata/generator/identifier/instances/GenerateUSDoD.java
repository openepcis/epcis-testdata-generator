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
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("usdod")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateUSDoD implements EPCStrategy {

  @Pattern(
      regexp = "^(\\s*|\\d{5,6})$",
      message = "USDoD CAGE/DoDAAC should be Min 5 digits and Max 6 digits")
  @NotNull(
      message = "Cage value for USDoD cannot be null, it should be Min 5 digits and Max 6 digits")
  @Schema(
      type = SchemaType.STRING,
      description = "Cage value for USDoD should be Min 5 digits and Max 6 digits",
      required = true)
  private String usdodCage;

  @NotNull(message = "Serial Numbers type can not be null.")
  @Schema(
      type = SchemaType.STRING,
      description = "Type of serial identifier generation.",
      enumeration = {"range", "random", "none"},
      required = true)
  private String serialType;

  @Min(value = 0, message = "Range start value cannot be less than 0.")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Starting value for range based identifier generation.")
  protected Integer rangeFrom;

  @Min(value = 1, message = "Range end value cannot be less than 1.")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Ending value for range based identifier generation.")
  protected Integer rangeTo;

  @Min(value = 1, message = "Number of required serial numbers cannot be less than 1.")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Count value for random based identifier generation.")
  protected Integer randomCount;

  @Schema(type = SchemaType.NUMBER, description = "Min character length for random serial numbers.")
  protected Integer randomMinLength;

  @Schema(type = SchemaType.NUMBER, description = "Max character length for random serial numbers.")
  protected Integer randomMaxLength;

  private static final String USDOD_URN_PART = "urn:epc:id:usdod:";

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    return generateURN(count);
  }

  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedUSDoD = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          formattedUSDoD.add(USDOD_URN_PART + usdodCage + "." + rangeID);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        randomMinLength = randomMinLength < 1 || randomMinLength > 11 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 11 ? 11 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedUSDoD.add(USDOD_URN_PART + usdodCage + "." + randomID);
        }
      }
      return formattedUSDoD;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of USDoD instance identifiers in URN format, Please check the values provided for USDoD instance identifiers : "
              + ex.getMessage());
    }
  }
}
