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
package io.openepcis.testdata.generator.template;

import io.openepcis.model.epcis.Action;
import io.openepcis.testdata.generator.format.SourceDestinationSyntax;
import io.openepcis.testdata.generator.format.UserExtensionSyntax;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
public class ObjectEventType extends EPCISEventType {
  // WHY Dimension Object Event Specific Element
  @Schema(
      type = SchemaType.ARRAY,
      description = "Source information associated with the ObjectEvent.")
  private List<@Valid SourceDestinationSyntax> sources;

  @Schema(
      type = SchemaType.ARRAY,
      description = "Destination information associated with the ObjectEvent.")
  private List<@Valid SourceDestinationSyntax> destinations;

  // OTHER Fields Object Event Specific Elements
  @NotNull(message = "Action value is required for ObjectEvent")
  @Schema(
      type = SchemaType.STRING,
      description = "Action associated with the ObjectEvent.",
      required = true)
  private Action action;

  @Schema(
      type = SchemaType.ARRAY,
      description = "ILMD extension information associated with the ObjectEvent")
  private List<@Valid UserExtensionSyntax> ilmd;
}
