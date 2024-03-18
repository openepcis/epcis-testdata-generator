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

import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class ErrorDeclarationFormatter {

  public static List<String> format(IdentifierVocabularyType syntax, List<String> input, final String dlURL) {
    if (IdentifierVocabularyType.WEBURI == syntax) {
      return formatWebURI(input, dlURL);
    } else {
      return formatURN(input);
    }
  }

  private static List<String> formatURN(List<String> input) {
    return input.stream().filter(Objects::nonNull).toList();
  }

  private static List<String> formatWebURI(List<String> input, final String dlURL) {
    return input.stream()
        .filter(Objects::nonNull)
        .toList();
  }
}
