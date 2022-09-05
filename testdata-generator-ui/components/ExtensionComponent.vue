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
    <div
      v-for="extension in extension.complex"
      :key="extension.ID"
      class="form-inline"
    >
      <span>{{ extension.namespace + ":" + extension.localName }}</span>
      <input v-if="extension.dataType == 'string'" :value="extension.text" type="text" @input="extensionText($event,extension.ID)">
      <span v-if="extension.dataType == 'complex'" class="horizontalSpace verticleSpace">
        <ExtensionComponent
          v-if="extension.dataType == 'complex'"
          :extension="extension"
        />
      </span>
      <span class="horizontalSpace verticleSpace" />
      <button class="btn btn-danger" @click="deleteExtension($event,extension.ID)">
        <em class="bi bi-trash" />
      </button>
    </div>
    <button @click="addNewExtension($event)">
      Add another
    </button>
  </div>
</template>

<script>
import ExtensionComponent from '@/components/ExtensionComponent.vue'

export default {
  components: {
    ExtensionComponent
  },
  props: {
    extension: Object
  },
  created () {
    this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', this.extension.type)
  },
  methods: {
    addNewExtension (event) {
      // Add the extension on click of the add button by the user
      event.preventDefault()
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', this.extension)
      this.$store.commit('modules/ExtensionDataStore/toggleExtensionModal')
    },
    deleteExtension (event, extensionID) {
      // Delete the extension on click of the delete button by the user
      event.preventDefault()
      this.$store.commit('modules/ExtensionDataStore/extensionTypePopulator', this.extension.type)
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', this.extension)
      this.$store.commit('modules/ExtensionDataStore/deleteExtension', extensionID)
    },
    extensionText (event, extensionID) {
    // Based on change in input text field change the respective value
      this.$store.commit('modules/ExtensionDataStore/setParentExtension', this.extension)
      this.$store.commit('modules/ExtensionDataStore/extensionText', { text: event.target.value, extensionID })
    }
  }
}
</script>

<style scoped>
.horizontalSpace{
  padding-right: 8px;
  padding-left: 8px;
}

.verticleSpace{
  padding-top:8px;
  padding-bottom: 8px;
}
</style>
