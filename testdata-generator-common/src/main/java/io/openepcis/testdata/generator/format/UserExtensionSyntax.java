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

import static io.openepcis.constants.EPCIS.EPCIS_DEFAULT_NAMESPACES;

import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocumentOutput;
import io.openepcis.testdata.generator.template.RandomGenerators;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@ToString
@RegisterForReflection
@Data
public class UserExtensionSyntax implements Serializable {
  @Schema(
      type = SchemaType.STRING,
      description =
          "Label associated with the web vocabulary (Ex: gs1:appDownload, gs1:Country, etc.)")
  private String label;

  @Schema(
      type = SchemaType.STRING,
      description =
          "Prefix associated with the custom user extension (Ex: cbvmda, example, ex1, ex1:lotNumber, etc.)")
  private String prefix;

  @Schema(
      type = SchemaType.STRING,
      description =
          "Property associated with the custom user extension (Ex: lotNumber, distance, ex1:lotNumber, etc.)")
  private String property;

  @Schema(
      type = SchemaType.STRING,
      description =
          "Data type associated with the custom user extension (Ex: array, object, string, etc.)")
  private String dataType;

  @Schema(
      type = SchemaType.STRING,
      description =
          "Context URL associated with the user extension (Ex: https://example.com, https://ex1.com, etc.)")
  private String contextURL;

  @Schema(
      type = SchemaType.STRING,
      description =
          "Data associated with the custom user extension (Ex: 1, value-1, 1234, ex1:lotNumber=1234 etc.)")
  private String stringData;

  @Schema(
      type = SchemaType.NUMBER,
      description =
          "Number Data associated with the custom user extension (Ex: 1, 10, 1234, 20.0 etc.)")
  private String numberData;

  @Schema(
      type = SchemaType.STRING,
      description =
          "Expression associated with the custom user extension (Ex: {{ epcList[0] }}, etc.)")
  private String expression;

  @Schema(
      type = SchemaType.OBJECT,
      description =
          "Random information associated with the custom user extension (Ex: { \"type\": \"static\", \"staticValue\": 10, \"randomID\": \"\" })")
  private ValueTypeSyntax random;

  @Schema(
      type = SchemaType.STRING,
      enumeration = {"string", "number", "complex"},
      description =
          "Type of user extension simple type with direct value or complex type with children elements")
  private List<UserExtensionSyntax> children;

  @Schema(
      type = SchemaType.OBJECT,
      description =
          "Raw JSON-LD formatted extension with @context which can be directly appended as extensions.")
  private transient Object rawJsonld;

  @Setter private static List<RandomGenerators> randomGenerators;

  // Converts UserExtensionSyntax to a Map representation, handling both raw JSON-LD and structured
  // data.
  public Map<String, Object> toMap() {
    final Map<String, Object> result = new LinkedHashMap<>();

    // If raw JSON-LD is provided, use it (excluding any "@context")
    if (rawJsonld instanceof Map<?, ?>) {
      @SuppressWarnings("unchecked")
      final Map<String, Object> rawMap = (Map<String, Object>) rawJsonld;
      rawMap.remove("@context");
      // Check for expressions in raw JSON-LD
      if (containsExpressions(rawMap)) {
        StreamingEPCISDocumentOutput.setShouldRunJinjaTemplate(true);
      }
      result.putAll(rawMap);
      return result;
    }

    // Compute key using label (if present) or prefix + ":" + property
    String key = null;
    if (StringUtils.isNotBlank(property) || StringUtils.isNotBlank(label)) {
      key =
          StringUtils.startsWith(property, "@")
              ? property
              : StringUtils.defaultIfBlank(label, prefix + ":" + property);
    }

    // If computed key is blank (avoid "null:null" by merging children directly)
    // If dataType equals "array" (return a list of children maps under the key)
    if (StringUtils.isBlank(key) || "array".equalsIgnoreCase(dataType)) {
      if (CollectionUtils.isNotEmpty(children)) {
        if (StringUtils.isBlank(key)) {
          // If key is blank, merge children directly.
          return children.stream()
                  .flatMap(child -> child.toMap().entrySet().stream())
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
          // For an array type, convert children to a List of Maps.
          final List<Map<String, Object>> childrenList =
                  children.stream().map(child -> {
                    // Check if child is an anonymous object
                    if ("anonymousObject".equalsIgnoreCase(child.getDataType())) {
                      if (CollectionUtils.isNotEmpty(child.getChildren())) {
                        final Map<String, Object> unorderedMap = child.getChildren().stream()
                                .flatMap(grandChild -> grandChild.toMap().entrySet().stream())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (a,b) -> b, // merge function to handle duplicates
                                        LinkedHashMap::new // preserve  order
                                ));

                        return reorderIdAndTypeFirst(unorderedMap);
                      } else {
                        // If anonymous object has no children, return empty map
                        return new LinkedHashMap<String, Object>();
                      }
                    } else {
                      // For non-anonymous objects, use the standard toMap() conversion
                      return child.toMap();
                    }
                  }).collect(Collectors.toList());
          result.put(key, childrenList);
        }
      } else {
        if (StringUtils.isBlank(key)) {
          return new LinkedHashMap<>();
        } else {
          result.put(key, new ArrayList<>());
        }
      }
      return result;
    }

    // If there are child and object type extensions, process them recursively
    if (CollectionUtils.isNotEmpty(children)) {
      final Map<String, Object> childrenMap =
          children.stream()
              .flatMap(child -> child.toMap().entrySet().stream())
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      result.put(key, childrenMap);
      return result;
    }

    // Process the leaf node based on the available data
    if (StringUtils.isNotBlank(expression)) {
      result.put(key, expression);
      // Flag that Jinja processing is needed after event creation
      StreamingEPCISDocumentOutput.setShouldRunJinjaTemplate(true);
    } else if (random != null) {
      // Generate a random value using the configured randomGenerators
      result.put(key, random.getValue(randomGenerators));
    } else if (StringUtils.isNotBlank(numberData)) {
      // Parse numeric data, handling decimals and scientific notation
      final Number number =
          (numberData.contains(".") || numberData.toLowerCase().contains("e"))
              ? new BigDecimal(numberData)
              : NumberUtils.createNumber(numberData);
      result.put(key, number);
    } else {
      // Default: use string data (or empty string if null)
      result.put(key, StringUtils.defaultIfBlank(stringData, ""));
    }

    return result;
  }

  // Reorder elements in Anonymous array children and if id/type present add it as first elements
  private Map<String, Object> reorderIdAndTypeFirst(final Map<String, Object> originalMap) {
        final Map<String, Object> ordered = new LinkedHashMap<>();

        if (originalMap.containsKey("id")) {
            ordered.put("id", originalMap.remove("id"));
        }

        if (originalMap.containsKey("type")) {
            ordered.put("type", originalMap.remove("type"));
        }

        ordered.putAll(originalMap);
        return ordered;
  }

  // Recursively processes namespaces from raw JSON-LD data and child extensions.
  public void extractNamespaces() {
    if (this.rawJsonld == null) {
      buildContextInfo(prefix, contextURL);

      // Loop over children and complex values to extract namespaces
      if (CollectionUtils.isNotEmpty(children)) {
        children.forEach(UserExtensionSyntax::extractNamespaces);
      }
    } else if (this.rawJsonld instanceof Map<?, ?>) {
      // Check if rawJsonld is a Map if so extract context info and append others to map
      @SuppressWarnings("unchecked")
      final Map<String, Object> genExtMap = (Map<String, Object>) this.rawJsonld;

      // Extract @context if present and append it to the StreamingEPCISDocument @context of EPCIS
      // Document
      Optional.ofNullable(genExtMap.get("@context"))
          .filter(List.class::isInstance)
          .map(obj -> (List<Map<String, String>>) obj)
          .ifPresent(
              contextList ->
                  contextList.stream()
                      .flatMap(contextMap -> contextMap.entrySet().stream())
                      .forEach(entry -> buildContextInfo(entry.getKey(), entry.getValue())));
    }
  }

  // Function to add the context url and the prefix to the StreamingEPCISDocument context to
  // generate the @context
  private void buildContextInfo(final String prefix, final String contextURL) {
    // Check if the namespace already exist within the context if not then only add.
    if (StreamingEPCISDocument.getContext() != null
        && !StreamingEPCISDocument.getContext().containsKey(prefix)
        && StringUtils.isNotBlank(prefix)
        && StringUtils.isNotBlank(contextURL)) {

      // Check if the namespace matches any of the default namespaces if so omit them
      boolean isDefaultContext =
          EPCIS_DEFAULT_NAMESPACES.entrySet().stream()
              .anyMatch(
                  entry -> entry.getKey().equals(prefix) && entry.getValue().equals(contextURL));

      // if no matches to default namespace then add
      if (!isDefaultContext) {
        StreamingEPCISDocument.getContext().put(prefix, contextURL);
      }
    }
  }

  /**
   * Recursively checks if a map contains any Jinja expression patterns in raw jsonld.
   * Looks for strings containing "{{" and "}}" patterns.
   */
  private boolean containsExpressions(Object obj) {
    if (obj instanceof String) {
      String str = (String) obj;
      return str.contains("{{") && str.contains("}}");
    } else if (obj instanceof Map<?, ?>) {
      Map<?, ?> map = (Map<?, ?>) obj;
      return map.values().stream().anyMatch(this::containsExpressions);
    } else if (obj instanceof List<?>) {
      List<?> list = (List<?>) obj;
      return list.stream().anyMatch(this::containsExpressions);
    } else if (obj instanceof Object[]) {
      Object[] array = (Object[]) obj;
      return Arrays.stream(array).anyMatch(this::containsExpressions);
    }
    return false;
  }
}
