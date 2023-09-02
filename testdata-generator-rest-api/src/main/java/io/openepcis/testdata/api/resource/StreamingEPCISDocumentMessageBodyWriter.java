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
package io.openepcis.testdata.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class StreamingEPCISDocumentMessageBodyWriter implements MessageBodyWriter<StreamingEPCISDocument> {

  private final ObjectMapper objectMapper;

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
    streamingEPCISDocument.writeToOutputStream(b -> b.objectMapper(objectMapper).outputStream(entityStream).build());
  }
}
