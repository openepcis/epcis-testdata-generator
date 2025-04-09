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
package io.openepcis.testdata.tests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.template.InputTemplate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import org.junit.Test;

public class GenerateEventsTest {
  @Test
  @SuppressWarnings("squid:S2699")
  public void generateTestDataEvents() throws IOException {
    InputTemplate inputTemplate = null;
    final ObjectMapper objectMapper = new ObjectMapper();
    final DefaultPrettyPrinter customPrinter = new DefaultPrettyPrinter();
    try {
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      objectMapper.registerModule(new Jdk8Module());
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.configure(
          DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

      // Formatting the generated JSON output with 4 spaces and new lines
      final DefaultIndenter indenter = new DefaultIndenter("    ", "\n");
      customPrinter.indentObjectsWith(indenter);
      customPrinter.indentArraysWith(indenter);

      inputTemplate =
          objectMapper.readValue(
              getClass().getResourceAsStream("/SampleInput.json"), InputTemplate.class);
    } catch (Exception e) {
      throw new TestDataGeneratorException(
          "Error occurred during the parsing of JSON InputTemplate : " + e.getMessage());
    }

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    final Set<ConstraintViolation<InputTemplate>> violations = validator.validate(inputTemplate);
    final String message =
        violations.stream().map(cv -> cv.getMessage()).collect(Collectors.joining(", "));
    System.out.println(message);

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                System.out.println(objectMapper.writer(customPrinter).writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });
  }
}
