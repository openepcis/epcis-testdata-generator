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
package io.openepcis.testdata.generator.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.eventhash.EventHashGenerator;
import io.openepcis.model.epcis.*;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RecordTimeType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.openepcis.testdata.generator.format.*;
import io.openepcis.testdata.generator.identifier.util.RandomSerialNumberGenerator;
import io.openepcis.testdata.generator.reactivestreams.EPCISEventDownstreamHandler;
import io.openepcis.testdata.generator.reactivestreams.EventIdentifierTracker;
import io.openepcis.testdata.generator.template.EPCISEventType;
import io.openepcis.testdata.generator.template.Identifier;
import io.openepcis.testdata.generator.template.RandomGenerators;
import io.openepcis.testdata.generator.template.ReferencedIdentifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Slf4j
public abstract class AbstractEventCreationModel<T extends EPCISEventType, E extends EPCISEvent>
    implements EventCreationModel<T, E> {

  protected ObjectMapper objectMapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  protected final T typeInfo;

  protected final List<Identifier> identifiers;

  protected final List<RandomGenerators> randomGenerators;

  private EPCISEventDownstreamHandler epcisEventDownstreamHandler = null;

  private final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

  private final RandomSerialNumberGenerator randomSerialNumberGenerator;

  public AbstractEventCreationModel(
      final T typeInfo,
      final List<Identifier> identifiers,
      final List<RandomGenerators> randomGenerators) {
    this.typeInfo = typeInfo;
    this.identifiers = identifiers;
    this.randomGenerators = randomGenerators;
    this.randomSerialNumberGenerator =
        RandomSerialNumberGenerator.getInstance(
            typeInfo.getSeed()); // Since instance of MersenneTwister for each model
    RandomValueGenerator.generateInstance(
        randomGenerators); // Generate Random instance for each of the randomGenerators
    // Configures the random number generators that will be used when processing extension/ilmd
    UserExtensionSyntax.setRandomGenerators(randomGenerators);
  }

  @Override
  public EPCISEventDownstreamHandler toEPCISDownstreamHandler() {
    if (epcisEventDownstreamHandler == null) {
      this.epcisEventDownstreamHandler = new EPCISEventDownstreamHandler(this);
    }
    return epcisEventDownstreamHandler;
  }

  protected void configure(final E epcisEvent, final List<EventIdentifierTracker> parentTracker) {

    final IdentifierVocabularyType syntax = typeInfo.getLocationPartyIdentifierSyntax();
    try {

      // Call the method to add the When dimension information associated with the event
      configureWhenDimension(epcisEvent, parentTracker);

      // Call the method to add the Where dimension information associated with the event
      configureWhereDimension(epcisEvent, syntax);

      // Call the method to add the Why dimension information associated with the event
      configureWhyDimension(epcisEvent);

      // Call the method to add the Error related information
      configureErrorInformation(epcisEvent, syntax);

      // Add Sensor Info by formatting the values based on Static/Random
      if (typeInfo.getSensorElementList() != null && !typeInfo.getSensorElementList().isEmpty()) {
        final List<SensorElementList> sensorElementList = new ArrayList<>();

        typeInfo
            .getSensorElementList()
            .forEach(
                element -> {
                  final SensorElementList sensorElement = new SensorElementList();
                  final List<SensorReport> sensorReports = new ArrayList<>();

                  sensorElement.setSensorMetadata(element.getSensorMetadata());
                  element
                      .getSensorReport()
                      .forEach(
                          report ->
                              sensorReports.add(
                                  report.format(
                                      randomGenerators))); // Format the SensorReportType to
                  // SensorReport

                  sensorElement.setSensorReport(sensorReports);
                  sensorElementList.add(sensorElement);
                });

        // Assign formatted sensorElementList to EPCISEvent SensorElementList
        epcisEvent.setSensorElementList(sensorElementList);
      }

      // Add the certificationInfo by formatting from UserExtension syntax
      if (typeInfo.getCertificationInfo() != null && !typeInfo.getCertificationInfo().isEmpty()) {
        final List<Object> formattedCertificationInfo =
            typeInfo.getCertificationInfo().stream()
                .flatMap(
                    root -> {
                      if (CollectionUtils.isNotEmpty(root.getChildren())) {
                        // Stream over children if present
                        return root.getChildren().stream()
                            .flatMap(c -> c.toMap().entrySet().stream());
                      } else if (root.getRawJsonld() instanceof Map) {
                        // Stream over rawJsonld if it's a Map
                        return root.toMap().entrySet().stream();
                      }
                      // Return an empty stream if neither are present
                      return Stream.empty();
                    })
                .collect(Collectors.toList());

        if (!formattedCertificationInfo.isEmpty()) {
          epcisEvent.setCertificationInfo(formattedCertificationInfo);
        }
      }

      // Processes user extensions from TypeInfo and sets them on the EPCIS event.
      if (typeInfo.getUserExtensions() != null && !typeInfo.getUserExtensions().isEmpty()) {
        epcisEvent.setUserExtensions(
            typeInfo.getUserExtensions().stream()
                .flatMap(
                    root -> {
                      if (CollectionUtils.isNotEmpty(root.getChildren())) {
                        // Stream over children if present
                        return root.getChildren().stream()
                            .flatMap(c -> c.toMap().entrySet().stream());
                      } else if (root.getRawJsonld() instanceof Map) {
                        // Stream over rawJsonld if it's a Map
                        return root.toMap().entrySet().stream();
                      } else if (root.getRawJsonld() instanceof String) {
                        // Handle raw JSON-LD string - processes JSON-LD format with context removal
                        try {
                          final Map<String, Object> rawMap =
                              objectMapper.readValue(
                                  root.getRawJsonld().toString(), new TypeReference<>() {});
                          rawMap.remove("@context");
                          return rawMap.entrySet().stream();
                        } catch (Exception e) {
                          throw new TestDataGeneratorException(
                              "Error during the conversion of RawJsonLD : "
                                  + typeInfo.getEventType()
                                  + e.getMessage(),
                              e);
                        }
                      }
                      // Return an empty stream if neither are present
                      return Stream.empty();
                    })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (r1, r2) -> r1)));
      }
    } catch (Exception e) {
      throw new TestDataGeneratorException(
          "Error during the configuring EPCIS event : " + typeInfo.getEventType() + e.getMessage(),
          e);
    }
  }

  // Private method used to add the When dimension (Event time, Event Timezone offset, Record time)
  // related information during the creation of event
  private void configureWhenDimension(
      final E epcisEvent, final List<EventIdentifierTracker> parentTracker) {
    // Set TimeZone Offset
    epcisEvent.setEventTimeZoneOffset(typeInfo.getEventTime().getTimeZoneOffset());

    // Setting the event Time, if custom option selected then set eventTime based on parent
    // eventTime
    final OffsetDateTime parentEventTime =
        CollectionUtils.isNotEmpty(parentTracker)
            ? parentTracker.get(0).getEvent().getEventTime()
            : null;
    epcisEvent.setEventTime(
        typeInfo.getEventTime().generate(parentEventTime, randomSerialNumberGenerator));

    // Set Record Time
    if (typeInfo.getRecordTimeType() == RecordTimeType.SAME_AS_EVENT_TIME) {
      epcisEvent.setRecordTime(epcisEvent.getEventTime());
    } else if (typeInfo.getRecordTimeType() == RecordTimeType.CURRENT_TIME) {
      epcisEvent.setRecordTime(OffsetDateTime.parse(dateFormatter.format(OffsetDateTime.now())));
    }
  }

  // Private method used to add the Where dimension (Readpoint, Business Location) related
  // information during the creation of event
  private void configureWhereDimension(final E epcisEvent, final IdentifierVocabularyType syntax) {
    // Set Read Point
    if (typeInfo.getReadPoint() != null) {
      final String formattedReadPoint =
          ReadpointBusinessLocationFormatter.format(
              syntax, typeInfo.getReadPoint(), typeInfo.getDlURL());
      var rp = new ReadPoint();

      try {
        rp.setId(new URI(formattedReadPoint != null ? formattedReadPoint : ""));
      } catch (URISyntaxException ex) {
        throw new TestDataGeneratorException(
            "Error during the addition of ReadPoint to EPCIS event : "
                + typeInfo.getEventType()
                + ex.getMessage(),
            ex);
      }

      // Set the ReadPoint for the EPCIS event
      epcisEvent.setReadPoint(rp);
    }

    // Set Biz Location
    if (typeInfo.getBizLocation() != null) {
      final String formattedBizLocation =
          ReadpointBusinessLocationFormatter.format(
              syntax, typeInfo.getBizLocation(), typeInfo.getDlURL());
      var biz = new BizLocation();

      try {
        biz.setId(new URI(formattedBizLocation != null ? formattedBizLocation : ""));
      } catch (URISyntaxException ex) {
        throw new TestDataGeneratorException(
            "Error during the addition of BizLocation to EPCIS event : "
                + typeInfo.getEventType()
                + ex.getMessage(),
            ex);
      }

      // Set the BizLocation for the EPCIS event
      epcisEvent.setBizLocation(biz);
    }
  }

  // Private method used to add the Where dimension (Readpoint, Business Location) related
  // information during the creation of event
  private void configureWhyDimension(final E epcisEvent) {
    // Set Business Step
    if (typeInfo.getBusinessStep() != null) {
      epcisEvent.setBizStep(
          BusinessStepFormatter.format(
              typeInfo.getBusinessStep(), typeInfo.getBusinessStepManualURI()));
    }

    // Set Disposition
    if (typeInfo.getDisposition() != null) {
      epcisEvent.setDisposition(
          DispositionFormatter.format(
              typeInfo.getDisposition(), typeInfo.getDispositionManualURI()));
    }
  }

  // Private method used to add the Error (Error declaration time, corrective Ids, Error extension,
  // Error reason) related information during the creation of event
  private void configureErrorInformation(
      final E epcisEvent, final IdentifierVocabularyType syntax) {
    // Set Error Declaration
    if (typeInfo.getErrorDeclaration() != null && !typeInfo.isOrdinaryEvent()) {
      var err = new ErrorDeclaration();

      // Set the Error declaration time
      err.setDeclarationTime(
          typeInfo
              .getErrorDeclaration()
              .getDeclarationTime()
              .generate(null, randomSerialNumberGenerator));

      // Set the Error declaration time offset this is missing in openEpcis-parent
      // err.setDeclarationTimezoneOffset(typeInfo.getErrorDeclaration().getDeclarationTime().getTimeZoneOffset());

      // Timezone offset which needs to be added
      if (typeInfo.getErrorDeclaration().getDeclarationReason() != null) {
        err.setReason(typeInfo.getErrorDeclaration().getDeclarationReason().toString());
      }

      // Add Error Corrective IDs if not null
      if (typeInfo.getErrorDeclaration().getCorrectiveIds() != null
          && !typeInfo.getErrorDeclaration().getCorrectiveIds().isEmpty()) {
        err.setCorrectiveEventIDs(
            ErrorDeclarationFormatter.format(
                syntax, typeInfo.getErrorDeclaration().getCorrectiveIds(), typeInfo.getDlURL()));
      }

      // Add Error Extensions if not null
      if (typeInfo.getErrorDeclaration().getExtensions() != null
          && !typeInfo.getErrorDeclaration().getExtensions().isEmpty()) {
        err.setUserExtensions(
            typeInfo.getErrorDeclaration().getExtensions().stream()
                .flatMap(
                    root -> {
                      if (CollectionUtils.isNotEmpty(root.getChildren())) {
                        // Stream over children if present
                        return root.getChildren().stream()
                            .flatMap(c -> c.toMap().entrySet().stream());
                      } else if (root.getRawJsonld() instanceof Map) {
                        // Stream over rawJsonld if it's a Map
                        return root.toMap().entrySet().stream();
                      }
                      // Return an empty stream if neither are present
                      return Stream.empty();
                    })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (r1, r2) -> r1)));
      }

      epcisEvent.setErrorDeclaration(err);
    }
  }

  // Method used for creating the parentID based on the value provided for the
  // parentReferenceIdentifier
  public List<String> referencedParentIdentifier(final List<EventIdentifierTracker> parentTracker) {
    // Create a List to store values related to Parent Identifiers
    List<String> parentList = new ArrayList<>();

    // Loop over the referenced identifier to get the parent identifiers
    for (ReferencedIdentifier epc : typeInfo.getReferencedIdentifier()) {
      // When user wants to inherit Parent-Ids from parent node into child node get the matching
      if (epc.getParentNodeId() != 0
          && epc.getInheritParentCount() != null
          && epc.getInheritParentCount() > 0) {
        parentTracker.stream()
            .filter(i -> i.getEventTypeInfo().getNodeId() == epc.getParentNodeId())
            .findFirst()
            .ifPresent(
                t ->
                    parentList.addAll(
                        EventModelUtil.parentIdentifiers(t, epc.getInheritParentCount())));
      } else if (epc.getParentNodeId() != 0
          && epc.getInheritEPCToParent() != null
          && epc.getInheritEPCToParent() > 0) {
        parentTracker.stream()
            .filter(i -> i.getEventTypeInfo().getNodeId() == epc.getParentNodeId())
            .findFirst()
            .ifPresent(
                t ->
                    parentList.addAll(
                        EventModelUtil.instanceIdentifiers(t, epc.getInheritEPCToParent())));
      }
    }
    return parentList;
  }

  // Common method to built or import the parentID for Aggregation/Transaction/Association
  // eventTypes
  public void configureParent(
      final EPCISEvent event,
      final List<EventIdentifierTracker> parentTracker,
      final Identifier matchingParentId) {
    // Determine the event type and cast it accordingly
    if (event instanceof AggregationEvent aggEvent) {
      aggEvent.setParentID(buildParentID(matchingParentId, parentTracker));
    } else if (event instanceof TransactionEvent txnEvent) {
      txnEvent.setParentID(buildParentID(matchingParentId, parentTracker));
    } else if (event instanceof AssociationEvent assocEvent) {
      assocEvent.setParentID(buildParentID(matchingParentId, parentTracker));
    }
  }

  // Method to either build/import parentID
  private String buildParentID(
      final Identifier matchingParentId, final List<EventIdentifierTracker> parentTracker) {
    if (matchingParentId != null && matchingParentId.getParentData() != null) {
      // If values for parentReferencedIdentifier is provided then generated and add ParentID.
      return matchingParentId
          .getParentData()
          .format(
              matchingParentId.getObjectIdentifierSyntax(),
              1,
              matchingParentId.getDlURL(),
              randomSerialNumberGenerator)
          .get(0);
    } else if (typeInfo.getParentIdentifier() != null
        && !typeInfo.getParentIdentifier().isEmpty()) {
      // If importing the existing event and if the existing event has parent identifier then
      // include it
      return typeInfo.getParentIdentifier();
    } else if (typeInfo.getReferencedIdentifier() != null
        && !typeInfo.getReferencedIdentifier().isEmpty()) {
      // If importing the parent identifier from previous ObjectEvent or other event EPCs
      final List<String> parentID = referencedParentIdentifier(parentTracker);
      if (!parentID.isEmpty()) {
        return parentID.get(0);
      }
    }
    return null;
  }

  /**
   * Method used for creating the EPCs based on the value provided for the field
   * ReferencedIdentifier
   *
   * @param parentTracker List of the parent identifiers node infprmation stored as parentTracker
   * @param inheritParentIdCountFlag flag to detect if inheritParentIdentifiers need to be added or
   *     not. Inherited only for Object/TransformationEvent.
   * @return returns List of EPC identifiers
   */
  public List<String> referencedEpcsIdentifierGenerator(
      final List<EventIdentifierTracker> parentTracker, final Boolean inheritParentIdCountFlag) {
    // Create a List to store all the values associated Instance Identifiers
    final List<String> epcList = new ArrayList<>();

    // Loop through all the referenceIdentifiers and get the matching identifiers based on
    // IdentifiersList and Reference Identifiers
    for (ReferencedIdentifier epc : typeInfo.getReferencedIdentifier()) {

      // If the EventNode is directly connected to the IdentifiersNode then create the class
      // identifiers based on the provided identifiers info
      if (epc.getIdentifierId() != 0 && epc.getEpcCount() > 0) {
        // Get the matching identifiers
        var matchingIdentifier =
            identifiers.stream()
                .filter(identifier -> identifier.getIdentifierId() == epc.getIdentifierId())
                .findFirst()
                .orElse(null);

        // If the Instance-data has been provided then create them and add it to the List
        if (matchingIdentifier != null && matchingIdentifier.getInstanceData() != null) {
          // Append all instance identifiers values onto the Instance-Identifiers List
          epcList.addAll(
              matchingIdentifier
                  .getInstanceData()
                  .format(
                      matchingIdentifier.getObjectIdentifierSyntax(),
                      epc.getEpcCount(),
                      matchingIdentifier.getDlURL(),
                      randomSerialNumberGenerator));
        }
      } else if (epc.getParentNodeId() != 0 && epc.getEpcCount() > 0) {
        // If referenced identifier contains the parent node id then obtain the identifiers from its
        // parent event and add it

        // Obtain the matching parentTracker from the List of Tracker and if present based on it get
        // the Instance identifiers from parent node and append to current event based on the
        // Instance count
        parentTracker.stream()
            .filter(i -> i.getEventTypeInfo().getNodeId() == epc.getParentNodeId())
            .findFirst()
            .ifPresent(
                t -> epcList.addAll(EventModelUtil.instanceIdentifiers(t, epc.getEpcCount())));
      } else if (epc.getParentNodeId() != 0 && epc.getInheritParentToEPC() > 0) {
        // If inheriting the identifiers from ParentID to subsequent ChildEPCS/EPCS then inherit
        // based on getInheritParentToEPC
        parentTracker.stream()
            .filter(i -> i.getEventTypeInfo().getNodeId() == epc.getParentNodeId())
            .findFirst()
            .ifPresent(
                t ->
                    epcList.addAll(
                        EventModelUtil.segregateParentToEPC(t, epc.getInheritParentToEPC())));
      }

      // Add the identifiers from ParentID Count only if the inheriting event is ObjectEvent or
      // TransformationEvent
      if (epc.getParentNodeId() != 0
          && epc.getInheritParentCount() > 0
          && Boolean.TRUE.equals(inheritParentIdCountFlag)) {
        // When user wants to inherit Parent-Ids from parent node into child node get the matching
        // Parent Identifiers. (AggregationEvent -> ObjectEvent)
        parentTracker.stream()
            .forEach(
                parent ->
                    epcList.addAll(
                        EventModelUtil.parentIdentifiers(parent, epc.getInheritParentCount())));
      }
    }

    // If user is importing the existing events into the design and if it contains instance
    // identifiers then add them to the event
    Optional.ofNullable(typeInfo.getEpcList()).ifPresent(epcList::addAll);

    return epcList;
  }

  // Method used for creating the Class identifiers based on the value provided for the field
  // ReferencedIdentifier
  public List<QuantityList> referencedClassIdentifierGenerator(
      final List<EventIdentifierTracker> parentTracker) {
    // Create a List to store all the values associated with the event
    final List<QuantityList> quantityList = new ArrayList<>();

    // Loop through all the referenceIdentifiers and get the matching identifiers based on
    // IdentifiersList and Reference Identifiers
    for (ReferencedIdentifier quantity : typeInfo.getReferencedIdentifier()) {

      // If the EventNode is directly connected to the IdentifiersNode then create the class
      // identifiers based on the provided identifiers info
      if (quantity.getIdentifierId() != 0
          && quantity.getClassCount() != null
          && quantity.getClassCount() > 0) {

        // Get the matching identifiers from the IdentifiersList based on the Identifiers present in
        // the ReferencedIdentifier
        var matchedClassIdentifier =
            identifiers.stream()
                .filter(identifier -> identifier.getIdentifierId() == quantity.getIdentifierId())
                .findFirst()
                .orElse(null);

        // If the matching identifier is found then create the respective Class Identifiers and
        // append it to the list created above
        if (matchedClassIdentifier != null && matchedClassIdentifier.getClassData() != null) {

          // Append all Class Identifiers values onto the Instance-Identifiers List
          quantityList.addAll(
              matchedClassIdentifier
                  .getClassData()
                  .format(
                      matchedClassIdentifier.getObjectIdentifierSyntax(),
                      quantity.getClassCount(),
                      quantity.getQuantity(),
                      matchedClassIdentifier.getDlURL(),
                      randomSerialNumberGenerator));
        }
      } else if (quantity.getParentNodeId() != 0
          && quantity.getClassCount() != null
          && quantity.getClassCount() > 0) {
        // If referenced identifier contains the parent node id then obtain the identifiers from its
        // parent event and add it

        // Obtain the matching parentTracker from the List of Tracker and if present based on it get
        // the Class identifiers from parent node and append to current event based on the Class
        // count
        parentTracker.stream()
            .filter(i -> i.getEventTypeInfo().getNodeId() == quantity.getParentNodeId())
            .findFirst()
            .ifPresent(
                t ->
                    quantityList.addAll(
                        EventModelUtil.classIdentifiers(
                            t, quantity.getClassCount(), quantity.getQuantity())));
      }
    }

    // If user is importing the existing events and if it contains class identifiers then add them
    // to the event
    Optional.ofNullable(typeInfo.getQuantityList()).ifPresent(quantityList::addAll);

    return quantityList;
  }

  // Method to check if user has requested for Hash id generation if so then generate Hash id based
  // on created event information
  public void setupEventHashId(final E epcisEvent) {
    // Check if setEventId is true and based on eventId type set the values accordingly
    if (typeInfo.isEventID()
        && typeInfo.getEventIdType() != null
        && typeInfo.getEventIdType().equals("UUID")) {
      epcisEvent.setEventID(EventIdUUIDGenerator.generateUUID());
    } else if (typeInfo.isEventID()
        && typeInfo.getEventIdType() != null
        && typeInfo.getEventIdType().equals("HashId")) {
      // If the setEventId is true and eventId type is Hash-id
      final ObjectNode objectNode = objectMapper.convertValue(epcisEvent, ObjectNode.class);
      final String hashAlgorithm =
          typeInfo.getHashAlgorithm() != null && !typeInfo.getHashAlgorithm().isEmpty()
              ? typeInfo.getHashAlgorithm()
              : "sha-256";
      epcisEvent.setEventID(new EventHashGenerator().fromObjectNode(objectNode, hashAlgorithm));
    }
  }
}
