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

import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.openepcis.constants.EPCIS.EPCIS_DEFAULT_NAMESPACES;

@Getter
@Setter
@ToString
@RegisterForReflection
@Data
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

    @Schema(type = SchemaType.NUMBER,
            description = "Number Data associated with the custom user extension (Ex: 1, 10, 1234, 20.0 etc.)"
    )
    private String numberData;

    @Schema(
            type = SchemaType.STRING,
            enumeration = {
                    "string",
                    "number",
                    "complex"
            },
            description =
                    "Type of user extension simple type with direct value or complex type with children elements")
    private List<UserExtensionSyntax> children;

  @Schema(type = SchemaType.OBJECT,
          description = "Raw JSON-LD formatted extension with @context which can be directly appended as extensions."
  )
  private transient Object rawJsonld;


  //Method to format the UserExtensions based on the Web Vocabulary or Custom extensions by recursively reading them
  public Map<String, Object> toMap() {
    final Map<String, Object> map = new HashMap<>();

        if (this.rawJsonld == null) {
            //Add the context prefix and url to the document context
            buildContextInfo(this.prefix, this.contextURL);

            // Determine the key based on Web Vocabulary or Custom Extensions
            final String key = StringUtils.defaultIfBlank(this.label, this.prefix + ":" + this.property);

            // If children are present, recursively process them
            if (CollectionUtils.isNotEmpty(children)) {
                final Map<String, Object> complexMap = children.stream()
                        .flatMap(c -> c.toMap().entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                map.put(key, complexMap);
            } else {
                // Determine the value to store
                final String value = StringUtils.defaultIfBlank(data, numberData);

                // If number add as Number else add as String
                if(NumberUtils.isNumber(value)){
                    final Number number = value.contains(".") || value.toLowerCase().contains("e")
                                            ?  new BigDecimal(value)
                                            : NumberUtils.createNumber(value);
                    map.put(key, number);
                }else {
                    map.put(key, value);
                }
            }
        } else if (this.rawJsonld instanceof Map<?, ?>) {
            // Check if rawJsonld is a Map if so extract context info and append others to map
            @SuppressWarnings("unchecked") final Map<String, Object> genExtMap = (Map<String, Object>) this.rawJsonld;

            // Extract @context if present and append it to the StreamingEPCISDocument @context of EPCIS Document
            Optional.ofNullable(genExtMap.get("@context"))
                    .filter(obj -> obj instanceof List)
                    .map(obj -> (List<Map<String, String>>) obj)
                    .ifPresent(contextList -> contextList.stream()
                            .flatMap(contextMap -> contextMap.entrySet().stream())
                            .forEach(entry -> buildContextInfo(entry.getKey(), entry.getValue())));


            genExtMap.remove("@context"); // exclude the @context from the rawJsonld
            map.putAll(genExtMap); // add remaining entries to the map
        }

        return map;
    }


  //Function to add the context url and the prefix to the StreamingEPCISDocument context to generate the @context
  private void buildContextInfo(final String prefix, final String contextURL) {
    // Check if the namespace already exist within the context if not then only add.
    if (StreamingEPCISDocument.getContext() != null
            && !StreamingEPCISDocument.getContext().containsKey(prefix)
            && StringUtils.isNotBlank(prefix)
            && StringUtils.isNotBlank(contextURL)) {

      //Check if the namespace matches any of the default namespaces if so omit them
      boolean isDefaultContext = EPCIS_DEFAULT_NAMESPACES.entrySet().stream().anyMatch(entry -> entry.getKey().equals(prefix) && entry.getValue().equals(contextURL));

      //if no matches to default namespace then add
      if (!isDefaultContext) {
        StreamingEPCISDocument.getContext().put(prefix, contextURL);
      }
    }
  }
}
