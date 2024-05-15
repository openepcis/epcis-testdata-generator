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

import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.openepcis.constants.EPCIS.EPCIS_DEFAULT_NAMESPACES;

@Getter
@Setter
@ToString
@RegisterForReflection
public class UserExtensionSyntax implements Serializable {
  @Schema(
          type = SchemaType.STRING,
          description = "Label associated with the web vocabulary (Ex: gs1:appDownload, gs1:Country, etc.)"
  )
  private String label;

  @Schema(type = SchemaType.STRING,
          description = "Prefix associated with the custom user extension (Ex: cbvmda, example, ex1, ex1:lotNumber, etc.)"
  )
  private String prefix;

  @Schema(type = SchemaType.STRING,
          description = "Property associated with the custom user extension (Ex: lotNumber, distance, ex1:lotNumber, etc.)"
  )
  private String property;

  @Schema(type = SchemaType.STRING,
          description = "Context URL associated with the user extension (Ex: https://example.com, https://ex1.com, etc.)"
  )
  private String contextURL;

  @Schema(type = SchemaType.STRING,
          description = "Data associated with the custom user extension (Ex: 1, value-1, 1234, ex1:lotNumber=1234 etc.)"
  )
  private String data;

  @Schema(
          type = SchemaType.ARRAY,
          description = "Complex user extension information with its own children elements.")
  private List<UserExtensionSyntax> children;

  //Method to format the UserExtensions based on the Web Vocabulary or Custom extensions by recursively reading them
  public Map<String, Object> toMap() {
    // Check if the namespace already exist within the context if not then only add.
    if (StreamingEPCISDocument.getContext() != null
            && !StreamingEPCISDocument.getContext().containsKey(this.prefix)
            && StringUtils.isNotBlank(this.prefix)
            && StringUtils.isNotBlank(this.contextURL)) {

      //Check if the namespace matches any of the default namespaces if so omit them
      boolean isDuplicate = EPCIS_DEFAULT_NAMESPACES.entrySet().stream().anyMatch(entry -> entry.getKey().equals(prefix) && entry.getValue().equals(contextURL));

      //If does not match then add
      if (!isDuplicate) {
        StreamingEPCISDocument.getContext().put(this.prefix, this.contextURL);
      }
    }

    final Map<String, Object> map = new HashMap<>();

    //Based on the Web Vocabulary or Custom Extensions get the respective key and add information
    final String key = StringUtils.isNotBlank(this.label) ? this.label : (this.prefix + ":" + this.property);

    //for complex children is not empty then recursively loop over children and format the elements
    if (CollectionUtils.isNotEmpty(children)) {
      final Map<String, Object> complexMap = children.stream()
              .flatMap(c -> c.toMap().entrySet().stream())
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      map.put(key, complexMap);
    } else {
      //for simple string directly add the information
      map.put(key, data);
    }
    return map;
  }
}
