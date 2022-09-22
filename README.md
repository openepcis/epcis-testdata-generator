[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Java CI](https://github.com/openepcis/epcis-testdata-generator/actions/workflows/maven.yml/badge.svg)

## Introduction

For various business requirements, including traceability (of goods, assets, etc.), process automation, compliance, and supply chain management, you and your trading partners must capture and share visibility data in addition to master data (such as the description and weight of a trade item) and transactional data (such as an order, invoice, etc.). This data category's fundamental and open-source standard, EPCIS has been already widely used across a number of industries.

The open standard for capturing and exchanging visibility data is EPCIS. It has been endorsed by GS1, ISO, and IEC. It includes a ready-to-use data model that enables you to consolidate every stage of a business process into a single dataset. The basic details of what happened, when, where, why, and how are recorded as EPCIS events. The key to making your items traceable is to keep track of all pertinent events (such as harvesting, producing, packing, shipping, and selling) throughout your supply network.

## Need for EPCIS Test Data Generator

End users, solution providers, research institutions, and other parties frequently need test data for load/performance tests, PoCs/research projects (for example, to populate test databases), initiating data interchange between business partners, and more. However, creating EPCIS test data can literally take you days of effort and is usually tedious. If you are in such a situation, you'll be relieved to learn that our new open-source tool OpenEPCIS test data generator. It is now available that can assist you in completing this task in a matter of minutes.

## OpenEPCIS test data generator

The initial version of the EPCIS test data generator was developed as part of master thesis with the collaboration of GS1 Germany from June to November 2020.  Various users of the EPCIS community were involved in the development process, which enabled the application to be customised according to their needs and requirements. The latest version of the tool has been developed as part of an organisational effort at benelog GmbH & Co. KG with many improvements and more advanced functionality. Anyone interested in adopting or implementing EPCIS can get assistance from the interactive application OpenEPCIS Test Data Generator. It is being offered as an open-source project. This essentially means that anyone is welcome to use the tool.

Regardless of the industry, use case, or application domain, the OpenEPCIS test data generator tool offers a flexible and potent solution for creating test events. The generated events adhere to the most recent EPCIS specification, i.e. EPCIS 2.0. Events are produced in JSON/JSON-LD, but if businesses need EPCIS events in XML (either in accordance with EPCIS 1.2 or the most recent version, i.e., 2.0), there are additional open source tools that can transform them into the required data format.

## Key features

The following are some of the tool's key characteristics:
* Adheres to the most recent revisions of CBV 2.0 and EPCIS 2.0.
* Supports object/location IDs using both GS1 Digital Link URIs and EPC/EPC Class URIs.
* Generates JSON/JSON-LD events (which in turn can be translated to XML through other tools).
* Contains a randomization technique for elements like timestamps and serial numbers.
* Supports the use of both UUIDs and EPCIS Event Hash IDs to identify EPCIS events.
* Offers functionality for modelling uniquely tailored event sequences.
* Offers copying or exporting of designs or events for later use.

## Applications overview:

The developed OpenEPCIS Test Data Generator application has been divided into 3 modules [testdata-generator-common](testdata-generator-common), [testdata-generator-rest-api](testdata-generator-rest-api), and [testdata-generator-ui](testdata-generator-ui). Following is the overview of the individual module:

### [testdata-generator-common](testdata-generator-common) (Generic java library for generating testdata events)

This is the core of the OpenEPCIS Test Data Generator where the events are actually generated. It includes the logic for generating the events and formatting the values according to the EPCIS standard. To make the application memory efficient and to generate a large number of test events quickly only one event information is stored in memory at a time. It employs the Reactive Stream approach to do the same. This module has been developed primarily using Java with some additional libraries such as Jackson, Lombok, SmallRye Mutiny etc.

## [testdata-generator-rest-api](testdata-generator-rest-api) (REST service wrapper for generating testdata events)

This is the service that acts as a bridge between the front-end and back-end server. When a user provides the input information in the form of InputTemplate to generate the test data events then this service module will capture that information and validates it. If the InputTemplate adheres to all the constraints and rules then it's sent to the backend server ([testdata-generator-common](testdata-generator-common)) to generate the events. This service can also be directly accessed via the cURL command or Swagger-UI. This module has been developed using the latest technologies such as Java with the Quarkus framework. However, it also utilizes some libraries such as Jackson, OpenAPI, Hibernate validator, etc.

## [testdata-generator-ui](testdata-generator-ui) (user interface for generating testdata events)

This is the user's view of the application where users can interact with various fields of EPCIS and provide the necessary values. Also, It does the task of converting the user-provided values 
into the InputTemplate required for the subsequent modules. It has been developed primarily using the technologies such as  HTML (HyperText Markup Language), JavaScript library Nuxt.js/Vue.js, and 
CSS(Cascading Style Sheets). It also includes some important front-end libraries/frameworks such as Drawflow, Bootstrap, Bootstrap icons, CodeMirror, etc.

## Local set-up

On your local system, the OpenEPCIS Test Data Generator can be set up in various ways. The following section explains how to quickly set up the application using Docker and Podman.

### Running with Podman:

As a prerequisite, you must install Podman on your system (Podman is an open-source software platform for fast-developing, testing, and deploying applications). Please refer to the following links for further information about Podman and for installation guidelines:

Official Podman: https://github.com/containers/podman
Documentation: https://github.com/containers/podman/tree/main/docs
Installation instruction for Windows/macOS: https://podman.io/getting-started/installation

Run the following command in a terminal or command prompt after installing Podman on your local machine:
```
podman run --rm -t --name testdata-generator -p 8080:8080 openepcis/testdata-generator
```

### Running with Docker:

Docker has to be installed on your system as a prerequisite (Note: Commercial usage of Docker is subject to license restrictions). Please refer to the following pages for further information about Docker and for installation guidelines:

For Windows: https://docs.docker.com/desktop/install/windows-install/
For macOS: https://docs.docker.com/desktop/install/mac-install/
For Linux: https://docs.docker.com/desktop/install/linux-install/

To run the testdata generator you can simply use the [Testdata Generator Docker](https://hub.docker.com/repository/docker/openepcis/testdata-generator) image provided on docker hub.
```
docker run --rm -t --name testdata-generator -p 8080:8080 openepcis/testdata-generator
```

## Usage

### Using local set-up

If you have the Podman pod or Docker container is up and running as mentioned in previous section, you can use the following URLs for accessing the services:

| Service            | URL                                                                         |
|--------------------|-----------------------------------------------------------------------------|
| User Interface     | [http://localhost:8080/ui/](http://localhost:8080/ui/)                      |
| OpenAPI Swagger-UI | [http://localhost:8080/q/swagger-ui/](http://localhost:8080/q/swagger-ui//) |


### Direct usage

The [EPCISEventGenerator class](testdata-generator-common/src/main/java/io/openepcis/testdata/generator/EPCISEventGenerator.java) within [testdata-generator-common](testdata-generator-common) 
contains the Java method `generate` for producing test data events. To generate events, you must have the proper `inputTemplate` JSON prepared with all necessary data, which you can pass to the 
following method to obtain 
the required events:

```
// Pass the required JSON inputTemplate to generate EPCIS testdata events.
EPCISEventGenerator.generate(inputTemplate).collect().asList().await().indefinitely().forEach(e - > {
    try {
        System.out.println(e);
    } catch (JsonProcessingException ex) {
        throw new CompletionException(ex);
    }
});
```