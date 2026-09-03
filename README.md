<p align="center">
  <a href="https://openepcis.io"><img src="https://openepcis.io/img/openepcis-logo.svg" width="30%" alt="OpenEPCIS logo"></a>
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License"></a>
  <a href="https://github.com/openepcis/epcis-testdata-generator/tags"><img src="https://img.shields.io/github/v/tag/openepcis/epcis-testdata-generator?label=version" alt="Version"></a>
  <a href="https://github.com/openepcis/epcis-testdata-generator/actions/workflows/maven-ci.yml"><img src="https://github.com/openepcis/epcis-testdata-generator/actions/workflows/maven-ci.yml/badge.svg" alt="Java CI"></a>
  <a href="https://github.com/openepcis/epcis-testdata-generator/stargazers"><img src="https://img.shields.io/github/stars/openepcis/epcis-testdata-generator?style=social" alt="Stars"></a>
</p>

<h1 align="center">OpenEPCIS Test Data Generator</h1>

Generates EPCIS 2.0 test data events in JSON/JSON-LD format, using the CBV 2.0 vocabulary. You describe the events and the identifiers you need in a JSON input template, and the tool streams back the generated events, either over the REST API or directly from the Java library.

## Why

Creating EPCIS events manually takes time and is tedious work. You need EPCIS event data for load and performance tests, for PoCs and research projects (for example to populate a test database), and for starting the data exchange with a trading partner. This tool does the same job in minutes.

The identifiers are generated for you in both syntaxes: EPC URN such as `urn:epc:id:sgtin:9384989388.893.100` (a URN cannot resolve to anything) and GS1 Digital Link Web URI (which can be resolved). Event times and serial numbers can be randomized, so you get events that look like real captured data instead of the same event repeated.

The first version was developed as a master thesis together with GS1 Germany between June and November 2020, with many users of the EPCIS community involved. The current version is developed at benelog GmbH & Co. KG with more features and improvements.

## Key features

* Follows the latest EPCIS 2.0 and CBV 2.0 revisions.
* Object and location identifiers in both GS1 Digital Link URI and EPC/EPC Class URI syntax.
* Generates JSON/JSON-LD events, which other OpenEPCIS tools can convert into XML (EPCIS 1.2 or 2.0).
* Randomization for values such as event times, serial numbers, etc.
* Event identifiers as UUID or as EPCIS Event Hash ID.
* Lets you model your own event sequences, and copy or export a design for later use.
* Streams one event at a time, so generating a large number of events does not fill up the memory.

## Run it locally

The quickest way is the published container image. It serves the REST API and the Swagger UI on port 8080.

Using Docker (commercial use of Docker is subject to license restrictions):

```bash
docker run --rm -t --name testdata-generator \
  -p 8080:8080 \
  ghcr.io/openepcis/testdata-generator:0.9.4
```

Using [Podman](https://podman.io/getting-started/installation):

```bash
podman run --rm -t --name testdata-generator \
  -p 8080:8080 \
  ghcr.io/openepcis/testdata-generator:0.9.4
```

Or download the runnable jar from the [latest release](https://github.com/openepcis/epcis-testdata-generator/releases/latest) and start it with Java 21 or newer:

```bash
java -jar testdata-generator-quarkus-rest-app-.jar
```

Once it is running:

| Service            | URL                                                          |
|--------------------|--------------------------------------------------------------|
| OpenAPI Swagger UI | [http://localhost:8080/q/swagger-ui/](http://localhost:8080/q/swagger-ui/) |
| OpenAPI definition | [http://localhost:8080/q/openapi](http://localhost:8080/q/openapi)         |
| Generate events    | `POST http://localhost:8080/api/generateTestData`            |

If you only want to try the tool without installing anything, use the hosted [Event Data Generator](https://tools.openepcis.io/ui/event-data-generator).

## Usage

### Input template

Both the REST API and the Java library take the same input template. It holds the events you want (`events`) and the identifiers those events should use (`identifiers`):

```json
{
    "events": [{
        "nodeId": 1,
        "eventType": "ObjectEvent",
        "eventCount": 5,
        "locationPartyIdentifierSyntax": "URN",
        "ordinaryEvent": true,
        "action": "ADD",
        "eventID": true,
        "eventTime": {
            "timeZoneOffset": "+02:00",
            "fromTime": "2022-04-01T18:30:04+02:00",
            "toTime": "2022-04-05T18:30:04+02:00"
        },
        "recordTimeType": "CURRENT_TIME",
        "businessStep": "COMMISSIONING",
        "disposition": "ACTIVE",
        "referencedIdentifier": [{
            "identifierId": 1,
            "epcCount": 10,
            "classCount": 5
        }],
        "parentReferencedIdentifier": {},
        "outputReferencedIdentifier": []
    }],
    "identifiers": [{
        "identifierId": 1,
        "objectIdentifierSyntax": "URN",
        "instanceData": {
            "sgtin": {
                "identifierType": "sgtin",
                "gcpLength": 10,
                "sgtin": "89384989388934",
                "serialType": "range",
                "rangeFrom": 100,
                "rangeTo": 110
            }
        },
        "classData": {
            "grai": {
                "identifierType": "grai",
                "gcpLength": 10,
                "quantityType": null,
                "uom": null,
                "serialType": "",
                "grai": "8384783874378",
                "classIdentifiersCount": 5
            }
        },
        "parentData": null
    }]
}
```

This template asks for 5 ObjectEvents, each carrying 10 SGTIN instance identifiers and 5 GRAI class identifiers.

### REST API

Save the template above as `inputTemplate.json` and post it:

```bash
curl -X POST http://localhost:8080/api/generateTestData \
  -H "Content-Type: application/json" \
  -d @inputTemplate.json
```

The response is a complete EPCIS document with the generated events in `epcisBody.eventList`:

```json
{
  "@context": ["https://ref.gs1.org/standards/epcis/epcis-context.jsonld"],
  "type": "EPCISDocument",
  "schemaVersion": "2.0",
  "creationDate": "2026-09-03T15:19:21.12Z",
  "epcisBody": {
    "eventList": [{
      "type": "ObjectEvent",
      "eventTime": "2022-04-04T18:29:05.266+02:00",
      "recordTime": "2026-09-03T15:19:21+02:00",
      "eventTimeZoneOffset": "+02:00",
      "epcList": ["urn:epc:id:sgtin:9384989388.893.100", "urn:epc:id:sgtin:9384989388.893.101"],
      "action": "ADD",
      "bizStep": "commissioning",
      "disposition": "active",
      "quantityList": [{"epcClass": "urn:epc:idpat:grai:8384783874.37.*"}]
    }]
  }
}
```

There is a second endpoint, `POST /api/generateTestDataXML`, which takes the generated JSON events and converts them into XML.

### Java library

The [EPCISEventGenerator](testdata-generator-common/src/main/java/io/openepcis/testdata/generator/EPCISEventGenerator.java) class in [testdata-generator-common](testdata-generator-common) does the actual work. Read your JSON into an `InputTemplate` and pass it to `generate`, which returns a Mutiny `Multi<EPCISEvent>`:

```java
// The mapper must ignore unknown properties, otherwise Jackson rejects
// template fields such as identifierType with an UnrecognizedPropertyException.
final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

final InputTemplate inputTemplate =
    objectMapper.readValue(new File("inputTemplate.json"), InputTemplate.class);

// Generate the events and print each one as JSON.
EPCISEventGenerator.generate(inputTemplate)
    .collect()
    .asList()
    .await()
    .indefinitely()
    .forEach(event -> {
      try {
        System.out.println(objectMapper.writeValueAsString(event));
      } catch (JsonProcessingException e) {
        throw new CompletionException(e);
      }
    });
```

`collect().asList()` keeps every event in memory, which is fine for a handful of events. For a large number of events, subscribe to the `Multi` instead and handle one event at a time.

## Project layout

| Module | What it does |
|--------|--------------|
| [testdata-generator-common](testdata-generator-common) | Core Java library. Creates the events and formats the values according to EPCIS. Builds on the openepcis-models classes and uses Jackson, Lombok and SmallRye Mutiny. |
| [testdata-generator-test-common](testdata-generator-test-common) | Shared input templates and expected outputs used by the tests. |
| [testdata-generator-rest-api](testdata-generator-rest-api) | REST service on Quarkus. Validates the input template and streams the response back. |
| [quarkus/runtime](quarkus/runtime), [quarkus/deployment](quarkus/deployment) | Quarkus extension, so the generator can be used from a Quarkus application. |
| [quarkus/quarkus-app](quarkus/quarkus-app) | The runnable application. Produces the uber jar and the container image. |

The [testdata-generator-ui](testdata-generator-ui) folder holds the Nuxt.js/Vue.js front end, which is built and run on its own and is not part of the Maven build.

## Building

You need JDK 25, because the Java version comes from the `openepcis-bom` parent and is currently set to 25.

```bash
mvn clean verify
```

Other useful commands:

```bash
mvn clean verify -Pcoverage
mvn quarkus:dev -pl quarkus/quarkus-app
```

## Related

- [Test Data Generator documentation](https://openepcis.io/docs/test-data-generator) - How to use the tool, step by step
- [Event Data Generator web app](https://tools.openepcis.io/ui/event-data-generator) - The hosted user interface, nothing to install
- [OpenEPCIS Tools](https://tools.openepcis.io/) - open source EPCIS 2.0 tools and services
- [OpenEPCIS](https://openepcis.io/) - Read more about OpenEPCIS
- [benelog GmbH & Co. KG](https://www.benelog.com/) - Company behind OpenEPCIS
- [GS1 EPCIS Standard](https://www.gs1.org/standards/epcis) - Learn more about EPCIS

## License

Apache License 2.0. See [LICENSE](LICENSE).
