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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.template.InputTemplate;
import java.util.ArrayList;
import java.util.List;
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

  private List<String> generateEvents(String inputFile, int expectedSize) throws Exception {
    final InputTemplate inputTemplate =
        objectMapper.readValue(getClass().getResourceAsStream(inputFile), InputTemplate.class);
    final List<String> eventList = new ArrayList<>();

    EPCISEventGenerator.generate(inputTemplate)
        .collect()
        .asList()
        .await()
        .indefinitely()
        .forEach(
            event -> {
              try {
                eventList.add(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event));
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            });

    assertTrue(eventList.size() > 0);
    assertEquals(expectedSize, eventList.size());

    return getEventTypes(eventList);
  }

  private List<String> getEventTypes(final List<String> eventList) throws JsonProcessingException {
    final List<String> eventTypes = new ArrayList<>();
    for (final String jsonString : eventList) {
      final JsonNode jsonNode = objectMapper.readTree(jsonString);
      if (jsonNode.has("type")) {
        eventTypes.add(jsonNode.get("type").asText());
      }
    }
    return eventTypes;
  }

  // Design Test Data for simple supply chain ObjectEvent(events - 1) -> AggregationEvent(events ->
  // 1)
  @Test
  public void DesignTestDataEvents1() throws Exception {
    final List<String> eventTypes = generateEvents("/DesignTestDataEvents1.json", 2);
    assertEquals(1, eventTypes.stream().filter("ObjectEvent"::equals).count());
    assertEquals(1, eventTypes.stream().filter("AggregationEvent"::equals).count());
  }

  // Design Test Data for simple supply chain TransactionEvent(events - 1) ->
  // TransformationEvent(events - 1) -> AssociationEvent(events-2)
  @Test
  public void DesignTestDataEvents2() throws Exception {
    final List<String> eventTypes = generateEvents("/DesignTestDataEvents2.json", 4);
    assertEquals(1, eventTypes.stream().filter("TransactionEvent"::equals).count());
    assertEquals(1, eventTypes.stream().filter("TransformationEvent"::equals).count());
    assertEquals(2, eventTypes.stream().filter("AssociationEvent"::equals).count());
  }

  // Design Test Data for supply chain ObjectEvent(events - 2) -> AggregationEvent(events-3)
  @Test
  public void DesignTestDataEvents3() throws Exception {
    final List<String> eventTypes = generateEvents("/DesignTestDataEvents3.json", 8);
    assertEquals(2, eventTypes.stream().filter("ObjectEvent"::equals).count());
    assertEquals(6, eventTypes.stream().filter("AggregationEvent"::equals).count());
  }

  // Design Test Data for supply chain ObjectEvent(events-1) -> AggregationEvent(events-2) ->
  // TransactionEvent(events - 3)
  @Test
  public void DesignTestDataEvents4() throws Exception {
    final List<String> eventTypes = generateEvents("/DesignTestDataEvents4.json", 9);
    assertEquals(1, eventTypes.stream().filter("ObjectEvent"::equals).count());
    assertEquals(2, eventTypes.stream().filter("AggregationEvent"::equals).count());
    assertEquals(6, eventTypes.stream().filter("TransactionEvent"::equals).count());
  }

  // Design Test data for supply chain with ObjectEvent(events-1) -> AggregationEvent(events-2) ->
  // TransactionEvent (events-3)
  @Test
  public void DesignTestDataEvents5() throws Exception {
    final List<String> eventTypes = generateEvents("/DesignTestDataEvents5.json", 18);
    assertEquals(2, eventTypes.stream().filter("ObjectEvent"::equals).count());
    assertEquals(4, eventTypes.stream().filter("AggregationEvent"::equals).count());
    assertEquals(12, eventTypes.stream().filter("TransactionEvent"::equals).count());
  }

  // Design Test data for supply chain with ObjectEvent(events-3) -> AggregationEvent(events-2) ->
  // TransactionEvent (events-3)
  @Test
  public void DesignTestDataEvents6() throws Exception {
    final List<String> eventTypes = generateEvents("/DesignTestDataEvents6.json", 11);
    assertEquals(5, eventTypes.stream().filter("ObjectEvent"::equals).count());
    assertEquals(2, eventTypes.stream().filter("AggregationEvent"::equals).count());
    assertEquals(4, eventTypes.stream().filter("TransactionEvent"::equals).count());
  }

  // Design Test data for supply chain with TransformationEvent(events-2) -> ObjectEvent(events-4)->
  // AggregationEvent(events-8)
  @Test
  public void DesignTestDataEvents7() throws Exception {
    final List<String> eventTypes = generateEvents("/DesignTestDataEvents7.json", 74);
    assertEquals(2, eventTypes.stream().filter("TransformationEvent"::equals).count());
    assertEquals(8, eventTypes.stream().filter("ObjectEvent"::equals).count());
    assertEquals(64, eventTypes.stream().filter("AggregationEvent"::equals).count());
  }
}
