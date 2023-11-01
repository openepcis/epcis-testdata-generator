/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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
package io.openepcis.testdata.generator;

import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.model.EventCreationModel;
import io.openepcis.testdata.generator.model.EventModelUtil;
import io.openepcis.testdata.generator.reactivestreams.EPCISEventPublisher;
import io.openepcis.testdata.generator.template.EPCISEventType;
import io.openepcis.testdata.generator.template.InputTemplate;
import io.smallrye.mutiny.Multi;
import java.util.List;
import java.util.Optional;

public class EPCISEventGenerator {

  private EPCISEventGenerator() {}

  public static List<EventCreationModel<EPCISEventType, EPCISEvent>> createModels(
      final InputTemplate inputTemplate) {
    try {
      return inputTemplate.getEvents().stream()
          .map(
              e ->
                  EventModelUtil.createModel(
                      e, EventModelUtil.usedIdentifiers(e, inputTemplate.getIdentifiers())))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .toList();
    } catch (Exception e) {
      throw new TestDataGeneratorException(
          "Exception occurred during the creation of model for input : " + e.getMessage(), e);
    }
  }

  public static Multi<EPCISEvent> generate(final InputTemplate inputTemplate) {
    try {
      return Multi.createFrom()
          .publisher(new EPCISEventPublisher(EPCISEventGenerator.createModels(inputTemplate)));
    } catch (Exception e) {
      throw new TestDataGeneratorException(
          "Exception occurred during the generation of EPCIS events : " + e.getMessage(), e);
    }
  }
}
