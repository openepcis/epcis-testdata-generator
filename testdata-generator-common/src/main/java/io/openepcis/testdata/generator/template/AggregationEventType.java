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
package io.openepcis.testdata.generator.template;

import io.openepcis.model.epcis.Action;
import io.openepcis.testdata.generator.format.SourceDestinationSyntax;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@RegisterForReflection
public class AggregationEventType extends EPCISEventType {
  // WHY dimension Aggregation Event Specific Elements
  @Schema(
      type = SchemaType.ARRAY,
      description = "Source information associated with the AggregationEvent.")
  private List<@Valid SourceDestinationSyntax> sources;

  @Schema(
      type = SchemaType.ARRAY,
      description = "Destination information associated with the AggregationEvent.")
  private List<@Valid SourceDestinationSyntax> destinations;

  // OTHER Fields Aggregation Event Specific Elements
  @NotNull(message = "Action value is required for AggregationEvent")
  @Schema(
      type = SchemaType.STRING,
      description = "Action associated with the AggregationEvent.",
      required = true)
  private @Valid Action action;
}
