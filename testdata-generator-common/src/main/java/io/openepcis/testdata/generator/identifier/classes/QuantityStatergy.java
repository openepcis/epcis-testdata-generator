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
package io.openepcis.testdata.generator.identifier.classes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({ //
  @JsonSubTypes.Type(value = GenerateLGTIN.class, name = "lgtin"), //
  @JsonSubTypes.Type(value = GenerateGTIN.class, name = "gtin"), //
  @JsonSubTypes.Type(value = GenerateGRAI.class, name = "grai"), //
  @JsonSubTypes.Type(value = GenerateGDTI.class, name = "gdti"), //
  @JsonSubTypes.Type(value = GenerateGCN.class, name = "gcn"), //
  @JsonSubTypes.Type(value = GenerateCPI.class, name = "cpi"), //
  @JsonSubTypes.Type(value = GenerateITIP.class, name = "itip"), //
  @JsonSubTypes.Type(value = GenerateUPUI.class, name = "upui"),
  @JsonSubTypes.Type(value = GenerateManualURI.class, name = "manualURI")
})
@RegisterForReflection
public interface QuantityStatergy {
  List<QuantityList> format(
      final IdentifierVocabularyType syntax,
      final Integer count,
      final Float refQuantity,
      final String dlURL,
      final RandomSerialNumberGenerator randomSerialNumberGenerator);
}
