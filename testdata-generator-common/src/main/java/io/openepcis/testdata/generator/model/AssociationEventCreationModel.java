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

import io.openepcis.model.epcis.AssociationEvent;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.format.BizTransactionsFormatter;
import io.openepcis.testdata.generator.format.DestinationFormatter;
import io.openepcis.testdata.generator.format.SourceFormatter;
import io.openepcis.testdata.generator.reactivestreams.EventIdentifierTracker;
import io.openepcis.testdata.generator.template.AssociationEventType;
import io.openepcis.testdata.generator.template.Identifier;
import java.util.List;

public class AssociationEventCreationModel
    extends AbstractEventCreationModel<AssociationEventType, AssociationEvent> {

  private Identifier matchingParentId = null;

  public AssociationEventCreationModel(
      final AssociationEventType typeInfo, final List<Identifier> identifiers) {
    super(typeInfo, identifiers);

    // Check if user has provided values for the Parent Identifiers
    if (typeInfo.getParentReferencedIdentifier() != null
        && typeInfo.getParentReferencedIdentifier().getIdentifierId() > 0) {
      // Find the matching Identifiers info based Parent identifier id
      matchingParentId =
          identifiers.stream()
              .filter(
                  identifier ->
                      identifier.getIdentifierId()
                          == typeInfo.getParentReferencedIdentifier().getIdentifierId())
              .findFirst()
              .orElse(null);
    }
  }

  @Override
  public AssociationEvent create(final List<EventIdentifierTracker> parentTracker) {
    var epcisEvent = new AssociationEvent();
    super.configure(epcisEvent);
    configureCommons(epcisEvent);
    configureParent(epcisEvent, parentTracker);
    configureIdentifier(epcisEvent, parentTracker);

    // Set the EventId to either UUID or HashID depending on the user provided parameter
    super.setupEventHashId(epcisEvent);
    return epcisEvent;
  }

  // Private method which will add the Parent Identifiers for each AssociationEvent
  private void configureParent(final AssociationEvent e, final List<EventIdentifierTracker> parentTracker) {
    // If user has provided values for parentReferencedIdentifier then generated and add Parent
    // identifier based on it.
    if (matchingParentId != null && matchingParentId.getParentData() != null) {
      // Append parent identifier value to the event
      e.setParentID(
          matchingParentId
              .getParentData()
              .format(matchingParentId.getObjectIdentifierSyntax(), 1, matchingParentId.getDlURL(), typeInfo.getSeed())
              .get(0));
    } else if (typeInfo.getParentIdentifier() != null
        && typeInfo.getParentIdentifier().equals("")) {
      // If user is importing the existing event and if the existing event has parent identifier
      // then include it
      e.setParentID(typeInfo.getParentIdentifier());
    }else if(typeInfo.getReferencedIdentifier() != null && !typeInfo.getReferencedIdentifier().isEmpty()){
      final List<String> parentID = super.referencedParentIdentifier(parentTracker);
      if(!parentID.isEmpty()){
        e.setParentID(parentID.get(0));
      }
    }
  }

  // Private method which will add the common elements to AssociationEvent
  private void configureCommons(final AssociationEvent e) {

    final IdentifierVocabularyType syntax = typeInfo.getLocationPartyIdentifierSyntax();

    // Add action value
    e.setAction(typeInfo.getAction());

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
  }

  // Private method which will generate Instance/ChildEPCs & Class/ChildQuantities identifiers if
  // available and add it to the AggregationEvent which will be created by OpenEPCIS
  private void configureIdentifier(
      final AssociationEvent e, final List<EventIdentifierTracker> parentTracker) {
    // Check if the user has provided values for the Instance identifiers or if the event is
    // imported then check if the event consist of existing generated Instance/Quantity identifiers
    if ((typeInfo.getReferencedIdentifier() != null
            && !typeInfo.getReferencedIdentifier().isEmpty())
        || !typeInfo.getEpcList().isEmpty()
        || !typeInfo.getQuantityList().isEmpty()) {

      // A list which will store all the instance identifiers generated or inherited from the
      // parents by calling referencedEpcsIdentifierGenerator method in IdentifiersUtil
      final List<String> childEpcList = super.referencedEpcsIdentifierGenerator(parentTracker, false);

      // Add the created EPC to the event
      if (!childEpcList.isEmpty()) {
        e.setChildEPCs(childEpcList);
      }

      // A list which will store all the class identifiers generated or inherited from the parents
      // by calling referencedIdentifierGenerator method in IdentifiersUtil
      final List<QuantityList> childQuantityList =
          super.referencedClassIdentifierGenerator(parentTracker);

      // Add the created EPC to the event
      if (!childQuantityList.isEmpty()) {
        e.setChildQuantityList(childQuantityList);
      }
    }
  }
}
