package io.openepcis.testdata.api.exception;

import io.openepcis.model.rest.ProblemResponseBody;
import lombok.extern.log4j.Log4j2;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.core.Response;

@Log4j2
public class ExceptionMapper  {

    @ServerExceptionMapper
    public final RestResponse<ProblemResponseBody> handleQueryValidationException(final TestDataGeneratorException exception) {
        final var responseBody = new ProblemResponseBody();
        responseBody.setStatus(422);
        responseBody.setDetail(exception.getMessage());
        return RestResponse.status(Response.Status.fromStatusCode(422), responseBody);
    }
}
