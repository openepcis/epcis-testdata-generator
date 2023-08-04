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
package io.openepcis.testdata.tests;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.template.InputTemplate;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;
import org.junit.Assert;
import org.junit.Test;

public class CreateTestDataEventsTest {
  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
          .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

  // Test the creation of ObjectEvent chains
  @Test
  public void ObjectEventTest() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/CreateObjectEventInput.json"), InputTemplate.class);
    final List<String> objectEventList = new ArrayList<>();
    final String objectEventOutput =
        new String(
            getClass()
                .getResourceAsStream("/CreateObjectEventOutput.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                objectEventList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
                // System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(objectEventList.size() > 0);
    assertEquals(2, objectEventList.size());

    // Compare the created events with the actual expected events
    // assertEquals(objectMapper.readTree(objectEventOutput),
    // objectMapper.readTree(objectEventList.toString()));

    // Ensure all the created events are ObjectEvent
    final List<EPCISEvent> objectEventOutputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(objectEventList.toString());
    assertEquals(
        2,
        objectEventOutputTemplate.stream()
            .filter(event -> event.getType().equals("ObjectEvent"))
            .count());
  }

  // Test for the creation of AggregationEvent chain
  @Test
  public void AggregationEventTest() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/CreateAggregationEventInput.json"),
            InputTemplate.class);
    final List<String> aggregationEventList = new ArrayList<>();
    final String aggregationEventOutput =
        new String(
            getClass()
                .getResourceAsStream("/CreateAggregationEventOutput.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                aggregationEventList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(aggregationEventList.size() > 0);
    assertEquals(10, aggregationEventList.size());

    // Compare the created events with the actual expected events
    // assertEquals(objectMapper.readTree(aggregationEventOutput),
    // objectMapper.readTree(aggregationEventList.toString()));

    // Ensure all the created events are AggregationEvent
    final List<EPCISEvent> aggregationEventOutputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(aggregationEventList.toString());
    assertEquals(
        10,
        aggregationEventOutputTemplate.stream()
            .filter(event -> event.getType().equals("AggregationEvent"))
            .count());
  }

  // Test for the creation TransactionEvent chain
  @Test
  public void TransactionEventTest() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/CreateTransactionEventInput.json"),
            InputTemplate.class);
    final List<String> transactionEventList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                transactionEventList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(transactionEventList.size() > 0);
    assertEquals(5, transactionEventList.size());

    // Ensure all the created events are TransactionEvent
    final List<EPCISEvent> transactionEventOutputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(transactionEventList.toString());
    assertEquals(
        5,
        transactionEventOutputTemplate.stream()
            .filter(event -> event.getType().equals("TransactionEvent"))
            .count());
  }

  // Test for creation of TransformationEvent chain
  @Test
  public void TransformationEventTest() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/CreateTransformationEventInput.json"),
            InputTemplate.class);
    final List<String> transformationEventList = new ArrayList<>();
    final String transformationEventOutput =
        new String(
            getClass()
                .getResourceAsStream("/CreateTransformationEventOutput.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                transformationEventList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(transformationEventList.size() > 0);
    assertEquals(3, transformationEventList.size());

    // Compare the created events with the actual expected events
    // assertEquals(objectMapper.readTree(transformationEventOutput),
    // objectMapper.readTree(transformationEventList.toString()));

    // Ensure all the created events are TransformationEvent
    final List<EPCISEvent> transformationEventOutputTemplate =
        objectMapper
            .readerForListOf(EPCISEvent.class)
            .readValue(transformationEventList.toString());
    assertEquals(
        3,
        transformationEventOutputTemplate.stream()
            .filter(event -> event.getType().equals("TransformationEvent"))
            .count());
  }

  // Test for creation of AssociationEvent chain
  @Test
  public void AssociationEventTest() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/CreateAssociationEventInput.json"),
            InputTemplate.class);
    final List<String> associationEventList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                associationEventList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(associationEventList.size() > 0);
    assertEquals(2, associationEventList.size());

    // Ensure all the created events are AssociationEvent
    final List<EPCISEvent> associationEventOutputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(associationEventList.toString());
    assertEquals(
        2,
        associationEventOutputTemplate.stream()
            .filter(event -> event.getType().equals("AssociationEvent"))
            .count());
  }
}
