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
    id="SourceDestination"
    title="Add Source/Destination"
    size="lg"
    width="100%"
    :visible="$store.state.modules.SourceDestinationStore.sourceDestinationModal"
    @hide="cancel"
  >
    <b-form id="AddSourceDestination" @submit.prevent="submitSourceDestination">
      <div class="form-group">
        <b-form-select v-model="sourceDestination.type" class="form-control">
          <b-form-select-option value="OWNING_PARTY" selected>
            Owning Party (CBV)
          </b-form-select-option>
          <b-form-select-option value="PROCESSING_PARTY">
            Processing Party (CBV)
          </b-form-select-option>
          <b-form-select-option value="LOCATION">
            Location (CBV)
          </b-form-select-option>
          <b-form-select-option value="OTHER">
            Other
          </b-form-select-option>
        </b-form-select>
      </div>

      <div class="form-group">
        <span v-if="sourceDestination.type == 'OWNING_PARTY' || sourceDestination.type == 'PROCESSING_PARTY'">
          <b-form-select v-model="sourceDestination.glnType" class="form-control" :required="sourceDestination.type == 'OWNING_PARTY' || sourceDestination.type == 'PROCESSING_PARTY'">
            <b-form-select-option value="SGLN">SGLN</b-form-select-option>
            <b-form-select-option value="PGLN">PGLN</b-form-select-option>
          </b-form-select>
        </span>
      </div>

      <div class="form-group">
        <span v-if="sourceDestination.type == 'OWNING_PARTY' || sourceDestination.type == 'PROCESSING_PARTY' || sourceDestination.type == 'LOCATION'">
          <div class="d-flex align-items-center mb-3">
            <span v-if="sourceDestination.glnType == 'SGLN' || sourceDestination.type == 'location&quot'" class="col-auto mx-2"> (414) </span>
            <span v-if="sourceDestination.glnType == 'PGLN'" class="col-auto mx-2"> (417) </span>
            <input
              v-model="sourceDestination.gln"
              type="text"
              pattern="\d{13}"
              oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
              maxlength="13"
              oninvalid="this.setCustomValidity('Source GLN must be 13 digits')"
              placeholder="Source GLN 13 digits"
              class="form-control"
              :required="sourceDestination.type == 'OWNING_PARTY' || sourceDestination.type == 'PROCESSING_PARTY' || sourceDestination.type == 'LOCATION'"
            >
          </div>

          <div class="d-flex align-items-center mb-3">
            <span v-if="sourceDestination.glnType == 'SGLN' || sourceDestination.type == 'LOCATION'" class="col-auto mx-2"> (254) </span>
            <span v-if="sourceDestination.glnType == 'SGLN' || sourceDestination.type == 'LOCATION'">
              <input
                v-model="sourceDestination.extension"
                type="text"
                oninput="this.value=this.value.replace(/[^0-9]/g,'');setCustomValidity('');"
                maxlength="13"
                oninvalid="this.setCustomValidity('Sources Extension is required and must be in digits')"
                placeholder="Source GLN Extension digits"
                class="form-control"
                :required="sourceDestination.glnType == 'SGLN' || sourceDestination.type == 'LOCATION'"
              >
            </span>
            <span v-if="$store.state.modules.SourceDestinationStore.vocabularySyntax == 'URN'" class="col-auto mx-2">
              <b-form-select v-model="sourceDestination.gcpLength" :options="dropdownValues" :required="$store.state.modules.SourceDestinationStore.vocabularySyntax == 'URN'" />
            </span>
          </div>
        </span>
      </div>

      <div class="form-group">
        <span v-if="sourceDestination.type == 'OTHER' ">
          <input v-model="sourceDestination.manualType" type="text" placeholder="Enter Source Type" class="form-control mb-3" :required="sourceDestination.type == 'OTHER'">
          <input v-model="sourceDestination.manualURI" type="text" placeholder="Enter Source URI" class="form-control" :required="sourceDestination.type == 'OTHER'">
        </span>
      </div>
    </b-form>
    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="AddSourceDestination">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
export default {
  data () {
    return {
      dropdownValues: {},
      sourceDestination: {
        type: 'OWNING_PARTY',
        glnType: 'SGLN',
        gcpLength: null
      }
    }
  },
  mounted () {
    this.dropdownValues = require('~/static/EpcisData/CommonDropdown.js').companyPrefixs

    // Check if user is trying to provide new SourceDestination or modifying existing SourceDestination, for modification show the existing information
    const currentSourceDestinationInfo = JSON.parse(JSON.stringify(this.$store.state.modules.SourceDestinationStore.currentSourceDestinationInfo))
    if (currentSourceDestinationInfo !== undefined && currentSourceDestinationInfo !== null && Object.keys(currentSourceDestinationInfo).length !== 0) {
      this.sourceDestination = currentSourceDestinationInfo
    }
  },
  created () {
  },
  methods: {
    submitSourceDestination () {
      // Function to get the data after the submission of modal
      this.$store.commit('modules/SourceDestinationStore/hideSourceDestinationModal')
      this.$store.commit('modules/SourceDestinationStore/sourceDestinationAddition', this.sourceDestination)
    },

    // Method to hide the SourceDestination modal on click of the Cancel/ESC button
    cancel () {
      // Hide the SourceDestination modal
      this.$store.commit('modules/SourceDestinationStore/hideSourceDestinationModal')
      // Reset current SourceDestination elements
      this.$store.commit('modules/SourceDestinationStore/resetCurrentSourceDestination')
    }
  }
}
</script>

  <style>
select {
    text-align: center;
    text-align-last: center;
    -moz-text-align-last: center;
}
  </style>
