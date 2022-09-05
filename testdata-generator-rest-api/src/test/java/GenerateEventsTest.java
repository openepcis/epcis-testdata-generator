/*
 * Copyright 2022 benelog GmbH & Co. KG
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
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.HttpHeaders;
import io.openepcis.testdata.generator.template.InputTemplate;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Test;

public class GenerateEventsTest {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  private InputTemplate inputTemplate = null;
  RequestSpecification httpRequest;
  Response response;

  @Before
  public void init() throws IOException {
    // API which needs to be called
    RestAssured.baseURI = "http://localhost:9001/api";
    inputTemplate =
        objectMapper.readValue(
            getClass().getResourceAsStream("/SampleInput.json"), InputTemplate.class);

    // Creating an HTTP request
    httpRequest = RestAssured.given();
    httpRequest.body(inputTemplate);
    httpRequest.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    // Making a request to /generateTestData
    response = httpRequest.request(Method.POST, "/generateTestData");
  }

  @Test
  public void statusCodeTest() {
    // Check if the HTTP POST is sending the response with status code 200
    assertEquals(200, response.getStatusCode());
  }

  @Test
  public void responseContentTypeTest() {
    // Check if the HTTP POST is sending the response with response content type JSON
    assertEquals("application/json;charset=UTF-8", response.getContentType());
  }

  @Test
  public void generateEventsTest() {
    // Check if the /generate API is generating EPCIS events as per the input count
    given()
        .body(inputTemplate)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post("http://localhost:9001/api/generateTestData")
        .then()
        .statusCode(200)
        .body("body.size()", is(10));
  }
}
