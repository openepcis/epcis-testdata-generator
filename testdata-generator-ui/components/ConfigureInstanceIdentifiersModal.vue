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
    id="InstanceIdentifiersModal"
    title="Instance/EPC Identifiers"
    size="lg"
    :visible="$store.state.modules.ConfigureIdentifiersInfoStore.instanceIdentifiersModal"
    @hide="cancel"
  >
    <b-form id="identifiersForm" @submit.prevent="submitIdentifiersData">
      <div>
        <b-form-select v-model="identifiersForm.identifierType" :options="instanceIdentifiers" required @change="instanceIdentifierSelection($event)" />
      </div>

      <span class="form-inline verticleSpace">
        <div v-show="identifiersForm.identifierType == 'sgtin'" class="verticleSpace horizontalSpace">
          <span>(01)</span>
          <input
            v-model="identifiersForm.sgtin"
            type="text"
            maxlength="14"
            pattern="([0-9]{14})"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('GTIN must be 14 digits')"
            class="form-control"
            style="width:200px"
            autocomplete="off"
            :required="identifiersForm.identifierType == 'sgtin'"
            placeholder="Enter SGTIN"
          >
        </div>

        <div v-show="showCommongcp" class="verticleSpace horizontalSpace">
          <input
            v-model="identifiersForm.gcp"
            type="text"
            min="6"
            max="12"
            pattern="([0-9]{6,12})"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('GCP must be between 6 and 12 digits')"
            placeholder="GCP"
            title="Enter GCP"
            class="form-control"
            :required="showCommongcp"
            style="width:120px;"
          >
        </div>

        <div v-show="identifiersForm.identifierType == 'grai'" class="verticleSpace horizontalSpace">
          <span>(8003) 0</span>
          <input
            v-model="identifiersForm.grai"
            type="text"
            :required="identifiersForm.identifierType == 'grai'"
            maxlength="13"
            pattern="([0-9]{13})"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('GRAI must be 13 digits')"
            placeholder="Enter GRAI"
            class="form-control"
            style="width: 200px;"
          >
        </div>

        <div v-show="identifiersForm.identifierType == 'gdti'" class="verticleSpace horizontalSpace">
          <span>(253)</span>
          <input
            v-model="identifiersForm.gdti"
            type="text"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            :required="identifiersForm.identifierType == 'gdti'"
            maxlength="13"
            oninvalid="this.setCustomValidity('GDTI should be of 13 digit')"
            class="form-control"
            style="width:200px"
            autocomplete="off"
            placeholder="Enter GDTI"
          >
        </div>

        <div v-show="identifiersForm.identifierType == 'cpi'" class="verticleSpace horizontalSpace">
          <span>(8010)</span>
          <input
            v-model="identifiersForm.cpi"
            type="text"
            :required="identifiersForm.identifierType == 'cpi'"
            maxlength="30"
            pattern="([A-Z0-9]{7,30})"
            oninvalid="this.setCustomValidity('CPI must be between 7 and 30 digits')"
            placeholder="Enter CPI"
            class="form-control"
            oninput="setCustomValidity('');"
            style="width: 200px;"
          >&nbsp;
        </div>

        <div v-show="identifiersForm.identifierType == 'itip'" class="verticleSpace horizontalSpace">
          <span>(8006)</span>
          <input
            v-model="identifiersForm.itip"
            type="text"
            :required="identifiersForm.identifierType == 'itip'"
            maxlength="18"
            pattern="([0-9]{18})$"
            oninvalid="this.setCustomValidity('ITIP must be 18 digits')"
            placeholder="Enter ITIP"
            class="form-control"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            style="width: 200px;"
          >&nbsp;
        </div>

        <div v-show="identifiersForm.identifierType == 'upui'" class="verticleSpace horizontalSpace">
          <span>(01)</span>
          <input
            v-model="identifiersForm.upui"
            type="text"
            :required="identifiersForm.identifierType == 'upui'"
            pattern="\d{14}"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            maxlength="14"
            class="form-control"
            oninvalid="this.setCustomValidity('UPUI must be 14 digits')"
            placeholder="Enter UPUI"
            style="width: 200px;"
          >&nbsp;
        </div>

        <div v-show="identifiersForm.identifierType == 'gid'" class="verticleSpace horizontalSpace">
          <input
            v-model="identifiersForm.manager"
            type="text"
            :required="identifiersForm.identifierType == 'gid'"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            maxlength="9"
            oninvalid="this.setCustomValidity('GID Manager must be upto 9 digits');"
            placeholder="Enter manager"
            class="form-control"
            style="width: 200px;"
          >
          <input
            v-model="identifiersForm.gidClass"
            type="text"
            :required="identifiersForm.identifierType == 'gid'"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            maxlength="8"
            oninvalid="this.setCustomValidity('GID Class must be upto 8 digit')"
            placeholder="Enter class"
            class="form-control"
            style="width: 200px;"
          >
        </div>

        <div v-show="identifiersForm.identifierType == 'usdod'" class="verticleSpace horizontalSpace">
          <input
            v-model="identifiersForm.usdodCage"
            type="text"
            :required="identifiersForm.identifierType == 'usdod'"
            pattern="[0-9]{5,6}"
            maxlength="6"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('USDoD CAGE/DoDAAC must be at between 5 and 6 digits')"
            placeholder="Enter CAGE/DoDAAC"
            class="form-control"
            style="width: 200px;"
          >
        </div>

        <div v-show="identifiersForm.identifierType == 'adi'" class="verticleSpace horizontalSpace">
          <input
            v-model="identifiersForm.adiCage"
            type="text"
            :required="identifiersForm.identifierType == 'adi'"
            pattern="[0-9]{5,6}"
            maxlength="6"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            oninvalid="this.setCustomValidity('ADI CAGE/DoDAAC must be between 5 and 6 digits')"
            placeholder="Enter CAGE/DoDAAC"
            class="form-control"
            style="width: 200px;"
          >

          <input
            v-model="identifiersForm.adiPNO"
            type="text"
            oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
            maxlength="20"
            oninvalid="this.setCustomValidity('ADI PNO must be upto 20 digits')"
            placeholder="Enter PNO"
            class="form-control"
            style="width: 200px;"
          >&nbsp;
        </div>

        <div v-show="identifiersForm.identifierType == 'adi'" class="verticleSpace horizontalSpace" />

        <div v-show="identifiersForm.identifierType == 'bic'" class="verticleSpace horizontalSpace">
          <input
            v-model="identifiersForm.bic"
            type="text"
            :required="identifiersForm.identifierType == 'bic'"
            oninvalid="this.setCustomValidity('Not a valid pure identity URI scheme')"
            oninput="setCustomValidity('');"
            placeholder="Enter URI"
            class="form-control"
            style="width: 200px;"
          >
        </div>

        <div v-show="identifiersForm.identifierType == 'imovn'" class="verticleSpace horizontalSpace">
          <div class="form-inline">
            <div class="custom-control custom-radio custom-control-inline">
              <input
                id="imovnOptionA"
                v-model="identifiersForm.imovnOption"
                type="radio"
                :required="identifiersForm.identifierType == 'imovn'"
                class="custom-control-input"
                value="imovnOptionA"
                name="imovnOption"
              >
              <label class="custom-control-label" for="imovnOptionA">Imovn</label>
            </div>

            <div class="custom-control custom-radio custom-control-inline">
              <input
                id="imovnOptionB"
                v-model="identifiersForm.imovnOption"
                type="radio"
                :required="identifiersForm.identifierType == 'imovn'"
                class="custom-control-input"
                value="imovnOptionB"
                name="imovnOption"
              >
              <label class="custom-control-label" for="imovnOptionB">Random</label>
            </div>

            <div v-if="identifiersForm.imovnOption == 'imovnOptionA' && identifiersForm.imovnOption != ''">
              <input
                v-model="identifiersForm.imovn"
                type="text"
                :required="identifiersForm.imovnOption == 'imovnOptionA'"
                oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                maxlength="7"
                pattern="\d{7}"
                oninvalid="this.setCustomValidity('IMOVN Value required')"
                title="IMOVN"
                placeholder="IMOVN"
                class="form-control"
                style="width: 200px;"
                autocomplete="off"
              >
            </div>
            <div v-if="identifiersForm.imovnOption == 'imovnOptionB' && identifiersForm.imovnOption != ''">
              <input
                v-model="identifiersForm.imovnRandomCount"
                type="text"
                oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                oninvalid="this.setCustomValidity('IMOVN Count required')"
                title="IMOV Count"
                placeholder="Enter count"
                class="form-control"
                style="width: 200px;"
              >&nbsp;
            </div>
          </div>
        </div>

        <div v-show="identifiersForm.identifierType == 'manualURI'" class="verticleSpace horizontalSpace">
          <div class="form-inline">
            <div class="custom-control custom-radio custom-control-inline">
              <input
                id="manualUriStatic"
                v-model="identifiersForm.manualUriType"
                type="radio"
                :required="identifiersForm.identifierType == 'manualURI'"
                class="custom-control-input"
                value="static"
                name="manualURIOption"
              >
              <label class="custom-control-label" for="manualUriStatic">Static</label>
            </div>
            <div class="custom-control custom-radio custom-control-inline">
              <input
                id="manualUriDynamic"
                v-model="identifiersForm.manualUriType"
                type="radio"
                :required="identifiersForm.identifierType == 'manualURI'"
                class="custom-control-input"
                value="dynamic"
                name="manualURIOption"
              >
              <label class="custom-control-label" for="manualUriDynamic">Dynamic</label>
            </div>
          </div>
        </div>

        <div v-show="identifiersForm.serialType == 'range'" class="verticleSpace horizontalSpace">
          <input
            v-model="identifiersForm.rangeFrom"
            type="number"
            min="1"
            placeholder="From"
            title="Enter the Range From"
            class="form-control"
            :required="identifiersForm.serialType == 'range'"
            style="width:120px;"
          >
        </div>

        <div v-show="identifiersForm.serialType == 'none'" class="verticleSpace horizontalSpace">
          <input
            v-model="identifiersForm.serialNumber"
            type="number"
            class="form-control"
            min="1"
            placeholder="Serial ref. number"
            title="Enter serial number"
            :required="identifiersForm.serialType == 'none'"
          >
        </div>

        <div v-show="gcpVisibility">
          <b-form-select v-model="identifiersForm.gcpLength" :options="commonDropdownInfos.companyPrefixs" :required="gcpVisibility" />
        </div>

      </span>

      <!-- If the ManualURI then based on Static or Dynamic show the options -->
      <div v-if="identifiersForm.identifierType == 'manualURI'" class="form-group">
        <div class="form-inline">
          <input
            v-model="identifiersForm.baseManualUri"
            type="text"
            class="form-control"
            placeholder="Base Manual URI"
            title="Manual URI"
            :required="identifiersForm.identifierType == 'manualURI'"
          >

          <div v-if="identifiersForm.manualUriType == 'dynamic'" class="verticleSpace horizontalSpace">
            <input
              v-model="identifiersForm.manualUriRangeFrom"
              type="number"
              min="1"
              placeholder="From value"
              title="Enter the Range From"
              class="form-control"
              :required="identifiersForm.identifierType == 'manualURI' && identifiersForm.manualUriType == 'dynamic'"
            >
          </div>
        </div>
      </div>

      <div v-show="autoGenerateOptionA == true" class="form-group">
        <div class="form-inline">
          <div class="custom-control custom-radio custom-control-inline">
            <input
              id="rangeOption"
              v-model="identifiersForm.serialType"
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
              v-model="identifiersForm.serialType"
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
              v-model="identifiersForm.serialType"
              type="radio"
              class="custom-control-input"
              value="none"
              name="autogenerateOptions"
            >
            <label class="custom-control-label" for="noneOption">None</label>
          </div>
        </div>
      </div>

      <div v-show="randomOptions && identifiersForm.serialType == 'random'" class="form-inline">
        <span>
          <div class="custom-control custom-radio custom-control-inline">
            <input
              id="randomType1"
              v-model="identifiersForm.randomType"
              type="radio"
              class="custom-control-input"
              value="NUMERIC"
              :required="identifiersForm.serialType == 'random' && randomOptions"
              name="randomType"
            >
            <label class="custom-control-label" for="randomType1">Numeric characters</label>
          </div>
          <div class="custom-control custom-radio custom-control-inline">
            <input
              id="randomType2"
              v-model="identifiersForm.randomType"
              type="radio"
              class="custom-control-input"
              value="ALPHA_NUMERIC"
              :required="identifiersForm.serialType == 'random' && randomOptions"
              name="randomType"
            >
            <label class="custom-control-label" for="randomType2">URL-safe characters</label>
          </div>
          <div class="custom-control custom-radio custom-control-inline">
            <input
              id="randomType3"
              v-model="identifiersForm.randomType"
              type="radio"
              class="custom-control-input"
              value="URL_SAFE_CHARACTERS"
              :required="identifiersForm.serialType == 'random' && randomOptions"
              name="randomType"
            >
            <label class="custom-control-label" for="randomType3">All permitted characters</label>
          </div>
        </span>
      </div>

      <div v-show="identifiersForm.serialType == 'random' && randomOptions" class="form-inline verticleSpace">
        <div class="custom-control-inline horizontalSpace">
          <label for="randomMinLength">Min. Length: </label>
          <input
            id="randomMinLength"
            v-model="identifiersForm.randomMinLength"
            :required="identifiersForm.serialType == 'random' && randomOptions"
            type="number"
            min="1"
            max="20"
            class="form-control"
            placeholder="Ex: 1"
            title="Please Enter a Number between 1 and 20"
            style="width:80px;"
          >
        </div>

        <div class="custom-control-inline horizontalSpace">
          <label for="randomMaxLength">Max. Length: </label>
          <input
            id="randomMaxLength"
            v-model="identifiersForm.randomMaxLength"
            :required="identifiersForm.serialType == 'random' && randomOptions"
            type="number"
            min="1"
            max="20"
            class="form-control"
            placeholder="Ex: 5"
            title="Please Enter a Number between 1 and 20"
            style="width:80px;"
          >
        </div>
      </div>
    </b-form>
    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="identifiersForm">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      commonDropdownInfos: {},
      autoGenerateOptionA: false,
      showCommongcp: false,
      randomOptions: false,
      instanceIdentifiers: [],
      gcpVisibility: false,
      identifiersForm: {
        identifierType: null,
        gcpLength: null
      }
    }
  },
  mounted () {
    const instanceIdentifiers = require('~/static/EpcisData/IdentifiersDropdown.js')
    this.commonDropdownInfos = require('~/static/EpcisData/CommonDropdown.js')
    this.instanceIdentifiers = instanceIdentifiers.InstanceIdentifierTypes

    // Get the current identifier info based on the identifiers node on which user has clicked and assign the value
    const currentIdentifierInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.currentIdentifierInfo))

    // Get all the identifiers information from the store
    const currentInstanceIdentifier = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray)).find(idNode => idNode.identifiersId === currentIdentifierInfo.nodeId)

    // Check if the instance data for the current identifiers node is already provided if so then populate the same
    if (currentInstanceIdentifier !== undefined && currentInstanceIdentifier.instanceData !== undefined) {
      this.identifiersForm = currentInstanceIdentifier.instanceData
      this.instanceIdentifierSelection('')
    }

    // Check if the parent data for the current identifiers node is already provided if so then populate the same
    if (currentInstanceIdentifier !== undefined && currentInstanceIdentifier.parentData !== undefined) {
      this.identifiersForm = currentInstanceIdentifier.parentData
      this.instanceIdentifierSelection('')
    }
  },
  methods: {
    instanceIdentifierSelection (event) {
      // If user changes the identifiers then reset the value only during the creatioin of new identifiers and not during edition.
      // On change of the identifiers set the values to default and only show the relevent fields based on selection
      if (event !== '') {
        this.identifiersForm = { identifierType: event, gcpLength: null }
      }

      // On change of the Instance Identifier type reset all the values
      this.autoGenerateOptionA = false
      this.randomOptions = false
      this.showCommongcp = false

      // If the identifier type is URN for the identifiers node then keep the gcp visibility true
      const currentNodeId = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.currentNodeId))
      const allNodeInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray, null, 4))

      if (allNodeInfo.find(node => node.identifiersId === currentNodeId).identifierSyntax === 'URN') {
        this.gcpVisibility = true
      } else {
        this.gcpVisibility = false
      }

      // Based on the Class Identifiers Type set the respective options
      if (this.identifiersForm.identifierType === 'sgtin' || this.identifiersForm.identifierType === 'grai' || this.identifiersForm.identifierType === 'gdti' || this.identifiersForm.identifierType === 'cpi' || this.identifiersForm.identifierType === 'itip') {
        this.autoGenerateOptionA = true
        this.randomOptions = true
        this.showCommongcp = false
      } else if (this.identifiersForm.identifierType === 'sscc' || this.identifiersForm.identifierType === 'giai' || this.identifiersForm.identifierType === 'gsrn' || this.identifiersForm.identifierType === 'gsrnp' || this.identifiersForm.identifierType === 'gcn' || this.identifiersForm.identifierType === 'ginc' || this.identifiersForm.identifierType === 'gsin') {
        this.autoGenerateOptionA = true
        this.showCommongcp = true
        this.randomOptions = false
      } else if (this.identifiersForm.identifierType === 'gid' || this.identifiersForm.identifierType === 'usdod' || this.identifiersForm.identifierType === 'adi') {
        this.autoGenerateOptionA = true
        this.randomOptions = true
        this.showCommongcp = false
      } else if (this.identifiersForm.identifierType === 'bic' || this.identifiersForm.identifierType === 'manualURI') {
        this.autoGenerateOptionA = this.randomOptions = this.gcpVisibility = this.showCommongcp = false
      }
    },
    submitIdentifiersData (event) {
      // Based on the serial number generation type convert the values from String to Integer
      this.identifiersForm.rangeFrom = this.identifiersForm.rangeFrom !== undefined ? parseInt(this.identifiersForm.rangeFrom) : undefined
      this.identifiersForm.randomMinLength = this.identifiersForm.randomMinLength !== undefined ? parseInt(this.identifiersForm.randomMinLength) : undefined
      this.identifiersForm.randomMaxLength = this.identifiersForm.randomMaxLength !== undefined ? parseInt(this.identifiersForm.randomMaxLength) : undefined

      // Send the data to the store and store the informaiton
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/saveInstanceIdentifiersInfo', { instanceData: this.identifiersForm })

      // After saving the information hide the modal
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/hideInstanceIdentifiersModal')
    },

    // Function to close the modal on hit of Cancel/ESC button
    cancel () {
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/hideInstanceIdentifiersModal')
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

span, label{
    color:black
}
</style>
