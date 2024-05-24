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
package io.openepcis.testdata.generator.model;

import io.openepcis.model.epcis.ObjectEvent;
import io.openepcis.model.epcis.PersistentDisposition;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.format.BizTransactionsFormatter;
import io.openepcis.testdata.generator.format.DestinationFormatter;
import io.openepcis.testdata.generator.format.PersistentDispositionFormatter;
import io.openepcis.testdata.generator.format.SourceFormatter;
import io.openepcis.testdata.generator.reactivestreams.EventIdentifierTracker;
import io.openepcis.testdata.generator.template.Identifier;
import io.openepcis.testdata.generator.template.ObjectEventType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectEventCreationModel
    extends AbstractEventCreationModel<ObjectEventType, ObjectEvent> {

  public ObjectEventCreationModel(
      final ObjectEventType typeInfo, final List<Identifier> identifiers) {
    super(typeInfo, identifiers);
  }

  @Override
  public ObjectEvent create(final List<EventIdentifierTracker> parentTracker) {
    var epcisEvent = new ObjectEvent();
    super.configure(epcisEvent);
    configureCommons(epcisEvent);
    configureIdentifiers(epcisEvent, parentTracker);

    // Set the EventId to either UUID or HashID depending on the user provided parameter
    super.setupEventHashId(epcisEvent);
    return epcisEvent;
  }

  // Private method which will add the common elements to Object event
  private void configureCommons(final ObjectEvent e) {

    final IdentifierVocabularyType syntax = typeInfo.getLocationPartyIdentifierSyntax();

    // Add action
    e.setAction(typeInfo.getAction());

    // Set Persistent Disposition for ObjectEvent
    if (typeInfo.getPersistentDisposition() != null) {
      var pd = new PersistentDisposition();
      if (typeInfo.getPersistentDisposition().getSet() != null
              && !typeInfo.getPersistentDisposition().getSet().isEmpty()) {
        pd.setSet(
                PersistentDispositionFormatter.format(typeInfo.getPersistentDisposition().getSet()));
      }

      if (typeInfo.getPersistentDisposition().getUnset() != null
              && !typeInfo.getPersistentDisposition().getUnset().isEmpty()) {
        pd.setUnset(
                PersistentDispositionFormatter.format(typeInfo.getPersistentDisposition().getUnset()));
      }
      e.setPersistentDisposition(pd);
    }

    // Add source list
    if (typeInfo.getSources() != null && !typeInfo.getSources().isEmpty()) {
      e.setSourceList(
          typeInfo.getSources().stream().map(src -> SourceFormatter.format(syntax, src, typeInfo.getDlURL())).toList());
    }

    // Add Destination list
    if (typeInfo.getDestinations() != null && !typeInfo.getDestinations().isEmpty()) {
      e.setDestinationList(
          typeInfo.getDestinations().stream()
              .map(dst -> DestinationFormatter.format(syntax, dst, typeInfo.getDlURL()))
              .toList());
    }

    // Add the BizTransaction
    if (typeInfo.getBizTransactions() != null && !typeInfo.getBizTransactions().isEmpty()) {
      e.setBizTransactionList(
          typeInfo.getBizTransactions().stream().map(BizTransactionsFormatter::format).toList());
    }

    // Add the ILMD
    if (typeInfo.getIlmd() != null && !typeInfo.getIlmd().isEmpty()) {
      final Map<String, Object> ilmdMap =
          typeInfo.getIlmd().stream()
                  .flatMap(root -> {
                    if (CollectionUtils.isNotEmpty(root.getChildren())) {
                      // Stream over children if present
                      return root.getChildren().stream().flatMap(c -> c.toMap().entrySet().stream());
                    } else if (root.getRawJsonld() instanceof Map) {
                      // Stream over rawJsonld if it's a Map
                      return root.toMap().entrySet().stream();
                    }
                    // Return an empty stream if neither are present
                    return Stream.empty();
                  })
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (r1, r2) -> r1));
      e.setIlmdXml(ilmdMap.isEmpty() ? null : ilmdMap);
    }
  }

  // Private method which will generate Instance & Class identifiers if available and add it to the
  // ObjectEvent which will be created by OpenEPCIS
  private void configureIdentifiers(
      final ObjectEvent e, final List<EventIdentifierTracker> parentTracker) {
    // Check if the user has provided values for the Instance identifiers or if the event is
    // imported then check if the event consist of existing generated Instance/Quantity identifiers
    if ((typeInfo.getReferencedIdentifier() != null
            && !typeInfo.getReferencedIdentifier().isEmpty())
        || !typeInfo.getEpcList().isEmpty()
        || !typeInfo.getQuantityList().isEmpty()) {

      // A list which will store all the instance identifiers generated or inherited from the
      // parents by calling referencedEpcsIdentifierGenerator method in IdentifiersUtil
      final List<String> epcList = super.referencedEpcsIdentifierGenerator(parentTracker, true);

      // Add all the instance identifiers created above to the ObjectEvent which will be created by
      // OpenEPCIS
      if (!epcList.isEmpty()) {
        e.setEpcList(epcList);
      }

      // A list which will store all the class identifiers generated or inherited from the parents
      // by calling referencedIdentifierGenerator method in IdentifiersUtil
      final List<QuantityList> quantityList =
          super.referencedClassIdentifierGenerator(parentTracker);

      // Add the created EPC to the event
      if (!quantityList.isEmpty()) {
        e.setQuantityList(quantityList);
      }
    }
  }
}
