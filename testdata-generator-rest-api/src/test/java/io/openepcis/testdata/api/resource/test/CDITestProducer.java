package io.openepcis.testdata.api.resource.test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openepcis.convert.VersionTransformer;
import jakarta.enterprise.inject.Produces;
import jakarta.xml.bind.JAXBContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
public class CDITestProducer {

    private final ObjectMapper objectMapper;

    private final ManagedExecutor managedExecutor;
    @Produces
    public JsonFactory jsonFactory() {
     return new JsonFactory().setCodec(objectMapper);
    }

    @Produces
    public VersionTransformer createVersionTransformer() throws Exception {
        final ResourceBundle bundle = ResourceBundle.getBundle("jakarta.xml.bind.Messages");
        if (bundle == null) {
            log.error("jakarta message bundle missing");
        } else {
            log.info("jakarta message bundle found");
        }
        final JAXBContext jaxbContext =
                JAXBContext.newInstance(
                        "io.openepcis.model.epcis", Thread.currentThread().getContextClassLoader());
        return new VersionTransformer(managedExecutor, jaxbContext);
    }
}
