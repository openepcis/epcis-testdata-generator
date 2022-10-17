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
  <div class="eventContainer">
    <div class="eventsNode" style="display: grid; grid-template-columns: repeat(3,1fr);">
      <div style="justify-self: left;" />
      <div style="justify-self: center;">
        {{ eventType }}
      </div>
      <div style="justify-self: right;padding-left:5px">
        <button
          ref="Btn"
          class="btn btn-primary btn-circle btn-sm justify-content-end"
          df-ID
          :value="ID"
          title="Add Event Information"
          @click="eventModal(ID)"
        >
          <em class="bi bi-pencil-square" />
        </button>
      </div>
    </div>
    <div class="text-center nodeContainer" style="font-size:9px;white-space: pre">
      <table style="width:80%;" class="center">
        <tr>
          <td>count</td>
          <td>:</td>
          <td>{{ eventCount }}</td>
        </tr>
        <tr>
          <td>action</td>
          <td>:</td>
          <td>{{ action }}</td>
        </tr>
        <tr>
          <td>bizStep</td>
          <td>:</td>
          <td>{{ businessStep }}</td>
        </tr>
        <tr>
          <td>disposition</td>
          <td>:</td>
          <td>{{ disposition }}</td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      ID: '',
      eventType: '',
      displayInfo: false,
      eventCount: '',
      action: '',
      businessStep: '',
      disposition: ''
    }
  },
  mounted () {
    this.$nextTick(() => {
      const id = this.$el.parentElement.parentElement.id
      const data = this.$df.getNodeFromId(id.slice(5))
      this.ID = data.data.ID
      this.eventType = data.data.eventType

      // Watch for the changes on Vuex eventInfoArray so after providing the node information they can be displayed on the respective EventNode
      this.$watch('$store.state.modules.ConfigureNodeEventInfoStore.nodeEventInfoArray', (val) => {
        const currentEventInfo = val.find(node => node.eventId === this.ID)

        // Check if the user has provided the values for eventInfo if so then obtain the respective variables
        if (currentEventInfo !== undefined && currentEventInfo.eventInfo !== undefined) {
          this.eventCount = currentEventInfo.eventInfo.eventCount !== undefined && currentEventInfo.eventInfo.eventCount !== null ? currentEventInfo.eventInfo.eventCount : ''
          this.action = currentEventInfo.eventInfo.action !== undefined && currentEventInfo.eventInfo.action !== null ? currentEventInfo.eventInfo.action.toLowerCase() : ''
          this.businessStep = currentEventInfo.eventInfo.businessStep !== undefined && currentEventInfo.eventInfo.businessStep !== null ? currentEventInfo.eventInfo.businessStep.toLowerCase() : ''
          this.disposition = currentEventInfo.eventInfo.disposition !== undefined && currentEventInfo.eventInfo.disposition !== null ? currentEventInfo.eventInfo.disposition.toLowerCase() : ''

          // Update the connections so it is always attached to the port
          this.$df.updateConnectionNodes('node-' + this.ID)
        }
      }, { immediate: true, deep: true })
    })
  },
  methods: {
    // On click of the button show the modal
    eventModal (nodeId) {
      // Store the current eventType based on the clicked event node
      this.$store.commit('modules/ConfigureNodeEventInfoStore/populateCurrentEventType', { eventType: this.eventType, nodeId: this.ID })

      // For each event reset the data so all values are set to their default values
      this.$store.commit('modules/ExtensionDataStore/resetExtensionsData')
      this.$store.commit('modules/SensorElementsStore/resetSensorData')

      // On click of the add info check if the event information is already present and if modifications are being done
      const nodeEventInfo = this.$store.state.modules.ConfigureNodeEventInfoStore.nodeEventInfoArray.find(node => parseInt(node.eventId) === parseInt(this.ID))

      // If the information is available then populate the store
      if (nodeEventInfo.eventInfo !== undefined) {
        const eventInfo = nodeEventInfo.eventInfo

        // Populate the raw information related to the Extensions
        this.$store.commit('modules/ExtensionDataStore/populateRawData', { userExtensions: eventInfo.userExtensions, ilmd: eventInfo.ilmd, errorExtensions: eventInfo.error.errorExtensions })

        // Populate the raw information related to the Sensorinformation
        this.$store.commit('modules/SensorElementsStore/populateRawData', { sensorData: eventInfo.sensorElementList })

        // Populate all other basic event information
        this.$store.commit('modules/ConfigureNodeEventInfoStore/populateRawData', eventInfo)
      } else {
        // Reset the existing Node Info from the ConfigureNodeEventInfoStore
        this.$store.commit('modules/ConfigureNodeEventInfoStore/populateRawData', {})
      }

      // On click of the button open the modal and request for information
      this.$store.commit('modules/ConfigureNodeEventInfoStore/showNodeEventInfoModal')
    }
  }
}
</script>

<style>
.btn-circle.btn-sm {
    width: 30px;
    padding: 6px 0px;
    border-radius: 15px;
    font-size: 9px;
    text-align: center;
}

table, th, td {
  border:1px grey;
}

.center {
  margin-left: auto;
  margin-right: auto;
  margin-bottom:10px;
}
</style>
