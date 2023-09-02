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
package io.openepcis.testdata.generator.reactivestreams;

import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.model.EventCreationModel;
import io.openepcis.testdata.generator.model.EventModelUtil;
import io.openepcis.testdata.generator.template.EPCISEventType;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EPCISEventDownstreamHandler {

  protected final EventCreationModel<? extends EPCISEventType, ? extends EPCISEvent> model;

  protected List<EPCISEventDownstreamHandler> downstreamHandlers = new ArrayList<>();

  protected List<EPCISEventUpstreamHandler> upstreamHandlers = new ArrayList<>();

  protected List<EventIdentifierTracker> upstreamTrackers = new ArrayList<>();

  protected Map<Integer, List<EventIdentifierTracker>> inheritParentTrackers = new HashMap<>();

  private final boolean rootHandler;

  private AtomicInteger rootEventCount;

  public EPCISEventDownstreamHandler(
      final EventCreationModel<? extends EPCISEventType, ? extends EPCISEvent> model) {
    this.model = model;
    rootHandler = EventModelUtil.isRootEvent(model.getTypeInfo());
    if (rootHandler) {
      rootEventCount = new AtomicInteger();
    }
  }

  public void next() {
    if (!rootHandler) {
      throw new UnsupportedOperationException("can only be called on root handler");
    }
    if (rootEventCount.get() < model.getTypeInfo().getEventCount()) {
      rootEventCount.incrementAndGet();
      final EPCISEvent event = model.create(upstreamTrackers);
      final var nextTracker = new EventIdentifierTracker(model.getTypeInfo(), event);
      upstreamHandlers.forEach(u -> u.next(event));
      if (!downstreamHandlers.isEmpty()) {
        downstreamHandlers.forEach(h -> h.next(nextTracker));
      }
    }
  }

  public void next(EventIdentifierTracker parentTracker) {
    upstreamTrackers.add(parentTracker);

    // run only if all upstream nodes have provided a tracker
    if (upstreamTrackers.size() == upstreamHandlers.size()) {
        for (var i = 0; i < model.getTypeInfo().getEventCount(); i++) {
          final EPCISEvent event = model.create(upstreamTrackers);
          final var nextTracker = new EventIdentifierTracker(model.getTypeInfo(), event);
          upstreamHandlers.forEach(u -> u.next(event));
          if (!downstreamHandlers.isEmpty()) {
            downstreamHandlers.forEach(h -> h.next(nextTracker));
          }
        }
      upstreamTrackers.clear();
    }
  }

  public void addDownstreamHandler(final EPCISEventDownstreamHandler handler) {
      downstreamHandlers.add(handler);

      //Adding unique upstream handles to avoid duplication and missing events
      final HashSet<EPCISEventUpstreamHandler> uniqueUpstreamHandlers = new HashSet<>(upstreamHandlers.stream().toList());
      handler.addUpstream(uniqueUpstreamHandlers.stream().toList());
  }

  public void addUpstream(final EPCISEventUpstreamHandler upstream) {
      upstreamHandlers.add(upstream);
  }

  public void addUpstream(final List<EPCISEventUpstreamHandler> upstream) {
    upstream.forEach(this::addUpstream);
  }
}
