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
    id="EventsListModal"
    title="Provide Input Events List"
    size="lg"
    width="100%"
    height="100%"
    :visible="$store.state.modules.ConfigureImportEventsListStore.eventsListModal"
    modal-class="modal-fullscreen"
    @hide="cancel"
  >
    <b-form id="eventsListData" class="form-horizontal" autocomplete="on" @submit.prevent="submitEventsList">
      <textarea
        ref="eventsList"
        v-model="eventsList.events"
        class="form-control h-25"
        rows="20"
        placeholder="EPCIS Events List"
        spellcheck="false"
        data-gramm="false"
      />
    </b-form>
    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="eventsListData">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
export default {
  data () {
    return {
      eventsList: {
        events: []
      }
    }
  },
  mounted () {
  },
  methods: {
    // On click of the submit form send the eventsList in the text area to the ConfigureImportEventsListStore and hide the modal
    submitEventsList () {
      // Hide the modal
      this.$store.commit('modules/ConfigureImportEventsListStore/hideEventsListModal')

      // Call the method to process the input EPCIS list and extract the corresponding information
      this.$store.dispatch('modules/ConfigureImportEventsListStore/eventsExtractor', { eventsData: this.eventsList })

      // After converting all the events into required format design the nodes in drawflow
      this.$root.$emit('drawSupplyChainDesign')
    },
    // Function to close the modal on hit of Cancel/ESC button
    cancel () {
      this.$store.commit('modules/ConfigureImportEventsListStore/hideConnectorModal')
    }
  }
}
</script>

<style>
</style>
