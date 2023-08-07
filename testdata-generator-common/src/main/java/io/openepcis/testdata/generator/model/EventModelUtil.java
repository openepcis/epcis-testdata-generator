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

import io.openepcis.model.epcis.*;
import io.openepcis.testdata.generator.reactivestreams.*;
import io.openepcis.testdata.generator.template.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EventModelUtil {

  private EventModelUtil() {}

  public static Optional<EventCreationModel<EPCISEventType, EPCISEvent>> createModel(
      final EPCISEventType epcisEventType, final List<Identifier> identifiers) {
    if (epcisEventType instanceof ObjectEventType objectEventType) {
      return createEventCreationModel(new ObjectEventCreationModel(objectEventType, identifiers));
    } else if (epcisEventType instanceof AggregationEventType aggregationEventType) {
      return createEventCreationModel(
          new AggregationEventCreationModel(aggregationEventType, identifiers));
    } else if (epcisEventType instanceof TransactionEventType transactionEventType) {
      return createEventCreationModel(
          new TransactionEventCreationModel(transactionEventType, identifiers));
    } else if (epcisEventType instanceof TransformationEventType transformationEventType) {
      return createEventCreationModel(
          new TransformationEventCreationModel(transformationEventType, identifiers));
    } else if (epcisEventType instanceof AssociationEventType associationEventType) {
      return createEventCreationModel(
          new AssociationEventCreationModel(associationEventType, identifiers));
    }
    return Optional.empty();
  }

  // Method used to create the EventModel for each event in the input template
  private static Optional<EventCreationModel<EPCISEventType, EPCISEvent>> createEventCreationModel(
      final EventCreationModel<? extends EPCISEventType, ? extends EPCISEvent> parentModel) {
    final EventCreationModel<EPCISEventType, EPCISEvent> model =
        new EventCreationModel<>() {
          @Override
          public EPCISEvent create(List<EventIdentifierTracker> parentTrackers) {
            return parentModel.create(parentTrackers);
          }

          @Override
          public EPCISEventType getTypeInfo() {
            return parentModel.getTypeInfo();
          }

          @Override
          public EPCISEventDownstreamHandler toEPCISDownstreamHandler() {
            return parentModel.toEPCISDownstreamHandler();
          }
        };
    return Optional.of(model);
  }

  // check for root event info which doesn't have any children
  public static boolean isRootEvent(final EPCISEventType e) {
    return e.getReferencedIdentifier().stream()
        .noneMatch(p -> p.getParentNodeId() > 0 && p.getIdentifierId() == 0);
  }

  // only return identifiers used by this event info
  public static List<Identifier> usedIdentifiers(
      final EPCISEventType e, final List<Identifier> identifiers) {
    List<Identifier> eventSpecificIdentifiers = new ArrayList<>();

    // Add the identifiers related to Instance/Class identifiers
    eventSpecificIdentifiers.addAll(
        identifiers.stream()
            .filter(
                i ->
                    e.getReferencedIdentifier().stream()
                        .anyMatch(r -> r.getIdentifierId() == i.getIdentifierId()))
            .toList());

    // Add the identifiers related to parent identifiers
    eventSpecificIdentifiers.addAll(
        identifiers.stream()
            .filter(i -> e.getParentReferencedIdentifier().getIdentifierId() == i.getIdentifierId())
            .toList());

    // Add the identifiers related to OutputEPC and OutputQuantity for TransformationEvent
    eventSpecificIdentifiers.addAll(
        identifiers.stream()
            .filter(
                i ->
                    e.getOutputReferencedIdentifier().stream()
                        .anyMatch(r -> r.getIdentifierId() == i.getIdentifierId()))
            .toList());

    return eventSpecificIdentifiers;
  }

  // Public method which will be invoked during the Instance Identifier creation if the Instance
  // Identifiers need to be taken from the Parent event node.
  public static List<String> instanceIdentifiers(
      final EventIdentifierTracker parentTracker, final int epcCount) {
    return segregateInstanceIdentifiers(parentTracker, epcCount);
  }

  // Public method which will be invoked during the Instance identifiers creation if child node
  // inherits parent identifiers
  public static List<String> parentIdentifiers(
      final EventIdentifierTracker parentTracker, final int inheritParentCount) {
    return segregateParentIdentifiers(parentTracker, inheritParentCount);
  }

  // Public method which will be invoked during the Class Identifier creation if the Class
  // Identifiers need to be inherited from the Parent event node
  public static List<QuantityList> classIdentifiers(
      final EventIdentifierTracker parentTracker, final int quantityCount, final Float quantity) {
    return segregateClassIdentifiers(parentTracker, quantityCount, quantity);
  }

  // Private method which will segregate the Instance identifier from the Parent Event based on the
  // type of the event
  private static List<String> segregateInstanceIdentifiers(
      final EventIdentifierTracker parentTracker, final int epcCount) {

    // List to store all the instance identifiers from parent
    List<String> instanceIdentifiersList = new ArrayList<>();

    // Based on the Event Type add the respective Instance Identifiers into the
    // instanceIdentifiersList
    if (parentTracker.getEvent() instanceof ObjectEvent objectEvent
        && objectEvent.getEpcList() != null
        && !objectEvent.getEpcList().isEmpty()) {
      // For ObjectEvent add the EPCList
      instanceIdentifiersList.addAll(objectEvent.getEpcList());
    } else if (parentTracker.getEvent() instanceof AggregationEvent aggregationEvent
        && aggregationEvent.getChildEPCs() != null
        && !aggregationEvent.getChildEPCs().isEmpty()) {
      // For AggregationEvent add the ChildEPCs
      instanceIdentifiersList.addAll(aggregationEvent.getChildEPCs());

    } else if (parentTracker.getEvent() instanceof TransactionEvent transactionEvent
        && transactionEvent.getEpcList() != null
        && !transactionEvent.getEpcList().isEmpty()) {
      // For TransactionEvent add the EPCList
      instanceIdentifiersList.addAll(transactionEvent.getEpcList());

    } else if (parentTracker.getEvent() instanceof TransformationEvent transformationEvent
        && transformationEvent.getOutputEPCList() != null
        && !transformationEvent.getOutputEPCList().isEmpty()) {
      // For TransformationEvent add the OutputEPCList
      instanceIdentifiersList.addAll(transformationEvent.getOutputEPCList());

    } else if (parentTracker.getEvent() instanceof AssociationEvent associationEvent
        && associationEvent.getChildEPCs() != null
        && !associationEvent.getChildEPCs().isEmpty()) {
      // For AssociationEvent add the ChildEPCs
      instanceIdentifiersList.addAll(associationEvent.getChildEPCs());
    }

    if (!instanceIdentifiersList.isEmpty()
        && instanceIdentifiersList.size() >= (parentTracker.getInstanceIndex() + epcCount)) {
      // If the instanceIdentifiersList has the values required by the child event then add the
      // values and keep track of the count
      instanceIdentifiersList =
          instanceIdentifiersList.subList(
              parentTracker.getInstanceIndex(), parentTracker.getInstanceIndex() + epcCount);
      parentTracker.setInstanceIndex(parentTracker.getInstanceIndex() + epcCount);
    } else if (!instanceIdentifiersList.isEmpty() && instanceIdentifiersList.size() >= epcCount) {
      // If the instanceIdentifiersList does not have the values required by the child event add all
      // the values
      instanceIdentifiersList = instanceIdentifiersList.subList(0, epcCount);
      parentTracker.setInstanceIndex(0);
    } else {
      parentTracker.setInstanceIndex(0);
    }
    return instanceIdentifiersList;
  }

  // Private method which will segregate the Parent identifier from the Parent Event based on the
  // type of the event
  private static List<String> segregateParentIdentifiers(
      final EventIdentifierTracker parentTracker, final int inheritParentCount) {
    // List to store all the Parent identifiers from parent
    List<String> parentIdentifiersList = new ArrayList<>();

    // Check the type of event based on which obtain relative Parent Identifiers
    if (parentTracker.getEvent() instanceof AggregationEvent aggregationEvent
        && aggregationEvent.getParentID() != null
        && !aggregationEvent.getParentID().isEmpty()) {
      // For AggregationEvent add the Parent-Ids
      parentIdentifiersList.add(aggregationEvent.getParentID());
    } else if (parentTracker.getEvent() instanceof TransactionEvent transactionEvent
        && transactionEvent.getParentID() != null
        && !transactionEvent.getParentID().isEmpty()) {
      // For TransactionEvent add the Parent-Ids
      parentIdentifiersList.add(transactionEvent.getParentID());
    } else if (parentTracker.getEvent() instanceof AssociationEvent associationEvent
        && associationEvent.getParentID() != null
        && !associationEvent.getParentID().isEmpty()) {
      // For AssociationEvent add the Parent-Ids
      parentIdentifiersList.add(associationEvent.getParentID());
    }else if(parentTracker.getEvent() instanceof ObjectEvent objectEvent && objectEvent.getEpcList() != null && !objectEvent.getEpcList().isEmpty()){
      //For objectEvent inherit from EPCS
      parentIdentifiersList.addAll(objectEvent.getEpcList());
    }else if(parentTracker.getEvent() instanceof TransformationEvent transformationEvent && transformationEvent.getOutputEPCList() != null && !transformationEvent.getOutputEPCList().isEmpty()){
      //For TransformationEvent inherit from InputEPCs
      parentIdentifiersList.addAll(transformationEvent.getOutputEPCList());
    }

    if (!parentIdentifiersList.isEmpty()
        && parentIdentifiersList.size() >= (parentTracker.getParentIndex() + inheritParentCount)) {
      // If the parentIdentifiersList has the values required by the child event
      parentIdentifiersList =
          parentIdentifiersList.subList(
              parentTracker.getParentIndex(), parentTracker.getParentIndex() + inheritParentCount);
      parentTracker.setParentIndex(parentTracker.getParentIndex() + inheritParentCount);
    } else if (!parentIdentifiersList.isEmpty()
        && parentIdentifiersList.size() >= inheritParentCount) {
      // If the instanceIdentifiersList does not have the values required by the child event add all
      parentIdentifiersList = parentIdentifiersList.subList(0, inheritParentCount);
      parentTracker.setParentIndex(0);
    } else {
      parentTracker.setParentIndex(0);
    }

    return parentIdentifiersList;
  }

  // Private method which will segregate the Class Identifiers from the Parent Event based on type
  // of the event
  private static List<QuantityList> segregateClassIdentifiers(
      final EventIdentifierTracker parentTracker, final int quantityCount, final Float quantity) {

    // List to store all the Class Identifiers from parent
    List<QuantityList> classIdentifiersList = new ArrayList<>();

    // Check the type of the event based on which obtain the respective Class Identifiers and add it
    // to the List
    if (parentTracker.getEvent() instanceof ObjectEvent objectEvent
        && objectEvent.getQuantityList() != null
        && !objectEvent.getQuantityList().isEmpty()) {
      // For ObjectEvent add the QuantityList
      classIdentifiersList.addAll(objectEvent.getQuantityList());

    } else if (parentTracker.getEvent() instanceof AggregationEvent aggregationEvent
        && aggregationEvent.getChildQuantityList() != null
        && !aggregationEvent.getChildQuantityList().isEmpty()) {
      // For AggregationEvent add the Child Quantity List
      classIdentifiersList.addAll(aggregationEvent.getChildQuantityList());

    } else if (parentTracker.getEvent() instanceof TransactionEvent transactionEvent
        && transactionEvent.getQuantityList() != null
        && !transactionEvent.getQuantityList().isEmpty()) {
      // For TransactionEvent add the Quantity List
      classIdentifiersList.addAll(transactionEvent.getQuantityList());

    } else if (parentTracker.getEvent() instanceof TransformationEvent transformationEvent
        && transformationEvent.getOutputQuantityList() != null
        && !transformationEvent.getOutputQuantityList().isEmpty()) {
      // For TransformationEvent add the Output Quantity List
      classIdentifiersList.addAll(transformationEvent.getOutputQuantityList());

    } else if (parentTracker.getEvent() instanceof AssociationEvent associationEvent
        && associationEvent.getChildQuantityList() != null
        && !associationEvent.getChildQuantityList().isEmpty()) {
      // For AssociationEvent add the Child Quantity List
      classIdentifiersList.addAll(associationEvent.getChildQuantityList());
    }

    if (!classIdentifiersList.isEmpty()
        && classIdentifiersList.size() >= (parentTracker.getQuantityIndex() + quantityCount)) {
      // If the classIdentifiersList has the values required by the child event then add the values
      // and keep track of the count
      classIdentifiersList =
          classIdentifiersList.subList(
              parentTracker.getQuantityIndex(), parentTracker.getQuantityIndex() + quantityCount);
      parentTracker.setQuantityIndex(parentTracker.getQuantityIndex() + quantityCount);
    } else if (!classIdentifiersList.isEmpty() && classIdentifiersList.size() >= quantityCount) {
      // If the classIdentifiersList does not have the values required by the child event add all
      // the values
      classIdentifiersList = classIdentifiersList.subList(0, quantityCount);
      parentTracker.setQuantityIndex(0);
    } else {
      parentTracker.setQuantityIndex(0);
    }

    // If user has provided quantity values then change the value for Quantity within QuantityList
    // based on it if not then return the Parent Quantity values
    return quantitySetter(classIdentifiersList, quantity);
  }

  // Method to check for quantity values and return the quantity based on it (written to avoid the
  // cognitive complexity)
  private static List<QuantityList> quantitySetter(
      final List<QuantityList> classIdentifiersList, final Float quantity) {
    return quantity != null && quantity != 0
        ? classIdentifiersList.stream()
            .filter(quantityList -> Objects.nonNull(quantityList.getQuantity()))
            .map(
                quantityList ->
                    new QuantityList(quantityList.getEpcClass(), quantity, quantityList.getUom()))
            .toList()
        : classIdentifiersList;
  }
}
