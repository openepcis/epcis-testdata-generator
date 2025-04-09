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

import io.openepcis.testdata.generator.constants.RandomizationType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@NoArgsConstructor
@ToString
@RegisterForReflection
public abstract class GenerateEPC implements EPCStrategy {
  @Min(value = 6, message = "Instance Identifiers GCP Length cannot be less than 6")
  @Max(value = 12, message = "Instance Identifiers GCP Length cannot be more than 12")
  @Schema(
      type = SchemaType.NUMBER,
      description = "GCP length between 6 and 12 digits",
      required = true)
  protected Integer gcpLength;

  @NotNull(message = "Serial Numbers type can not be null")
  @Schema(
      type = SchemaType.STRING,
      description = "Type of serial identifier generation.",
      enumeration = {"range", "random", "none"},
      required = true)
  protected String serialType;

  @Min(value = 0, message = "Range start value cannot be less than 0")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Starting value for range based identifier generation.")
  protected BigInteger rangeFrom;

  @Min(value = 1, message = "Range end value cannot be less than 1")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Ending value for range based identifier generation.")
  protected BigInteger rangeTo;

  @Min(value = 1, message = "Number of required serial numbers cannot be less than 1")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Count value for random based identifier generation.")
  protected Integer randomCount;

  @Schema(
      type = SchemaType.STRING,
      description = "Type of randomization required for the serial numbers")
  protected RandomizationType randomType = RandomizationType.NUMERIC;

  @Schema(
      type = SchemaType.STRING,
      description = "Serial number for none based identifier generation.")
  protected String serialNumber;

  @Schema(type = SchemaType.NUMBER, description = "Min character length for random serial numbers.")
  protected Integer randomMinLength;

  @Schema(type = SchemaType.NUMBER, description = "Max character length for random serial numbers.")
  protected Integer randomMaxLength;
}
