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
package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.annotations.ConditionalValidation;
import io.openepcis.testdata.generator.constants.ReadPointBizLocationType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@Getter
@ConditionalValidation.List({
  @ConditionalValidation(
      fieldName = "manualURI",
      dependFieldName = {"gln"},
      message = "GLN cannot be Null for Read point/Business Location")
})
@RegisterForReflection
public class ReadPointBizLocationSyntax implements Serializable {
  @Schema(type = SchemaType.STRING, description = "Type of the readPoint SGLN/Manually")
  private ReadPointBizLocationType type;

  @Pattern(
      regexp = "^(\\s*|\\d{13})$",
      message = "GLN must be of 13 digit for ReadPoint/BizLocation")
  @Schema(type = SchemaType.STRING, description = "GLN consisting of 13 digits")
  private String gln;

  @Schema(type = SchemaType.NUMBER, description = "GCP length between 6-12 digits")
  private int gcpLength;

  @Schema(type = SchemaType.STRING, description = "Manual values for ReadPoint/BizLocation ")
  private String manualURI;

  @Schema(
      type = SchemaType.STRING,
      description = "Static extension value for ReadPoint/BizLocation")
  private String extension;
}
