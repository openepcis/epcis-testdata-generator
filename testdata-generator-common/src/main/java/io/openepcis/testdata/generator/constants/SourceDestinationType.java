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
package io.openepcis.testdata.generator.constants;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum SourceDestinationType {
  OWNING_PARTY("owning_party"),
  PROCESSING_PARTY("processing_party"),
  LOCATION("location"),
  OTHER("other");

  private String type;

  SourceDestinationType(String type) {
    this.type = type;
  }

  String getSourceDestinationTypeValues() {
    return type;
  }
}
