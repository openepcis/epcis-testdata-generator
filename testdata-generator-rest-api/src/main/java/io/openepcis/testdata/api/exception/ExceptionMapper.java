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
package io.openepcis.testdata.api.exception;

import io.openepcis.core.exception.ResourceNotFoundException;
import io.openepcis.model.epcis.exception.NotAcceptedException;
import io.openepcis.model.rest.ProblemResponseBody;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Slf4j
public class ExceptionMapper {

  @ServerExceptionMapper
  public final RestResponse<ProblemResponseBody> mapException(
      final TestDataGeneratorException exception) {
    log.info(exception.getMessage());
    final ProblemResponseBody responseBody = new ProblemResponseBody();
    responseBody.setType("Exception occurred during creation of EPCIS Test Data events");
    responseBody.title("Bad request to Test Data Generator");
    responseBody.setStatus(RestResponse.StatusCode.BAD_REQUEST);
    responseBody.setDetail(exception.getMessage());
    return RestResponse.status(RestResponse.Status.BAD_REQUEST, responseBody);
  }

  @ServerExceptionMapper
  public final RestResponse<ProblemResponseBody> mapException(final SecurityException exception) {
    log.info(exception.getMessage());
    final ProblemResponseBody responseBody = new ProblemResponseBody();
    responseBody.setType("Exception occurred during creation of EPCIS Test Data events");
    responseBody.title("Access denied for Test Data Generator");
    responseBody.setStatus(RestResponse.StatusCode.UNAUTHORIZED);
    responseBody.setDetail(exception.getMessage());
    return RestResponse.status(RestResponse.Status.UNAUTHORIZED, responseBody);
  }

  @ServerExceptionMapper
  public final RestResponse<ProblemResponseBody> mapException(
      final ResourceNotFoundException exception) {
    log.info(exception.getMessage());
    final ProblemResponseBody responseBody = new ProblemResponseBody();
    responseBody.setType("Exception occurred during creation of EPCIS Test Data events");
    responseBody.title("Resource not found for Test Data Generator");
    responseBody.setStatus(RestResponse.StatusCode.NOT_FOUND);
    responseBody.setDetail(exception.getMessage());
    return RestResponse.status(RestResponse.Status.NOT_FOUND, responseBody);
  }

  @ServerExceptionMapper
  public final RestResponse<ProblemResponseBody> mapException(
      final NotAcceptedException exception) {
    log.info(exception.getMessage());
    final ProblemResponseBody responseBody = new ProblemResponseBody();
    responseBody.setType("Exception occurred during creation of EPCIS Test Data events");
    responseBody.title("Error in data format for Test Data Generator");
    responseBody.setStatus(RestResponse.StatusCode.NOT_ACCEPTABLE);
    responseBody.setDetail(exception.getMessage());
    return RestResponse.status(RestResponse.Status.NOT_ACCEPTABLE, responseBody);
  }

  @ServerExceptionMapper
  public final RestResponse<ProblemResponseBody> mapException(final Exception exception) {
    log.error(exception.getMessage(), exception);
    final ProblemResponseBody responseBody = new ProblemResponseBody();
    responseBody.setType("Exception occurred during creation of EPCIS Test Data events");
    responseBody.setTitle("Internal server error for Test Data Generator");
    responseBody.setStatus(RestResponse.StatusCode.INTERNAL_SERVER_ERROR);
    responseBody.setDetail(exception.getMessage());
    return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, responseBody);
  }

  @ServerExceptionMapper
  public final RestResponse<ProblemResponseBody> mapException(
      final WebApplicationException exception) {
    final ProblemResponseBody responseBody = new ProblemResponseBody();
    responseBody.setType(exception.getClass().getSimpleName());
    responseBody.setTitle(exception.getResponse().getStatusInfo().getReasonPhrase());
    responseBody.setStatus(exception.getResponse().getStatus());
    responseBody.setDetail(exception.getMessage());
    return RestResponse.status(exception.getResponse().getStatusInfo(), responseBody);
  }
}
