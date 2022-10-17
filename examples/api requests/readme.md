# Making API requests to Test Data Generator

<div style="text-align:justify;">
Using the OpenEPCIS Test Data Generator API endpoints to generate EPCIS events.

## Introduction

Utilizing the OpenEPCIS test data generator API endpoint, you can quickly generate EPCIS events by using the pre-existing InputTemplate. To make requests, you can use a variety of technologies, including cURL, Swagger UI, Postman, and others. The instructions to generate EPCIS events by making requests to the API endpoint using the Swagger UI and cURL command has been provided in the next section.

## Swagger UI:

A part of the OpenAPI effort is Swagger. It is a collection of open-source development tools for RESTful APIs. Swagger-UI is an open-source application that generates a web page which lists all 
the APIs the Swagger standard has generated. It gives a summary and illustrates various features of the APIs, such as requests and responses, visually. Anyone may view and interact with API resources using the Swagger UI, including 
developers and end users, without needing to understand how they work. 

If you have configured the Podman pod or Docker container as described in the [set-up section](https://github.com/openepcis/epcis-testdata-generator#usage), you should be able to access 
the API link (http://localhost:8080/q/swagger-ui/). You can 
start making requests to the Test Data Generator service API by using an InputTemplate. Swagger-UI is especially useful if you already have one or more InputTemplate at hand 
(created manually or via the UI and saved on your computer) and want to scale up the number of events or modify any attribute information. 

![Swagger UI](../screenshots/Swagger%20UI.png)
<p align=center>Figure 1: Swagger UI view.</p>

## cURL:

A command-line tool known as Client URL, also popularly referred to as cURL, is used to send and receive data to and from servers. Similar to the Swagger-UI, the cURL (Client URL) command can be 
used on the command line or terminal to generate test data events. If you followed the instructions for setting up the Podman pod or Docker container as described in the [set-up section](https://github.com/openepcis/epcis-testdata-generator#usage), you should be 
able to make the cURL request using your InputTemplate to API (http://localhost:8080/api/generateTestData?pretty=true)

An example cURL command to generate 10 ObjectEvent using various other information is as follows: 
```
curl -X 'POST' \
  'http://localhost:8080/api/generateTestData?pretty=true' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
    "events": [{
        "nodeId": 1,
        "eventType": "ObjectEvent",
        "eventCount": 10,
        "locationPartyIdentifierSyntax": "URN",
        "ordinaryEvent": true,
        "action": "ADD",
        "eventID": false,
        "eventTime": {
            "timeZoneOffset": "+02:00",
            "specificTime": "2022-10-06T17:23:05.000+02:00"
        },
        "businessStep": "COMMISSIONING",
        "disposition": "ACTIVE",
        "readPoint": {
            "gcpLength": 7,
            "gln": "9859849889548",
            "extensionType": "dynamic",
            "extensionFrom": "430"
        },
        "referencedIdentifier": [],
        "parentReferencedIdentifier": {},
        "outputReferencedIdentifier": []
    }],
    "identifiers": []
}'
```

To generate the EPCIS events as needed, you can alter the InputTemplate data contained within the argument `-d`.

</div>

