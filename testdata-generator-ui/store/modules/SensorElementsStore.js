/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
// OpenEPCIS Testdata Generator UI
// Copyright (C) 2022  benelog GmbH & Co. KG
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// See LICENSE in the project root for license information.
const getDefaultState = () => {
  return {
    sensorModal: false,
    sensorElementCount: 0,
    sensorElementList: [],
    currentSensorElementInfo: null,
    currentSensorElementId: null
  }
}

export const state = () => getDefaultState()

export const mutations = {
  showSensorModal (state) {
    // Function to show the Sensor Modal
    state.sensorModal = true
  },
  hideSensorModal (state) {
    // Function to show the Sensor Modal
    state.sensorModal = false
  },
  saveSensorElementInformation (state, sensorElement) {
    // On submit of the sensor form if user is adding new sensor element
    if (state.currentSensorElementId == null && state.currentSensorElementInfo == null) {
      sensorElement.ID = state.sensorElementCount
      state.sensorElementList.push(sensorElement)
      state.sensorElementCount++
    } else {
      // If user is trying to modify existing sensor element information then replace particular sensor element
      const replaceSensorElementInfo = state.sensorElementList.find(sensorElement => sensorElement.ID === state.currentSensorElementId)
      Object.assign(replaceSensorElementInfo, sensorElement)
      state.currentSensorElementInfo = null
      state.currentSensorElementId = null
    }
  },
  deleteSensorElement (state, sensorElementID) {
    // Based on user selection remove respective sensorElement information
    state.sensorElementList.splice(state.sensorElementList.filter(obj => obj.ID === sensorElementID), 1)
  },
  resetSensorData (state) {
    // Reset all state variables to its default value
    Object.assign(state, getDefaultState())
  },
  // During modification Nodeevent info populate the Extensions store info with raw data of the Identifiers
  populateRawData (state, payload) {
    state.sensorElementList = payload.sensorData !== undefined ? payload.sensorData : []

    // Update the counter values for addition of the next values do not conflict with existing values
    state.sensorElementCount = state.sensorElementList.length > 0 ? state.sensorElementList.at(-1).ID + 1 : 0
  },
  modifySensorElement (state, payload) {
    // On click of the modify sensor element button populate the respectie sensor element information to display on modal
    state.currentSensorElementInfo = state.sensorElementList.find(senElement => senElement.ID === payload.sensorElementID)
    state.currentSensorElementId = parseInt(payload.sensorElementID)
  },
  resetCurrentSensorElementInfo (state) {
    // Reset the currentSensorElementInfo on pressing escape or cancel button in modal
    state.currentSensorElementInfo = null
    state.currentSensorElementId = null
  }
}
