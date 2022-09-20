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

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@ToString
@RegisterForReflection
public class ReferencedIdentifier implements Serializable {
  @Schema(
      type = SchemaType.NUMBER,
      description = "Event Node id of the parent event if identifiers need to be inherited")
  private int parentNodeId;

  @Schema(type = SchemaType.NUMBER, description = "Identifier Node id associated with event")
  private int identifierId;

  @Schema(
      type = SchemaType.NUMBER,
      description = "Number of instance identifiers associated with event node")
  private int epcCount;

  @Schema(
      type = SchemaType.NUMBER,
      description = "Number of class identifiers associated with event node")
  private int classCount;

  @Schema(
      type = SchemaType.NUMBER,
      description = "Number of parent identifiers associated with event node")
  private int parentCount;

  @Schema(
      type = SchemaType.NUMBER,
      description = "Quantity of the class identifiers associated with event node")
  private Float quantity;

  @Schema(
      type = SchemaType.NUMBER,
      description = "Number of parent identifiers inherited from parent node to child node")
  private int inheritParentCount;
}
