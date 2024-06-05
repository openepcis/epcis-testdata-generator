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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.template.InputTemplate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateTestDataEventsTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

    private void generateEvents(final String inputFile, final int expectedSize) throws Exception {
        final InputTemplate inputTemplate = objectMapper.readValue(getClass().getResourceAsStream(inputFile), InputTemplate.class);
        final List<String> eventList = new ArrayList<>();

        EPCISEventGenerator.generate(inputTemplate)
                .collect()
                .asList()
                .await()
                .indefinitely()
                .forEach(event -> {
                    try {
                        eventList.add(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        assertTrue(eventList.size() > 0);
        assertEquals(expectedSize, eventList.size());

        final List<String> eventTypes = getEventTypes(eventList);
        assertEquals(expectedSize, eventTypes.size());

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

    @Test
    public void ObjectEventTest() throws Exception {
        generateEvents("/CreateObjectEventInput.json", 2);
    }

    @Test
    public void AggregationEventTest() throws Exception {
        generateEvents("/CreateAggregationEventInput.json", 10);
    }

    @Test
    public void TransactionEventTest() throws Exception {
        generateEvents("/CreateTransactionEventInput.json", 5);
    }

    @Test
    public void TransformationEventTest() throws Exception {
        generateEvents("/CreateTransformationEventInput.json", 3);
    }

    @Test
    public void AssociationEventTest() throws Exception {
        generateEvents("/CreateAssociationEventInput.json", 2);
    }
}
