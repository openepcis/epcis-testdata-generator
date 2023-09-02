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
package io.openepcis.testdata.generator.reactivestreams;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.smallrye.mutiny.Multi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StreamingEPCISDocumentOutput {

    private final Executor executor;

    private final ObjectMapper objectMapper;

    private final OutputStream outputStream;

    private final Writer writer;

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");

    private StreamingEPCISDocumentOutput( final Executor executor, final ObjectMapper objectMapper, final OutputStream outputStream, final Writer writer) {
        this.executor = executor;
        this.objectMapper = objectMapper;
        this.outputStream = outputStream;
        this.writer = writer;
    }

    StreamingEPCISDocumentOutput(final Executor executor, final ObjectMapper objectMapper, final OutputStream outputStream) {
        this(executor, objectMapper, outputStream, null);
    }

    StreamingEPCISDocumentOutput(final Executor executor, final ObjectMapper objectMapper, final Writer writer) {
        this(executor, objectMapper, null, writer);
    }

    public static OutputStreamBuilder outputStreamBuilder() {
        return new OutputStreamBuilder();
    }

    public static WriterBuilder writerBuilder() {
        return new WriterBuilder();
    }

    private JsonGenerator createJsonGenerator() throws IOException {
        final JsonGenerator jsonGenerator = outputStream != null?JSON_FACTORY.createGenerator(outputStream):JSON_FACTORY.createGenerator(writer);
        jsonGenerator.setCodec(objectMapper);
        return jsonGenerator;
    }

    void writeTo(final StreamingEPCISDocument streamingEPCISDocument) throws IOException {
        // If user requested for pretty print of EPCIS document then add the pretty print
        if (streamingEPCISDocument.isPrettyPrint()) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        } else {
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        }

        final AtomicBoolean running = new AtomicBoolean(true);

        Multi<EPCISEvent> epcisEventMulti = streamingEPCISDocument
                .getEpcisEvents();
        if (executor != null) {
            epcisEventMulti = epcisEventMulti.runSubscriptionOn(executor);
        }
        epcisEventMulti.subscribe()
                .withSubscriber(createSubsriber(createJsonGenerator(), running));

        while (running.get()) {
            Thread.yield();
        }
    }

    private Flow.Subscriber<EPCISEvent> createSubsriber(final JsonGenerator jsonGenerator, final AtomicBoolean running)  {
        return new Flow.Subscriber<>() {
            final AtomicReference<Flow.Subscription> refSubscription = new AtomicReference<>();

            @Override
            public void onSubscribe(Flow.Subscription s) {
                refSubscription.set(s);

                try {

                    // create Outermost JsonObject
                    jsonGenerator.writeStartObject();

                    // Write the info related to Context element in JSON
                    jsonGenerator.writeFieldName("@context");
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeString(
                            "https://ref.gs1.org/standards/epcis/2.0.0/epcis-context.jsonld");

                    // If context contains any values then add them to context array
                    if (StreamingEPCISDocument.getContext() != null
                            && !StreamingEPCISDocument.getContext().isEmpty()) {
                        StreamingEPCISDocument.getContext()
                                .forEach(
                                        (key, value) -> {
                                            try {
                                                jsonGenerator.writeStartObject();
                                                jsonGenerator.writeStringField(key, value);
                                                jsonGenerator.writeEndObject();
                                            } catch (IOException ex) {
                                                throw new TestDataGeneratorException(
                                                        "Exception occurred during EPCIS document creation: Error occurred during the addition of Namespaces: "
                                                                + ex.getMessage());
                                            }
                                        });
                    }

                    jsonGenerator.writeEndArray(); // end context array

                    // Write Other header fields of JSON
                    jsonGenerator.writeStringField("type", "EPCISDocument");
                    jsonGenerator.writeStringField("schemaVersion", "2.0");
                    jsonGenerator.writeStringField(
                            "creationDate", DATE_TIME_FORMATTER.format(ZonedDateTime.now()));

                    // Start epcisBody object
                    jsonGenerator.writeFieldName("epcisBody");
                    jsonGenerator.writeStartObject();

                    // Start eventList
                    jsonGenerator.writeFieldName("eventList");
                    jsonGenerator.writeStartArray();
                    refSubscription.get().request(1L);
                } catch (Exception ex) {
                    refSubscription.get().cancel();
                    running.set(false);
                    throw new TestDataGeneratorException(
                            "Exception occurred during EPCIS document creation: adding the header for EPCIS document failed "
                                    + ex.getMessage());
                }
            }

            @Override
            public void onNext(EPCISEvent epcisEvent) {
                try {
                    jsonGenerator.writeObject(epcisEvent);
                    refSubscription.get().request(1L);
                } catch (IOException ex) {
                    refSubscription.get().cancel();
                    running.set(false);
                    throw new TestDataGeneratorException(
                            "Exception occurred during EPCIS document wrapper creation: addition of EPCIS event to eventsList failed "
                                    + ex.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                try {
                    jsonGenerator.writeEndArray(); // End the eventList array
                    jsonGenerator.writeEndObject(); // End epcisBody
                    jsonGenerator.writeEndObject(); // End whole json file
                } catch (IOException ex) {
                    throw new TestDataGeneratorException(
                            "Exception occurred during EPCIS document wrapper creation : creation of EPCIS document failed "
                                    + ex.getMessage());
                } finally {
                    running.set(false);
                }
            }

            @Override
            public void onComplete() {
                try {
                    jsonGenerator.writeEndArray(); // End the eventList array
                    jsonGenerator.writeEndObject(); // End epcisBody
                    jsonGenerator.writeEndObject(); // End whole json file

                    jsonGenerator.flush();
                    jsonGenerator.close();
                } catch (IOException ex) {
                    throw new TestDataGeneratorException(
                            "Exception occurred during EPCIS document creation: completion of document failed  "
                                    + ex.getMessage());
                } finally {
                    running.set(false);
                }
            }
        };
    }

    public static class OutputStreamBuilder {
        private Executor executor;
        private ObjectMapper objectMapper;
        private OutputStream outputStream;

        OutputStreamBuilder() {
        }

        public OutputStreamBuilder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public OutputStreamBuilder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public OutputStreamBuilder outputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
            return this;
        }

        public StreamingEPCISDocumentOutput build() {
            return new StreamingEPCISDocumentOutput(executor, objectMapper, outputStream);
        }

        public String toString() {
            return "StreamingEPCISDocumentOutput.OutputStreamBuilder(executor=" + this.executor + ", objectMapper=" + this.objectMapper + ", outputStream=" + this.outputStream + ")";
        }
    }

    public static class WriterBuilder {
        private Executor executor;
        private ObjectMapper objectMapper;
        private Writer writer;

        WriterBuilder() {
        }

        public WriterBuilder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public WriterBuilder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public WriterBuilder writer(Writer writer) {
            this.writer = writer;
            return this;
        }

        public StreamingEPCISDocumentOutput build() {
            return new StreamingEPCISDocumentOutput(executor, objectMapper, writer);
        }

        public String toString() {
            return "StreamingEPCISDocumentOutput.WriterBuilder(executor=" + this.executor + ", objectMapper=" + this.objectMapper + ", writer=" + this.writer + ")";
        }
    }
}
