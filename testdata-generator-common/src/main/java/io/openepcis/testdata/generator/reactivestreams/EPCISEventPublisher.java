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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class EPCISEventPublisher implements Publisher<EPCISEvent>, EPCISEventUpstreamHandler {

  private final List<EPCISEventDownstreamHandler> downstreamRootHandlers;

  private final List<EPCISEventSubscription> subscriptions = new ArrayList<>();

  private class EPCISEventSubscription implements Subscription {

    private final ConcurrentLinkedQueue<EPCISEvent> eventQueue = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    private final AtomicLong demand = new AtomicLong();

    private final AtomicReference<Subscriber<? super EPCISEvent>> subscriber;

    private EPCISEventSubscription(Subscriber<? super EPCISEvent> subscriber) {
      if (subscriber == null) {
        throw new NullPointerException("subscriber must not be null");
      }
      this.subscriber = new AtomicReference<>(subscriber);
    }

    @Override
    public void request(long l) {
      if (l <= 0 && !terminate()) {
        subscriber.get().onError(new IllegalArgumentException("negative subscription request"));
        return;
      }

      if (demand.get() > 0) {
        demand.getAndAdd(l);
        return;
      }
      demand.getAndAdd(l);

      try {
        for (; demand.get() > 0 && isTerminated(); demand.decrementAndGet()) {
          if (eventQueue.isEmpty()) {
            nextEvents();
          }
          final EPCISEvent e = eventQueue.poll();
          if (e != null) {
            subscriber.get().onNext(e);
          } else if (isTerminated()) {
            subscriber.get().onComplete();
            return;
          }
        }
      } catch (Exception ex) {
        if (!terminate()) {
          subscriber.get().onError(ex);
        }
      }
    }

    @Override
    public void cancel() {
      terminate();
      eventQueue.clear();
      subscriber.set(null);
    }

    private boolean terminate() {
      return isTerminated.getAndSet(true);
    }

    private boolean isTerminated() {
      return !isTerminated.get();
    }

    void addEvent(EPCISEvent event) {
      if (!eventQueue.contains(event)) {
        eventQueue.add(event);
      }
    }

    private void nextEvents() {
      synchronized (downstreamRootHandlers) {
        downstreamRootHandlers.forEach(EPCISEventDownstreamHandler::next);
      }
    }
  }

  public EPCISEventPublisher(final List<? extends EventCreationModel<?, ?>> models) {
    var handlers = models.stream().map(EventCreationModel::toEPCISDownstreamHandler).toList();
    this.downstreamRootHandlers =
        handlers.stream().filter(h -> EventModelUtil.isRootEvent(h.model.getTypeInfo())).toList();
    this.downstreamRootHandlers.forEach(h -> h.addUpstream(this));
    new ArrayList<>(models).forEach(e -> attach(e, models));
  }

  @Override
  public void subscribe(final Subscriber<? super EPCISEvent> subscriber) {
    final var subscription = new EPCISEventSubscription(subscriber);
    subscriptions.add(subscription);
    subscriber.onSubscribe(subscription);
    if (downstreamRootHandlers.isEmpty()) {
      subscriber.onError(new IllegalArgumentException("downstream handlers must not be empty"));
    }
  }

  @Override
  public void next(final EPCISEvent epcisEvent) {
    subscriptions.forEach(s -> s.addEvent(epcisEvent));
  }

  private void attach(
      final EventCreationModel<?, ?> e, final List<? extends EventCreationModel<?, ?>> models) {
    models.forEach(
        m -> {
          if (m.getTypeInfo().getReferencedIdentifier().stream()
              .anyMatch(i -> i.getParentNodeId() == e.getTypeInfo().getNodeId())) {
            e.toEPCISDownstreamHandler().addDownstreamHandler(m.toEPCISDownstreamHandler());
          }
        });
  }
}
