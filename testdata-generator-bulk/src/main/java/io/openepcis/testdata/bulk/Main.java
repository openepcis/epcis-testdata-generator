package io.openepcis.testdata.bulk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.identifier.instances.GenerateSGTIN;
import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import io.openepcis.testdata.generator.template.InputTemplate;
import io.openepcis.testdata.generator.template.ObjectEventType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {


  private static final ObjectMapper MAPPER = new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .registerModule(new Jdk8Module())
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
            "gcpLength": null,
            "gln": "9521999100003",
            "extension": "0",
            "type": "SGLN"
        },
        "referencedIdentifier": [{
            "identifierId": 1,
            "epcCount": 4,
            "classCount": 0
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
                "gcpLength": null,
                "sgtin": "09521987654327",
                "serialType": "range",
                "rangeFrom": 1,
                "count": 4
            }
        },
        "classData": null,
        "parentData": null
    }]
}          
          """;

  public static void main(String[] args) throws JsonProcessingException, IOException {

    InputTemplate template = MAPPER.readValue(TEMPLATE, InputTemplate.class);
    GenerateSGTIN generateSGTIN = (GenerateSGTIN) template.getIdentifiers().get(0).getInstanceData();
    //System.out.println(generateSGTIN);
    ObjectEventType objectEventType = (ObjectEventType) template.getEvents().get(0);
    System.out.println(objectEventType);
    String baseBath = args[0];
    create(template,baseBath+"/10-1", 10, 1);
    create(template,baseBath+"/100-1", 100, 1);
    create(template,baseBath+"/1000-1", 1000, 1);
    create(template,baseBath+"/10-10", 10, 10);
    create(template,baseBath+"/100-10", 100, 10);
    create(template,baseBath+"/1000-10", 1000, 10);
    create(template,baseBath+"/10-100", 10, 100);
    create(template,baseBath+"/100-100", 100, 100);
    create(template,baseBath+"/1000-100", 1000, 100);
    create(template,baseBath+"/10-1000", 10, 1000);
    create(template,baseBath+"/100-1000", 100, 1000);
    create(template,baseBath+"/1000-1000", 1000, 1000);
  }

  private static void create(InputTemplate template, String dir, int count, int size) throws IOException {
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