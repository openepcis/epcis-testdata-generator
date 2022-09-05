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
package io.openepcis.testdata.generator.model;

import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.reactivestreams.EPCISEventDownstreamHandler;
import io.openepcis.testdata.generator.reactivestreams.EventIdentifierTracker;
import io.openepcis.testdata.generator.template.EPCISEventType;
import java.util.List;

public interface EventCreationModel<T extends EPCISEventType, E extends EPCISEvent> {

  E create(List<EventIdentifierTracker> parentTrackers);

  T getTypeInfo();

  EPCISEventDownstreamHandler toEPCISDownstreamHandler();
}
