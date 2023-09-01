package io.openepcis.testdata.api.resource.test;

import io.openepcis.testdata.api.test.AbstractTestDataGeneratorTest;
import io.openepcis.testdata.api.resource.TestDataGeneratorResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

import java.net.URL;

@QuarkusTest
public class TestDataGeneratorResourceTest extends AbstractTestDataGeneratorTest {

  @TestHTTPEndpoint(TestDataGeneratorResource.class)
  @TestHTTPResource
  URL url;

  @Override
  protected String url() {
    return url.toString();
  }

}
