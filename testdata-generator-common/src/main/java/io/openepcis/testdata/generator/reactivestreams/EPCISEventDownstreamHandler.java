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
package io.openepcis.testdata.generator.reactivestreams;

import io.openepcis.model.epcis.EPCISEvent;
import io.openepcis.testdata.generator.model.EventCreationModel;
import io.openepcis.testdata.generator.model.EventModelUtil;
import io.openepcis.testdata.generator.template.EPCISEventType;
import io.openepcis.testdata.generator.template.ReferencedIdentifier;
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

      // Check if the Child node event inherits multiple parent event ids from parent node event
      if (model.getTypeInfo().getReferencedIdentifier().stream()
          .anyMatch(
              ref -> ref.getInheritParentCount() != null && ref.getInheritParentCount() > 0)) {
        // Get all the ReferencedIdentifiers object information from Child Event Nodes which
        // inherits the Parent Identifiers from Parent Event Node
        final List<ReferencedIdentifier> inheritParentIdentifiers =
            model.getTypeInfo().getReferencedIdentifier().stream()
                .filter(
                    childConnector ->
                        childConnector.getInheritParentCount() > 0
                            && childConnector.getParentNodeId()
                                == parentTracker.getEventTypeInfo().getNodeId())
                .toList();

        // Loop over the list of Child Event Nodes ReferencedIdentifiers which is inheriting
        // Parent-Ids from Parent Event Node.
        inheritParentIdentifiers.forEach(
            inheritParentRefIdentifier -> {
              // Get the event information from associated with Parent Node-Id stored in
              // inheritParentTrackers
              List<EventIdentifierTracker> inheritedTrackers =
                  inheritParentTrackers.get(parentTracker.getEventTypeInfo().getNodeId());

              // If no information associated with Parent Node-Id found then create new ArrayList
              if (inheritedTrackers == null) {
                inheritedTrackers = new ArrayList<>();
              }

              // Store the created Parent event in the ArrayList
              inheritedTrackers.add(
                  new EventIdentifierTracker(
                      parentTracker.getEventTypeInfo(), parentTracker.getEvent()));

              // Add the information within the Map inheritParentTrackers along with the Parent
              // Node-Id
              inheritParentTrackers.put(
                  parentTracker.getEventTypeInfo().getNodeId(), inheritedTrackers);

              // If the number of events in inheritParentTrackers matches the count of Inherited
              // Parent count then create the child event
              if (inheritedTrackers.size() == inheritParentRefIdentifier.getInheritParentCount()) {
                final EPCISEvent event = model.create(inheritedTrackers);
                final var nextTracker = new EventIdentifierTracker(model.getTypeInfo(), event);
                upstreamHandlers.forEach(u -> u.next(event));
                if (!downstreamHandlers.isEmpty()) {
                  downstreamHandlers.forEach(h -> h.next(nextTracker));
                }
                inheritedTrackers.clear();
              }
            });
      } else {
        for (var i = 0; i < model.getTypeInfo().getEventCount(); i++) {
          final EPCISEvent event = model.create(upstreamTrackers);
          final var nextTracker = new EventIdentifierTracker(model.getTypeInfo(), event);
          upstreamHandlers.forEach(u -> u.next(event));
          if (!downstreamHandlers.isEmpty()) {
            downstreamHandlers.forEach(h -> h.next(nextTracker));
          }
        }
      }
      upstreamTrackers.clear();
    }
  }

  public void addDownstreamHandler(final EPCISEventDownstreamHandler handler) {
    downstreamHandlers.add(handler);
    handler.addUpstream(upstreamHandlers);
  }

  public void addUpstream(final EPCISEventUpstreamHandler upstream) {
    upstreamHandlers.add(upstream);
  }

  public void addUpstream(final List<EPCISEventUpstreamHandler> upstream) {
    upstreamHandlers.addAll(upstream);
  }
}
