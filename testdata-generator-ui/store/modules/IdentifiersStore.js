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
const getDefaultState = () => {
  return {
    eventCount: 1,
    identifierSyntax: 'URN',
    parentFlag: false,
    instaceIdentifiersModal: false,
    classIdentifiersModal: false,
    identifiersFlagType: '',
    identifiersCounter: 1,
    instanceIdentifiersList: [],
    classIdentifiersList: [],
    outputInstanceIdentifiersList: [],
    outputclassIdentifiersList: [],
    parentIdentifiersList: [],
    currentIdentifiersInfo: {}
  }
}

export const state = () => getDefaultState()

export const mutations = {
  parentFlagToggle (state) {
    // Set the parent flag if user is trying to provide value for ParentID
    state.parentFlag = !state.parentFlag
  },
  resetIdentifiersData (state) {
    // Reset all data if the eventType changes
    state.identifiersCounter = 1
    state.instanceIdentifiersList = []
    state.classIdentifiersList = []
    state.outputInstanceIdentifiersList = []
    state.outputclassIdentifiersList = []
    state.parentIdentifiersList = []
  },
  populateEventCount (state, eventCount) {
    // Populate the event count values for the parent id creation
    state.eventCount = eventCount
  },
  populateIdentifiersType (state, payload) {
    // Function to populate the identifiers type
    state.identifierSyntax = payload.identifierSyntax
  },
  showInstanceIdentifiersModal (state) {
    // Function to show the modal for instance identifiers
    state.instaceIdentifiersModal = true
  },
  hideInstanceIdentifiersModal (state) {
    // Function to hide the modal for instance identifiers
    state.instaceIdentifiersModal = false
  },
  populateInstanceIdentifiers (state, instanceIdentifiersList) {
    // Function to populate the Instance Identifiers
    state.instanceIdentifiersList = instanceIdentifiersList
  },
  showClassIdentifiersModal (state) {
    // Function to show the modal for class identifiers
    state.classIdentifiersModal = true
  },
  hideClassIdentifiersModal (state) {
    // Function to hide the modal for class identifiers
    state.classIdentifiersModal = false
  },
  saveInstanceIdentifiersInformation (state, payload) {
    // If there are Instance Identifiers already then remove them (added to handle the modification of instance identifiers)
    if (state.instanceIdentifiersList.length > 0) {
      state.instanceIdentifiersList = []
    }

    // Function to add the instance identifiers information into the array
    const instanceIdentifierObj = { ID: state.identifiersCounter, name: 'EPC : ' + state.identifiersCounter, type: payload.instanceData.identifierType }
    instanceIdentifierObj[payload.instanceData.identifierType] = payload.instanceData
    state.instanceIdentifiersList.push(instanceIdentifierObj)
    state.identifiersCounter++

    // Empty the current identifiers object
    state.currentIdentifiersInfo = {}
  },
  // Function to remove the instance Identifiers information from the Array
  deleteInstnaceIdentifiersInformation (state, payload) {
    // Check if the flag is transformation Output EPC if so delete from output list else from instance list
    if (payload.flagType === 'TransformationEventOutputEPC') {
      state.outputInstanceIdentifiersList.splice(state.outputInstanceIdentifiersList.findIndex(obj => obj.ID === payload.instanceID), 1)
    } else {
      state.instanceIdentifiersList.splice(state.instanceIdentifiersList.findIndex(obj => obj.ID === payload.instanceID), 1)
    }
  },
  saveClassIdentifiersInformation (state, payload) {
    // If there are Class Identifiers already then remove them (added to handle the modification of class identifiers)
    if (state.classIdentifiersList.length > 0) {
      state.classIdentifiersList = []
    }

    // Function to add the Class identifiers information into the array
    const classIdentifierObj = { ID: state.identifiersCounter, name: 'Quantity : ' + state.identifiersCounter, type: payload.classData.identifierType }
    classIdentifierObj[payload.classData.identifierType] = payload.classData
    state.classIdentifiersList.push(classIdentifierObj)
    state.identifiersCounter++

    // Empty the current identifiers object
    state.currentIdentifiersInfo = {}
  },
  // Function to remove the Class Identifiers information from the Array
  deleteClassIdentifiersInformation (state, payload) {
    // Check if the flag is transformation Output Quantity if so delete from output quantity list else from Quantity list
    if (payload.flagType === 'TransformationEventOutputQuantity') {
      state.outputclassIdentifiersList.splice(state.outputclassIdentifiersList.findIndex(obj => obj.ID === payload.classId), 1)
    } else {
      state.classIdentifiersList.splice(state.classIdentifiersList.findIndex(obj => obj.ID === payload.classId), 1)
    }
  },
  populateIdentifiersFlagType (state, flagType) {
    // Populate the type to indicate which event type information are being added currently
    state.identifiersFlagType = flagType
  },
  saveoutputInstanceIdentifiersInformation (state, payload) {
    // If there are Output Instance Identifiers already then remove them (added to handle the modification of Output Instance identifiers)
    if (state.outputInstanceIdentifiersList.length > 0) {
      state.outputInstanceIdentifiersList = []
    }

    // Function to add the information related to Output Instance Identifiers
    const instanceIdentifierObj = { ID: state.identifiersCounter, name: 'Output EPC : ' + state.identifiersCounter, type: payload.instanceData.identifierType }
    instanceIdentifierObj[payload.instanceData.identifierType] = payload.instanceData
    state.outputInstanceIdentifiersList.push(instanceIdentifierObj)
    state.identifiersCounter++

    // Empty the current identifiers object
    state.currentIdentifiersInfo = {}
  },
  deleteOutputInstanceIdentifiersInformation (state, payload) {
    // Function to remove the Output Instance Identifiers information from the Array
    state.outputInstanceIdentifiersList.splice(state.outputInstanceIdentifiersList.findIndex(obj => obj.ID === payload.instanceID), 1)
  },
  saveoutputclassIdentifiersInformation (state, payload) {
    // If there are Output Class Identifiers already then remove them (added to handle the modification of Output Class identifiers)
    if (state.outputclassIdentifiersList.length > 0) {
      state.outputclassIdentifiersList = []
    }

    // Function to add the information related to Output Classs Identifiers
    const classIdentifierObj = { ID: state.identifiersCounter, name: 'Output Quantity : ' + state.identifiersCounter, type: payload.classData.identifierType }
    classIdentifierObj[payload.classData.identifierType] = payload.classData
    state.outputclassIdentifiersList.push(classIdentifierObj)
    state.identifiersCounter++

    // Empty the current identifiers object
    state.currentIdentifiersInfo = {}
  },
  deleteOutputClassIdentifiersInformation (state, payload) {
    // Function to remove the Class Identifiers information from the Array
    state.outputclassIdentifiersList.splice(state.outputclassIdentifiersList.findIndex(obj => obj.ID === payload.classId), 1)
  },
  saveParentIdentifiersInformation (state, payload) {
    // If there are Output Class Identifiers already then remove them (added to handle the modification of Parent identifiers)
    if (state.parentIdentifiersList.length > 0) {
      state.parentIdentifiersList = []
    }

    // Function to save the information related to Parent Id
    const instanceIdentifierObj = { ID: state.identifiersCounter, name: 'Parent ID : ' + state.identifiersCounter, type: payload.instanceData.identifierType }
    instanceIdentifierObj[payload.instanceData.identifierType] = payload.instanceData
    state.parentIdentifiersList.push(instanceIdentifierObj)
    state.identifiersCounter++

    // Empty the current identifiers object
    state.currentIdentifiersInfo = {}
  },
  deleteParentIdentifiersInformation (state, payload) {
    // Function to reset the parentIdentifiers information
    state.parentIdentifiersList = []
  },
  generateInstanceIdentifiers (state, inputData) {
    // Function to generate Instance Identifiers based on the inputdata provided
    state.epc = inputData
  },
  modifyInstanceIdentifiersInformation (state, payload) {
    // Check if the flag is transformation Output EPC if so populate from output list else from instance list
    if (payload.flagType === 'TransformationEventOutputEPC') {
      state.currentIdentifiersInfo = state.outputInstanceIdentifiersList.find(idNode => idNode.ID === payload.instanceID)
    } else {
      state.currentIdentifiersInfo = state.instanceIdentifiersList.find(idNode => idNode.ID === payload.instanceID)
    }
  },
  modifyParentIdentifiersInformation (state, payload) {
    // On click of the modify button for Parent Identifiers show the modal with existing information
    state.currentIdentifiersInfo = state.parentIdentifiersList.find(idNode => idNode.ID === payload.parentId)
  },
  modifyClassIdentifiersInformation (state, payload) {
    // Check if the flag is TransformationEvent Output Quantities if so then populate from Output class list else from class list
    if (payload.flagType === 'TransformationEventOutputQuantity') {
      state.currentIdentifiersInfo = state.outputclassIdentifiersList.find(idNode => idNode.ID === payload.classId)
    } else {
      state.currentIdentifiersInfo = state.classIdentifiersList.find(idNode => idNode.ID === payload.classId)
    }
  },
  resetCurrentIdentifiersInfo (state) {
    // Reset the currentIdentifiersInfo on pressing escape or cancel button in modal
    state.currentIdentifiersInfo = {}
  },
  // During modification Nodeevent info populate the Identifiers store info with raw data of the Identifiers
  populateRawData (state, payload) {
    state.instanceIdentifiersList = payload.epc
    state.classIdentifiersList = payload.quantity
    state.outputInstanceIdentifiersList = payload.outputEpc
    state.outputclassIdentifiersList = payload.outputQuantity
    state.parentIdentifiersList = payload.parentID

    // Update the counter values for addition of the next values do not conflict with existing values
    state.instanceIdentifiersCount = state.instanceIdentifiersList.length > 0 ? state.instanceIdentifiersList.at(-1).ID + 1 : 0
    state.classIdentifiersCount = state.classIdentifiersList.length > 0 ? state.classIdentifiersList.at(-1).ID + 1 : 0
    state.outputInstanceIdentifiersCount = state.outputInstanceIdentifiersList.length > 0 ? state.outputInstanceIdentifiersList.at(-1).ID + 1 : 0
    state.outputClassIdentifiersCount = state.outputclassIdentifiersList.length > 0 ? state.outputclassIdentifiersList.at(-1).ID + 1 : 0
  }
}
