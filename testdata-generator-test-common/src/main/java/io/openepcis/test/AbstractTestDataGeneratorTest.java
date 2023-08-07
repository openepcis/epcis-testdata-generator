package io.openepcis.test;

import com.google.common.net.HttpHeaders;
import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.resources.util.Commons;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public abstract class AbstractTestDataGeneratorTest {

  public static String testDataGeneratorApi;
  abstract protected String url();

  @BeforeEach
  public void testApiEndpoint() {
    testDataGeneratorApi = url() + "/generateTestData";
  }

  /* Simple test case that creates 2 ObjectEvents */
  @Test
  public void createObjectEventTest() {
      Response response = given()
          .body(Commons.getInputStream("CreateObjectEventInput.json"))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
          .when()
          .post(testDataGeneratorApi)
          .then()
          .statusCode(200).extract().response();

      assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 2);
  }

  /* Simple test case that creates 10 AggregationEvents */
  @Test
  public void createAggregationEventTest() {
    Response response = given()
          .body(Commons.getInputStream("CreateAggregationEventInput.json"))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
          .when()
          .post(testDataGeneratorApi)
          .then()
        .statusCode(200).extract().response();

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 10);
  }

  /* Simple test case that creates 2 AggregationEvents */
  @Test
  public void createAssociationEventTest() {
    Response response = given()
        .body(Commons.getInputStream("CreateAssociationEventInput.json"))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post(testDataGeneratorApi)
        .then()
        .statusCode(200).extract().response();

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 2);
  }

  /* Simple test case that creates 5 TransactionEvents */
  @Test
  public void createTransactionEventTest() {
    Response response = given()
        .body(Commons.getInputStream("CreateTransactionEventInput.json"))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post(testDataGeneratorApi)
        .then()
            .statusCode(200).extract().response();
    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 5);
  }

  /* Simple test case that creates 3 TransformationEvents */
  @Test
  public void createTransformationEventTest() {
    Response response = given()
        .body(Commons.getInputStream("CreateTransformationEventInput.json"))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post(testDataGeneratorApi)
        .then()
        .statusCode(200).extract().response();
    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 3);
  }

  /*
   Design Test Data for simple supply chain ObjectEvent(events - 1) -> AggregationEvent(events -
  */
  @Test
  public void designTestDataEvents1Test() {
    Response response = executeRestAPI("DesignTestDataEvents1.json", testDataGeneratorApi);
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 2);

    assertEquals(1, getEventTypeCount(response.jsonPath(), "ObjectEvent"));
    assertEquals(1, getEventTypeCount(response.jsonPath(), "AggregationEvent"));
  }

  /*
   Design Test Data for simple supply chain TransactionEvent(events - 1) ->
   TransformationEvent(events - 1) -> AssociationEvent(events-2)
  */
  @Test
  public void designTestDataEvents2Test() {
    Response response = executeRestAPI("DesignTestDataEvents2.json", testDataGeneratorApi);
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 4);

    assertEquals(1, getEventTypeCount(response.jsonPath(), "TransactionEvent"));
    assertEquals(1, getEventTypeCount(response.jsonPath(), "TransformationEvent"));
    assertEquals(2, getEventTypeCount(response.jsonPath(), "AssociationEvent"));
  }

  /* Design Test Data for supply chain ObjectEvent(events - 2) -> AggregationEvent(events-3) */
  @Test
  public void designTestDataEvents3Test() {
    Response response = executeRestAPI("DesignTestDataEvents3.json", testDataGeneratorApi);
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 8);

    assertEquals(2, getEventTypeCount(response.jsonPath(), "ObjectEvent"));
    assertEquals(6, getEventTypeCount(response.jsonPath(), "AggregationEvent"));
  }

  /*
   Design Test Data for supply chain ObjectEvent(events-1) -> AggregationEvent(events-2) ->
   TransactionEvent(events - 3)
  */
  @Test
  public void designTestDataEvents4Test() {
    Response response = executeRestAPI("DesignTestDataEvents4.json", testDataGeneratorApi);
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 9);

    assertEquals(1, getEventTypeCount(response.jsonPath(), "ObjectEvent"));
    assertEquals(2, getEventTypeCount(response.jsonPath(), "AggregationEvent"));
    assertEquals(6, getEventTypeCount(response.jsonPath(), "TransactionEvent"));
  }

  /*
   Design Test data for supply chain with ObjectEvent(events-1) -> AggregationEvent(events-2) ->
   TransactionEvent (events-3)
  */
  @Test
  public void designTestDataEvents5Test() {
    Response response = executeRestAPI("DesignTestDataEvents5.json", testDataGeneratorApi);
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 18);

    assertEquals(2, getEventTypeCount(response.jsonPath(), "ObjectEvent"));
    assertEquals(4, getEventTypeCount(response.jsonPath(), "AggregationEvent"));
    assertEquals(12, getEventTypeCount(response.jsonPath(), "TransactionEvent"));
  }


  /*
   Design Test data for supply chain with ObjectEvent(events-3) -> AggregationEvent(events-2) ->
   TransactionEvent (events-3)
  */
  @Test
  public void designTestDataEvents6Test() {
    Response response = executeRestAPI("DesignTestDataEvents6.json", testDataGeneratorApi);
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 11);

    assertEquals(5, getEventTypeCount(response.jsonPath(), "ObjectEvent"));
    assertEquals(2, getEventTypeCount(response.jsonPath(), "AggregationEvent"));
    assertEquals(4, getEventTypeCount(response.jsonPath(), "TransactionEvent"));
  }

  /*
   Design Test data for supply chain with TransformationEvent(events-2) -> ObjectEvent(events-4)->
   AggregationEvent(events-8)
  */
  @Test
  public void designTestDataEvents7Test() {
    Response response = executeRestAPI("DesignTestDataEvents7.json", testDataGeneratorApi);
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList("epcisBody.eventList").size(), 74);

    assertEquals(2, getEventTypeCount(response.jsonPath(), "TransformationEvent"));
    assertEquals(8, getEventTypeCount(response.jsonPath(), "ObjectEvent"));
    assertEquals(64, getEventTypeCount(response.jsonPath(), "AggregationEvent"));
  }

  private Response executeRestAPI(final String filePath, final String url) {
    return given()
        .body(Commons.getInputStream(filePath))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post(url).then().extract().response();
  }

  private static void validateStatusCode(final Response response) {
    response.then().statusCode(200);
  }

  /**
   * @param eventType EventType whose count is required
   * @param jsonPath  the JsonPath object in which we have to count the eventType
   *
   * @return count of eventType in the jsonPath provided
   */
  private long getEventTypeCount(JsonPath jsonPath, String eventType) {
    return jsonPath.getList("epcisBody.eventList", EPCISEvent.class)
        .stream()
        .filter(event -> event.getType().equals(eventType))
        .count();
  }
}
