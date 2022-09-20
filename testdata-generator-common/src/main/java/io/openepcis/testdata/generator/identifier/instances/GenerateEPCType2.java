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

import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.validation.constraints.Pattern;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@NoArgsConstructor
@ToString
@RegisterForReflection
public abstract class GenerateEPCType2 extends GenerateEPC implements EPCStrategy {
  @Pattern(regexp = "[0-9]{6,12}", message = "Invalid GCP, GCP should be b/w 6-12 digits")
  @Schema(type = SchemaType.STRING, description = "GCP consisting of 6-12 digits.", required = true)
  protected String gcp;
}
