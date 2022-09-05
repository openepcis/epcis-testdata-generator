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
  <div>
    <div class="row float-right">
      <div class="col-5">
        <button type="button" class="btn btn-info" title="Export Test Data Info to Local" @click="exportData()">
          Export
        </button>
      </div>
      <div class="col-3">
        <span>
          <input
            ref="readEventsInfo"
            type="file"
            hidden
            accept=".json"
            @change="importInfo()"
          >
          <button
            type="button"
            class="btn btn-secondary"
            title="Upload Test Data Info from Local"
            @click="$refs.readEventsInfo.click()"
          >
            Import
          </button>
        </span>
      </div>
    </div>
    <div id="eventForm" class="row" style="margin-top:1%">
      <form ref="testDataForm" class="form-horizontal" autocomplete="on" @submit.prevent="generateTestData">
        <table class="table table-bordered">
          <caption />
          <th id="tableHeader" />
          <tbody>
            <!-- COMMON INFORMATION APPLICABLE FOR ALL TYPE OF EVENTS -->
            <tr>
              <td id="eventDimension" :rowspan="EventTypeRowSpan" />
              <td> Event Count </td>
              <td>
                <input
                  :value="$store.state.modules.DesignTestDataStore.eventCount"
                  type="number"
                  min="1"
                  max="1000"
                  class="form-control"
                  oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                  oninvalid="this.setCustomValidity('Number of Events must be between 1 and 1000')"
                  placeholder="Number of EPCIS events"
                  required
                  @input="eventCountChange($event)"
                >
              </td>
            </tr>
            <tr>
              <td>Object identifier syntax</td>
              <td>
                <div class="custom-control custom-radio custom-control-inline">
                  <input
                    id="identifierSyntaxURN"
                    :checked="$store.state.modules.IdentifiersStore.identifierSyntax == 'URN'"
                    type="radio"
                    class="custom-control-input"
                    value="URN"
                    name="identifierSyntax"
                    @change="identifierSyntaxChange('URN')"
                  >
                  <label class="custom-control-label" for="identifierSyntaxURN">URN</label>
                </div>

                <div class="custom-control custom-radio custom-control-inline">
                  <input
                    id="identifierSyntaxWebURI"
                    :checked="$store.state.modules.IdentifiersStore.identifierSyntax == 'WebURI'"
                    type="radio"
                    class="custom-control-input"
                    value="WebURI"
                    name="identifierSyntax"
                    @change="identifierSyntaxChange('WebURI')"
                  >
                  <label class="custom-control-label" for="identifierSyntaxWebURI">Web URI</label>
                </div>
              </td>
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
                <b-form-select :value="$store.state.modules.DesignTestDataStore.eventType" :options="commonDropdownInfos.eventType" required @change="eventTypeChange($event)" />
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

            <tr v-if="$store.state.modules.DesignTestDataStore.eventType == 'ObjectEvent' || $store.state.modules.DesignTestDataStore.eventType == 'AggregationEvent' || $store.state.modules.DesignTestDataStore.eventType == 'TransactionEvent' || $store.state.modules.DesignTestDataStore.eventType == 'AssociationEvent' ">
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

            <!-- WHAT DIMENSION INFORMATION -->
            <tr>
              <td id="whatDimension" :rowspan="rowspanWHAT">
                <strong>WHAT</strong>
              </td>

              <!--When No EVENT TYPE has been selected-->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == null" class="what" />
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == null">
                Please Select the required Event type
              </td>

              <!-- When OBJECT EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'ObjectEvent' " class="what">
                EPCs
              </td>

              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'ObjectEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.instanceIdentifiersList.length === 0" type="button" class="btn-btn-info" @click="showInstanceIdentifiersModal('ObjectEventEPC')">
                  Add EPCs
                </button>

                <span v-for="epc in $store.state.modules.IdentifiersStore.instanceIdentifiersList" :key="epc.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(epc)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Instance Identifier" @click="modifyInstanceIdentifier(epc.ID, 'ObjectEvent')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Instance Identifier" @click="deleteInstanceIdentifier(epc.ID, 'ObjectEvent')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When AGGREGATION EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AggregationEvent' " class="what">
                Parent ID
              </td>

              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AggregationEvent' ">
                <div v-if="$store.state.modules.IdentifiersStore.parentIdentifiersList == 0">
                  <button type="button" class="btn-btn-info" @click="parentIdPopulator('AggregationEvent')">
                    Add Parent ID
                  </button>
                </div>

                <span v-for="parent in $store.state.modules.IdentifiersStore.parentIdentifiersList" :key="parent.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(parent)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Parent Identifier" @click="modifyParentID(parent.ID, 'AggregationEventParentID')">
                        <em class="bi bi-pencil" />
                      </button>
                      <button type="button" title="Delete Parent Identifier" @click="deleteParentID(parent.ID, 'AggregationEventParentID')">
                        <em class="bi bi-trash" />
                      </button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When TRANSACTION EVENT has been Selected show PARENT ID -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransactionEvent' " class="what">
                Parent ID
              </td>

              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransactionEvent' ">
                <button v-if="$store.state.modules.IdentifiersStore.parentIdentifiersList == 0" type="button" class="btn-btn-info" @click="parentIdPopulator('TransactionEventParentID')">
                  Add Parent ID
                </button>

                <span v-for="parent in $store.state.modules.IdentifiersStore.parentIdentifiersList" :key="parent.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(parent)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Parent Identifier" @click="modifyParentID(parent.ID, 'TransactionEventParentID')">
                        <em class="bi bi-pencil" />
                      </button>
                      <button type="button" title="Delete Parent Identifier" @click="deleteParentID(parent.ID, 'TransactionEventParentID')">
                        <em class="bi bi-trash" />
                      </button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When TRANSFORMATION EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' " class="what">
                Input EPCs
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.instanceIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showInstanceIdentifiersModal('TransformationEventInputEPC')">
                  Add Input EPCs
                </button>

                <span v-for="epc in $store.state.modules.IdentifiersStore.instanceIdentifiersList" :key="epc.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(epc)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify InputEPCs Identifier" @click="modifyInstanceIdentifier(epc.ID, 'TransformationEvent')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete InputEPCs Identifier" @click="deleteInstanceIdentifier(epc.ID, 'TransformationEvent')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When ASSOCIATION EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AssociationEvent' " class="what">
                Parent ID
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AssociationEvent' ">
                <button v-if="$store.state.modules.IdentifiersStore.parentIdentifiersList == 0" type="button" class="btn-btn-info" @click="parentIdPopulator('AssociationEventParentID')">
                  Add Parent ID
                </button>

                <span v-for="parent in $store.state.modules.IdentifiersStore.parentIdentifiersList" :key="parent.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(parent)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Parent Identifier" @click="modifyParentID(parent.ID,'AssociationEventParentID')">
                        <em class="bi bi-pencil" />
                      </button>
                      <button type="button" title="Delete Parent Identifier" @click="deleteParentID(parent.ID,'AssociationEventParentID')">
                        <em class="bi bi-trash" />
                      </button>
                    </span>
                  </span>
                </span>
              </td>
            </tr>

            <!-- 2nd ELEMENT OF THE WHAT DIMENSION -->
            <tr ng-show="$store.state.modules.DesignTestDataStore.eventType !== null ">
              <!-- When OBJECT EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'ObjectEvent' " class="what">
                Quantities
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'ObjectEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.classIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showClassIdentifiersModal('ObjectEventQuantity')">
                  Add Quantites
                </button>

                <span v-for="quantity in $store.state.modules.IdentifiersStore.classIdentifiersList" :key="quantity.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(quantity)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Quantities" @click="modifyClassIdentifier(quantity.ID, 'ObjectEventQuantity')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Quantities" @click="deleteClassIdentifier(quantity.ID, 'ObjectEventQuantity')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When AGGREGATION EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AggregationEvent' " class="what">
                Child EPCs
              </td>

              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AggregationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.instanceIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showInstanceIdentifiersModal('AggregationEventChildEPC')">
                  Add Child EPCs
                </button>

                <span v-for="epc in $store.state.modules.IdentifiersStore.instanceIdentifiersList" :key="epc.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(epc)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Child EPCs" @click="modifyInstanceIdentifier(epc.ID, 'AggregationEvent')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Child EPCs" @click="deleteInstanceIdentifier(epc.ID, 'AggregationEvent')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When TRANSACTION EVENT has been Selected show EPCS -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransactionEvent' " class="what">
                EPCs
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransactionEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.instanceIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showInstanceIdentifiersModal('TransactionEventEPC')">
                  Add EPCs
                </button>

                <span v-for="epc in $store.state.modules.IdentifiersStore.instanceIdentifiersList" :key="epc.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(epc)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Instance Identifier" @click="modifyInstanceIdentifier(epc.ID, 'TransactionEvent')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Instance Identifier" @click="deleteInstanceIdentifier(epc.ID, 'TransactionEvent')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- Transformation Event OUTPUT EPCS -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' " class="what">
                Input Quantities
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.classIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showClassIdentifiersModal('TransformationEventInputQuantities')">
                  Add Input Quantities
                </button>

                <span v-for="quantity in $store.state.modules.IdentifiersStore.classIdentifiersList" :key="quantity.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(quantity)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Input Quantities" @click="modifyClassIdentifier(quantity.ID, 'TransformationEvent')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Input Quantities" @click="deleteClassIdentifier(quantity.ID, 'TransformationEvent')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When ASSOCIATION EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AssociationEvent' " class="what">
                Child EPCs
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AssociationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.instanceIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showInstanceIdentifiersModal('AssociationEventChildEPC')">
                  Add Child EPCs
                </button>

                <span v-for="quantity in $store.state.modules.IdentifiersStore.instanceIdentifiersList" :key="quantity.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(quantity)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Child EPCs" @click="modifyInstanceIdentifier(epc.ID, 'AssociationEvent')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Child EPCs" @click="deleteInstanceIdentifier(epc.ID, 'AssociationEvent')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>
            </tr>

            <!-- 3rd ELEMENT OF THE WHAT DIMENSION -->
            <tr>
              <!-- AGGREGATION EVENT Child Quantites -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AggregationEvent' " class="what">
                Child Quantities
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AggregationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.classIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showClassIdentifiersModal('AggregationEventQuantity')">
                  Add Child Quantities
                </button>

                <span v-for="quantity in $store.state.modules.IdentifiersStore.classIdentifiersList" :key="quantity.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(quantity)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Quantities" @click="modifyClassIdentifier(quantity.ID, 'AggregationEventQuantity')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Quantities" @click="deleteClassIdentifier(quantity.ID, 'AggregationEventQuantity')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- TRANSACTION EVENT Quantities -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransactionEvent' " class="what">
                Quantities
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransactionEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.classIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showClassIdentifiersModal('TransactionEventQuantity')">
                  Add Quantities
                </button>

                <span v-for="quantity in $store.state.modules.IdentifiersStore.classIdentifiersList" :key="quantity.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(quantity)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Quantities" @click="modifyClassIdentifier(quantity.ID, 'TransactionEventQuantity')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Quantities" @click="deleteClassIdentifier(quantity.ID, 'TransactionEventQuantity')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- Transformation Event OUTPUT EPCS -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' " class="what">
                Output EPCs
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.outputInstanceIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showInstanceIdentifiersModal('TransformationEventOutputEPC')">
                  Add Output EPCs
                </button>

                <span v-for="epc in $store.state.modules.IdentifiersStore.outputInstanceIdentifiersList" :key="epc.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(epc)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Output EPC" @click="modifyInstanceIdentifier(epc.ID, 'TransformationEventOutputEPC')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Output EPC" @click="deleteInstanceIdentifier(epc.ID, 'TransformationEventOutputEPC')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>

              <!-- When ASSOCIATION EVENT has been selected -->
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AssociationEvent' " class="what">
                Child Quantities
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'AssociationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.classIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showClassIdentifiersModal('AssociationEventQuantity')">
                  Add Child Quantities
                </button>

                <span v-for="quantity in $store.state.modules.IdentifiersStore.classIdentifiersList" :key="quantity.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(quantity)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Child Quantities" @click="modifyClassIdentifier(quantity.ID, 'AssociationEventQuantity')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Child Quantities" @click="deleteClassIdentifier(quantity.ID, 'AssociationEventQuantity')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>
            </tr>

            <!-- 4th ELEMENT OF THE WHAT DIMENSION -->
            <tr>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' " class="what">
                Ouput Quantities
              </td>
              <td v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' ">
                <button v-show="$store.state.modules.IdentifiersStore.outputclassIdentifiersList.length == 0" type="button" class="btn-btn-info" @click="showClassIdentifiersModal('TransformationEventOutputQuantity')">
                  Add Output Quantites
                </button>

                <span v-for="quantity in $store.state.modules.IdentifiersStore.outputclassIdentifiersList" :key="quantity.ID" class="verticleSpace">
                  <span class="form-inline">
                    <span class="horizontalSpace">
                      {{ Object.keys(quantity)[3].toUpperCase() }}
                    </span>

                    <span class="horizontalSpace">
                      <button type="button" title="Modify Output Quantities" @click="modifyClassIdentifier(quantity.ID, 'TransformationEventOutputQuantity')"><em class="bi bi-pencil" /></button>
                      <button type="button" title="Delete Output Quantities" @click="deleteClassIdentifier(quantity.ID, 'TransformationEventOutputQuantity')"><em class="bi bi-trash" /></button>
                    </span>
                  </span>
                </span>
              </td>
            </tr>

            <!-- WHEN DIMENSION FIELFDS -->
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

            <!-- WHERE DIMENSION START -->
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
                  <span class="horizontalSpace"> (414) </span>
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

                  <!-- GCP Length for ReadPoint if the vocabularySyntax is URN -->
                  <span v-if="formData.vocabularySyntax == 'URN' " class="horizontalSpace">
                    <b-form-select v-model="formData.readPoint.gcpLength" :options="commonDropdownInfos.companyPrefixs" :required="formData.vocabularySyntax == 'URN' && formData.readpointselector=='SGLN'" />
                  </span>

                  <!-- Extension static value for Static ReadPoint type with GLN-->
                  <span v-if="formData.readpointselector == 'SGLN' && formData.readPoint.extensionType == 'static' " class="horizontalSpace">
                    <span> (254) </span>
                    <input v-model="formData.readPoint.extension" type="text" class="form-control" placeholder="Extension">
                  </span>

                  <!-- Extension range value for Dynamic ReadPoint type with GLN-->
                  <span v-if="formData.readpointselector == 'SGLN' && formData.readPoint.extensionType == 'dynamic' " class="horizontalSpace">
                    <span> Range: </span>
                    <input v-model="formData.readPoint.extensionFrom" type="text" class="form-control" placeholder="From extension" :required="formData.readpointselector == 'SGLN' && formData.readPoint.extensionType == 'dynamic'">

                    <span> Formatter: </span>
                    <input v-model="formData.readPoint.extensionFormat" type="text" class="form-control" placeholder="Extension Formatter like %03d,990%03d">
                  </span>

                  <div class="form-inline horizontalSpace verticleSpace">
                    <span class="horizontalSpace">Extension Type: </span>
                    <div class="custom-control custom-radio">
                      <input
                        id="readPointTypeStatic"
                        v-model="formData.readPoint.extensionType"
                        type="radio"
                        :required="formData.readpointselector == 'SGLN'"
                        class="custom-control-input"
                        value="static"
                        name="readPointType"
                      >
                      <label class="custom-control-label" for="readPointTypeStatic">Static</label>
                    </div>
                    <div class="custom-control custom-radio">
                      <div class="custom-control custom-radio">
                        <input
                          id="readPointTypeDynamic"
                          v-model="formData.readPoint.extensionType"
                          type="radio"
                          :required="formData.readpointselector == 'SGLN'"
                          class="custom-control-input"
                          value="dynamic"
                          name="readPointType"
                        >
                        <label class="custom-control-label" for="readPointTypeDynamic">Dynamic</label>
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
                  <b-form-select-option value="null" disabled selected>
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

                  <!-- GCP Length for BizLocation if the vocabularySyntax is URN -->
                  <span v-if="formData.vocabularySyntax == 'URN' " class="horizontalSpace">
                    <b-form-select v-model="formData.bizLocation.gcpLength" :options="commonDropdownInfos.companyPrefixs" :required="formData.vocabularySyntax == 'URN' && formData.businesslocationselector =='SGLN'" />
                  </span>

                  <!-- Extension static value for Static BizLocation type with GLN-->
                  <span v-if="formData.businesslocationselector == 'SGLN' && formData.bizLocation.extensionType == 'static' " class="horizontalSpace">
                    <span> (254) </span>
                    <input v-model="formData.bizLocation.extension" type="text" class="form-control" placeholder="Extension">
                  </span>

                  <!-- Extension range value for Dynamic BizLocation type with GLN-->
                  <span v-if="formData.businesslocationselector == 'SGLN' && formData.bizLocation.extensionType == 'dynamic' " class="horizontalSpace">
                    <span> Range: </span>
                    <input v-model="formData.bizLocation.extensionFrom" type="text" class="form-control" placeholder="From extension" :required="formData.businesslocationselector == 'SGLN' && formData.bizLocation.extensionType == 'dynamic'">

                    <span> Formatter: </span>
                    <input v-model="formData.bizLocation.extensionFormat" type="text" class="form-control" placeholder="Extension Formatter like %03d,990%03d">
                  </span>

                  <div class="form-inline horizontalSpace verticleSpace">
                    <span class="horizontalSpace">Extension Type: </span>
                    <div class="custom-control custom-radio">
                      <input
                        id="bizLocationStatic"
                        v-model="formData.bizLocation.extensionType"
                        type="radio"
                        :required="formData.businesslocationselector == 'SGLN'"
                        class="custom-control-input"
                        value="static"
                        name="bizLocationType"
                      >
                      <label class="custom-control-label" for="bizLocationStatic">Static</label>
                    </div>
                    <div class="custom-control custom-radio">
                      <div class="custom-control custom-radio">
                        <input
                          id="bizLocationDynamic"
                          v-model="formData.bizLocation.extensionType"
                          type="radio"
                          :required="formData.businesslocationselector == 'SGLN'"
                          class="custom-control-input"
                          value="dynamic"
                          name="bizLocationType"
                        >
                        <label class="custom-control-label" for="bizLocationDynamic">Dynamic</label>
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
                <button class="btn-btn-info" @click="addBTT($event)">
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
                    <button type="button" title="Delete Business Transaction" @click="deleteBizTransaction(btt.ID)"><em class="bi bi-trash" /></button>
                  </span>
                </span>
              </td>
            </tr>

            <tr v-if="$store.state.modules.DesignTestDataStore.eventType === 'ObjectEvent' || $store.state.modules.DesignTestDataStore.eventType === 'AggregationEvent' || $store.state.modules.DesignTestDataStore.eventType === 'TransactionEvent' || $store.state.modules.DesignTestDataStore.eventType === 'AssociationEvent' ">
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

            <tr v-if="$store.state.modules.DesignTestDataStore.eventType === 'ObjectEvent' || $store.state.modules.DesignTestDataStore.eventType === 'AggregationEvent' || $store.state.modules.DesignTestDataStore.eventType === 'TransactionEvent' || $store.state.modules.DesignTestDataStore.eventType === 'AssociationEvent' ">
              <td class="why">
                Destinations
              </td>

              <td>
                {{ formData.destinationsType }}
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
                  <span class="horizontalSpace">
                    <button class="btn btn-danger" @click="deleteSensorElement($event, sensorElement.ID)"><em class="bi bi-trash" /></button>
                  </span>
                  <span>
                    Sensor Information
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

              <td> Extensions </td>
              <td>
                <button type="button" class="btn-btn-info" @click="userExtensionAddition($event,'userExtension')">
                  Add Extension
                </button>
                <div
                  v-for="extension in $store.state.modules.ExtensionDataStore.userExtensions"
                  :key="extension.ID"
                  class="form-inline horizontalSpace verticleSpace"
                >
                  <span>{{ extension.namespace + ":" + extension.localName }}</span>
                  <input v-if="extension.dataType == 'string'" :value="extension.text" type="text" @input="extensionText($event, extension.ID, 'userExtension')">
                  <span v-if="extension.dataType == 'complex'" class="horizontalSpace verticleSpace">
                    <ExtensionComponent
                      v-if="extension.dataType == 'complex'"
                      :extension="extension"
                    />
                  </span>
                  <span class="horizontalSpace verticleSpace" />
                  <button class="btn btn-danger" @click="deleteExtension($event,extension.ID, 'userExtension')">
                    <em class="bi bi-trash" />
                  </button>
                </div>
              </td>
            </tr>

            <!-- Second ELEMENT of OTHER FIELDS -->
            <tr v-if="$store.state.modules.DesignTestDataStore.eventType == 'ObjectEvent' || $store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' ">
              <td> ILMD </td>
              <td>
                <button type="button" class="btn-btn-info" @click="userExtensionAddition($event,'ilmd')">
                  Add ILMD
                </button>
                <div
                  v-for="extension in $store.state.modules.ExtensionDataStore.ilmd"
                  :key="extension.ID"
                  class="form-inline horizontalSpace verticleSpace"
                >
                  <span>{{ extension.namespace + ":" + extension.localName }}</span>
                  <input v-if="extension.dataType == 'string'" :value="extension.text" type="text" @input="extensionText($event, extension.ID, 'ilmd')">
                  <span v-if="extension.dataType == 'complex'" class="horizontalSpace verticleSpace">
                    <ExtensionComponent
                      v-if="extension.dataType == 'complex'"
                      :extension="extension"
                    />
                  </span>
                  <span class="horizontalSpace verticleSpace" />
                  <button class="btn btn-danger" @click="deleteExtension($event,extension.ID, 'ilmd')">
                    <em class="bi bi-trash" />
                  </button>
                </div>
              </td>
            </tr>

            <!-- Third ELEMENT of OTHER FIELDS -->
            <tr v-if="$store.state.modules.DesignTestDataStore.eventType == 'TransformationEvent' ">
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

                <span v-for="errCorr in formData.errorCorrectiveIdsList" :key="errCorr.ID" class="form-inline verticleSpace">
                  <span class="horizontalSpace">
                    <input v-model="errCorr.correctiveId">
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
                  class="form-inline horizontalSpace verticleSpace"
                >
                  <span>{{ extension.namespace + ":" + extension.localName }}</span>
                  <input v-if="extension.dataType == 'string'" :value="extension.text" type="text" @input="extensionText($event, extension.ID, 'ErrorExtension')">
                  <span v-if="extension.dataType == 'complex'" class="horizontalSpace verticleSpace">
                    <ExtensionComponent
                      v-if="extension.dataType == 'complex'"
                      :extension="extension"
                    />
                  </span>
                  <span class="horizontalSpace verticleSpace" />
                  <button class="btn btn-danger" @click="deleteExtension($event,extension.ID, 'ErrorExtension')">
                    <em class="bi bi-trash" />
                  </button>
                </div>
              </td>
            </tr>
          <!-- ERROR DECLARATION FIELDS END -->
          </tbody>
        </table>
        <div class="row">
          <div class="col-md-5" />
          <div class="col-md-5">
            <button type="submit" class="btn btn-success">
              Submit
            </button>
          </div>
        </div>
      </form>
      <InstanceIdentifiersModal v-if="$store.state.modules.IdentifiersStore.instaceIdentifiersModal" />
      <ClassIdentifiersModal v-if="$store.state.modules.IdentifiersStore.classIdentifiersModal" />
      <ExtensionModal v-if="$store.state.modules.ExtensionDataStore.extensionModal" />
      <SensorElementsModal v-if="$store.state.modules.SensorElementsStore.sensorModal" />
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import InstanceIdentifiersModal from '@/components/InstanceIdentifiersModal.vue'
import ClassIdentifiersModal from '@/components/ClassIdentifiersModal.vue'
import ExtensionModal from '@/components/ExtensionModal.vue'
import ExtensionComponent from '@/components/ExtensionComponent.vue'
import SensorElementsModal from '@/components/SensorElementsModal.vue'

export default {
  components: {
    InstanceIdentifiersModal,
    ClassIdentifiersModal,
    ExtensionModal,
    ExtensionComponent,
    SensorElementsModal
  },
  data () {
    return {
      commonDropdownInfos: {},
      selected: null,
      EventTypeRowSpan: 6,
      rowspanWHAT: 1,
      rowspanWHY: 4,
      rowSpanOtherFields: 1,
      eventType: [],
      formData: {
        vocabularySyntax: 'URN',
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
        PersistentDispositionType: null,
        PersistentDisposition: null,
        persistentDispositionCount: 0,
        persistentDispositionList: [],
        businessTransactionCount: 0,
        businessTransactionList: [],
        errorCorrectiveCount: 0,
        errorCorrectiveIdsList: [],
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
        error: {}
      }
    }
  },
  computed: mapState([
    'modules.IdentifiersStore.identifierSyntax',
    'modules.DesignTestDataStore.eventType'
  ]),
  mounted () {
    this.$store.dispatch('modules/DesignTestDataStore/readStaticData')
    this.commonDropdownInfos = this.$store.state.modules.DesignTestDataStore.commonDropdownInfos
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
    this.formData.error = { ErrorTimeZone: '+02:00', ErrorDeclarationTime: now, ErrorDeclarationTimeFrom: yesterday, ErrorDeclarationTimeTo: now, ErrorReasonType: null, ErrorDeclarationTimeSelector: 'SpecificTime' }
  },
  methods: {
    // On change of the event count change the event count in store
    eventCountChange (event) {
      this.$store.commit('modules/DesignTestDataStore/eventCountPopulator', event.target.value)
      this.$store.commit('modules/IdentifiersStore/populateEventCount', event.target.value)
    },

    // Function to change the identifier syntax based on user selection
    identifierSyntaxChange (identifierSyntax) {
      this.$store.commit('modules/IdentifiersStore/populateIdentifiersType', { identifierSyntax })
    },

    // Function to generate Test data onclick of the submit button
    generateTestData () {
      // Navigate to Generate Test data page to display the inputTemplate and Generated output events
      this.$router.push('/GenerateEvents')

      // Generate the Test Data input based on user provided information
      this.$store.dispatch('modules/TestDataJsonPreparator/jsonPreparation', { formData: this.formData })

      // Send the data to the Generate Test Data
      this.$store.commit('modules/TestDataGeneratorStore/populateTestDataInput', JSON.stringify(this.$store.state.modules.TestDataJsonPreparator.testDataInputTemplate))

      // Call the action to convert the provided Test Data JSON input to Test Data events
      this.$store.dispatch('modules/TestDataGeneratorStore/testdataGenerator')
    },

    // Trigger the function based on the change of the event Object/Aggregation/Transaction/Transformation
    eventTypeChange (event) {
      // Populate the eventType based on the change to eventType
      this.$store.commit('modules/DesignTestDataStore/eventTypePopulator', event)

      this.$store.commit('modules/IdentifiersStore/resetIdentifiersData')

      // Based on eventType show the respective information corresponding to eventType
      switch (this.$store.state.modules.DesignTestDataStore.eventType) {
        case 'ObjectEvent':
          this.rowspanWHAT = 2
          this.rowspanWHY = 6
          this.rowSpanOtherFields = 2
          this.EventTypeRowSpan = 7
          break
        case 'AggregationEvent':
        case 'TransactionEvent':
        case 'AssociationEvent':
          this.rowspanWHAT = 3
          this.rowspanWHY = 6
          this.rowSpanOtherFields = 1
          this.EventTypeRowSpan = 7
          break
        case 'TransformationEvent':
          this.rowspanWHAT = 4
          this.rowspanWHY = 4
          this.rowSpanOtherFields = 3
          this.EventTypeRowSpan = 6
          break
        default:
          this.rowspanWHY = 4
          this.rowspanWHAT = 1
          this.rowSpanOtherFields = 1
          this.EventTypeRowSpan = 6
          break
      }
    },

    // Add the Business Trasactions based on the addition by user
    addBTT (event) {
      event.preventDefault()
      const BTTObj = { ID: this.formData.businessTransactionCount, type: '', bizTransaction: '' }
      this.formData.businessTransactionList.push(BTTObj)
      this.formData.businessTransactionCount++
    },

    // Delete Business Transaction based on user selection
    deleteBizTransaction (btID) {
      // On click of the delete Business Transaction button delete the respective persistent Disposition
      const index = this.formData.businessTransactionList.findIndex(obj => obj.ID === btID)
      this.formData.businessTransactionList.splice(index, 1)
    },

    // Add the Empty Persistent Disposition based on the click of ADD PD by user
    addPD (event) {
      // On click of the add Persistent Disposition button and an element into Persistent Disposition List
      event.preventDefault()
      const persistentDispObj = { ID: this.formData.persistentDispositionCount, type: '', value: '' }
      this.formData.persistentDispositionList.push(persistentDispObj)
      this.formData.persistentDispositionCount++
    },

    // Delete Persistnet Disposition based on user click of the respective PD
    deletePD (pdID) {
      // On click of the delete Persistent Disposition button delete the respective persistent Disposition
      const index = this.formData.persistentDispositionList.findIndex(obj => obj.ID === pdID)
      this.formData.persistentDispositionList.splice(index, 1)
    },

    // Add the Empty Error Corrective based on the click of ADD Error Corrective by user
    addErrorCorrective (event) {
      // On click of the add Error Corrective id button and an element into CorrectiveIDs List
      const errCorrectiveObj = { ID: this.formData.errorCorrectiveCount, value: '' }
      this.formData.errorCorrectiveIdsList.push(errCorrectiveObj)
      this.formData.errorCorrectiveCount++
    },

    // Delete Error Corrective based on user click of the respective Error Corrective
    deleteErrCorrectiveInfo (corrID) {
      const index = this.formData.errorCorrectiveIdsList.findIndex(obj => obj.ID === corrID)
      this.formData.errorCorrectiveIdsList.splice(index, 1)
    },

    // Method to show the Instance Identifiers modal on click of the Instance Identifiers button
    showInstanceIdentifiersModal (flagType) {
      // Populate the flagType to indicate which event information are being currently filled
      this.$store.commit('modules/IdentifiersStore/populateIdentifiersFlagType', flagType)

      // Show the modal for adding the information for Instance Identifiers
      this.$store.commit('modules/IdentifiersStore/showInstanceIdentifiersModal')
    },

    // Method to show the Instance Identifiers modal on click of the Parent Identifiers
    parentIdPopulator (flagType) {
      // Set the parent ID flag to indicate the parent id is being set
      this.$store.commit('modules/IdentifiersStore/parentFlagToggle')

      // Show the modal for adding the information related to the ParentID
      this.$store.commit('modules/IdentifiersStore/showInstanceIdentifiersModal')
    },

    // Method to modify the Parent Identifiers on click of the Modiy Parent ID button
    modifyParentID (parentId, flagType) {
      // Populate the flagType to indicate which event information are being currently filled
      this.$store.commit('modules/IdentifiersStore/parentFlagToggle')

      // Send the Id for the instance identifier based on which Instance Identifiers modul should be populated.
      this.$store.commit('modules/IdentifiersStore/modifyParentIdentifiersInformation', { parentId })

      // Show the modal for adding the information for Instance Identifiers
      this.$store.commit('modules/IdentifiersStore/showInstanceIdentifiersModal')
    },

    // Method to delete the Instance Identifiers modal on click of the delete parent ID button
    deleteParentID (parentId, flagType) {
      this.$store.commit('modules/IdentifiersStore/deleteParentIdentifiersInformation')
    },

    // Function to show the Class Identifiers modal on click of the Quantites Identifiers button
    showClassIdentifiersModal (flagType) {
      // Populate the flagType to indicate which event information are being currently filled
      this.$store.commit('modules/IdentifiersStore/populateIdentifiersFlagType', flagType)

      // Show the modal for adding the information for Class Identifiers
      this.$store.commit('modules/IdentifiersStore/showClassIdentifiersModal')
    },

    // Function to delete Instance Identifiers based on the user selection
    deleteInstanceIdentifier (instanceID, flagType) {
      this.$store.commit('modules/IdentifiersStore/deleteInstnaceIdentifiersInformation', { instanceID, flagType })
    },

    // Function to modify Instance Identifiers based on the user selection. Display the Instance Identifiers modal with currently provided values for modification.
    modifyInstanceIdentifier (instanceID, flagType) {
      // Populate the flagType to indicate which event information are being currently filled
      this.$store.commit('modules/IdentifiersStore/populateIdentifiersFlagType', flagType)

      // Send the Id for the instance identifier based on which Instance Identifiers modul should be populated.
      this.$store.commit('modules/IdentifiersStore/modifyInstanceIdentifiersInformation', { instanceID, flagType })

      // Show the modal for modifying the information for Instance Identifiers
      this.$store.commit('modules/IdentifiersStore/showInstanceIdentifiersModal')
    },

    // Function to modify class identifiers based on user selection. Display the Class identifiers modal with currently provided values for modification.
    modifyClassIdentifier (classId, flagType) {
      // Populate the flagType to indicate which event information are being currently filled
      this.$store.commit('modules/IdentifiersStore/populateIdentifiersFlagType', flagType)

      // Send the Id for the instance identifier based on which Instance Identifiers modul should be populated.
      this.$store.commit('modules/IdentifiersStore/modifyClassIdentifiersInformation', { classId, flagType })

      // Show the modal for modifying the information for Class Identifiers
      this.$store.commit('modules/IdentifiersStore/showClassIdentifiersModal')
    },

    // Function to delete Class Identifiers based on user selection
    deleteClassIdentifier (classId, flagType) {
      this.$store.commit('modules/IdentifiersStore/deleteClassIdentifiersInformation', { classId, flagType })
    },

    // Function to add the User extension onclick of the Add button
    userExtensionAddition (event, type) {
      event.preventDefault()
      this.$store.commit('modules/ExtensionDataStore/toggleExtensionModal')
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', type)
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', null)
    },

    // Based on the text change in String extension input change the value in List
    extensionText (event, extensionID, type) {
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', null)
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', type)
      this.$store.commit('modules/ExtensionDataStore/extensionText', { text: event.target.value, extensionID })
    },

    // Function to delete the User extension onclick of the Delete button
    deleteExtension (event, extensionID, type) {
      event.preventDefault()
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', type)
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', null)
      this.$store.commit('modules/ExtensionDataStore/deleteExtension', extensionID)
    },

    // Function that will be triggered on click of the Add sensor Information
    addSensorInformation () {
      this.$store.commit('modules/SensorElementsStore/showSensorModal')
    },

    // Based on user click remove the respective sensor element information
    deleteSensorElement (event, sensorElementID) {
      event.preventDefault()
      this.$store.commit('modules/SensorElementsStore/deleteSensorElement', sensorElementID)
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

    // On click of the export button export the JSON file containing the information what users have provided in the form
    exportData () {
      const createDataInfo = {}
      createDataInfo.identifierSyntax = this.$store.state.modules.IdentifiersStore.identifierSyntax
      createDataInfo.eventType = this.$store.state.modules.DesignTestDataStore.eventType
      createDataInfo.eventCount = this.$store.state.modules.DesignTestDataStore.eventCount
      createDataInfo.instanceIdentifiersList = this.$store.state.modules.IdentifiersStore.instanceIdentifiersList
      createDataInfo.classIdentifiersList = this.$store.state.modules.IdentifiersStore.classIdentifiersList
      createDataInfo.outputInstanceIdentifiersList = this.$store.state.modules.IdentifiersStore.outputInstanceIdentifiersList
      createDataInfo.outputclassIdentifiersList = this.$store.state.modules.IdentifiersStore.outputclassIdentifiersList
      createDataInfo.parentIdentifiersList = this.$store.state.modules.IdentifiersStore.parentIdentifiersList
      createDataInfo.sensorElementList = this.$store.state.modules.SensorElementsStore.sensorElementList
      createDataInfo.userExtensions = this.$store.state.modules.ExtensionDataStore.userExtensions
      createDataInfo.ilmd = this.$store.state.modules.ExtensionDataStore.ilmd
      createDataInfo.errorExtensions = this.$store.state.modules.ExtensionDataStore.errorExtensions
      createDataInfo.formData = this.formData

      const DateTime = new Date().toISOString().replace('Z', '').replace('T', '')
      const textFileAsBlob = new Blob([JSON.stringify(createDataInfo, null, 4)], { type: 'text/json' })
      const downloadLink = document.createElement('a')
      downloadLink.download = 'Test_Data_Generator_Create_' + DateTime + '.json'
      downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob)
      downloadLink.click()
    },

    // On click of the import button import the existing JSON file containing the information into the Create Test Data table
    importInfo () {
      const reader = new FileReader()
      this.file = this.$refs.readEventsInfo.files[0]
      this.$refs.readEventsInfo.value = null

      reader.onload = (res) => {
        // Parse the JSON to get the information related to each of the components in table such as FormData, IdentifiersData, Extensions, Sensor, etc.
        const importData = JSON.parse(res.target.result)

        // If identifierSyntax is present then populate the identifierSyntax accordingly
        if (importData.identifierSyntax !== undefined) {
          this.$store.commit('modules/IdentifiersStore/populateIdentifiersType', { identifierSyntax: importData.identifierSyntax })
        }

        // If eventType is present then populate the eventType accordingly
        if (importData.eventType !== undefined) {
          this.eventTypeChange(importData.eventType)
        }

        // Store the event count information based on the import information.
        if (importData.eventCount !== undefined) {
          this.$store.commit('modules/DesignTestDataStore/populateRawData', { eventCount: importData.eventCount })
        }

        // If the instanceIdentifiersList is present then populate the instanceIdentifiersList accordingly.
        if (importData.instanceIdentifiersList !== undefined) {
          this.$store.commit('modules/IdentifiersStore/populateInstanceIdentifiers', importData.instanceIdentifiersList)
        }

        // If identifiers information are present then add them to object accordingly
        const identifiersObj = {}
        identifiersObj.epc = importData.instanceIdentifiersList !== undefined ? importData.instanceIdentifiersList : []
        identifiersObj.quantity = importData.classIdentifiersList !== undefined ? importData.classIdentifiersList : []
        identifiersObj.outputEpc = importData.outputInstanceIdentifiersList !== undefined ? importData.outputInstanceIdentifiersList : []
        identifiersObj.outputQuantity = importData.outputclassIdentifiersList !== undefined ? importData.outputclassIdentifiersList : []
        identifiersObj.parentID = importData.parentIdentifiersList !== undefined ? importData.parentIdentifiersList : []
        this.$store.commit('modules/IdentifiersStore/populateRawData', identifiersObj)

        // If the error informations are present then add them to Extensions data store
        const extensionsInfo = {}
        extensionsInfo.userExtensions = importData.userExtensions !== undefined ? importData.userExtensions : []
        extensionsInfo.ilmd = importData.ilmd !== undefined ? importData.ilmd : []
        extensionsInfo.errorExtensions = importData.errorExtensions !== undefined ? importData.errorExtensions : []
        this.$store.commit('modules/ExtensionDataStore/populateRawData', extensionsInfo)

        // If the sensor informations are present then add them to SensorElementList
        this.$store.commit('modules/SensorElementsStore/populateRawData', { sensorData: importData.sensorElementList })

        // If formData is present then add it to the form
        this.formData = importData.formData !== undefined ? importData.formData : {}
      }

      reader.onerror = (err) => {
        alert('Error during the import of the TestData Generator Create Data : ' + err)
      }
      reader.readAsText(this.file)
    }
  }
}
</script>

<style scoped>
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
</style>
