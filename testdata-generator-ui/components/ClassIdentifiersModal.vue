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
    :visible="$store.state.modules.IdentifiersStore.classIdentifiersModal"
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
            placeholder="From"
            title="Enter the Range From"
            class="form-control"
            :required="classIdentifiersForm.serialType == 'range' && autoGenerateOptionA == true"
            style="width:120px;"
          >

          <input
            v-model="classIdentifiersForm.count"
            type="number"
            min="1"
            placeholder="Count"
            title="Enter the Count of identifiers per event"
            class="form-control"
            :required="classIdentifiersForm.serialType == 'range' && autoGenerateOptionA == true"
            style="width:120px;"
          >
        </div>

        <div v-show="classIdentifiersForm.serialType == 'random' && autoGenerateOptionA == true" class="verticleSpace horizontalSpace">
          <input
            v-model="classIdentifiersForm.randomCount"
            type="number"
            class="form-control"
            min="1"
            placeholder="Number of Objects"
            title="Enter the Number of Objects"
            :required="classIdentifiersForm.serialType == 'random' && autoGenerateOptionA == true"
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
            :required="classIdentifiersForm.serialType == 'none' && autoGenerateOptionA == true && classIdentifiersForm.identifierType == 'lgtin'"
          >
        </div>

        <div v-show="autoGenerateOptionA == false && classIdentifiersForm.identifierType != null && classIdentifiersForm.identifierType != 'manualURI'" class="verticleSpace horizontalSpace">
          <input
            v-model="classIdentifiersForm.classIdentifiersCount"
            type="number"
            class="form-control"
            min="1"
            placeholder="Number of Objects"
            title="Enter the Number of Objects"
            :required="!autoGenerateOptionA && classIdentifiersForm.identifierType != 'manualURI'"
          >
        </div>

        <div v-show="gcpVisibility && $store.state.modules.IdentifiersStore.identifierSyntax === 'URN'" class="verticleSpace horizontalSpace">
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
              placeholder="From value"
              title="Enter the Range From"
              class="form-control"
              :required="classIdentifiersForm.identifierType == 'manualURI' && classIdentifiersForm.manualUriType == 'dynamic'"
              style="width:120px;"
            >

            <input
              v-model="classIdentifiersForm.manualUriRangeTo"
              type="number"
              min="1"
              placeholder="To value"
              title="Enter the Range To"
              class="form-control"
              :required="classIdentifiersForm.identifierType == 'manualURI' && classIdentifiersForm.manualUriType == 'dynamic'"
              style="width:120px;"
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

    // Check if user is trying to provide new identifiers or modifying existing identifiers, for modification show the existing information
    const currentIdentifiersInfo = JSON.parse(JSON.stringify(this.$store.state.modules.IdentifiersStore.currentIdentifiersInfo))
    if (currentIdentifiersInfo !== undefined && currentIdentifiersInfo !== null && Object.keys(currentIdentifiersInfo).length !== 0) {
      const identifierInfo = currentIdentifiersInfo[Object.keys(currentIdentifiersInfo)[3]]
      this.classIdentifierSelection(identifierInfo.identifierType)
      this.classIdentifiersForm = identifierInfo
    }
  },
  methods: {
    submitIdentifiersData () {
      // Based on the serial number generation type convert the values from String to Integer
      this.classIdentifiersForm.rangeFrom = this.classIdentifiersForm.rangeFrom !== undefined ? parseInt(this.classIdentifiersForm.rangeFrom) : undefined
      this.classIdentifiersForm.count = this.classIdentifiersForm.count !== undefined ? parseInt(this.classIdentifiersForm.count) : undefined
      this.classIdentifiersForm.randomCount = this.classIdentifiersForm.randomCount !== undefined ? parseInt(this.classIdentifiersForm.randomCount) : undefined
      this.classIdentifiersForm.classIdentifiersCount = this.classIdentifiersForm.classIdentifiersCount !== undefined ? parseInt(this.classIdentifiersForm.classIdentifiersCount) : undefined
      this.classIdentifiersForm.quantity = this.classIdentifiersForm.quantity !== undefined ? parseFloat(this.classIdentifiersForm.quantity) : undefined

      // If flagType is transformationEvent then fill the Output EPC
      if (this.$store.state.modules.IdentifiersStore.identifiersFlagType === 'TransformationEventOutputQuantity') {
        this.$store.commit('modules/IdentifiersStore/saveoutputclassIdentifiersInformation', { classData: this.classIdentifiersForm })
      } else {
        // Send the data to the store and store the informaiton
        this.$store.commit('modules/IdentifiersStore/saveClassIdentifiersInformation', { classData: this.classIdentifiersForm })
      }

      // Hide the modal after submission
      this.$store.commit('modules/IdentifiersStore/hideClassIdentifiersModal')
    },

    // Function to show the various options on change of the Class identifier type
    classIdentifierSelection (event) {
      // On change of the identifiers set the values to default and only show the relevent fields based on selection
      this.classIdentifiersForm = { identifierType: event, gcpLength: null, quantityType: null, uom: null }

      this.gcpVisibility = true

      if (this.classIdentifiersForm.identifierType !== null) {
        // Only for Lgtin show the autogenerate option for all other hide the option
        if (this.classIdentifiersForm.identifierType === 'lgtin') {
          this.autoGenerateOptionA = true
        } else {
          this.autoGenerateOptionA = false
          this.classIdentifiersForm.serialType = ''
        }

        // For Manual URI identifier type and WebURI identifier syntax hide the GCP length
        if (this.classIdentifiersForm.identifierType === 'manualURI' || this.$store.state.modules.IdentifiersStore.identifierSyntax === 'WebURI') {
          this.gcpVisibility = false
        }
      }
    },

    // Function to close the modal on hit of Cancel/ESC button and reset the current identifiers information.
    cancel () {
      this.$store.commit('modules/IdentifiersStore/resetCurrentIdentifiersInfo')
      this.$store.commit('modules/IdentifiersStore/hideClassIdentifiersModal')
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
</style>
yle>
