package io.openepcis.testdata.api.test;

import com.google.common.net.HttpHeaders;
import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.model.rest.EPCISEventTypes;
import io.openepcis.resources.util.Commons;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public abstract class AbstractTestDataGeneratorTest {

  public static final String EPCIS_BODY_EVENT_LIST = "epcisBody.eventList";

  protected abstract String url();

  String testDataGeneratorApi() {
    return url() + "/generateTestData";
  }

  /* Simple test case that creates 2 ObjectEvents */
  @Test
  void createObjectEventTest() {
      Response response = given()
          .body(Commons.getInputStream("CreateObjectEventInput.json"))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
          .when()
          .post(testDataGeneratorApi())
          .then()
          .statusCode(200).extract().response();

      assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 2);
  }

  /* Simple test case that creates 10 AggregationEvents */
  @Test
  void createAggregationEventTest() {
    Response response = given()
          .body(Commons.getInputStream("CreateAggregationEventInput.json"))
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
          .when()
          .post(testDataGeneratorApi())
          .then()
        .statusCode(200).extract().response();

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 10);
  }

  /* Simple test case that creates 2 AggregationEvents */
  @Test
  void createAssociationEventTest() {
    Response response = given()
        .body(Commons.getInputStream("CreateAssociationEventInput.json"))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post(testDataGeneratorApi())
        .then()
        .statusCode(200).extract().response();

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 2);
  }

  /* Simple test case that creates 5 TransactionEvents */
  @Test
  void createTransactionEventTest() {
    Response response = given()
        .body(Commons.getInputStream("CreateTransactionEventInput.json"))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post(testDataGeneratorApi())
        .then()
            .statusCode(200).extract().response();
    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 5);
  }

  /* Simple test case that creates 3 TransformationEvents */
  @Test
  void createTransformationEventTest() {
    Response response = given()
        .body(Commons.getInputStream("CreateTransformationEventInput.json"))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        .when()
        .post(testDataGeneratorApi())
        .then()
        .statusCode(200).extract().response();
    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 3);
  }

  /*
   Design Test Data for simple supply chain ObjectEvent(events - 1) -> AggregationEvent(events -
  */
  @Test
  void designTestDataEvents1Test() {
    Response response = executeRestAPI("DesignTestDataEvents1.json", testDataGeneratorApi());
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 2);

    assertEquals(1, getEventTypeCount(response.jsonPath(), EPCISEventTypes.OBJECT_EVENT.toString()));
    assertEquals(1, getEventTypeCount(response.jsonPath(), EPCISEventTypes.AGGREGATION_EVENT.toString()));
  }

  /*
   Design Test Data for simple supply chain TransactionEvent(events - 1) ->
   TransformationEvent(events - 1) -> AssociationEvent(events-2)
  */
  @Test
  void designTestDataEvents2Test() {
    Response response = executeRestAPI("DesignTestDataEvents2.json", testDataGeneratorApi());
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 4);

    assertEquals(1, getEventTypeCount(response.jsonPath(), EPCISEventTypes.TRANSACTION_EVENT.toString()));
    assertEquals(1, getEventTypeCount(response.jsonPath(), EPCISEventTypes.TRANSFORMATION_EVENT.toString()));
    assertEquals(2, getEventTypeCount(response.jsonPath(), EPCISEventTypes.ASSOCIATION_EVENT.toString()));
  }

  /* Design Test Data for supply chain ObjectEvent(events - 2) -> AggregationEvent(events-3) */
  @Test
  void designTestDataEvents3Test() {
    Response response = executeRestAPI("DesignTestDataEvents3.json", testDataGeneratorApi());
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 8);

    assertEquals(2, getEventTypeCount(response.jsonPath(), EPCISEventTypes.OBJECT_EVENT.toString()));
    assertEquals(6, getEventTypeCount(response.jsonPath(), EPCISEventTypes.AGGREGATION_EVENT.toString()));
  }

  /*
   Design Test Data for supply chain ObjectEvent(events-1) -> AggregationEvent(events-2) ->
   TransactionEvent(events - 3)
  */
  @Test
  void designTestDataEvents4Test() {
    Response response = executeRestAPI("DesignTestDataEvents4.json", testDataGeneratorApi());
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 9);

    assertEquals(1, getEventTypeCount(response.jsonPath(), EPCISEventTypes.OBJECT_EVENT.toString()));
    assertEquals(2, getEventTypeCount(response.jsonPath(), EPCISEventTypes.AGGREGATION_EVENT.toString()));
    assertEquals(6, getEventTypeCount(response.jsonPath(), EPCISEventTypes.TRANSACTION_EVENT.toString()));
  }

  /*
   Design Test data for supply chain with ObjectEvent(events-1) -> AggregationEvent(events-2) ->
   TransactionEvent (events-3)
  */
  @Test
  void designTestDataEvents5Test() {
    Response response = executeRestAPI("DesignTestDataEvents5.json", testDataGeneratorApi());
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 18);

    assertEquals(2, getEventTypeCount(response.jsonPath(), EPCISEventTypes.OBJECT_EVENT.toString()));
    assertEquals(4, getEventTypeCount(response.jsonPath(), EPCISEventTypes.AGGREGATION_EVENT.toString()));
    assertEquals(12, getEventTypeCount(response.jsonPath(), EPCISEventTypes.TRANSACTION_EVENT.toString()));
  }


  /*
   Design Test data for supply chain with ObjectEvent(events-3) -> AggregationEvent(events-2) ->
   TransactionEvent (events-3)
  */
  @Test
  void designTestDataEvents6Test() {
    Response response = executeRestAPI("DesignTestDataEvents6.json", testDataGeneratorApi());
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 11);

    assertEquals(5, getEventTypeCount(response.jsonPath(), EPCISEventTypes.OBJECT_EVENT.toString()));
    assertEquals(2, getEventTypeCount(response.jsonPath(), EPCISEventTypes.AGGREGATION_EVENT.toString()));
    assertEquals(4, getEventTypeCount(response.jsonPath(), EPCISEventTypes.TRANSACTION_EVENT.toString()));
  }

  /*
   Design Test data for supply chain with TransformationEvent(events-2) -> ObjectEvent(events-4)->
   AggregationEvent(events-8)
  */
  @Test
  void designTestDataEvents7Test() {
    Response response = executeRestAPI("DesignTestDataEvents7.json", testDataGeneratorApi());
    validateStatusCode(response);

    assertEquals(response.jsonPath().getList(EPCIS_BODY_EVENT_LIST).size(), 74);

    assertEquals(2, getEventTypeCount(response.jsonPath(), EPCISEventTypes.TRANSFORMATION_EVENT.toString()));
    assertEquals(8, getEventTypeCount(response.jsonPath(), EPCISEventTypes.OBJECT_EVENT.toString()));
    assertEquals(64, getEventTypeCount(response.jsonPath(), EPCISEventTypes.AGGREGATION_EVENT.toString()));
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
    return jsonPath.getList(EPCIS_BODY_EVENT_LIST, EPCISEvent.class)
        .stream()
        .filter(event -> event.getType().equals(eventType))
        .count();
  }
}
