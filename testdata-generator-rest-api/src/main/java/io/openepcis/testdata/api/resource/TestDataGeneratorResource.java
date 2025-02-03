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
package io.openepcis.testdata.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openepcis.constants.EPCISFormat;
import io.openepcis.constants.EPCISVersion;
import io.openepcis.converter.VersionTransformer;
import io.openepcis.model.epcis.EPCISDocument;
import io.openepcis.model.rest.ProblemResponseBody;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.reactivestreams.StreamingEPCISDocument;
import io.openepcis.testdata.generator.template.InputTemplate;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api")
@Tag(name = "Test Data Generator")
@RegisterForReflection
public class TestDataGeneratorResource {

  @Inject ObjectMapper objectMapper;

  @Inject Validator validator;

  @Inject
  VersionTransformer versionTransformer;

  // Method to Generator test data based on the provided JSON data template and show the appropriate
  // error messages
  @Operation(
      summary =
          "Generate list of EPCIS test data events using the desired supply chain systems InputTemplate.")
  @Path("/generateTestData")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RequestBody(
      description = "TestData Generator InputTemplate",
      content =
          @Content(
              schema = @Schema(implementation = InputTemplate.class),
              examples = {
                @ExampleObject(
                    name = "Create TestData Schema",
                    ref = "CreateTestDataSchema",
                    description = "Example schema file for Create TestData"),
                @ExampleObject(
                    name = "Design TestData Schema",
                    ref = "DesignTestDataSchema",
                    description = "Example schema file for Design TestData")
              }))
  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "OK: EPCIS Test Data events created successfully.",
            content =
                @Content(
                    schema =
                        @Schema(type = SchemaType.OBJECT, implementation = EPCISDocument.class))),
        @APIResponse(
            responseCode = "400",
            description =
                "Bad Request: Unable to generate events as request contains missing/invalid InputTemplate information.",
            content = @Content(schema = @Schema(implementation = ProblemResponseBody.class))),
        @APIResponse(
            responseCode = "401",
            description =
                "Unauthorized: Unable to generate events as request contains missing/invalid authorization information.",
            content = @Content(schema = @Schema(implementation = ProblemResponseBody.class))),
        @APIResponse(
            responseCode = "404",
            description =
                "Not Found: Unable to generate events as the requested resource not found.",
            content = @Content(schema = @Schema(implementation = ProblemResponseBody.class))),
        @APIResponse(
            responseCode = "406",
            description =
                "Not Acceptable: Unable to generate events as server cannot find content confirming request.",
            content = @Content(schema = @Schema(implementation = ProblemResponseBody.class))),
        @APIResponse(
            responseCode = "500",
            description =
                "Internal Server Error: Unable to generate events as server encountered problem.",
            content = @Content(schema = @Schema(implementation = ProblemResponseBody.class)))
      })
  public StreamingEPCISDocument generateTestData(
      final Map<String, Object> input,
      @Parameter(
              description = "Use pretty print for output",
              example = "?pretty, ?pretty=true, ?pretty=false",
              schema =
                  @Schema(
                      type = SchemaType.BOOLEAN,
                      required = false,
                      description = "empty defaults to true",
                      enumeration = {"true", "false", ""}))
          @QueryParam("pretty")
          final boolean pretty)
      throws TestDataGeneratorException {
    final InputTemplate inputTemplate;

    // Check if there are any error during the deserialization of JSON to Object if so then throw
    // the error and terminate the execution
    try {
      // Convert the Map input into InputTemplate using ObjectMapper if any error found in data then
      // show them in front-end
      inputTemplate = objectMapper.convertValue(input, InputTemplate.class);
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Error occurred during the deserialization of JSON InputTemplate, Please check the provided InputTemplate : "
              + ex.getMessage(), ex);
    }

    // If there are no error during deserialization of the JSON then continue with execution.
    try {
      // Check if all the provided values match the validation constraint if not then show them in
      // the front-end
      final Set<ConstraintViolation<InputTemplate>> violations = validator.validate(inputTemplate);

      // If there are no validation error then continue to generation of events based on
      // InputTemplate contents
      if (violations.isEmpty()) {
        final StreamingEPCISDocument streamingEPCISDocument = new StreamingEPCISDocument();
        StreamingEPCISDocument.storeContextInfo(inputTemplate.getEvents());
        StreamingEPCISDocument.storeContextUrls(inputTemplate.getContextUrls());
        streamingEPCISDocument.setPrettyPrint(pretty);
        streamingEPCISDocument.setEpcisEvents(EPCISEventGenerator.generate(inputTemplate));
        return streamingEPCISDocument;
      } else {
        // If there are any validation error then append all messages using , operator
        throw new TestDataGeneratorException(
            violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", ")));
      }
    } catch (Exception exception) {
      throw new TestDataGeneratorException(exception.getMessage(), exception);
    }
  }

  @Operation(summary = "Convert the generated JSON events to XML format.", hidden = true)
  @Path("/generateTestDataXML")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_XML)
  public Uni<InputStream> convertToXml(final InputStream jsonEvents) throws IOException {
    return Uni.createFrom()
        .item(versionTransformer.convert(jsonEvents,
                b -> b.fromMediaType(EPCISFormat.JSON_LD).toMediaType(EPCISFormat.XML).toVersion(EPCISVersion.VERSION_2_0_0)));
  }
}
