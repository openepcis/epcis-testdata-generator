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
    id="ImportDesignInfo"
    title="Import Design from URL"
    size="lg"
    width="100%"
    :visible="$store.state.modules.DesignTestDataStore.showImportDesignModal"
    @hide="cancel"
  >
    <b-form id="ImportDesign" @submit.prevent="submitImportDesignURL">
      <div class="form-group">
        <input
          id="designURL"
          v-model="designURL"
          type="text"
          class="form-control"
          placeholder="Enter URL to import the design"
          required
        >
      </div>
    </b-form>

    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="ImportDesign">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  data () {
    return {
      designURL: ''
    }
  },
  methods: {
    ...mapActions(['modules/DesignTestDataStore/obtainURLData']),

    submitImportDesignURL () {
      this.$store.dispatch('modules/DesignTestDataStore/obtainURLData', this.designURL)

      // Function to get the data after the submission of modal
      this.$store.commit('modules/DesignTestDataStore/hideImportDesignModal')
    },

    // Method to hide the extension on click of the Cancel/ESC button
    cancel () {
      // Hide the Import Design modal
      this.$store.commit('modules/DesignTestDataStore/hideImportDesignModal')
    }
  }
}
</script>

  <style>

  </style>
