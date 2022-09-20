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

import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.identifier.classes.*;
import io.openepcis.testdata.generator.identifier.classes.GenerateCPI;
import io.openepcis.testdata.generator.identifier.classes.GenerateGCN;
import io.openepcis.testdata.generator.identifier.classes.GenerateGDTI;
import io.openepcis.testdata.generator.identifier.classes.GenerateGRAI;
import io.openepcis.testdata.generator.identifier.classes.GenerateITIP;
import io.openepcis.testdata.generator.identifier.classes.GenerateManualURI;
import io.openepcis.testdata.generator.identifier.classes.GenerateUPUI;
import io.openepcis.testdata.generator.identifier.instances.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@RegisterForReflection
public class Identifier {
  @NotNull(message = "Identifiers ID Cannot be Null")
  @Min(value = 1, message = "Identifier ID cannot be less than 1")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Unique identifier assigned to each identifier node.",
      required = true)
  private @Valid int identifierId;

  @NotNull(message = "Identifier Syntax cannot be Null, should be URN/WebURI")
  @Schema(description = "Identifiers generation format.", required = true)
  private @Valid IdentifierVocabularyType objectIdentifierSyntax = IdentifierVocabularyType.URN;

  @Schema(
      description = "Instance identifiers information required for identifier generation.",
      oneOf = {
        GenerateADI.class,
        GenerateBIC.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateCPI.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateGCN.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateGDTI.class,
        GenerateGIAI.class,
        GenerateGID.class,
        GenerateGINC.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateGRAI.class,
        GenerateGSIN.class,
        GenerateGSRN.class,
        GenerateGSRNP.class,
        GenerateIMOVN.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateITIP.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateManualURI.class,
        GenerateSGTIN.class,
        GenerateSSCC.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateUPUI.class,
        GenerateUSDoD.class
      })
  private @Valid EPCStrategy instanceData;

  @Schema(
      type = SchemaType.OBJECT,
      description = "Class/Quantity identifiers information required for identifier generation.",
      oneOf = {
        GenerateCPI.class,
        GenerateGCN.class,
        GenerateGDTI.class,
        GenerateGRAI.class,
        GenerateGTIN.class,
        GenerateITIP.class,
        GenerateLGTIN.class,
        GenerateManualURI.class,
        GenerateUPUI.class
      })
  private @Valid QuantityStatergy classData;

  @Schema(
      type = SchemaType.OBJECT,
      description = "Parent identifiers information required for ParentId generation.",
      oneOf = {
        GenerateADI.class,
        GenerateBIC.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateCPI.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateGCN.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateGDTI.class,
        GenerateGIAI.class,
        GenerateGID.class,
        GenerateGINC.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateGRAI.class,
        GenerateGSIN.class,
        GenerateGSRN.class,
        GenerateGSRNP.class,
        GenerateIMOVN.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateITIP.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateManualURI.class,
        GenerateSGTIN.class,
        GenerateSSCC.class,
        io.openepcis.testdata.generator.identifier.instances.GenerateUPUI.class,
        GenerateUSDoD.class
      })
  private @Valid EPCStrategy parentData;
}
