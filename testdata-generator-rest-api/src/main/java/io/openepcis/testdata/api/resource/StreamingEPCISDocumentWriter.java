package io.openepcis.testdata.api.resource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;

@Provider
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class StreamingEPCISDocumentWriter implements MessageBodyWriter<StreamingEPCISDocument> {

  private final ObjectMapper objectMapper;

  private final JsonFactory factory = new JsonFactory();

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");

  @PostConstruct
  public void postConstruct() {
    factory.setCodec(objectMapper);
  }

  @Override
  public long getSize(
      StreamingEPCISDocument streamingEPCISDocument,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType) {
    return -1;
  }

  @Override
  public boolean isWriteable(
      Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return type.isAssignableFrom(StreamingEPCISDocument.class);
  }

  @Override
  public void writeTo(
      StreamingEPCISDocument streamingEPCISDocument,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream)
      throws IOException, WebApplicationException {

    // If user requested for pretty print of EPCIS document then add the pretty print
    if (streamingEPCISDocument.isPrettyPrint()) {
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    } else {
      objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
    }

    final AtomicBoolean running = new AtomicBoolean(true);

    streamingEPCISDocument
        .getEpcisEvents()
        .subscribe()
        .withSubscriber(
            new Subscriber<EPCISEvent>() {

              final JsonGenerator jsonGenerator = factory.createGenerator(entityStream);
              final AtomicReference<Subscription> refSubscription = new AtomicReference();

              @Override
              public void onSubscribe(Subscription s) {
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
                      "creationDate", formatter.format(ZonedDateTime.now()));

                  // Start epcisBody object
                  jsonGenerator.writeFieldName("epcisBody");
                  jsonGenerator.writeStartObject();

                  // Start eventList
                  jsonGenerator.writeFieldName("eventList");
                  jsonGenerator.writeStartArray();
                  refSubscription.get().request(1l);
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
                  refSubscription.get().request(1l);
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
            });
    while (running.get()) {
      Thread.yield();
    }
  }
}
