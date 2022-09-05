[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Java CI](https://github.com/openepcis/epcis-testdata-generator/actions/workflows/maven.yml/badge.svg)

# Test Data Generator

## [testdata-generator-common](testdata-generator-common)

Generic java library for creating EPCIS 2.0 test data

## [testdata-generator-rest-api](testdata-generator-rest-api)

EPCIS 2.0 testdata generator REST service wrapper 

## [testdata-generator-ui](testdata-generator-ui)

user interface for EPCIS 2.0 testdata generator REST service layer

## Running with Docker

To run the testdata generator you can simply use the [Testdata Generator Docker](https://hub.docker.com/repository/docker/openepcis/testdata-generator) image provided on docker hub.

### run docker container
```console
docker run --rm -t --name testdata-generator -p 8080:8080 openepcis/testdata-generator
```

### usage

Once the docker container is up and running, you can use the following URLs for accessing the services:

| Service                           | URL                                                                         |
|-----------------------------------|-----------------------------------------------------------------------------|
| Testdata Generator User Interface | [http://localhost:8080/ui/](http://localhost:8080/ui/)                      |
| OpenAPI Swagger-UI                | [http://localhost:8080/q/swagger-ui/](http://localhost:8080/q/swagger-ui//) |