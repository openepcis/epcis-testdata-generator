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
    id="Sensor Elements"
    title="Add Sensor Information"
    size="xl"
    :visible="$store.state.modules.SensorElementsStore.sensorModal"
    @hide="cancel"
  >
    <b-form id="AddSensorInformation" @submit.prevent="submitSensorInformation">
      <div class="form-check-inline">
        <label class="form-check-label horizontalSpace text-nowrap" for="sensorMetadataList">Metadata Elements</label>
        <multiselect
          id="sensorMetadataList"
          v-model="selectedSensorMetaData"
          :options="sensorMetadatas.map((i) => i.value)"
          :custom-label="
            (opt) => sensorMetadatas.find((x) => x.value == opt).text
          "
          :multiple="true"
          :close-on-select="false"
        />

        <label class="form-check-label horizontalSpace text-nowrap" for="SelectReqReport">Report Elements</label>
        <multiselect
          id="SelectReqReport"
          v-model="selectedSensorReport"
          :options="SensorReportDatas.map((i) => i.value)"
          :custom-label="
            (opt) => SensorReportDatas.find((x) => x.value == opt).text
          "
          :multiple="true"
          :close-on-select="false"
        />
      </div>

      <div class="pull-right" style="display:inline-block;">
        <button class="btn btn-info" @click="addSensorReportElement($event)">
          Add Sensor Report
        </button>
      </div>

      <!--Show the Information for MetaData-->
      <div v-if="selectedSensorMetaData.length > 0" class="verticleSpace">
        <span v-if="selectedSensorMetaData.length > 0">
          <strong>Sensor Metadata Elements: </strong></span>
        <span class="form-group form-inline">
          <span v-if="selectedSensorMetaData.indexOf('time') >= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">Datetime</label>
            <input
              v-model="sensorMetaData.time"
              :required="selectedSensorMetaData.indexOf('time') >= 0"
              type="datetime-local"
              class="form-control"
              title="Set Sensor Metadata time"
              step="1"
            >
          </span>

          <span v-if="selectedSensorMetaData.indexOf('startTime') >= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">Start Time</label>
            <input
              v-model="sensorMetaData.startTime"
              :required="selectedSensorMetaData.indexOf('startTime') >= 0"
              type="datetime-local"
              class="form-control"
              title="Set Sensor Metadata Start time"
              step="1"
            >
          </span>

          <span v-if="selectedSensorMetaData.indexOf('endTime') >= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">End Time</label>
            <input
              v-model="sensorMetaData.endTime"
              :required="selectedSensorMetaData.indexOf('endTime') >= 0"
              type="datetime-local"
              class="form-control"
              title="Set Sensor Metadata End time"
              step="1"
            >
          </span>

          <span v-if="selectedSensorMetaData.indexOf('deviceID') >= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">Device ID</label>
            <input
              v-model="sensorMetaData.deviceID"
              :required="selectedSensorMetaData.indexOf('deviceID') >= 0"
              type="text"
              class="form-control"
            >
          </span>

          <span v-if="selectedSensorMetaData.indexOf('deviceMetadata') >= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">Device Metadata</label>
            <input
              v-model="sensorMetaData.deviceMetadata"
              :required="selectedSensorMetaData.indexOf('deviceMetadata') >= 0"
              type="text"
              class="form-control"
            >
          </span>

          <span v-if="selectedSensorMetaData.indexOf('rawData') >= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">Raw Data</label>
            <input
              v-model="sensorMetaData.rawData"
              :required="selectedSensorMetaData.indexOf('rawData') >= 0"
              type="text"
              class="form-control"
            >
          </span>

          <span v-if="selectedSensorMetaData.indexOf('dataProcessingMethod')>= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">Data Processing Method</label>
            <input
              v-model="sensorMetaData.dataProcessingMethod"
              :required="selectedSensorMetaData.indexOf('dataProcessingMethod') >= 0"
              type="text"
              class="form-control"
            >
          </span>

          <span v-if="selectedSensorMetaData.indexOf('bizRules')>= 0" class="horizontalSpace verticleSpace">
            <label class="form-label">Business Rules</label>
            <input v-model="sensorMetaData.bizRules" :required="selectedSensorMetaData.indexOf('bizRules') >= 0" type="text" class="form-control">
          </span>
        </span>
      </div>

      <!-- Show the Sensor Element on click of the button -->
      <div class="horizontalSpace verticleSpace">
        <span v-if="sensorReportArray.length > 0"><strong>Sensor Report Elements: </strong></span>
        <div v-if="sensorReportArray.length > 0 && selectedSensorReport.length > 0">
          <div v-for="reportElement in sensorReportArray" :key="reportElement.ID" class="form-group">
            <span class="form-group form-inline">
              <span v-if="selectedSensorReport.indexOf('type') >= 0" class="horizontalSpace">
                <label class="form-label">Type</label>
                <b-form-select v-model="reportElement.type" class="form-control" :options="sensorReportTypes" :selected="null" />
              </span>

              <span v-if="selectedSensorReport.indexOf('exception') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Exception</label>
                <input v-model="reportElement.exception" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('deviceID') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Device ID</label>
                <input v-model="reportElement.deviceID" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('deviceMetadata') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Device MetaData</label>
                <input v-model="reportElement.deviceMetadata" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('rawData') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Raw Data</label>
                <input v-model="reportElement.rawData" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('dataProcessingMethod') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Data Processing Method</label>
                <input v-model="reportElement.dataProcessingMethod" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('time') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Time</label>
                <input v-model="reportElement.time" type="datetime-local" class="form-control" step="1">
              </span>

              <span v-if="selectedSensorReport.indexOf('microorganism') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Microorganism</label>
                <input v-model="reportElement.microorganism" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('chemicalSubstance') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Chemical Substance</label>
                <input v-model="reportElement.chemicalSubstance" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('value') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Value</label>
                <input v-model="reportElement.value" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('component') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Component</label>
                <input v-model="reportElement.component" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('stringValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">String Value</label>
                <input v-model="reportElement.stringValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('booleanValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Boolean Value</label>
                <input v-model="reportElement.booleanValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('hexBinaryValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Hex Binary Value</label>
                <input v-model="reportElement.hexBinaryValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('uriValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">URI Value</label>
                <input v-model="reportElement.uriValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('minValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Min Value</label>
                <input v-model="reportElement.minValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('maxValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Max Value</label>
                <input v-model="reportElement.maxValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('meanValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Mean Value</label>
                <input v-model="reportElement.meanValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('sDev') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Standard Deviation</label>
                <input v-model="reportElement.sDev" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('percRank') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Perc Rank</label>
                <input v-model="reportElement.percRank" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('percValue') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">Perc Value</label>
                <input v-model="reportElement.percValue" type="text" class="form-control">
              </span>

              <span v-if="selectedSensorReport.indexOf('uom') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">UOM</label>
                <b-form-select v-model="reportElement.uom" class="form-control" :options="sensorReportUOMs" :selected="null" />
              </span>

              <span v-if="selectedSensorReport.indexOf('coordinateReferenceSystem') >= 0" class="horizontalSpace verticleSpace">
                <label class="form-label">CRS</label>
                <input v-model="reportElement.coordinateReferenceSystem" type="text" class="form-control">
              </span>

              <!-- Add Delete button for each row -->
              <span v-if="sensorReportArray.length > 0" style="margin-top:4%" class="horizontalSpace verticleSpace">
                <button class="btn btn-danger" @click="deleteSensorReport(reportElement.ID)"><em class="bi bi-trash" /></button>
              </span>
            </span>
            <hr>
          </div>
        </div>
      </div>
    </b-form>
    <template #modal-footer="{ cancel }">
      <b-btn @click="cancel">
        Cancel
      </b-btn>
      <b-btn variant="primary" type="submit" form="AddSensorInformation">
        OK
      </b-btn>
    </template>
  </b-modal>
</template>

<script>
import Multiselect from 'vue-multiselect'

export default {
  components: {
    Multiselect
  },
  data () {
    return {
      sensorMetadatas: [],
      SensorReportDatas: [],
      sensorReportTypes: [],
      sensorReportUOMs: [],
      selectedSensorMetaData: [],
      selectedSensorReport: [],
      sensorMetaData: {},
      sensorReportArray: [],
      sensorReportCount: 0
    }
  },
  mounted () {
    const sensorDatas = require('~/static/EpcisData/CommonDropdown.js')
    this.sensorMetadatas = sensorDatas.SensorMetaDatas
    this.SensorReportDatas = sensorDatas.SensorReportDatas
    this.sensorReportTypes = sensorDatas.sensorReportTypes
    this.sensorReportUOMs = sensorDatas.sensorReportUOMs

    // Check if user is trying to provide new SensorElement or modifying existing SensorElement, for modification show the existing information
    const currentSensorElementInfo = JSON.parse(JSON.stringify(this.$store.state.modules.SensorElementsStore.currentSensorElementInfo))
    if (currentSensorElementInfo !== undefined && currentSensorElementInfo !== null && Object.keys(currentSensorElementInfo).length !== 0) {
      // If metadata is present then only add the values
      if (currentSensorElementInfo.sensorMetadata) {
        this.selectedSensorMetaData = Object.keys(currentSensorElementInfo.sensorMetadata)
        this.sensorMetaData = currentSensorElementInfo.sensorMetadata
      }

      // If reportData is present then only add the values
      if (currentSensorElementInfo.sensorReport.length > 0) {
        this.selectedSensorReport = Object.keys(currentSensorElementInfo.sensorReport[0]).filter(item => item !== 'ID')
        this.sensorReportArray = currentSensorElementInfo.sensorReport
        this.sensorReportCount = currentSensorElementInfo.sensorReport.length
      }
    }
  },
  methods: {
    // Function called on submission of the sensor information
    submitSensorInformation () {
      // Keep only those sensor metadata values which are selected by user using the multiselect option
      this.sensorMetaData = Object.fromEntries(this.selectedSensorMetaData.map(k => [k, this.sensorMetaData[k]]))

      // Keep only those sensor report values which are selected by user using the multiselect option
      this.sensorReportArray = this.sensorReportArray.map(v => Object.fromEntries(Object.entries(v).filter(([k, v]) => this.selectedSensorReport.includes(k))))

      const sensorElement = {}
      sensorElement.sensorMetadata = this.sensorMetaData
      sensorElement.sensorReport = this.sensorReportArray

      // Save the sensorElement information into the Store SensorElementList
      this.$store.commit('modules/SensorElementsStore/saveSensorElementInformation', sensorElement)

      // Hide the sensor modal
      this.$store.commit('modules/SensorElementsStore/hideSensorModal')
    },

    // Function to add the sensor report elements
    addSensorReportElement (event) {
      event.preventDefault()
      const sensorReport = {}
      sensorReport.ID = this.sensorReportCount
      this.sensorReportArray.push(sensorReport)
      this.sensorReportCount++
    },

    // Based on user click delete the respective sensor report elements
    deleteSensorReport (sensorReportID) {
      const idx = this.sensorReportArray.findIndex(obj => obj.ID === sensorReportID)
      if (idx !== -1) {
        this.sensorReportArray.splice(idx, 1)
      }
    },

    // Method to hide the Sensor Elements modal on click of the Cancel/ESC button
    cancel () {
      // Hide the sensor modal
      this.$store.commit('modules/SensorElementsStore/hideSensorModal')
      // Reset current sensor element information
      this.$store.commit('modules/SensorElementsStore/resetCurrentSensorElementInfo')
    }
  }
}
</script>

<style  scoped>
.horizontalSpace {
  padding-right: 8px;
  padding-left: 8px;
}

.verticleSpace {
  padding-top: 10px;
  padding-bottom: 10px;
}

.text-nowrap {
    white-space: nowrap;
}
</style>
