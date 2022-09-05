[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Test Data Generator

EPCIS (Electronic Product Code Information Services) is an ISO/IEC/GS1standard that helps to generate and exchange the visibility data within an organization or across multiple organizations based on the business context. Open EPCIS is based on this GS1 developed EPCIS standard that can be adopted by various industry sectors such as automotive, food, healthcare, retail, and many more. For demonstration, verification, and various other purposes Open EPCIS requires bulk test data that can be generated based on a particular scenario and provided information. Hence, this utility has been developed as a part of Open EPCIS which can generate bulk events in JSON format and according to EPCIS 2.0 standard.


#### Workflow:
This test data generator utility requires inputs such as eventType, eventTime, EPCs/Quantity information etc. based on this information bulk events can be generated. It can also generate bulk EPCs/Quantities based on the provided information. These information needs to be provided in the JSON format as the input to this utility which are processed to obtain the necessary information. Based on these information events are created. This utility supports the bulk event creation in both URN and Digital Link URI format. The attached files provide the JSON template for creating the different event type.


#### Technologies and Packages used:
- Java 17 (For formatting and event creation)
- Open EPCIS Core (For the creation of the events in JSON format)



#### Bulk event creation:
Provide the InputTemplate JSON data with all the events and identifiers related information to the following method which will return the List of EPCIS events based on the 
event information provided:

```
EPCISEventCreator.generate(InputTemplate).collect().asList().await().indefinitely().forEach(e -> {
            try {
                System.out.println(e.toString());
            } catch (JsonProcessingException ex) {
                throw new CompletionException(ex);
            }
        });
```


### Sample Input template:
````
{
    "events": [{
            "nodeId": 2,
            "eventType": "ObjectEvent",
            "eventCount": 1,
            "vocabularySyntax": "URN",
            "ordinaryEvent": true,
            "action": "ADD",
            "eventID": false,
            "eventTime": {
                "timeZoneOffset": "+02:00",
                "specificTime": "2022-03-03T15:34:17+01:00"
            },
            "businessStep": "COMMISSIONING",
            "disposition": "ACTIVE",
            "referencedIdentifier": [{
                "identifierId": 1,
                "epcCount": 10,
                "classCount": 6
            }],
            "outputReferencedIdentifier": [],
            "parentReferencedIdentifier": {}
        },
        {
            "nodeId": 3,
            "eventType": "AggregationEvent",
            "eventCount": 2,
            "vocabularySyntax": "URN",
            "ordinaryEvent": true,
            "action": "ADD",
            "eventID": false,
            "eventTime": {
                "timeZoneOffset": "+02:00",
                "specificTime": "2022-03-03T15:34:33+01:00"
            },
            "businessStep": "PACKING",
            "disposition": "IN_PROGRESS",
            "referencedIdentifier": [{
                "parentNodeId": 2,
                "epcCount": 5,
                "classCount": 3
            }],
            "outputReferencedIdentifier": [],
            "parentReferencedIdentifier": {
                "identifierId": 4,
                "parentCount": 2
            }
        }
    ],
    "identifiers": [{
            "identifierId": 1,
            "identifierSyntax": "URN",
            "instanceData": {
                "sgtin": {
                    "identifierType": "sgtin",
                    "gcpLength": "10",
                    "sgtin": "12345678901234",
                    "serialType": "range",
                    "rangeFrom": "100"
                }
            },
            "classData": {
                "gtin": {
                    "identifierType": "gtin",
                    "gcpLength": "10",
                    "quantityType": null,
                    "uom": null,
                    "serialType": "",
                    "gtin": "09876543210987"
                }
            },
            "parentData": null
        },
        {
            "identifierId": 4,
            "identifierSyntax": "URN",
            "instanceData": null,
            "classData": null,
            "parentData": {
                "sscc": {
                    "identifierType": "sscc",
                    "gcpLength": "10",
                    "gcp": "456789",
                    "serialType": "range",
                    "rangeFrom": "500"
                }
            }
        }
    ]
}
````