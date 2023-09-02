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
package io.openepcis.testdata.generator.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.openepcis.model.epcis.BizTransactionList;
import io.openepcis.model.epcis.PersistentDisposition;
import io.openepcis.model.epcis.QuantityList;
import io.openepcis.model.epcis.SensorElementList;
import io.openepcis.testdata.generator.constants.BusinessStep;
import io.openepcis.testdata.generator.constants.Disposition;
import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.RecordTimeType;
import io.openepcis.testdata.generator.format.ErrorDeclarationSyntax;
import io.openepcis.testdata.generator.format.EventTime;
import io.openepcis.testdata.generator.format.ReadPointBizLocationSyntax;
import io.openepcis.testdata.generator.format.UserExtensionSyntax;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@ToString
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
    property = "eventType",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = ObjectEventType.class, name = "ObjectEvent"), //
  @JsonSubTypes.Type(value = AggregationEventType.class, name = "AggregationEvent"), //
  @JsonSubTypes.Type(value = TransactionEventType.class, name = "TransactionEvent"), //
  @JsonSubTypes.Type(value = TransformationEventType.class, name = "TransformationEvent"), //
  @JsonSubTypes.Type(value = AssociationEventType.class, name = "AssociationEvent")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class EPCISEventType implements Serializable {

  @NotNull(message = "Node identifier cannot be Null")
  @Schema(
      type = SchemaType.NUMBER,
      description = "Unique identifier assigned to each event node",
      required = true)
  private @Valid int nodeId;

  // Common Header information for the all events
  @Min(value = 1, message = "Event count cannot be less than 1")
  @NotNull(message = "Event count cannot be Null")
  @Schema(type = SchemaType.NUMBER, description = "Number of events required", required = true)
  private @Valid int eventCount;

  @NotNull(message = "Event type cannot be Null")
  @Schema(
      type = SchemaType.STRING,
      enumeration = {
        "ObjectEvent",
        "AggregationEvent",
        "TransactionEvent",
        "TransformationEvent",
        "AssociationEvent"
      },
      required = true)
  private @Valid String eventType;

  @NotNull(message = "Vocabulary Syntax cannot be Null, should be URN/WebURI")
  @Schema(
      type = SchemaType.STRING,
      description = "Type of vocabulary syntax used for generating the EPCIS event information",
      required = true)
  private @Valid IdentifierVocabularyType locationPartyIdentifierSyntax =
      IdentifierVocabularyType.URN;

  @NotNull(message = "Event type cannot be Null, should be Ordinary/Error")
  @Schema(
      type = SchemaType.BOOLEAN,
      description = "Ordinary event: true, Error event: False",
      required = true)
  private @Valid boolean ordinaryEvent;

  // WHAT Dimension is present in respective Event type file: ObjectEventType,
  // TransformationEventType, TransactionEventType, AggregationEventType,AssociationEventType

  // WHEN Dimension Common Information
  @NotNull(message = "Event Time cannot be Null, it is required")
  @Schema(
      type = SchemaType.OBJECT,
      description = "Event time information associated with event.",
      required = true)
  private @Valid EventTime eventTime;

  @Schema(type = SchemaType.STRING, description = "Type of Record time associated with event.")
  private @Valid RecordTimeType recordTimeType;

  // Where Dimension Common Information
  @Schema(type = SchemaType.OBJECT, description = "Read point associated with event.")
  private @Valid ReadPointBizLocationSyntax readPoint;

  @Schema(type = SchemaType.OBJECT, description = "Business location associated with event.")
  private @Valid ReadPointBizLocationSyntax bizLocation;

  // WHY Dimension Common Information
  @Schema(type = SchemaType.OBJECT, description = "Business step associated with event.")
  private @Valid BusinessStep businessStep;

  @Schema(type = SchemaType.OBJECT, description = "Disposition associated with event.")
  private @Valid Disposition disposition;

  @Schema(
      type = SchemaType.OBJECT,
      description = "Persistent disposition associated with the event")
  private @Valid PersistentDisposition persistentDisposition;

  @Schema(
      type = SchemaType.ARRAY,
      description = "Business transaction information associated with the event")
  private List<@Valid BizTransactionList> bizTransactions;

  // HOW Dimension Common Information
  @Schema(
      type = SchemaType.ARRAY,
      description = "Sensor element information associated with the event")
  private List<@Valid SensorElementList> sensorElementList;

  // OTHER Fields
  @Schema(
      type = SchemaType.BOOLEAN,
      description = "Event ID associated with the event. True: add eventId, False: omit eventId")
  private @Valid boolean eventID;

  @Schema(
      type = SchemaType.STRING,
      enumeration = {"UUID", "HashId"},
      description = "Type of Event ID associated with the event. UUID or HashId")
  private @Valid String eventIdType;

  @Schema(
      type = SchemaType.STRING,
      enumeration = {
        "sha-1",
        "sha-224",
        "sha-256",
        "sha-384",
        "sha-512",
        "sha3-224",
        "sha3-256",
        "sha3-384",
        "sha3-512",
        "md2",
        "md5"
      },
      description =
          "Type of Hash Algorithm : sha-1, sha-224, sha-256, sha-384, sha-512, sha3-224, sha3-256, sha3-384, sha3-512, md2, md5")
  private @Valid String hashAlgorithm;

  @Schema(
      type = SchemaType.STRING,
      description = "URL at which certification details can be found.")
  private String certificationInfo;

  @Schema(
      type = SchemaType.ARRAY,
      description = "User extension information associated with the event")
  private List<@Valid UserExtensionSyntax> userExtensions;

  @Schema(
      type = SchemaType.OBJECT,
      description = "Error information associated with event node if event is not ordinary event")
  private @Valid ErrorDeclarationSyntax errorDeclaration;

  // Variables to store the information regarding the EPC Instance/Class identifiers template
  @Schema(
      type = SchemaType.ARRAY,
      description = "EPCs/Class identifiers information associated with event node")
  private List<@Valid ReferencedIdentifier> referencedIdentifier;

  @Schema(
      type = SchemaType.ARRAY,
      description = "Output EPCs/Class identifiers information associated with TransformationEvent")
  private List<@Valid ReferencedIdentifier> outputReferencedIdentifier;

  @Schema(
      type = SchemaType.ARRAY,
      description =
          "Parent identifiers information associated with AggregationEvent/TransactionEvent/AssociationEvent")
  private @Valid ReferencedIdentifier parentReferencedIdentifier;

  // Variables to store the information associated with existing Instance/Class/Parent identifiers
  // during the import events
  @Schema(
      type = SchemaType.ARRAY,
      description = "Generated Instance/EPC identifiers associated with event.")
  private List<String> epcList = new ArrayList<>();

  @Schema(
      type = SchemaType.ARRAY,
      description = "Generated Class/Quantity identifiers associated with event.")
  private List<QuantityList> quantityList = new ArrayList<>();

  @Schema(
      type = SchemaType.ARRAY,
      description = "Generated Parent identifiers associated with event.")
  private String parentIdentifier;

  @Schema(
      type = SchemaType.ARRAY,
      description = "Generated Output Instance/EPC identifiers associated with event.")
  private List<String> outputEPCList = new ArrayList<>();

  @Schema(
      type = SchemaType.ARRAY,
      description = "Generated Output Class/Quantity identifiers associated with event.")
  private List<QuantityList> outputQuantityList = new ArrayList<>();
}
