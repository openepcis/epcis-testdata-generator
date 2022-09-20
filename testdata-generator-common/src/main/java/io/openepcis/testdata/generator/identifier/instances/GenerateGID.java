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
import io.openepcis.testdata.generator.constants.RandomizationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.RandomValueGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@JsonTypeName("gid")
@ToString(callSuper = true)
@RegisterForReflection
public class GenerateGID implements EPCStrategy {

  @Pattern(regexp = "[0-9]{1,9}", message = "GID Manager should be of length 1-9")
  @NotNull(message = "GID manager cannot be Null.")
  @Schema(type = SchemaType.STRING, description = "GID manager with length 1-9.", required = true)
  private String manager;

  @Pattern(regexp = "[0-9]{1,8}", message = "GID Class should be of length 1-8.")
  @NotNull(message = "GID class cannot be Null.")
  @Schema(type = SchemaType.STRING, description = "GID class with length 1-8.", required = true)
  private String gidClass;

  @NotNull(message = "Serial Numbers type can not be null")
  @Schema(
      type = SchemaType.STRING,
      description = "Type of serial identifier generation.",
      enumeration = {"range", "random", "none"},
      required = true)
  private String serialType;

  @Min(value = 0, message = "Range start value cannot be less than 0")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Starting value for range based identifier generation.")
  private Integer rangeFrom;

  @Min(value = 1, message = "Range end value cannot be less than 1")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Ending value for range based identifier generation.")
  private Integer rangeTo;

  @Min(value = 1, message = "Number of required serial numbers cannot be less than 1")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Count value for random based identifier generation.")
  private Integer randomCount;

  @Schema(type = SchemaType.NUMBER, description = "Min character length for random serial numbers.")
  protected Integer randomMinLength;

  @Schema(type = SchemaType.NUMBER, description = "Max character length for random serial numbers.")
  protected Integer randomMaxLength;

  @Schema(
      type = SchemaType.NUMBER,
      description = "Serial number for none based identifier generation.")
  protected Integer serialNumber;

  private static final String GID_URN_PART = "urn:epc:id:gid:";

  @Override
  public List<String> format(IdentifierVocabularyType syntax, Integer count) {
    if (manager != null && gidClass != null) {
      return generateURN(count);
    }
    return Collections.emptyList();
  }

  private List<String> generateURN(Integer count) {
    try {
      final List<String> formattedSSCC = new ArrayList<>();

      if (serialType.equalsIgnoreCase("range")
          && rangeFrom != null
          && count != null
          && count > 0
          && rangeFrom >= 0) {
        for (var rangeID = rangeFrom; rangeID < rangeFrom + count; rangeID++) {
          formattedSSCC.add(GID_URN_PART + manager + "." + gidClass + "." + rangeID);
        }
        this.rangeFrom = this.rangeFrom + count;
      } else if (serialType.equalsIgnoreCase("random") && count != null && count > 0) {
        randomMinLength = randomMinLength < 1 || randomMinLength > 11 ? 1 : randomMinLength;
        randomMaxLength = randomMaxLength < 1 || randomMaxLength > 11 ? 11 : randomMaxLength;

        final List<String> randomSerialNumbers =
            RandomValueGenerator.randomGenerator(
                RandomizationType.NUMERIC, randomMinLength, randomMaxLength, count);

        for (var randomID : randomSerialNumbers) {
          formattedSSCC.add(GID_URN_PART + manager + "." + gidClass + "." + randomID);
        }
      } else if (serialType.equalsIgnoreCase("none") && serialNumber != null) {
        formattedSSCC.add(GID_URN_PART + manager + "." + gidClass + "." + serialNumber);
      }

      return formattedSSCC;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during generation of GID instance identifiers in URN format, Please check the values provided for GID instance identifiers : "
              + ex.getMessage());
    }
  }
}
