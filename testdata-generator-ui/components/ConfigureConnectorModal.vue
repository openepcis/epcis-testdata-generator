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
    id="ConnectorModal"
    title="Add connector information"
    size="lg"
    width="100%"
    :visible="$store.state.modules.ConnectorStore.connectorModal"
    @hide="cancel"
  >
    <b-form id="connectorData" class="form-horizontal" autocomplete="on" @submit.prevent="submitConnectorData">
      <div class="row">
        <div class="col-sm-12">
          <label class="col-form-label" style="font-weight:bold">EPC Count</label>
          <input
            v-model="connector.epcCount"
            type="number"
            class="form-control"
            autocomplete="off"
            required
          >
        </div>
      </div>

      <div class="row" style="padding-top:3%">
        <div class="col-sm-6">
          <label class="col-form-label" style="font-weight:bold">Quantity Count</label>
          <input
            v-model="connector.classCount"
            type="number"
            class="form-control"
            autocomplete="off"
            required
          >
        </div>
        <div class="col-sm-6">
          <label class="col-form-label" style="font-weight:bold">Quantity</label>
          <input
            v-model="connector.quantity"
            type="number"
            pattern="[0-9]+([\.,][0-9]+)?"
            step="0.01"
            class="form-control"
            autocomplete="off"
            required
          >
        </div>
      </div>
    </b-form>
    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="connectorData">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
export default {
  data () {
    return {
      connector: {
        epcCount: 0,
        classCount: 0,
        quantity: 0.0
      }
    }
  },
  mounted () {
    // Check if the connector values have been already provided if so then assign the values and open the modal
    if (JSON.stringify(this.$store.state.modules.ConnectorStore.currentConnector) !== '{}') {
      // Get the current connector information
      const connectorInfo = this.$store.state.modules.ConnectorStore.connectorArray.find(con => parseInt(con.source) === parseInt(this.$store.state.modules.ConnectorStore.currentConnector.output_id) && parseInt(con.target) === parseInt(this.$store.state.modules.ConnectorStore.currentConnector.input_id))

      if (connectorInfo !== undefined) {
        // Based on the current connector info assign the values
        this.connector.epcCount = connectorInfo.epcCount !== undefined ? connectorInfo.epcCount : 0
        this.connector.classCount = connectorInfo.classCount !== undefined ? connectorInfo.classCount : 0
        this.connector.quantity = connectorInfo.quantity !== undefined ? connectorInfo.quantity : 0.0
      }
    }
  },
  methods: {
    // Function to get the data after the submission of modal
    submitConnectorData () {
      this.$store.commit('modules/ConnectorStore/hideConnectorModal')
      this.$store.commit('modules/ConnectorStore/populateConnectorInfo', this.connector)
    },
    // Function to close the modal on hit of Cancel/ESC button
    cancel () {
      this.$store.commit('modules/ConnectorStore/hideConnectorModal')
    }
  }
}
</script>

<style>

</style>
