package io.openepcis.testdata.api.util;

import io.openepcis.convert.VersionTransformer;
import io.quarkus.runtime.Startup;
import jakarta.xml.bind.JAXBContext;
import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;

@ApplicationScoped
@Startup
@Slf4j
public class VersionTransformerProducer {

  @Inject ManagedExecutor managedExecutor;

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
