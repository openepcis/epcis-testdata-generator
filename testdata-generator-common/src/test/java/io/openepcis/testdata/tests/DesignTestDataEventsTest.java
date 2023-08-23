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

public class DesignTestDataEventsTest {

  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

  // Design Test Data for simple supply chain ObjectEvent(events - 1) -> AggregationEvent(events -
  // 1)
  @Test
  public void DesignTestDataEvents1() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/DesignTestDataEvents1.json"), InputTemplate.class);
    final List<String> designEventsList = new ArrayList<>();
    final String designEventsOutput =
        new String(
            getClass()
                .getResourceAsStream("/DesignTestDataEventsOutput1.json")
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
                designEventsList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(designEventsList.size() > 0);
    assertEquals(2, designEventsList.size());

    // Compare the created events with the actual expected events
    // assertEquals(objectMapper.readTree(designEventsOutput),
    // objectMapper.readTree(designEventsList.toString()));

    // Convert the list of generated EPCIS events into respective EPCIS events using ObjectMapper
    final List<EPCISEvent> outputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(designEventsList.toString());

    // Confirm the number TransactionEvent, TransformationEvent, and AssociationEvent count in the
    // created events
    assertEquals(
        1, outputTemplate.stream().filter(event -> event.getType().equals("ObjectEvent")).count());
    assertEquals(
        1,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("AggregationEvent"))
            .count());
  }

  // Design Test Data for simple supply chain TransactionEvent(events - 1) ->
  // TransformationEvent(events - 1) -> AssociationEvent(events-2)
  @Test
  public void DesignTestDataEvents2() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/DesignTestDataEvents2.json"), InputTemplate.class);
    final List<String> designEventsList = new ArrayList<>();
    final String designEventsOutput =
        new String(
            getClass()
                .getResourceAsStream("/DesignTestDataEventsOutput2.json")
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
                designEventsList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(designEventsList.size() > 0);
    assertEquals(4, designEventsList.size());

    // Compare the created events with the actual expected events
    // assertEquals(objectMapper.readTree(designEventsOutput),
    // objectMapper.readTree(designEventsList.toString()));

    // Convert the list of generated EPCIS events into respective EPCIS events using ObjectMapper
    final List<EPCISEvent> outputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(designEventsList.toString());

    // Confirm the number TransactionEvent, TransformationEvent, and AssociationEvent count in the
    // created events
    assertEquals(
        1,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("TransactionEvent"))
            .count());
    assertEquals(
        1,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("TransformationEvent"))
            .count());
    assertEquals(
        2,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("AssociationEvent"))
            .count());
  }

  // Design Test Data for supply chain ObjectEvent(events - 2) -> AggregationEvent(events-3)
  @Test
  public void DesignTestDataEvents3() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/DesignTestDataEvents3.json"), InputTemplate.class);
    final List<String> designEventsList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                designEventsList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(designEventsList.size() > 0);
    assertEquals(8, designEventsList.size());

    // Convert the list of generated EPCIS events into respective EPCIS events
    final List<EPCISEvent> outputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(designEventsList.toString());

    // Confirm the number ObjectEvent and AggregationEvent count in the created events
    assertEquals(
        2, outputTemplate.stream().filter(event -> event.getType().equals("ObjectEvent")).count());
    assertEquals(
        6,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("AggregationEvent"))
            .count());
  }

  // Design Test Data for supply chain ObjectEvent(events-1) -> AggregationEvent(events-2) ->
  // TransactionEvent(events - 3)
  @Test
  public void DesignTestDataEvents4() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/DesignTestDataEvents4.json"), InputTemplate.class);
    final List<String> designEventsList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                designEventsList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(designEventsList.size() > 0);
    assertEquals(9, designEventsList.size());

    // Convert the list of generated EPCIS events into respective EPCIS events
    final List<EPCISEvent> outputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(designEventsList.toString());

    // Confirm the number ObjectEvent, AggregationEvent and TransactionEvent count in the created
    // events
    assertEquals(
        1, outputTemplate.stream().filter(event -> event.getType().equals("ObjectEvent")).count());
    assertEquals(
        2,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("AggregationEvent"))
            .count());
    assertEquals(
        6,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("TransactionEvent"))
            .count());
  }

  // Design Test data for supply chain with ObjectEvent(events-1) -> AggregationEvent(events-2) ->
  // TransactionEvent (events-3)
  @Test
  public void DesignTestDataEvents5() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/DesignTestDataEvents5.json"), InputTemplate.class);
    final List<String> designEventsList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                designEventsList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(designEventsList.size() > 0);
    assertEquals(18, designEventsList.size());

    // Convert the list of generated EPCIS events into respective EPCIS events
    final List<EPCISEvent> outputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(designEventsList.toString());

    // Confirm the number ObjectEvent, AggregationEvent and TransactionEvent count in the created
    // events
    assertEquals(
        2, outputTemplate.stream().filter(event -> event.getType().equals("ObjectEvent")).count());
    assertEquals(
        4,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("AggregationEvent"))
            .count());
    assertEquals(
        12,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("TransactionEvent"))
            .count());
  }

  // Design Test data for supply chain with ObjectEvent(events-3) -> AggregationEvent(events-2) ->
  // TransactionEvent (events-3)
  @Test
  public void DesignTestDataEvents6() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/DesignTestDataEvents6.json"), InputTemplate.class);
    final List<String> designEventsList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                designEventsList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(designEventsList.size() > 0);
    assertEquals(11, designEventsList.size());

    // Convert the list of generated EPCIS events into respective EPCIS events
    final List<EPCISEvent> outputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(designEventsList.toString());

    // Confirm the number ObjectEvent, AggregationEvent and TransactionEvent count in the created
    // events
    assertEquals(
        5, outputTemplate.stream().filter(event -> event.getType().equals("ObjectEvent")).count());
    assertEquals(
        2,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("AggregationEvent"))
            .count());
    assertEquals(
        4,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("TransactionEvent"))
            .count());
  }

  // Design Test data for supply chain with TransformationEvent(events-2) -> ObjectEvent(events-4)->
  // AggregationEvent(events-8)
  @Test
  public void DesignTestDataEvents7() throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/DesignTestDataEvents7.json"), InputTemplate.class);
    final List<String> designEventsList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            e -> {
              try {
                designEventsList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(e));
              } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
              }
            });

    // Check for the number of events present within the list of created events
    Assert.assertTrue(designEventsList.size() > 0);
    assertEquals(74, designEventsList.size());

    // Convert the list of generated EPCIS events into respective EPCIS events
    final List<EPCISEvent> outputTemplate =
        objectMapper.readerForListOf(EPCISEvent.class).readValue(designEventsList.toString());

    // Confirm the number ObjectEvent, AggregationEvent and TransactionEvent count in the created
    // events
    assertEquals(
        2,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("TransformationEvent"))
            .count());
    assertEquals(
        8, outputTemplate.stream().filter(event -> event.getType().equals("ObjectEvent")).count());
    assertEquals(
        64,
        outputTemplate.stream()
            .filter(event -> event.getType().equals("AggregationEvent"))
            .count());
  }
}
