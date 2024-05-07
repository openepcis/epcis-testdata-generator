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
package io.openepcis.testdata.api.filter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.openepcis.resources.oas.EPCISExampleOASFilter;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.examples.Example;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RegisterForReflection
@Slf4j
public class SchemaExampleOASFilter extends EPCISExampleOASFilter implements OASFilter {

  @Override
  public void filterOpenAPI(OpenAPI openAPI) {
    super.filterOpenAPI(openAPI);
    try {
      generateExamples().forEach(openAPI.getComponents()::addExample);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  Map<String, Example> generateExamples() throws IOException {
    final Map<String, Example> examples = new LinkedHashMap<>();

    final String createTestDataSchema =
        new String(
            Objects.requireNonNull(
                    SchemaExampleOASFilter.class.getResourceAsStream(
                        "/static/CreateTestDataSchema.json"))
                .readAllBytes());
    final String designTestDataSchema =
        new String(
            Objects.requireNonNull(
                    SchemaExampleOASFilter.class.getResourceAsStream(
                        "/static/DesignTestDataSchema.json"))
                .readAllBytes());

    examples.put(
        "CreateTestDataSchema",
        OASFactory.createExample()
            .value(objectMapper.readValue(createTestDataSchema, ObjectNode.class)));
    examples.put(
        "DesignTestDataSchema",
        OASFactory.createExample()
            .value(objectMapper.readValue(designTestDataSchema, ObjectNode.class)));
    return examples;
  }
}
