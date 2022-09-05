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
package io.openepcis.testdata.generator.format;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.model.AbstractEventCreationModel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@ToString
public class UserExtensionSyntax implements Serializable {
  @Schema(
      type = SchemaType.STRING,
      description = "User extension namespace eg. https://example.com",
      required = true)
  private String namespace;

  @NotNull(message = "Local name cannot be Null for extension")
  @Schema(
      type = SchemaType.STRING,
      description = "User extension local name eg. example",
      required = true)
  private String localName;

  private String text;
  private List<UserExtensionSyntax> complex;
  private String namespacePrefix;

  @JsonCreator
  public static UserExtensionSyntax createUserExtensionSyntax( //
      @JsonProperty("namespace") String namespace, //
      @JsonProperty("localName") String localName, //
      @JsonProperty("text") String text,
      @JsonProperty("complex") List<UserExtensionSyntax> complex) {
    if (complex != null && !complex.isEmpty()) {
      return new UserExtensionSyntax(namespace, localName, complex);
    }
    return new UserExtensionSyntax(namespace, localName, text);
  }

  public Map<String, Object> toMap() {
    try {
      final Map<String, Object> map = new HashMap<>();

      // Store the namespace prefix and namespaces into AbstractEventCreationModel context variable
      // for addition into @context
      @SuppressWarnings("unchecked")
      Optional<Map<String, String>> temp =
          Optional.of(
              AbstractEventCreationModel.getContext().stream()
                  .filter(c -> c instanceof Map<?, ?>)
                  .map(c -> (Map<String, String>) c)
                  .flatMap(m -> m.entrySet().stream())
                  .collect(
                      Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (r1, r2) -> r1)));

      // Check if the namespace already exist within the List of HashMap if not then only add it if
      // exists already then skip it.
      if (!temp.get().containsKey(this.namespacePrefix)) {
        AbstractEventCreationModel.getContext().add(Map.of(this.namespacePrefix, this.namespace));
      }

      if (complex != null && !complex.isEmpty()) {
        final Map<String, Object> complexMap =
            complex.stream()
                .flatMap(c -> c.toMap().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        map.put(namespacePrefix + ":" + localName, complexMap);
      } else {
        map.put(namespacePrefix + ":" + localName, text);
      }
      return map;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of the Extensions, Please check the values provided values for User Extensions/ILMD/Error Extension : "
              + ex.getMessage());
    }
  }

  // Constructor for String DataType
  public UserExtensionSyntax(String namespace) {
    try {
      if (namespace.toLowerCase().contains("https://")
          || namespace.toLowerCase().contains("http://")) {
        this.namespacePrefix =
            namespace.substring(
                namespace.indexOf("/", namespace.indexOf("/") + 1) + 1, namespace.indexOf("."));
      } else {
        this.namespacePrefix = namespace;
      }
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of the Extensions Namespace prefix, Please check the values provided values for User Extensions/ILMD/Error Extension Namespace : "
              + ex.getMessage());
    }
  }

  // Constructor for String DataType
  public UserExtensionSyntax(String namespace, String localName, String text) {
    this(namespace);
    this.namespace = namespace;
    this.localName = localName;
    this.text = text;
  }

  // Constructor for Complex DataType
  public UserExtensionSyntax(
      String namespace, String localName, List<UserExtensionSyntax> complex) {
    this(namespace);
    this.namespace = namespace;
    this.localName = localName;
    this.complex = complex;
  }
}
