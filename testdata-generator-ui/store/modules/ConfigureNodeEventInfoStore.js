/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
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
import Vue from 'vue'

const getDefaultState = () => {
  return {
    nodeCounter: 1,
    nodeEventInfoModal: false,
    currentNodeInfo: {},
    nodeEventInfoArray: [],
    getExistingNodeInfo: {}
  }
}

export const state = () => getDefaultState()

export const mutations = {
  // Function to increment the Event node counter to make all nodes unique
  incrementNodeCounter (state) {
    state.nodeCounter = state.nodeCounter + 1
  },
  // Function to show the eventInfoModal
  showNodeEventInfoModal (state) {
    state.nodeEventInfoModal = true
  },
  // Function to Hide the eventInfoModal
  hideNodeEventInfoModal (state) {
    state.nodeEventInfoModal = false
  },
  // On click of the button in Node store the respective eventType in the node
  populateCurrentEventType (state, payload) {
    // Populate the current nodeInfo
    state.currentNodeInfo.eventType = payload.eventType
    state.currentNodeInfo.nodeId = payload.nodeId
  },
  // Store information provided in each node for each event info
  populateNodeInfo (state, nodeObj) {
    state.nodeEventInfoArray.push(nodeObj)
  },
  // Update the node information with the provided information
  populateNodeEventInfo (state, payload) {
    const nodeInfo = state.nodeEventInfoArray.find(
      node =>
        parseInt(node.eventId) === parseInt(state.currentNodeInfo.nodeId)
    )
    Vue.set(nodeInfo, 'eventInfo', payload.eventInfo)
  },
  // If the node is removed from Drawflow then remove the respective nodes information from allEventInfo
  removeNodeEventInfo (state, nodeId) {
    state.nodeEventInfoArray.splice(
      state.nodeEventInfoArray.findIndex(
        node => parseInt(node.eventId) === parseInt(nodeId)
      ),
      1
    )
  },
  // If value is already present for selected NodeEvent then populate the information with existing value
  populateRawData (state, existingInfo) {
    state.getExistingNodeInfo = existingInfo
  },
  // Populate the array to store the information related to all of its parents
  populateNodeRelations (state, nodeRelations) {
    state.nodeParentRelation = nodeRelations
  },
  // Function to populate all the Events Node information into the nodeEventInfoArray during the import of the supply chain template
  populateNodeEventInfoArray (state, payload) {
    state.nodeEventInfoArray = payload.eventInfoArray
    state.nodeCounter = parseInt(payload.finalNodeId) + 1
  }
}

export const actions = {
  addNodeEventInfoArray ({ commit, rootState }, payload) {
    commit('populateNodeEventInfoArray', payload)
  }
}
