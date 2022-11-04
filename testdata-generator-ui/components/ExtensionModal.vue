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
    id="Extension"
    title="Add Another Element"
    size="lg"
    width="100%"
    :visible="$store.state.modules.ExtensionDataStore.extensionModal"
    @hide="cancel"
  >
    <b-form id="AddExtension" @submit.prevent="submitExtension">
      <div class="form-group">
        <label for="recipient-name" class="col-form-label">Data Form</label>
        <b-form-select v-model="extension.element" class="form-control">
          <b-form-select-option value="extensionElement">
            Element
          </b-form-select-option>
        </b-form-select>
      </div>

      <div class="form-group">
        <label for="message-text" class="col-form-label">Namespace URI:</label>
        <input
          v-model="extension.namespace"
          type="text"
          placeholder="https://example.com"
          class="form-control"
          autocomplete="off"
          required
        >
      </div>
      <div class="form-group">
        <label for="message-text" class="col-form-label">Local Name:</label>
        <input
          v-model="extension.localName"
          type="text"
          class="form-control"
          autocomplete="off"
          required
        >
      </div>
      <div class="form-group">
        <label for="AddExtensionDataType" class="col-form-label">Data Type:</label>
        <b-form-select v-model="extension.dataType" class="form-control">
          <b-form-select-option value="string">
            String
          </b-form-select-option>
          <b-form-select-option value="complex">
            Complex
          </b-form-select-option>
        </b-form-select>
      </div>
    </b-form>
    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="AddExtension">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
export default {
  data () {
    return {
      extension: {
        type: this.$store.state.modules.ExtensionDataStore.extensionType,
        element: 'extensionElement',
        dataType: 'string',
        namespace: '',
        localName: '',
        text: '',
        complex: null
      }
    }
  },
  mounted () {
    // Check if user is trying to provide new Extension or modifying existing Extension, for modification show the existing information
    const currentExtensionInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ExtensionDataStore.currentExtensionInfo))
    if (currentExtensionInfo !== undefined && currentExtensionInfo !== null && Object.keys(currentExtensionInfo).length !== 0) {
      this.extension = currentExtensionInfo
    }
  },
  created () {
  },
  methods: {
    submitExtension () {
      // Function to get the data after the submission of modal
      this.$store.commit('modules/ExtensionDataStore/hideExtensionModal')
      this.$store.commit('modules/ExtensionDataStore/extensionsAddition', this.extension)
    },

    // Method to hide the extension on click of the Cancel/ESC button
    cancel () {
      // Hide the Extension modal
      this.$store.commit('modules/ExtensionDataStore/hideExtensionModal')
      // Reset current extension elements
      this.$store.commit('modules/ExtensionDataStore/resetCurrentExtensionInfo')
    }
  }
}
</script>

<style>

</style>
