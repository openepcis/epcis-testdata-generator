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
    instanceIdentifiersModal: false,
    classIdentifiersModal: false,
    parentIdentifierFlag: false,
    currentIdentifierInfo: {},
    currentNodeId: 0,
    identifiersArray: []
  }
}

export const state = () => getDefaultState()

export const mutations = {
  showInstanceIdentifiersModal (state) {
    // Function to Show the Instance Identifiers Modal
    state.instanceIdentifiersModal = true
  },
  hideInstanceIdentifiersModal (state) {
    // Function to Hide the Instance Identifiers Modal
    state.instanceIdentifiersModal = false
  },
  showClassIdentifiersModal (state) {
    // Function to Show the Class Identifiers Modal
    state.classIdentifiersModal = true
  },
  hideClassIdentifiersModal (state) {
    // Function to Hide the Class Identifiers Modal
    state.classIdentifiersModal = false
  },
  populateIdentifiersInfo (state, identifiersObj) {
    // After creation of Identifiers node add the object to IdentifiersArray
    state.identifiersArray.push(identifiersObj)
  },
  parentIdentifierFlagToggle (state) {
    // If information provided is for ParentID then taggle the flag accordingly
    state.parentIdentifierFlag = !state.parentIdentifierFlag
  },
  populateCurrentNodeId (state, nodeId) {
    // Populate the currentNodeid to track which node is clicked and information being populated
    state.currentNodeId = nodeId
  },
  saveInstanceIdentifiersInfo (state, payload) {
    // Upon saving the Instance Identifiers info save the information into respective object of InstanceIdentifiersArray
    const identifiersNode = state.identifiersArray.find(node => node.identifiersId === state.currentNodeId)

    // If the information provided is for parentID then save the informaiton for parentiD
    if (state.parentIdentifierFlag) {
      // Use the Vue reactive approach to add the parentData object to existing object
      Vue.set(identifiersNode, 'parentData', payload.instanceData)
      state.parentIdentifierFlag = false
    } else {
      // Use the Vue reactive approach to add the instanceData object to existing object
      Vue.set(identifiersNode, 'instanceData', payload.instanceData)
    }
  },
  saveClassIdentifiersInfo (state, payload) {
    // Upon saving the Class Identifiers info save the information in repective object of ClassIdentifiersArray
    const identifiersNode = state.identifiersArray.find(node => node.identifiersId === state.currentNodeId)
    Vue.set(identifiersNode, 'classData', payload.classData)
  },
  removeIdentifiersInfo (state, nodeId) {
    // If the Node is removed then remove the respective information Identifiers Array
    state.identifiersArray.splice(state.identifiersArray.findIndex(idNode => parseInt(idNode.identifiersId) === parseInt(nodeId)), 1)
  },
  identifiersSyntaxChange (state, payload) {
    // On change of the IdentifierSytax value change the respective value within the node information
    const identifiersNode = state.identifiersArray.find(node => node.identifiersId === payload.nodeId)
    identifiersNode.identifierSyntax = payload.syntaxValue
  },
  // On click of the button in Node store the respective eventType in the node
  populateCurrentIdentifierInfo (state, payload) {
    // Populate the current nodeInfo
    state.currentIdentifierInfo.identifierType = payload.identifierType
    state.currentIdentifierInfo.nodeId = payload.nodeId
  },
  // Function to populate all the Identifiers Node information into the identifiersArray during the import of the supply chain template
  populateIdentifiersArray (state, identifiersArray) {
    state.identifiersArray = identifiersArray
  }
}
