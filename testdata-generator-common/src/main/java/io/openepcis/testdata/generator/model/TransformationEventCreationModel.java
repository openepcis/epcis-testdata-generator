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

import io.openepcis.model.epcis.PersistentDisposition;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.model.epcis.TransformationEvent;
import io.openepcis.testdata.generator.format.PersistentDispositionFormatter;
import io.openepcis.testdata.generator.reactivestreams.EventIdentifierTracker;
import io.openepcis.testdata.generator.template.Identifier;
import io.openepcis.testdata.generator.template.ReferencedIdentifier;
import io.openepcis.testdata.generator.template.TransformationEventType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformationEventCreationModel
    extends AbstractEventCreationModel<TransformationEventType, TransformationEvent> {

  public TransformationEventCreationModel(
      final TransformationEventType typeInfo, final List<Identifier> identifiers) {
    super(typeInfo, identifiers);
  }

  @Override
  public TransformationEvent create(final List<EventIdentifierTracker> parentTracker) {
    var epcisEvent = new TransformationEvent();
    super.configure(epcisEvent);
    configureCommons(epcisEvent);
    configureInputIdentifiers(epcisEvent, parentTracker);
    configureOutputEpcs(epcisEvent);
    configureOutputQuantities(epcisEvent);

    // Set the EventId to either UUID or HashID depending on the user provided parameter
    super.setupEventHashId(epcisEvent);
    return epcisEvent;
  }

  private void configureCommons(final TransformationEvent e) {

    // Set Persistent Disposition for TransformationEvent
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

    // If TransformationID is populated then add the value
    if (typeInfo.getTransformationID() != null) {
      e.setTransformationID(typeInfo.getTransformationID());
    }

    // If ILMD is populated then add the value
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
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      e.setIlmdXml(ilmdMap.isEmpty() ? null : ilmdMap);
    }
  }

  // Private method which will generate Instance/InputEPCs & Class/InputQuantity identifiers if
  // available and add it to the Transformation which will be created by OpenEPCIS
  private void configureInputIdentifiers(
      final TransformationEvent e, final List<EventIdentifierTracker> parentTracker) {
    // Check if the user is provided values for the Input Class identifiers or if the event is
    // Imported event then check of generated Input instance/class identifiers.
    if ((typeInfo.getReferencedIdentifier() != null
            && !typeInfo.getReferencedIdentifier().isEmpty())
        || !typeInfo.getEpcList().isEmpty()
        || !typeInfo.getQuantityList().isEmpty()) {

      // A list which will store all the instance identifiers generated or inherited from the
      // parents by calling referencedEpcsIdentifierGenerator method in IdentifiersUtil
      final List<String> inputEpcList = super.referencedEpcsIdentifierGenerator(parentTracker, true);

      // Add the created EPC to the event
      if (!inputEpcList.isEmpty()) {
        e.setInputEPCList(inputEpcList);
      }

      // A list which will store all the class identifiers generated or inherited from the parents
      // by calling referencedIdentifierGenerator method in IdentifiersUtil
      final List<QuantityList> inputQuantityList =
          super.referencedClassIdentifierGenerator(parentTracker);

      // Add the created EPC to the event
      if (!inputQuantityList.isEmpty()) {
        e.setInputQuantityList(inputQuantityList);
      }
    }
  }

  // Private method for creation and adding Output Instance Identifiers for TransformationEvent
  private void configureOutputEpcs(final TransformationEvent e) {
    // Create a List to store all the values associated with the event
    final List<String> outputEpcList = new ArrayList<>();

    // Check if the user is provided values for the Output Instance identifiers
    if (typeInfo.getOutputReferencedIdentifier() != null
        && !typeInfo.getOutputReferencedIdentifier().isEmpty()) {
      // Loop through all the referenceIdentifiers and get the matching identifiers based on
      // IdentifiersList and Reference Identifiers
      for (ReferencedIdentifier outputEpc : typeInfo.getOutputReferencedIdentifier()) {

        // If the Node is connected to the Identifiers' node then create the identifiers
        if (outputEpc.getIdentifierId() != 0 && outputEpc.getEpcCount() > 0) {

          // Get the matching identifiers from the IdentifiersList based on the Identifiers present
          // in the ReferencedIdentifier
          this.identifiers.stream()
                  .filter(identifier -> identifier.getIdentifierId() == outputEpc.getIdentifierId())
                  .findFirst()
                  .ifPresent(
                          m ->
                                  outputEpcList.addAll(
                                          m.getInstanceData()
                                                  .format(m.getObjectIdentifierSyntax(), outputEpc.getEpcCount(), m.getDlURL(), typeInfo.getSeed())));
        }
      }
    }

    // if the event is Imported event then check for generated Output instance/EPC identifiers if
    // present then add it to the list.
    if (!typeInfo.getOutputEPCList().isEmpty()) {
      Optional.ofNullable(typeInfo.getOutputEPCList()).ifPresent(outputEpcList::addAll);
    }

    // Add the created EPC to the event
    if (!outputEpcList.isEmpty()) {
      e.setOutputEPCList(outputEpcList);
    }
  }

  // Private method for creating and adding Output Class identifiers for TransformationEvent
  private void configureOutputQuantities(final TransformationEvent e) {
    // Create a List to store all the values associated Instance Identifiers
    final List<QuantityList> outputQuantityList = new ArrayList<>();

    // Check if the user is provided values for the Output EPC identifiers
    if (typeInfo.getOutputReferencedIdentifier() != null
        && !typeInfo.getOutputReferencedIdentifier().isEmpty()) {

      // Loop through all the referenceIdentifiers and get the matching identifiers based on
      // IdentifiersList and Reference Identifiers
      for (ReferencedIdentifier outputQuantity : typeInfo.getOutputReferencedIdentifier()) {

        // For output quantity the node has to be connected to the identifiers node based on it
        // create identifiers node
        if (outputQuantity.getIdentifierId() != 0 && outputQuantity.getClassCount() > 0) {

          // Get the matching identifiers from the IdentifiersList based on the Identifiers present
          // in the ReferencedIdentifier and append all Class Identifiers values onto the
          // Instance-Identifiers List
          this.identifiers.stream()
                  .filter(
                          identifier -> identifier.getIdentifierId() == outputQuantity.getIdentifierId())
                  .findFirst()
                  .ifPresent(
                          oq ->
                                  outputQuantityList.addAll(
                                          oq.getClassData()
                                                  .format(
                                                          oq.getObjectIdentifierSyntax(),
                                                          outputQuantity.getClassCount(),
                                                          outputQuantity.getQuantity(), oq.getDlURL(), typeInfo.getSeed())));
        }
      }
    }

    // if the event is Imported event then check for generated Output class/quantity identifiers if
    // present then add it to the list.
    if (!typeInfo.getOutputQuantityList().isEmpty()) {
      Optional.ofNullable(typeInfo.getOutputQuantityList()).ifPresent(outputQuantityList::addAll);
    }

    // Add the created EPC to the event
    if (!outputQuantityList.isEmpty()) {
      e.setOutputQuantityList(outputQuantityList);
    }
  }
}
