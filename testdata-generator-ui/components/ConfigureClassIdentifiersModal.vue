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
    id="Class IdentifiersModal"
    title="Class/Quantity Identifiers"
    size="lg"
    width="100%"
    :visible="$store.state.modules.ConfigureIdentifiersInfoStore.classIdentifiersModal"
    @hide="cancel"
  >
    <b-form id="classIdentifiersForm" @submit.prevent="submitIdentifiersData">
      <div>
        <b-form-select v-model="classIdentifiersForm.identifierType" :options="classIdentifiers" required @change="classIdentifierSelection($event)" />
      </div>

      <span class="form-inline verticleSpace">
        <div v-if="classIdentifiersForm.identifierType == 'lgtin'" class="verticleSpace horizontalSpace">
          <span>(01)</span>
          <input
            v-model="classIdentifiersForm.lgtin"
            type="text"
            pattern="([0-9]{14})$"
            :required="classIdentifiersForm.identifierType == 'lgtin'"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            maxlength="14"
            oninvalid="this.setCustomValidity('GTIN must be 14 digits')"
            class="form-control"
            style="width:200px"
            autocomplete="off"
            placeholder="Enter LGTIN"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'gtin'" class="verticleSpace horizontalSpace">
          <span>(01)</span>
          <input
            v-model="classIdentifiersForm.gtin"
            type="text"
            pattern="([0-9]{14})$"
            :required="classIdentifiersForm.identifierType == 'gtin'"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            maxlength="14"
            oninvalid="this.setCustomValidity('GTIN must be 14 digits')"
            class="form-control"
            style="width:200px"
            placeholder="Enter GTIN"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'grai'" class="verticleSpace horizontalSpace">
          <span>(8003) 0</span>
          <input
            v-model="classIdentifiersForm.grai"
            type="text"
            :required="classIdentifiersForm.identifierType == 'grai'"
            maxlength="13"
            pattern="([0-9]{13})"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('GRAI must be 13 digits')"
            placeholder="Enter GRAI"
            class="form-control"
            style="width: 200px;"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'gdti'" class="verticleSpace horizontalSpace">
          <span>(253)</span>
          <input
            v-model="classIdentifiersForm.gdti"
            type="text"
            :required="classIdentifiersForm.identifierType == 'gdti'"
            maxlength="13"
            pattern="([0-9]{13})$"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('GDTI must be 13 digits')"
            placeholder="Enter GDTI"
            class="form-control"
            style="width: 200px;"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'gcn'" class="verticleSpace horizontalSpace">
          <span>(255)</span>
          <input
            v-model="classIdentifiersForm.gcn"
            type="text"
            :required="classIdentifiersForm.identifierType == 'gcn'"
            maxlength="13"
            pattern="([0-9]{13})$"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('GCN must be 13 digits')"
            placeholder="Enter GCN"
            class="form-control"
            style="width: 200px;"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'cpi'" class="verticleSpace horizontalSpace">
          <span>(8010)</span>
          <input
            v-model="classIdentifiersForm.cpi"
            type="text"
            :required="classIdentifiersForm.identifierType == 'cpi'"
            maxlength="30"
            pattern="^.{7,30}$"
            oninvalid="this.setCustomValidity('CPI must be between 7 and 30 digits')"
            placeholder="Enter CPI"
            class="form-control"
            oninput="setCustomValidity('');"
            style="width: 200px;"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'itip'" class="verticleSpace horizontalSpace">
          <span>(8006)</span>&nbsp;
          <input
            v-model="classIdentifiersForm.itip"
            type="text"
            :required="classIdentifiersForm.identifierType == 'itip'"
            maxlength="18"
            pattern="([0-9]{18})$"
            oninvalid="this.setCustomValidity('ITIP must be 18 digits')"
            placeholder="Enter ITIP"
            class="form-control"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            style="width: 200px;"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'upui'" class="verticleSpace horizontalSpace">
          <input
            v-model="classIdentifiersForm.upui"
            type="text"
            :required="classIdentifiersForm.identifierType == 'upui'"
            maxlength="14"
            pattern="([0-9]{14})$"
            oninvalid="this.setCustomValidity('UPUI must be 14 digits')"
            placeholder="Enter UPUI"
            class="form-control"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            style="width: 200px;"
          >
        </div>

        <div v-if="classIdentifiersForm.identifierType == 'manualURI'" class="verticleSpace horizontalSpace">
          <div class="form-inline">
            <div class="custom-control custom-radio custom-control-inline">
              <input
                id="manualUriStatic"
                v-model="classIdentifiersForm.manualUriType"
                type="radio"
                :required="classIdentifiersForm.identifierType == 'manualURI'"
                class="custom-control-input"
                value="static"
                name="manualURIOption"
              >
              <label class="custom-control-label" for="manualUriStatic">Static</label>
            </div>
            <div class="custom-control custom-radio custom-control-inline">
              <input
                id="manualUriDynamic"
                v-model="classIdentifiersForm.manualUriType"
                type="radio"
                :required="classIdentifiersForm.identifierType == 'manualURI'"
                class="custom-control-input"
                value="dynamic"
                name="manualURIOption"
              >
              <label class="custom-control-label" for="manualUriDynamic">Dynamic</label>
            </div>
          </div>
        </div>

        <div v-show="classIdentifiersForm.serialType == 'range' && autoGenerateOptionA == true" class="verticleSpace horizontalSpace">
          <input
            v-model="classIdentifiersForm.rangeFrom"
            type="number"
            min="1"
            placeholder="From value"
            title="Enter the Range From"
            class="form-control"
            :required="classIdentifiersForm.serialType == 'range' && autoGenerateOptionA == true"
            style="width:120px;"
          >
        </div>

        <div v-show="classIdentifiersForm.serialType == 'none' && autoGenerateOptionA == true && classIdentifiersForm.identifierType == 'lgtin'" class="verticleSpace horizontalSpace">
          <input
            v-model="classIdentifiersForm.serialNumber"
            type="number"
            class="form-control"
            min="1"
            placeholder="Serial ref. number"
            title="Enter serial number"
            :required="classIdentifiersForm.serialType == 'none'"
          >
        </div>

        <div v-show="gcpVisibility" class="verticleSpace horizontalSpace">
          <b-form-select v-model="classIdentifiersForm.gcpLength" :options="commonDropdownInfos.companyPrefixs" :required="gcpVisibility" />
        </div>
      </span>

      <div v-show="autoGenerateOptionA == true" class="form-group">
        <div class="form-inline">
          <div class="custom-control custom-radio custom-control-inline">
            <input
              id="rangeOption"
              v-model="classIdentifiersForm.serialType"
              type="radio"
              :required="autoGenerateOptionA"
              class="custom-control-input"
              value="range"
              name="autogenerateOptions"
            >
            <label class="custom-control-label" for="rangeOption">Range</label>
          </div>

          <div class="custom-control custom-radio custom-control-inline">
            <input
              id="randomOption"
              v-model="classIdentifiersForm.serialType"
              type="radio"
              class="custom-control-input"
              value="random"
              name="autogenerateOptions"
            >
            <label class="custom-control-label" for="randomOption">Random</label>
          </div>

          <div class="custom-control custom-radio custom-control-inline">
            <input
              id="noneOption"
              v-model="classIdentifiersForm.serialType"
              type="radio"
              class="custom-control-input"
              value="none"
              name="autogenerateOptions"
            >
            <label class="custom-control-label" for="noneOption">None</label>
          </div>
        </div>
      </div>

      <!-- If the ManualURI then based on Static or Dynamic show the options -->
      <div v-if="classIdentifiersForm.identifierType == 'manualURI'" class="form-group">
        <div class="form-inline">
          <input
            v-model="classIdentifiersForm.baseManualUri"
            type="text"
            class="form-control"
            placeholder="Base Manual URI"
            title="Manual URI"
            :required="classIdentifiersForm.identifierType == 'manualURI'"
          >

          <div v-if="classIdentifiersForm.manualUriType == 'dynamic'" class="verticleSpace horizontalSpace">
            <input
              v-model="classIdentifiersForm.manualUriRangeFrom"
              type="number"
              min="1"
              placeholder="From value for Objects"
              title="Enter the Range From"
              class="form-control"
              :required="classIdentifiersForm.identifierType == 'manualURI' && classIdentifiersForm.manualUriType == 'dynamic'"
            >
          </div>
        </div>
      </div>

      <div class="verticleSpace">
        <h5>Quantity</h5>

        <div class="form-inline horizontalSpace">
          <b-form-select v-model="classIdentifiersForm.quantityType" :options="quantitiesType" />

          <span v-if="classIdentifiersForm.quantityType == 'Fixed Measure Quantity' || classIdentifiersForm.quantityType == 'Variable Measure Quantity'" class="horizontalSpace">
            <input
              v-model="classIdentifiersForm.quantity"
              placeholder="Quantity"
              class="form-control"
              style="width:200px;"
              autocomplete="off"
              type="number"
              pattern="[0-9]+([\.,][0-9]+)?"
              step="0.01"
            >
          </span>

          <span v-if="classIdentifiersForm.quantityType == 'Variable Measure Quantity' " class="horizontalSpace verticleSpace">
            <b-form-select v-model="classIdentifiersForm.uom" :options="uoms" />
          </span>
        </div>
      </div>
    </b-form>

    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="classIdentifiersForm">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
export default {
  data () {
    return {
      commonDropdownInfos: {},
      classIdentifiers: [],
      quantitiesType: [],
      uoms: [],
      autoGenerateOptionA: false,
      gcpVisibility: false,
      classIdentifiersForm: {
        identifierType: null,
        gcpLength: null,
        quantityType: null,
        uom: null
      }
    }
  },
  mounted () {
    const classIdentifiers = require('~/static/EpcisData/IdentifiersDropdown.js')
    this.commonDropdownInfos = require('~/static/EpcisData/CommonDropdown.js')
    this.classIdentifiers = classIdentifiers.ClassIdentifierTypes
    this.quantitiesType = classIdentifiers.QuantityType
    this.uoms = classIdentifiers.UOMs

    // Get the current identifier info based on the identifiers node on which user has clicked and assign the value
    const currentIdentifierInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.currentIdentifierInfo))

    // Get all the identifiers information from the store
    const currentClassIdentifier = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray)).find(idNode => idNode.identifiersId === currentIdentifierInfo.nodeId)

    // Check if the class data for the current identifiers node is already provided if so then populate the same
    if (currentClassIdentifier !== undefined && currentClassIdentifier.classData !== undefined) {
      this.classIdentifiersForm = currentClassIdentifier.classData
      this.classIdentifierSelection('')
    }
  },
  methods: {
    submitIdentifiersData () {
      // Based on the serial number generation type convert the values from String to Integer
      this.classIdentifiersForm.rangeFrom = this.classIdentifiersForm.rangeFrom !== undefined ? parseInt(this.classIdentifiersForm.rangeFrom) : undefined
      this.classIdentifiersForm.serialNumber = this.classIdentifiersForm.serialNumber !== undefined ? parseInt(this.classIdentifiersForm.serialNumber) : undefined
      this.classIdentifiersForm.quantity = this.classIdentifiersForm.quantity !== undefined ? parseFloat(this.classIdentifiersForm.quantity) : undefined

      // Send the data to the store and store the informaiton
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/saveClassIdentifiersInfo', { classData: this.classIdentifiersForm })

      // Hide the modal after submission
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/hideClassIdentifiersModal')
    },

    // Function to show the various options on change of the Class identifier type
    classIdentifierSelection (event) {
      // If user changes the identifiers then reset the value only during the creatioin of new identifiers and not during edition.
      // On change of the identifiers set the values to default and only show the relevent fields based on selection
      if (event !== '') {
        this.classIdentifiersForm = { identifierType: event, gcpLength: null, quantityType: null, uom: null }
      }

      // If the identifier type is URN for the identifiers node then keep the gcp visibility true
      const currentNodeId = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.currentNodeId))
      const allNodeInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray, null, 4))

      if (allNodeInfo.find(node => node.identifiersId === currentNodeId).identifierSyntax === 'URN') {
        this.gcpVisibility = true
      } else {
        this.gcpVisibility = false
      }

      // On change of identifier type set the autogenerate option
      if (this.classIdentifiersForm.identifierType !== null) {
        if (this.classIdentifiersForm.identifierType === 'lgtin') {
          this.autoGenerateOptionA = true
        } else {
          this.autoGenerateOptionA = false
          this.classIdentifiersForm.serialType = undefined
        }

        if (this.classIdentifiersForm.identifierType === 'manualURI') {
          this.gcpVisibility = false
        }
      }
    },

    // Function to close the modal on hit of Cancel/ESC button
    cancel () {
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/hideClassIdentifiersModal')
    }
  }
}
</script>

<style>
.verticleSpace{
  padding-top:8px;
  padding-bottom: 8px;
}

.horizontalSpace{
  padding-right: 8px;
  padding-left: 8px;
}

span, label,h5{
    color:black
}
</style>
