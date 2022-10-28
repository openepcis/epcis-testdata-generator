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
package io.openepcis.testdata.generator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.ContextNamespaceBuilder;
import io.openepcis.testdata.generator.model.EventCreationModel;
import io.openepcis.testdata.generator.model.EventModelUtil;
import io.openepcis.testdata.generator.reactivestreams.EPCISEventPublisher;
import io.openepcis.testdata.generator.template.EPCISEventType;
import io.openepcis.testdata.generator.template.InputTemplate;
import io.smallrye.mutiny.Multi;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class EPCISEventGenerator {

  private static final ObjectMapper objectMapper = new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

  private EPCISEventGenerator() {}

  public static List<EventCreationModel<EPCISEventType, EPCISEvent>> createModels(
      final InputTemplate inputTemplate) {
    try {
      return inputTemplate.getEvents().stream()
          .map(
              e ->
                  EventModelUtil.createModel(
                      e, EventModelUtil.usedIdentifiers(e, inputTemplate.getIdentifiers())))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .toList();
    } catch (Exception e) {
      throw new TestDataGeneratorException(
          "Exception occurred during the creation of model for input : " + e);
    }
  }

  public static Multi<String> generate(final InputTemplate inputTemplate) {

    // Call the method to build the context based on the information present within inputTemplate
    ContextNamespaceBuilder.storeContextInfo(inputTemplate.getEvents());

    try {
      final Multi<String> beginDocument =
          Multi.createFrom()
              .items(
                  "{\"@context\":"
                      + objectMapper.writeValueAsString(ContextNamespaceBuilder.getContext())
                      + ",\"isA\":\"EPCISDocument\", \"schemaVersion\": \"2.0\", \"creationDate\":\""
                      + Instant.now().toString()
                      + "\", \"epcisBody\":{ \"eventList\":[");
      final AtomicBoolean firstEntry = new AtomicBoolean(true);
      final Multi<String> generatedEvents =
          Multi.createFrom()
              .publisher(new EPCISEventPublisher(EPCISEventGenerator.createModels(inputTemplate)))
              .onItem()
              .transform(
                  event -> {
                    try {
                      if (firstEntry.getAndSet(false)) {
                        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);
                      }
                      return "," + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);
                    } catch (JsonProcessingException e) {
                      e.printStackTrace();
                    }
                    return null;
                  });

      // Multi<String> allEvents = generatedEvents.skip().last().map(s -> s + ",");
      // Multi<String> lastEvent = generatedEvents.select().last();

      return Multi.createBy()
          .concatenating()
          .streams(beginDocument, generatedEvents, Multi.createFrom().items("]}}"));
      // return allEvents.onCompletion().switchTo(lastEvent);
      // return Multi.createBy().concatenating().streams(beginDocument, allEvents, lastEvent,
      // Multi.createFrom().items("]}}"));
      // return generatedEvents;

    } catch (Exception e) {
      throw new TestDataGeneratorException(
          "Exception occurred during the generation of EPCIS events : " + e);
    }
  }
}
