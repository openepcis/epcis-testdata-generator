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
package io.openepcis.testdata.bulk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import io.openepcis.testdata.generator.template.InputTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {


  private static final ObjectMapper MAPPER = new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .registerModule(new JavaTimeModule());

  private static final String TEMPLATE = """
{
    "events": [{
        "nodeId": 1,
        "eventType": "ObjectEvent",
        "eventCount": 1,
        "locationPartyIdentifierSyntax": "WebURI",
        "ordinaryEvent": true,
        "action": "ADD",
        "eventID": false,
        "eventTime": {
            "timeZoneOffset": "+02:00",
            "fromTime": "2023-01-01T12:37:22+01:00",
            "toTime": "2023-12-20T12:37:22+01:00"
        },
        "readPoint": {
            "gcpLength": 7,
            "gln": "9521999100003",
            "extension": "0",
            "type": "SGLN"
        },
        "referencedIdentifier": [{
            "identifierId": 1,
            "epcCount": 4
        }],
        "parentReferencedIdentifier": {},
        "outputReferencedIdentifier": []
    }],
    "identifiers": [{
        "identifierId": 1,
        "objectIdentifierSyntax": "WebURI",
        "instanceData": {
            "sgtin": {
                "identifierType": "sgtin",
                "gcpLength": 7,
                "sgtin": "09521987654327",
                "serialType": "range",
                "rangeFrom": 1,
                "count": 4
            }
        }
    }]
}          
          """;

  public static void main(String[] args) throws JsonProcessingException, IOException {
    InputTemplate template = MAPPER.readValue(TEMPLATE, InputTemplate.class);
    String baseBath = args[0];
    int[] step = new int[] {1, 10, 100, 1000};
    for (int eventCount : step) {
      for (int documentCount : step) {
        create(template,baseBath+"/"+documentCount+"-"+eventCount, documentCount, eventCount);
      }
    }
  }

  private static void create(final InputTemplate template, final String dir, final int count, final int size) throws IOException {
    template.getEvents().get(0).setEventCount(size);
    Path d = Path.of(dir);
    if (!Files.exists(d)) {
      Files.createDirectory(d);
    }
    for (int i = 0; i < count; i++) {
      Path f = d.resolve("ObjectEvent_"+i+".jsonld");
      if (!Files.exists(f)) {
        Files.createFile(f);
      }
      final StreamingEPCISDocument streamingEPCISDocument = new StreamingEPCISDocument();
      StreamingEPCISDocument.storeContextInfo(template.getEvents());
      streamingEPCISDocument.setPrettyPrint(true);
      streamingEPCISDocument.setEpcisEvents(EPCISEventGenerator.generate(template));
      final FileOutputStream out = new FileOutputStream(f.toFile());
      streamingEPCISDocument.writeToOutputStream(b -> {
        return b.objectMapper(MAPPER).outputStream(out).build();
      });
    }
  }

}