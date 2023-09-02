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
package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.annotations.ConditionalValidation;
import io.openepcis.testdata.generator.constants.SourceDestinationGLNType;
import io.openepcis.testdata.generator.constants.SourceDestinationType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConditionalValidation.List({
  @ConditionalValidation(
      fieldName = "type",
      fieldValue = {"OWNING_PARTY", "POSSESSING_PARTY"},
      dependFieldName = {"glnType", "gln"},
      message =
          "GLN type/GLN cannot be Null for Source/Destination if the type is OWNING_PARTY/PROCESSING_PARTY"),
  @ConditionalValidation(
      fieldName = "type",
      fieldValue = {"LOCATION"},
      dependFieldName = {"gln"},
      message = "GLN cannot be Null for Source/Destination if the type is LOCATION"),
  @ConditionalValidation(
      fieldName = "type",
      fieldValue = {"OTHER"},
      dependFieldName = {"manualType", "manualURI"},
      message = "ManualType/ManualURI cannot be Null for Source/Destination if the type is Other")
})
@RegisterForReflection
public class SourceDestinationSyntax implements Serializable {

  @NotNull(message = "Type cannot be Null for Source/Destination")
  private SourceDestinationType type;

  private SourceDestinationGLNType glnType;

  @Pattern(
      regexp = "^(\\s*|\\d{13})$",
      message = "GLN should be of 13 digits for Source/Destination")
  private String gln;

  @Pattern(
      regexp = "^[\\x21-\\x22\\x25-\\x2F\\x30-\\x39\\x3A-\\x3F\\x41-\\x5A\\x5F\\x61-\\x7A]{0,20}$",
      message =
          "SGLN extension should consist of valid characters between 1 and 20 for Source/Destination")
  private String extension;

  private int gcpLength;

  private String manualType;
  private String manualURI;
}
