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
package io.openepcis.testdata.tests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.EPCISEventGenerator;
import io.openepcis.testdata.generator.reactivestreams.EPCISEventPublisher;
import io.openepcis.testdata.generator.template.InputTemplate;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.flow.FlowPublisherVerification;
import java.util.concurrent.Flow.Publisher;

@Slf4j
public class EPCISEventPublisherVerificationTest extends FlowPublisherVerification<EPCISEvent> {

  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);

  public EPCISEventPublisherVerificationTest() {
    super(new TestEnvironment());
  }

  @Override
  public Publisher<EPCISEvent> createFlowPublisher(long l) {
    try {
      final InputTemplate inputTemplate =
          objectMapper.readValue(
              EPCISEventPublisherVerificationTest.class.getResourceAsStream(
                  "/singleObjectEvent.json"),
              InputTemplate.class);
      inputTemplate.getEvents().get(0).setEventCount((int) l);
      return new EPCISEventPublisher(EPCISEventGenerator.createModels(inputTemplate));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public Publisher<EPCISEvent> createFailedFlowPublisher() {
    return new EPCISEventPublisher(Collections.emptyList());
  }
}
