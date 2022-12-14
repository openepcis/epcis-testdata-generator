<!--

    Copyright 2022 benelog GmbH & Co. KG

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.

-->
<template>
  <b-modal
    id="configureEventInfoModal"
    title="Add Event Information"
    size="xl"
    width="150%"
    :visible="$store.state.modules.ConfigureNodeEventInfoStore.nodeEventInfoModal"
    modal-class="modal-fullscreen"
    @hide="cancel"
  >
    <b-form id="configureEventInfoForm" class="form-horizontal" autocomplete="on" @submit.prevent="submitEventData">
      <table class="table table-bordered">
        <caption />
        <th id="eventInfoTable" />
        <tbody>
          <tr>
            <td id="eventDimension" :rowspan="EventTypeRowSpan" />
            <td> Event Count </td>
            <input
              v-model="formData.eventCount"
              type="number"
              min="1"
              max="1000"
              class="form-control"
              oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
              oninvalid="this.setCustomValidity('Number of Events must be between 1 and 1000')"
              placeholder="Number of EPCIS events"
              required
            >
          </tr>
          <tr>
            <td>Location party identifier syntax</td>
            <td>
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="vocabularySyntaxURN"
                  v-model="formData.vocabularySyntax"
                  type="radio"
                  class="custom-control-input"
                  value="URN"
                >
                <label class="custom-control-label" for="vocabularySyntaxURN">URN</label>
              </div>
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="vocabularySyntaxWebURI"
                  v-model="formData.vocabularySyntax"
                  type="radio"
                  class="custom-control-input"
                  value="WebURI"
                >
                <label class="custom-control-label" for="vocabularySyntaxWebURI">Web URI</label>
              </div>
            </td>
          </tr>

          <tr>
            <td rowspan="2" class="column2">
              Event Type
            </td>
            <td>
              <input v-model="formData.eventType" type="text" class="form-control" readonly>
            </td>
          </tr>

          <tr>
            <td>
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="ordinaryEvent"
                  :checked="formData.ordinaryEvent == true"
                  type="radio"
                  class="custom-control-input"
                  value="ordinaryEvent"
                  @change="formData.ordinaryEvent = true"
                >
                <label class="custom-control-label" for="ordinaryEvent">Ordinary</label>
              </div>
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="errorEvent"
                  :checked="!formData.ordinaryEvent"
                  type="radio"
                  class="custom-control-input"
                  value="errorEvent"
                  @change="formData.ordinaryEvent = false"
                >
                <label class="custom-control-label" for="errorEvent">Error</label>
              </div>
            </td>
          </tr>

          <tr v-if="formData.eventType == 'ObjectEvent' || formData.eventType == 'AggregationEvent' || formData.eventType == 'TransactionEvent' || formData.eventType == 'AssociationEvent' ">
            <td>
              Action
            </td>
            <td>
              <b-form-select v-model="formData.action" :options="commonDropdownInfos.actions" class="form-control" />
            </td>
          </tr>

          <tr>
            <td> Event ID </td>
            <td class="form-inline" style="display: block;margin: auto;">
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="EventIDOption1"
                  :checked="formData.eventID == true"
                  type="radio"
                  class="custom-control-input"
                  value="eventIDYes"
                  name="eventID"
                  @change="formData.eventID = true"
                >
                <label class="custom-control-label" for="EventIDOption1">Yes</label>
              </div>

              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="EventIDOption2"
                  :checked="formData.eventID == false"
                  type="radio"
                  class="custom-control-input"
                  value="eventIDNo"
                  name="eventID"
                  @change="formData.eventID = false"
                >
                <label class="custom-control-label" for="EventIDOption2">No</label>
              </div>
            </td>

            <!-- If eventId is required then show the UUID and HashId option -->
            <td v-if="formData.eventID == true">
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="EventIDTypeOption1"
                  :checked="formData.eventIdType == 'UUID'"
                  type="radio"
                  class="custom-control-input"
                  value="UUID"
                  name="eventIdType"
                  :required="formData.eventID == true"
                  @change="formData.eventIdType = 'UUID'"
                >
                <label class="custom-control-label" for="EventIDTypeOption1">UUID</label>
              </div>

              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="EventIDTypeOption2"
                  :checked="formData.eventIdType == 'HashId'"
                  type="radio"
                  class="custom-control-input"
                  value="HashId"
                  name="eventIdType"
                  :required="formData.eventID == true"
                  @change="formData.eventIdType = 'HashId'"
                >
                <label class="custom-control-label" for="EventIDTypeOption2">Hash ID</label>
              </div>
            </td>

            <!-- If eventId is true and if the eventIdType is Hash Id then display the Hash Algorithm types -->
            <td v-if="formData.eventID == true && formData.eventIdType == 'HashId'">
              <b-form-select v-model="formData.hashAlgorithm" :options="commonDropdownInfos.hashAlgorithmTypes" :required="formData.eventID == true && formData.eventIdType == 'HashId'" />
            </td>
          </tr>

          <!-- WHEN DIMENSION FIELFDS START -->
          <tr>
            <td rowspan="2" style="background-color: #a854a8;text-align: center;">
              <strong>WHEN</strong>
            </td>

            <td class="when">
              Event Time
            </td>

            <td>
              <b-form-select v-model="formData.eventTimeSelector" :options="commonDropdownInfos.eventTimeSelector" />
            </td>

            <td v-if="formData.eventTimeSelector == 'SpecificTime' ">
              <input
                v-model="formData.eventTime.specificTime"
                type="datetime-local"
                class="form-control"
                title="Set Specific Event Time"
                :required="formData.eventTimeSelector == 'SpecificTime'"
                step="1"
              >
            </td>

            <td v-if="formData.eventTimeSelector == 'TimeRange' " class="form-inline">
              <span class="horizontalSpace"> From </span>
              <input
                v-model="formData.eventTime.fromTime"
                type="datetime-local"
                class="form-control"
                title="Event Time Range FROM"
                :required="formData.eventTimeSelector == 'TimeRange'"
                step="1"
              >
              <span class="horizontalSpace"> To </span>
              <input
                v-model="formData.eventTime.toTime"
                type="datetime-local"
                class="form-control"
                title="Event Time Range TO"
                :required="formData.eventTimeSelector == 'TimeRange'"
                step="1"
              >
            </td>

            <td>
              <b-form-select v-model="formData.eventTime.timeZoneOffset" :options="commonDropdownInfos.TimeZones" />
            </td>
          </tr>

          <tr>
            <td class="when">
              Record Time
            </td>

            <td>
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="RecordTimeOption1"
                  v-model="formData.RecordTimeOption"
                  type="radio"
                  class="custom-control-input"
                  value="yes"
                  name="RecordTimeOption"
                >
                <label class="custom-control-label" for="RecordTimeOption1">Yes</label>
              </div>

              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="RecordTimeOption2"
                  v-model="formData.RecordTimeOption"
                  type="radio"
                  class="custom-control-input"
                  value="no"
                  name="RecordTimeOption"
                >
                <label class="custom-control-label" for="RecordTimeOption2">No</label>
              </div>
            </td>

            <td v-if="formData.RecordTimeOption == 'yes'" class="form-inline" style="display: block;margin: auto;">
              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="RecordTimeOptionType1"
                  v-model="formData.recordTimeType"
                  type="radio"
                  class="custom-control-input"
                  value="CURRENT_TIME"
                  name="recordTimeType"
                >
                <label class="custom-control-label" for="RecordTimeOptionType1">Current Time</label>
              </div>

              <div class="custom-control custom-radio custom-control-inline">
                <input
                  id="RecordTimeOptionType2"
                  v-model="formData.recordTimeType"
                  type="radio"
                  class="custom-control-input"
                  value="SAME_AS_EVENT_TIME"
                  name="recordTimeType"
                >
                <label class="custom-control-label" for="RecordTimeOptionType2">Same As Event Time</label>
              </div>
            </td>
          </tr>
          <!-- WHEN DIMENSION FIELDS END -->

          <!-- WHAT DIMENSION INFORMATION during the import of events -->
          <!-- DISPLAY the WHAT dimension only for Imported Events -->
          <tr v-if="formData.importEvent">
            <td id="whatDimension" :rowspan="rowspanWHAT">
              <strong>WHAT</strong>
            </td>

            <!-- First Element of the EPCIS event WHAT dimension -->

            <!--Show option to remove EPCs for ObjectEvent/TransactionEvent -->
            <td v-if="formData.eventType === 'ObjectEvent'">
              EPCs
            </td>

            <!--Show option to remove Parent Identifier for AggregationEvent/AssociationEvent/TransactionEvent -->
            <td v-if="formData.eventType === 'AggregationEvent' || formData.eventType === 'TransactionEvent' || formData.eventType === 'AssociationEvent'">
              Parent Identifiers
            </td>

            <!--Show option to remove InputEPCs for TransformationEvent -->
            <td v-if="formData.eventType === 'TransformationEvent'">
              Input EPCs
            </td>

            <!--For ObjectEvent/TransformationEvent display the epcList delete button-->
            <td v-if="formData.epcList.length > 0 && (formData.eventType === 'ObjectEvent' || formData.eventType === 'TransformationEvent')">
              <button class="btn btn-danger" placeholder="Delete Instance Identifiers" @click="deleteInstanceIdentifiers($event ,'EPC')">
                <em class="bi bi-trash-fill" />
              </button>
            </td>

            <!--For AggregationEvent/TransactionEvent/AssociationEvent display the parentID delete button-->
            <td v-if="formData.parentIdentifier != '' && (formData.eventType === 'AggregationEvent' || formData.eventType === 'TransactionEvent' || formData.eventType === 'AssociationEvent')">
              <button class="btn btn-danger" placeholder="Delete Parent Identifiers" @click="deleteParentIdentifiers($event,'ParentIdentifier')">
                <em class="bi bi-trash-fill" />
              </button>
            </td>
          </tr>

          <!-- Second Element of the EPCIS event WHAT dimension -->
          <tr v-if="formData.importEvent">
            <!--Show option to remove Quantities for ObjectEvent -->
            <td v-if="formData.eventType === 'ObjectEvent'">
              Quantities
            </td>

            <!--Show option to remove ChildEPCs for AggregationEvent/AssociationEvent -->
            <td v-if="formData.eventType === 'AggregationEvent' || formData.eventType === 'AssociationEvent'">
              Child EPCs
            </td>

            <!--Show option to remove ChildEPCs for AggregationEvent/AssociationEvent -->
            <td v-if="formData.eventType === 'TransactionEvent'">
              EPCs
            </td>

            <!--Show option to remove Input Quantities for TransformationEvent -->
            <td v-if="formData.eventType === 'TransformationEvent'">
              Input Quantities
            </td>

            <!--For ObjectEvent/TransformationEvent display the QuantityList/Input Quantities delete button-->
            <td v-if="formData.quantityList.length > 0 && (formData.eventType === 'ObjectEvent' || formData.eventType === 'TransformationEvent')">
              <button class="btn btn-danger" placeholder="Delete Quantity/Class Identifiers" @click="deleteQuantityIdentifiers($event ,'Class')">
                <em class="bi bi-trash-fill" />
              </button>
            </td>

            <!--For AggregationEvent/TransactionEvent/AssociationEvent display the EPCList delete button-->
            <td v-if="formData.epcList.length > 0 && (formData.eventType === 'AggregationEvent' || formData.eventType === 'TransactionEvent' || formData.eventType === 'AssociationEvent')">
              <button class="btn btn-danger" placeholder="Delete Instance Identifiers" @click="deleteInstanceIdentifiers($event ,'EPC')">
                <em class="bi bi-trash-fill" />
              </button>
            </td>
          </tr>

          <!-- Third Element of the EPCIS event WHAT dimension -->
          <tr v-if="formData.importEvent">
            <!--Show option to remove Child Quantities for AggregationEvent/AssociationEvent -->
            <td v-if="formData.eventType === 'AggregationEvent' || formData.eventType === 'AssociationEvent'">
              Child Quantities
            </td>

            <!--Show option to remove Quantities for TransactionEvent -->
            <td v-if="formData.eventType === 'TransactionEvent'">
              Quantities
            </td>

            <!--Show option to remove OutputEPCs for TransformationEvent -->
            <td v-if="formData.eventType === 'TransformationEvent'">
              Output EPCs
            </td>

            <!--For AggregationEvent/TransactionEvent/AssociationEvent display the Quantities delete button-->
            <td v-if="formData.quantityList.length > 0 && (formData.eventType === 'AggregationEvent' || formData.eventType === 'TransactionEvent' || formData.eventType === 'AssociationEvent')">
              <button class="btn btn-danger" placeholder="Delete Quantity/Class Identifiers" @click="deleteQuantityIdentifiers($event ,'Class')">
                <em class="bi bi-trash-fill" />
              </button>
            </td>

            <!--For TransformationEvent display the Output EPCList delete button-->
            <td v-if="formData.outputEPCList.length > 0 && formData.eventType === 'TransformationEvent'">
              <button class="btn btn-danger" placeholder="Delete Quantity/Class Identifiers" @click="deleteInstanceIdentifiers($event ,'OutputEPC')">
                <em class="bi bi-trash-fill" />
              </button>
            </td>
          </tr>

          <!-- Fourth Element of the EPCIS event WHAT dimension -->
          <tr v-if="formData.importEvent">
            <!--Show option to remove Output Quantities for TransformationEvent -->
            <td v-if="formData.eventType === 'TransformationEvent'">
              Output Quantities
            </td>

            <!--For TransformationEvent display the Output Quantities delete button-->
            <td v-if="formData.outputQuantityList.length > 0 && formData.eventType === 'TransformationEvent'">
              <button class="btn btn-danger" placeholder="Delete Quantity/Class Identifiers" @click="deleteQuantityIdentifiers($event ,'OutputClass')">
                <em class="bi bi-trash-fill" />
              </button>
            </td>
          </tr>

          <!-- WHERE DIMENSION FIELDS START -->
          <tr>
            <td rowspan="2" style="background-color: #478f77;text-align: center;">
              <strong>WHERE</strong>
            </td>

            <td class="where">
              Read Point
            </td>

            <td>
              <b-form-select v-model="formData.readpointselector" class="form-control" @change="resetFields('readPoint')">
                <b-form-select-option value="null" selected>
                  Choose Read Point type
                </b-form-select-option>
                <b-form-select-option value="gs1Key" disabled style="font-weight:bold;">
                  GS1 Key
                </b-form-select-option>
                <b-form-select-option value="SGLN">
                  &nbsp; SGLN + ext (Al 414 + Al 254)
                </b-form-select-option>
                <b-form-select-option style="font-weight:bold;" disabled value="other">
                  Other
                </b-form-select-option>
                <b-form-select-option value="manually">
                  &nbsp; Enter URI manually
                </b-form-select-option>
              </b-form-select>
            </td>

            <td v-if="formData.readpointselector != null" class="form-inline">
              <span v-if="formData.readpointselector == 'manually' ">
                <span class="horizontalSpace"> URI </span>
                <input v-model="formData.readPoint.manualURI" type="text" class="form-control" placeholder="Ex:urn:example:loc:123" :required="formData.readpointselector == 'manually'">
              </span>

              <span v-if="formData.readpointselector == 'SGLN'">
                <div class="form-group">
                  <label class=" col-sm-2 col-xs-3">(414)</label>
                  <div class="col-sm-3 col-xs-3">
                    <input
                      v-model="formData.readPoint.gln"
                      type="text"
                      class="form-control"
                      pattern="[0-9]{13,13}"
                      maxlength="13"
                      oninput="this.value=this.value.replace(/[^0-9]/g,'');"
                      title="GLN must be 13 digits"
                      placeholder="13 digits GLN"
                      :required="formData.readpointselector == 'SGLN'"
                    >
                  </div>
                </div>

                <!-- GCP Length for ReadPoint if the vocabularySyntax is URN -->
                <div v-if="formData.vocabularySyntax == 'URN' " class="form-group" style="white-space: pre">
                  <label class=" col-sm-2 col-xs-3" />
                  <div class="col-sm-3 col-xs-3">
                    <b-form-select v-model="formData.readPoint.gcpLength" :options="commonDropdownInfos.companyPrefixs" :required="formData.vocabularySyntax == 'URN' && formData.readpointselector=='SGLN'" />
                  </div>
                </div>

                <!-- Extension static value for Static ReadPoint type with GLN-->
                <div v-if="formData.readpointselector == 'SGLN' && formData.readPoint.extensionType == 'static' " class="form-group" style="white-space: pre">
                  <label class=" col-sm-2 col-xs-3">(254)</label>
                  <div class="col-sm-3 col-xs-3">
                    <input
                      v-model="formData.readPoint.extension"
                      type="text"
                      class="form-control"
                      placeholder="Extension"
                    >
                  </div>
                </div>

                <!-- Extension range value for Dynamic ReadPoint type with GLN-->
                <div v-if="formData.readpointselector == 'SGLN' && formData.readPoint.extensionType == 'dynamic' ">
                  <div class="form-group">
                    <label class=" col-sm-2 col-xs-3"> Range: </label>
                    <div class="col-sm-3 col-xs-3">
                      <input v-model="formData.readPoint.extensionFrom" type="text" class="form-control" placeholder="From extension" :required="formData.readpointselector == 'SGLN' && formData.readPoint.extensionType == 'dynamic'">
                    </div>
                  </div>

                  <div class="form-group">
                    <label class="col-sm-2 col-xs-3"> Formatter: </label>
                    <div class="col-sm-3 col-xs-3">
                      <input v-model="formData.readPoint.extensionFormat" type="text" class="form-control" placeholder="Extension Formatter like %03d,990%03d">
                    </div>
                  </div>
                </div>

                <div class="form-group">
                  <label class="col-sm-2 col-xs-3">Extension Type:</label>
                  <div class="col-sm-3 col-xs-3">
                    <div class="custom-control custom-radio">
                      <label class="custom-control custom-radio">
                        <input
                          id="readPointTypeStatic"
                          v-model="formData.readPoint.extensionType"
                          type="radio"
                          :required="formData.readpointselector == 'SGLN'"
                          class="custom-control-input"
                          value="static"
                          name="readPointType"
                        >
                        <span class="custom-control-indicator" />
                        <label class="custom-control-label" for="readPointTypeStatic">Static</label>
                      </label>
                    </div>

                    <div class="custom-control custom-radio">
                      <label class="custom-control custom-radio">
                        <input
                          id="readPointTypeDynamic"
                          v-model="formData.readPoint.extensionType"
                          type="radio"
                          :required="formData.readpointselector == 'SGLN'"
                          class="custom-control-input"
                          value="dynamic"
                          name="readPointType"
                        >
                        <span class="custom-control-indicator" />
                        <label class="custom-control-label" for="readPointTypeDynamic">Dynamic</label>
                      </label>
                    </div>
                  </div>
                </div>

              </span>
            </td>
          </tr>

          <tr>
            <td class="where">
              Business Location
            </td>

            <td>
              <b-form-select v-model="formData.businesslocationselector" class="form-control" @change="resetFields('bizLocation')">
                <b-form-select-option value="null" selected>
                  Choose Business Location type
                </b-form-select-option>
                <b-form-select-option value="gs1Key" disabled style="font-weight:bold;">
                  GS1 Key
                </b-form-select-option>
                <b-form-select-option value="SGLN">
                  &nbsp; SGLN + ext (Al 414 + Al 254)
                </b-form-select-option>
                <b-form-select-option style="font-weight:bold;" disabled value="other">
                  Other
                </b-form-select-option>
                <b-form-select-option value="manually">
                  &nbsp; Enter URI manually
                </b-form-select-option>
              </b-form-select>
            </td>

            <td v-if="formData.businesslocationselector != null" class="form-inline">
              <span v-if="formData.businesslocationselector == 'manually' ">
                <span class="horizontalSpace"> URI </span>
                <input v-model="formData.bizLocation.manualURI" type="text" class="form-control" placeholder="Ex: urn:epc:id:1234.121" :required="formData.businesslocationselector =='manually'">
              </span>

              <span v-if="formData.businesslocationselector == 'SGLN' ">
                <!-- GLN value for the Business Location -->
                <span class="horizontalSpace"> (414) </span>
                <input
                  v-model="formData.bizLocation.gln"
                  type="text"
                  class="form-control"
                  pattern="[0-9]{13,13}"
                  maxlength="13"
                  oninput="this.value=this.value.replace(/[^0-9]/g,'');"
                  title="GLN must be 13 digits"
                  placeholder="13 digits GLN"
                  :required="formData.businesslocationselector =='SGLN'"
                >

                <!-- GCP Length for bizLocation if the vocabularySyntax is URN -->
                <div v-if="formData.vocabularySyntax == 'URN' " class="form-group" style="white-space: pre">
                  <label class=" col-sm-2 col-xs-3" />
                  <div class="col-sm-3 col-xs-3">
                    <b-form-select v-model="formData.bizLocation.gcpLength" :options="commonDropdownInfos.companyPrefixs" :required="formData.vocabularySyntax == 'URN' && formData.businesslocationselector=='SGLN'" />
                  </div>
                </div>

                <!-- Extension static value for Static bizLocation type with GLN-->
                <div v-if="formData.businesslocationselector == 'SGLN' && formData.bizLocation.extensionType == 'static' " class="form-group" style="white-space: pre">
                  <label class=" col-sm-2 col-xs-3">(254)</label>
                  <div class="col-sm-3 col-xs-3">
                    <input
                      v-model="formData.bizLocation.extension"
                      type="text"
                      class="form-control"
                      placeholder="Extension"
                    >
                  </div>
                </div>

                <!-- Extension range value for Dynamic bizLocation type with GLN-->
                <div v-if="formData.businesslocationselector == 'SGLN' && formData.bizLocation.extensionType == 'dynamic' ">
                  <div class="form-group">
                    <label class=" col-sm-2 col-xs-3"> Range: </label>
                    <div class="col-sm-3 col-xs-3">
                      <input v-model="formData.bizLocation.extensionFrom" type="text" class="form-control" placeholder="From extension" :required="formData.businesslocationselector == 'SGLN' && formData.bizLocation.extensionType == 'dynamic'">
                    </div>
                  </div>

                  <div class="form-group">
                    <label class="col-sm-2 col-xs-3"> Formatter: </label>
                    <div class="col-sm-3 col-xs-3">
                      <input v-model="formData.bizLocation.extensionFormat" type="text" class="form-control" placeholder="Extension Formatter like %03d,990%03d">
                    </div>
                  </div>
                </div>

                <!-- Extension type value for bizLocation-->
                <div class="form-group">
                  <label class="col-sm-2 col-xs-3">Extension Type:</label>
                  <div class="col-sm-3 col-xs-3">
                    <div class="custom-control custom-radio">
                      <label class="custom-control custom-radio">
                        <input
                          id="bizLocationStatic"
                          v-model="formData.bizLocation.extensionType"
                          type="radio"
                          :required="formData.businesslocationselector == 'SGLN'"
                          class="custom-control-input"
                          value="static"
                          name="bizLocationType"
                        >
                        <span class="custom-control-indicator" />
                        <label class="custom-control-label" for="bizLocationStatic">Static</label>
                      </label>
                    </div>

                    <div class="custom-control custom-radio">
                      <label class="custom-control custom-radio">
                        <input
                          id="bizLocationDynamic"
                          v-model="formData.bizLocation.extensionType"
                          type="radio"
                          :required="formData.businesslocationselector == 'SGLN'"
                          class="custom-control-input"
                          value="dynamic"
                          name="bizLocationType"
                        >
                        <span class="custom-control-indicator" />
                        <label class="custom-control-label" for="bizLocationDynamic">Dynamic</label>
                      </label>
                    </div>
                  </div>
                </div>

              </span>
            </td>
          </tr>
          <!-- WHERE DIMENSION END -->

          <!--WHY DIMESION START -->
          <tr>
            <td :rowspan="rowspanWHY" style="background-color: #F7DC6F;text-align: center;">
              <strong>WHY</strong>
            </td>
            <td class="why">
              Business Step
            </td>
            <td>
              <b-form-select v-model="formData.businessStep" :options="commonDropdownInfos.businessSteps" />
            </td>
            <td v-if="formData.businessStep == 'BUSINESSSTEPENTER'">
              <input
                v-model="formData.EnterBusinessStepText"
                type="text"
                class="form-control"
                placeholder="Enter Business Step URI"
                title="Please Enter a valid URI"
                :required="formData.businessStep == 'BUSINESSSTEPENTER'"
              >
            </td>
          </tr>

          <tr>
            <td class="why">
              Disposition
            </td>
            <td>
              <b-form-select v-model="formData.disposition" :options="commonDropdownInfos.dispositions" />
            </td>
            <td v-if="formData.disposition == 'DISPOSITIONENTER'">
              <input v-model="formData.EnterDispositionText" type="text" class="form-control" placeholder="Enter Disposition URI" :required="formData.disposition == 'DISPOSITIONENTER'">
            </td>
          </tr>

          <tr>
            <td class="why">
              Persistent Disposition
            </td>
            <td>
              <button class="btn-btn-info" @click="addPD($event)">
                Add PD
              </button>
              <span v-for="pd in formData.persistentDispositionList" :key="pd.ID" class="form-inline verticleSpace">
                <span class="horizontalSpace">
                  <select v-model="pd.type" class="form-control">
                    <option class="dropdown-item" value="null" disabled> Choose </option>
                    <option class="dropdown-item" value="set" selected> Set </option>
                    <option class="dropdown-item" value="unset"> Unset </option>
                  </select>
                </span>
                <span class="horizontalSpace">
                  <b-form-select v-model="pd.value" :options="commonDropdownInfos.dispositions" />
                </span>
                <span class="horizontalSpace">
                  <button type="button" title="Delete Persistent Disposition" @click="deletePD(pd.ID)"><em class="bi bi-trash" /></button>
                </span>
              </span>
            </td>
          </tr>

          <tr>
            <td class="why">
              Business Transactions
            </td>
            <td>
              <button class="btn-btn-info" @click="addBusinessTransaction($event)">
                Add BTT
              </button>
              <span v-for="btt in formData.businessTransactionList" :key="btt.ID" class="form-inline verticleSpace">
                <span class="horizontalSpace">
                  <b-form-select v-model="btt.type" class="form-control" :options="commonDropdownInfos.BusinessTransactions" />&ensp;
                </span>
                <span class="horizontalSpace">
                  <input v-model="btt.bizTransaction" type="text" class="form-control">&ensp;
                </span>
                <span class="horizontalSpace">
                  <button type="button" title="Delete Business Transaction" @click="deleteBusinessTransaction(btt.ID)"><em class="bi bi-trash" /></button>
                </span>
              </span>
            </td>
          </tr>

          <tr v-if="formData.eventType === 'ObjectEvent' || formData.eventType === 'AggregationEvent' || formData.eventType === 'TransactionEvent' || formData.eventType === 'AssociationEvent' ">
            <td class="why">
              Sources
            </td>
            <td>
              <b-form-select v-model="formData.sources.type" :options="commonDropdownInfos.sources" />
            </td>

            <td v-if="formData.sources.type !== null" class="form-inline">
              <span v-if="formData.sources.type == 'OWNING_PARTY' || formData.sources.type == 'PROCESSING_PARTY'">
                <b-form-select v-model="formData.sources.glnType" class="form-control">
                  <b-form-select-option value="SGLN">
                    SGLN
                  </b-form-select-option>
                  <b-form-select-option value="PGLN">
                    PGLN
                  </b-form-select-option>
                </b-form-select>
              </span>

              <span v-if="formData.sources.type == 'OWNING_PARTY' || formData.sources.type == 'PROCESSING_PARTY' || formData.sources.type == 'LOCATION'">
                <span v-if="formData.sources.glnType == 'SGLN' || formData.sources.type == 'location&quot'" class="horizontalSpace"> (414) </span>
                <span v-if="formData.sources.glnType == 'PGLN'" class="horizontalSpace"> (417) </span>
                <input
                  v-model="formData.sources.gln"
                  type="text"
                  pattern="\d{13}"
                  oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                  maxlength="13"
                  oninvalid="this.setCustomValidity('Source GLN must be 13 digits')"
                  placeholder="Source GLN 13 digits"
                  class="form-control"
                >
                <span v-if="formData.sources.glnType == 'SGLN' || formData.sources.type == 'LOCATION'" class="horizontalSpace"> (254) </span>
                <span v-if="formData.sources.glnType == 'SGLN' || formData.sources.type == 'LOCATION'" class="horizontalSpace">
                  <input
                    v-model="formData.sources.extension"
                    type="text"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                    maxlength="13"
                    oninvalid="this.setCustomValidity('Sources Extension is required and must be in digits')"
                    placeholder="Source GLN Extension digits"
                    class="form-control"
                  >
                </span>
                <span v-if="formData.vocabularySyntax == 'URN' " class="horizontalSpace">
                  <b-form-select v-model="formData.sources.gcpLength" :options="commonDropdownInfos.companyPrefixs" />
                </span>
              </span>

              <span v-if="formData.sources.type == 'OTHER' ">
                <input v-model="formData.sources.OtherSourceURI1" type="text" placeholder="Enter Source Type" class="form-control">
                <input v-model="formData.sources.OtherSourceURI2" type="text" placeholder="Enter Source URI" class="form-control">
              </span>
            </td>
          </tr>
          <tr v-if="formData.eventType === 'ObjectEvent' || formData.eventType === 'AggregationEvent' || formData.eventType === 'TransactionEvent' || formData.eventType === 'AssociationEvent' ">
            <td class="why">
              Destinations
            </td>

            <td>
              <b-form-select v-model="formData.destinations.type" :options="commonDropdownInfos.sources" class="form-control" />
            </td>

            <td v-if="formData.destinations.type !== null" class="form-inline">
              <span v-if="formData.destinations.type == 'OWNING_PARTY' || formData.destinations.type == 'PROCESSING_PARTY'">
                <b-form-select v-model="formData.destinations.glnType" class="form-control">
                  <b-form-select-option value="SGLN">
                    SGLN
                  </b-form-select-option>
                  <b-form-select-option value="PGLN">
                    PGLN
                  </b-form-select-option>
                </b-form-select>
              </span>

              <span v-if="formData.destinations.type == 'OWNING_PARTY' || formData.destinations.type == 'PROCESSING_PARTY' || formData.destinations.type == 'LOCATION'">
                <span v-if="formData.destinations.glnType == 'SGLN' || formData.destinations.type == 'LOCATION'" class="horizontalSpace"> (414) </span>
                <span v-if="formData.destinations.glnType == 'PGLN'" class="horizontalSpace"> (417) </span>
                <input
                  v-model="formData.destinations.gln"
                  type="text"
                  pattern="\d{13}"
                  oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                  maxlength="13"
                  oninvalid="this.setCustomValidity('Destination GLN must be 13 digits')"
                  placeholder="Destination GLN 13 digits"
                  class="form-control"
                >
                <span v-if="formData.destinations.glnType == 'SGLN'" class="horizontalSpace"> (254) </span>
                <span v-if="formData.destinations.glnType == 'SGLN' || formData.destinations.type == 'LOCATION'">
                  <input
                    v-model="formData.destinations.extension"
                    type="text"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                    maxlength="13"
                    oninvalid="this.setCustomValidity('Destination Extension is required and must be in digits')"
                    placeholder="Destination GLN Extension digits"
                    class="form-control"
                  >
                </span>

                <span v-if="formData.vocabularySyntax == 'URN' " class="horizontalSpace">
                  <b-form-select v-model="formData.destinations.gcpLength" :options="commonDropdownInfos.companyPrefixs" />
                </span>
              </span>

              <span v-if="formData.destinations.type == 'OTHER'">
                <input v-model="formData.destinations.OtherDestinationURI1" type="text" placeholder="Enter Destination Type" class="form-control">
                <input v-model="formData.destinations.OtherDestinationURI2" type="text" placeholder="Enter Destination URI" class="form-control">
              </span>
            </td>
          </tr>
          <!--WHY DIMESION END -->

          <!-- HOW DIMENSION -->
          <tr>
            <td rowspan="1" style="background-color: #08A9A8;text-align: center;">
              <strong>HOW</strong>
            </td>
            <td style="background-color: #A5FBFA;">
              Conditions
            </td>
            <td>
              <button type="button" class="btn-btn-info" @click="addSensorInformation">
                Add Sensor Info
              </button>
              <span v-for="sensorElement in $store.state.modules.SensorElementsStore.sensorElementList" :key="sensorElement.ID" class="form-inline verticleSpace">
                <span>
                  Sensor Information - {{ sensorElement.ID + 1 }}
                </span>
                <span class="horizontalSpace">
                  <button type="button" title="Modify Sensor Element" @click="modifySensorElement($event, sensorElement.ID)"><em class="bi bi-pencil" /></button>
                  <button type="button" title="Delete Sensor Element" @click="deleteSensorElement($event, sensorElement.ID)"><em class="bi bi-trash" /></button>
                </span>
              </span>
            </td>
          </tr>
          <!--HOW DIMENSION ENDS -->

          <!-- OTHER FIELDS START -->
          <tr>
            <!-- FIRST ELEMENT of OTHER FIELDS -->
            <td :rowspan="rowSpanOtherFields" style="background-color: #F2F3F4;">
              <strong>OTHER</strong>
            </td>

            <td>Certification Info</td>
            <td>
              <input
                v-model="formData.certificationInfo"
                type="text"
                class="form-control"
                placeholder="Certification Info"
              >
            </td>
          </tr>

          <tr>
            <td> Extensions </td>
            <td>
              <button type="button" class="btn-btn-info" @click="userExtensionAddition($event,'userExtension')">
                Add Extension
              </button>
              <div
                v-for="extension in $store.state.modules.ExtensionDataStore.userExtensions"
                :key="extension.ID"
              >
                <tr style="white-space:nowrap">
                  <td v-if="extension.dataType == 'string'">
                    <span>{{ extension.namespace + ":" + extension.localName }}</span>
                    <input v-if="extension.dataType == 'string'" :value="extension.text" type="text" @input="extensionText($event, extension.ID, 'userExtension')">
                  </td>

                  <td v-if="extension.dataType == 'complex'">
                    <span>{{ extension.namespace + ":" + extension.localName }}</span>
                    <ExtensionComponent
                      v-if="extension.dataType == 'complex'"
                      :extension="extension"
                    />
                  </td>

                  <td>
                    <button type="button" class="modifyButton" title="Modify User Extension" @click="modifyExtension($event, extension.ID, 'userExtension')">
                      <em class="bi bi-pencil" />
                    </button>
                    <button type="button" class="deleteButton" title="Delete User Extension" @click="deleteExtension($event, extension.ID, 'userExtension')">
                      <em class="bi bi-trash" />
                    </button>
                  </td>
                </tr>
              </div>
            </td>
          </tr>

          <!-- SECOND ELEMENT of OTHER FIELDS -->
          <tr v-if="formData.eventType == 'ObjectEvent' || formData.eventType == 'TransformationEvent' ">
            <td> ILMD </td>
            <td>
              <button type="button" class="btn-btn-info" @click="userExtensionAddition($event,'ilmd')">
                Add ILMD
              </button>
              <div
                v-for="extension in $store.state.modules.ExtensionDataStore.ilmd"
                :key="extension.ID"
              >
                <tr style="white-space:nowrap">
                  <td v-if="extension.dataType == 'string'">
                    <span>{{ extension.namespace + ":" + extension.localName }}</span>
                    <input v-if="extension.dataType == 'string'" :value="extension.text" type="text" @input="extensionText($event, extension.ID, 'ilmd')">
                  </td>

                  <td v-if="extension.dataType == 'complex'">
                    <span>{{ extension.namespace + ":" + extension.localName }}</span>
                    <ExtensionComponent
                      v-if="extension.dataType == 'complex'"
                      :extension="extension"
                    />
                  </td>

                  <td>
                    <button type="button" class="modifyButton" title="Modify ILMD Extension" @click="modifyExtension($event, extension.ID, 'ilmd')">
                      <em class="bi bi-pencil" />
                    </button>
                    <button type="button" class="deleteButton" title="Delete ILMD Extension" @click="deleteExtension($event, extension.ID, 'ilmd')">
                      <em class="bi bi-trash" />
                    </button>
                  </td>
                </tr>
              </div>
            </td>
          </tr>

          <!-- THIRD ELEMENT of OTHER FIELDS -->
          <tr v-if="formData.eventType == 'TransformationEvent' ">
            <td> Transformation ID </td>
            <td>
              <input v-model="formData.transformationXformId" type="text" class="form-control" placeholder="Transformation ID URI (Optional)">
            </td>
          </tr>
          <!-- OTHER FIELDS END -->

          <!-- ERROR DECLARATION FIELDS START -->
          <tr v-if="!formData.ordinaryEvent">
            <td rowspan="4" style="background-color: #cc0000;text-align: center;">
              Error
            </td>
            <td class="errorDimension">
              Declaration Time
            </td>

            <td>
              <b-form-select v-model="formData.error.ErrorDeclarationTimeSelector" :options="commonDropdownInfos.eventTimeSelector" />
            </td>

            <td v-if="formData.error.ErrorDeclarationTimeSelector == 'SpecificTime' ">
              <input
                v-model="formData.error.ErrorDeclarationTime"
                type="datetime-local"
                class="form-control"
                title="Set Specific Event Time"
                :required="formData.ordinaryEvent == false && formData.error.ErrorDeclarationTimeSelector == 'SpecificTime'"
                step="1"
              >
            </td>

            <td v-if="formData.error.ErrorDeclarationTimeSelector == 'TimeRange' " class="form-inline">
              <span class="horizontalSpace"> From </span>
              <input
                v-model="formData.error.ErrorDeclarationTimeFrom"
                type="datetime-local"
                class="form-control"
                title="Error Declaration Time Range FROM"
                :required="formData.ordinaryEvent == false && formData.error.ErrorDeclarationTimeSelector == 'TimeRange'"
                step="1"
              >
              <span class="horizontalSpace"> To </span>
              <input
                v-model="formData.error.ErrorDeclarationTimeTo"
                type="datetime-local"
                class="form-control"
                title="Error Declaration Time Range TO"
                :required="formData.ordinaryEvent == false && formData.error.ErrorDeclarationTimeSelector == 'TimeRange'"
                step="1"
              >
            </td>

            <td>
              <b-form-select v-model="formData.error.ErrorTimeZone" :options="commonDropdownInfos.TimeZones" />
            </td>
          </tr>

          <tr v-if="!formData.ordinaryEvent">
            <td class="errorDimension">
              Reason
            </td>
            <td>
              <b-form-select v-model="formData.error.ErrorReasonType" :options="commonDropdownInfos.ErrorReasons" />
            </td>

            <td v-if="formData.error.ErrorReasonType == 'Other' ">
              <input v-model="formData.ErrorReasonOther" type="text" class="form-control" placeholder="Enter Error Reason">
            </td>
          </tr>

          <tr v-if="!formData.ordinaryEvent">
            <td class="errorDimension">
              Corrective IDs
            </td>
            <td>
              <button type="button" class="btn-btn-info" @click="addErrorCorrective($event)">
                Add Another
              </button>

              <span v-for="errCorr in formData.error.errorCorrectiveIdsList" :key="errCorr.ID" class="form-inline verticleSpace">
                <span class="horizontalSpace">
                  <input v-model="errCorr.value" type="text" class="form-control">
                </span>
                <span class="horizontalSpace">
                  <button type="button" title="Delete Error Corretive" @click="deleteErrCorrectiveInfo(errCorr.ID)"><em class="bi bi-trash" /></button>
                </span>
              </span>
            </td>
          </tr>

          <tr v-if="!formData.ordinaryEvent">
            <td class="errorDimension">
              Extension
            </td>
            <td>
              <button type="button" class="btn-btn-info" @click="userExtensionAddition($event,'ErrorExtension')">
                Add Another
              </button>
              <div
                v-for="extension in $store.state.modules.ExtensionDataStore.errorExtensions"
                :key="extension.ID"
              >
                <tr style="white-space:nowrap">
                  <td v-if="extension.dataType == 'string'">
                    <span>{{ extension.namespace + ":" + extension.localName }}</span>
                    <input v-if="extension.dataType == 'string'" :value="extension.text" type="text" @input="extensionText($event, extension.ID, 'ErrorExtension')">
                  </td>

                  <td v-if="extension.dataType == 'complex'">
                    <span>{{ extension.namespace + ":" + extension.localName }}</span>
                    <ExtensionComponent
                      v-if="extension.dataType == 'complex'"
                      :extension="extension"
                    />
                  </td>

                  <td>
                    <button type="button" class="modifyButton" title="Modify Error Extension" @click="modifyExtension($event, extension.ID, 'ErrorExtension')">
                      <em class="bi bi-pencil" />
                    </button>
                    <button type="button" class="deleteButton" title="Delete Error Extension" @click="deleteExtension($event, extension.ID, 'ErrorExtension')">
                      <em class="bi bi-trash" />
                    </button>
                  </td>
                </tr>
              </div>
            </td>
          </tr>
          <!-- ERROR DECLARATION FIELDS END -->
        </tbody>
      </table>
    </b-form>
    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="configureEventInfoForm">
        OK
      </b-btn>
    </template>
    <ExtensionModal v-if="$store.state.modules.ExtensionDataStore.extensionModal" />
    <SensorElementsModal v-if="$store.state.modules.SensorElementsStore.sensorModal" />
  </b-modal>
</template>

<script>
import { mapGetters } from 'vuex'
import SensorElementsModal from '@/components/SensorElementsModal.vue'
import ExtensionModal from '@/components/ExtensionModal.vue'

function initialState () {
  return {
    commonDropdownInfos: {},
    selected: null,
    rowspanWHAT: 1,
    EventTypeRowSpan: 5,
    rowspanWHY: 4,
    rowSpanOtherFields: 2,
    eventType: [],
    bttArray: {
      type: [],
      bizTransaction: []
    },
    errCorrArray: {
      corrValue: []
    },
    formData: {
      eventCount: 1,
      identifierSyntax: 'URN',
      vocabularySyntax: 'URN',
      eventType: null,
      importEvent: false,
      ordinaryEvent: true,
      eventTimeSelector: 'SpecificTime',
      eventTime: {},
      RecordTimeOption: 'no',
      recordTimeType: 'SAME_AS_EVENT_TIME',
      readpointselector: null,
      readPoint: {
        gcpLength: null
      },
      businesslocationselector: null,
      bizLocation: {
        gcpLength: null
      },
      businessStep: null,
      disposition: null,
      sources: {
        type: null,
        glnType: 'SGLN',
        gcpLength: null
      },
      destinations: {
        type: null,
        glnType: 'SGLN',
        gcpLength: null
      },
      eventID: false,
      eventIdType: 'UUID',
      hashAlgorithm: 'sha-256',
      action: 'ADD',
      error: {
        ErrorTimeZone: '+02:00',
        ErrorDeclarationTimeSelector: 'SpecificTime',
        ErrorReasonType: null,
        errorCorrectiveIdsList: [],
        errorCorrectiveCount: 0
      },
      persistentDispositionCount: 0,
      persistentDispositionList: [],
      bizTransactionCount: 0,
      businessTransactionList: [],
      sensorElementList: [],
      referencedIdentifier: [],
      outputReferencedIdentifier: [],
      parentReferencedIdentifier: {},
      userExtensions: [],
      ilmd: [],
      parentID: '',
      epcList: [],
      quantityList: [],
      outputEPCList: [],
      outputQuantityList: []
    }
  }
}

export default {
  components: {
    SensorElementsModal,
    ExtensionModal
  },
  data () {
    return initialState()
  },
  computed: {
    ...mapGetters(['modules.IdentifiersStore.epc'])
  },
  mounted () {
    // Reset all the data for the modal
    Object.assign(this.$data, initialState())
    this.commonDropdownInfos = require('~/static/EpcisData/CommonDropdown.js')

    // Get the current eventType based on the EventNode on which user has clicked and assign the value
    const currentEventInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureNodeEventInfoStore.currentNodeInfo))

    // Check if user is trying to modify the values if so then populate the modal with existing information
    if (JSON.stringify(this.$store.state.modules.ConfigureNodeEventInfoStore.getExistingNodeInfo) !== '{}') {
      this.formData = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureNodeEventInfoStore.getExistingNodeInfo))
      this.eventTypeChange(this.formData.eventType)
    } else {
      this.formData.eventType = currentEventInfo.eventType
      this.eventTypeChange(this.formData.eventType)

      let now = new Date()
      now.setMinutes(now.getMinutes() - now.getTimezoneOffset())
      now.setSeconds(now.getSeconds(), 0)
      now = now.toISOString().slice(0, -1)

      let yesterday = new Date()
      yesterday.setDate(new Date().getDate() - 1)
      yesterday.setMinutes(yesterday.getMinutes() - yesterday.getTimezoneOffset())
      yesterday.setSeconds(yesterday.getSeconds(), 0)
      yesterday = yesterday.toISOString().slice(0, -1)

      this.formData.eventTime = { specificTime: now, fromTime: yesterday, toTime: now, timeZoneOffset: '+02:00' }
      this.formData.error.ErrorDeclarationTime = now
      this.formData.error.ErrorDeclarationTimeFrom = yesterday
      this.formData.error.ErrorDeclarationTimeTo = now
    }
  },
  methods: {
    // Function to change the identifier syntax based on user selection
    identifierSyntaxChange (identifierSyntax) {
      this.$store.commit('modules/IdentifiersStore/populateIdentifiersType', { identifierSyntax })
    },

    // Actions to perform after the submission of the event information for each event
    submitEventData (event) {
      // Add the values for the form for further modification of the Node info if user tries to modify
      this.formData.sensorElementList = this.$store.state.modules.SensorElementsStore.sensorElementList
      this.formData.userExtensions = this.$store.state.modules.ExtensionDataStore.userExtensions
      this.formData.ilmd = this.$store.state.modules.ExtensionDataStore.ilmd
      this.formData.error.errorExtensions = this.$store.state.modules.ExtensionDataStore.errorExtensions

      // On Sumission of the modal toggle the  to hide the modal
      this.$store.commit('modules/ConfigureNodeEventInfoStore/hideNodeEventInfoModal')

      // On Submission of the modal store the infromation related to event in an Array
      this.$store.commit('modules/ConfigureNodeEventInfoStore/populateNodeEventInfo', { eventInfo: this.formData })

      // Emit the function to update the Annotations based on event info
      this.$root.$emit('populateNodeInfo')
    },

    // Trigger the function based on the change of the event Object/Aggregation/Transaction/Transformation
    eventTypeChange (event) {
      // Based on eventType show the respective information corresponding to eventType
      switch (this.formData.eventType) {
        case 'ObjectEvent':
          this.rowspanWHAT = 2
          this.rowspanWHY = 6
          this.rowSpanOtherFields = 3
          this.EventTypeRowSpan = 6
          break
        case 'AggregationEvent':
        case 'TransactionEvent':
        case 'AssociationEvent':
          this.rowspanWHAT = 3
          this.rowspanWHY = 6
          this.rowSpanOtherFields = 2
          this.EventTypeRowSpan = 6
          break
        case 'TransformationEvent':
          this.rowspanWHAT = 4
          this.rowspanWHY = 4
          this.rowSpanOtherFields = 4
          this.EventTypeRowSpan = 5
          break
        default:
          this.rowspanWHAT = 1
          this.rowspanWHY = 4
          this.rowSpanOtherFields = 2
          this.EventTypeRowSpan = 5
          break
      }
    },

    // Function that will be triggered on click of the Add sensor Information
    addSensorInformation () {
      this.$store.commit('modules/SensorElementsStore/showSensorModal')
    },

    // Based on user click show the Sensor Element modal to modify the existing information
    modifySensorElement (event, sensorElementID) {
      event.preventDefault()
      this.$store.commit('modules/SensorElementsStore/modifySensorElement', { sensorElementID })
      this.$store.commit('modules/SensorElementsStore/showSensorModal')
    },

    // Based on user click remove the respective sensor element information
    deleteSensorElement (event, sensorElementID) {
      event.preventDefault()
      this.$store.commit('modules/SensorElementsStore/deleteSensorElement', sensorElementID)
    },

    // Function to add the Disposition for the respective event
    addPD (event) {
      event.preventDefault()
      const persistentDispObj = {}
      persistentDispObj.ID = this.formData.persistentDispositionCount
      persistentDispObj.type = null
      persistentDispObj.value = null
      this.formData.persistentDispositionList.push(persistentDispObj)
      this.formData.persistentDispositionCount++
    },

    // Delete Persistnet Disposition based on user click of the respective PD
    deletePD (pdID) {
      this.formData.persistentDispositionList.splice(this.formData.persistentDispositionList.findIndex(pd => pd.ID === pdID), 1)
    },

    // Add the Business Trasactions based on the addition by user
    addBusinessTransaction (event) {
      event.preventDefault()
      const bttObj = {}
      bttObj.ID = this.formData.bizTransactionCount
      bttObj.type = null
      bttObj.bizTransaction = null
      this.formData.businessTransactionList.push(bttObj)
      this.formData.bizTransactionCount++
    },

    // Delete Business Transactions based on user click of the respective Business Transaction element
    deleteBusinessTransaction (bttID) {
      this.formData.businessTransactionList.splice(this.formData.businessTransactionList.findIndex(btt => btt.ID === bttID), 1)
    },

    // Add the Error Corrective information
    addErrorCorrective (event) {
      event.preventDefault()
      const errorCorrectiveObj = {}
      errorCorrectiveObj.ID = this.formData.error.errorCorrectiveCount
      errorCorrectiveObj.value = null
      this.formData.error.errorCorrectiveIdsList.push(errorCorrectiveObj)
      this.formData.error.errorCorrectiveCount++
    },

    // Delete Error CorrectiveIDs based on user click of the respective Business Transaction element
    deleteErrCorrectiveInfo (correctiveID) {
      this.formData.error.errorCorrectiveIdsList.splice(this.formData.error.errorCorrectiveIdsList.findIndex(corrective => corrective.ID === correctiveID), 1)
    },

    // Function to add the User extension onclick of the Add button
    userExtensionAddition (event, type) {
      event.preventDefault()
      this.$store.commit('modules/ExtensionDataStore/showExtensionModal')
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', type)
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', null)
    },

    // Based on the text change in String extension input change the value in List
    extensionText (event, extensionID, type) {
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', null)
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', type)
      this.$store.commit('modules/ExtensionDataStore/extensionText', { text: event.target.value, extensionID })
    },

    // Function to modify the User Extension onClick of the modify button
    modifyExtension (event, extensionID, type) {
      event.preventDefault()
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', type)
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', null)
      this.$store.commit('modules/ExtensionDataStore/modifyExtension', extensionID)
      this.$store.commit('modules/ExtensionDataStore/showExtensionModal')
    },

    // Function to delete the User extension onclick of the Delete button
    deleteExtension (event, extensionID, type) {
      event.preventDefault()
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', type)
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', null)
      this.$store.commit('modules/ExtensionDataStore/deleteExtension', extensionID)
    },

    // Function to delete parent identifiers during the import of events -> design
    deleteParentIdentifiers (event, type) {
      event.preventDefault()
      if (type === 'ParentIdentifier') {
        this.formData.parentIdentifier = ''
      }
    },

    // Function to delete instance identifiers during the import of events->design
    deleteInstanceIdentifiers (event, type) {
      event.preventDefault()
      if (type === 'EPC') {
        this.formData.epcList = []
      } else if (type === 'OutputEPC') {
        this.formData.outputEPCList = []
      }
    },

    // Function to delete quantity identifiers during the import of events->design
    deleteQuantityIdentifiers (event, type) {
      event.preventDefault()
      if (type === 'Class') {
        this.formData.quantityList = []
      } else if (type === 'OutputClass') {
        this.formData.outputQuantityList = []
      }
    },

    // On change of the readPoint/BizLocation reset the values present within the formData so only required data are present within the formData
    resetFields (fieldName) {
      switch (fieldName) {
        case 'readPoint':
          this.formData.readPoint = { gcpLength: null }
          break
        case 'bizLocation':
          this.formData.bizLocation = { gcpLength: null }
      }
    },

    // Onclick of the cancel/ESC button hide the modal and toggle the modal flag
    cancel () {
      this.$store.commit('modules/ConfigureNodeEventInfoStore/hideNodeEventInfoModal')
    }
  }
}
</script>

<style>
#eventForm{
  display: flex;
  zoom:85%;
}

.table-nonfluid {
   width: auto !important;
}

.table td {
   text-align: center;
}

.table > tbody > tr > td {
    vertical-align: middle;
}

table td[class*=col-], table th[class*=col-] {
    position: static;
    display: table-cell;
    float: none;
}

.table-bordered {
   width: auto !important;
}

#eventDimension{
  background-color: #F2F3F4;
  text-align: center;
}

#whatDimension{
  background-color: #607fbf;
  text-align: center;
}

.what{
  background-color: #dfe5f1;
}

.when{
  background-color: #eedded;
  text-align: center;
}

.why{
background-color: #faf4d5;
text-align: center;
}

.horizontalSpace{
  padding-right: 8px;
  padding-left: 8px;
}

.verticleSpace{
  padding-top:8px;
  padding-bottom: 8px;
}

.where{
  background-color: #dae9e4;
}

::-webkit-input-placeholder {
   text-align: center;
}

.errorDimension{
  background-color: #f2c2c2;
}

.modifyButton{
  color:#F8C471
}

.deleteButton{
  color:#dc3545
}
</style>
