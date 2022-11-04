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
    userExtensionCount: 0,
    extensionType: '',
    extensionModal: false,
    userExtensions: [],
    extension: {},
    ilmdCount: 0,
    ilmd: [],
    parentIlmd: {},
    errorExtensions: [],
    errorParentExtension: {},
    errorExtensionCount: 0
  }
}

export const state = () => getDefaultState()

export const mutations = {
  // Function to reset all extensions data
  resetExtensionsData (state) {
    // Reset all state variables to its default value for every node event during the design
    Object.assign(state, getDefaultState())
  },
  extensionTypePopulator (state, extensionType) {
    // On click of the add extension button populate the extension type
    state.extensionType = extensionType
  },
  toggleExtensionModal (state) {
    // Function to show/hide the extension Modal
    state.extensionModal = !state.extensionModal
  },
  extensionsAddition (state, extension) {
    // Vales relevent to User Extension add to userExtensions List
    if (state.extensionType === 'userExtension') {
      extension.ID = state.userExtensionCount
      if (!state.parentExtension) {
        state.userExtensions.push(extension)
      } else {
        if (state.parentExtension.complex === null) {
          state.parentExtension.complex = []
        }
        state.parentExtension.complex.push(extension)
      }
      state.userExtensionCount++
    } else if (state.extensionType === 'ilmd') {
      // Values relevant to ILMD add to ILMD list
      extension.ID = state.ilmdCount
      if (!state.parentIlmd) {
        state.ilmd.push(extension)
      } else {
        if (state.parentIlmd.complex === null) {
          state.parentIlmd.complex = []
        }
        state.parentIlmd.complex.push(extension)
      }
      state.ilmdCount++
    } else if (state.extensionType === 'ErrorExtension') {
      extension.ID = state.errorExtensionCount
      if (!state.errorParentExtension) {
        state.errorExtensions.push(extension)
      } else {
        if (state.errorParentExtension.complex === null) {
          state.errorParentExtension.complex = []
        }
        state.errorParentExtension.complex.push(extension)
      }
      state.errorExtensionCount++
    }
  },
  setParentExtension (state, extension) {
    // Set the value for the parent User Extension for complex structure
    if (state.extensionType === 'userExtension') {
      state.parentExtension = extension
    } else if (state.extensionType === 'ilmd') {
      state.parentIlmd = extension
    } else if (state.extensionType === 'ErrorExtension') {
      state.errorParentExtension = extension
    }
  },
  extensionText (state, payload) {
    if (state.extensionType === 'userExtension') {
      // Based on change in input text field change the respective value
      if (!state.parentExtension) {
        const idx = state.userExtensions.findIndex(
          obj => obj.ID === payload.extensionID
        )
        state.userExtensions[idx].text = payload.text
      } else {
        const idx = state.parentExtension.complex.findIndex(
          obj => obj.ID === payload.extensionID
        )
        state.parentExtension.complex[idx].text = payload.text
      }
    } else if (state.extensionType === 'ilmd') {
      // Values relevant to ILMD add to ILMD list
      if (!state.parentIlmd) {
        const idx = state.ilmd.findIndex(obj => obj.ID === payload.extensionID)
        state.ilmd[idx].text = payload.text
      } else {
        const idx = state.parentIlmd.complex.findIndex(
          obj => obj.ID === payload.extensionID
        )
        state.parentIlmd.complex[idx].text = payload.text
      }
    } else if (state.extensionType === 'ErrorExtension') {
      // Values relevant to Error Extensions add to Error Extension list
      if (!state.errorParentExtension) {
        const idx = state.errorExtensions.findIndex(
          obj => obj.ID === payload.extensionID
        )
        state.errorExtensions[idx].text = payload.text
      } else {
        const idx = state.errorParentExtension.complex.findIndex(
          obj => obj.ID === payload.extensionID
        )
        state.errorParentExtension.complex[idx].text = payload.text
      }
    }
  },
  deleteExtension (state, extensionID) {
    // Based on user selection delete the respective extension ID
    if (state.extensionType === 'userExtension') {
      if (!state.parentExtension) {
        const idx = state.userExtensions.findIndex(
          obj => obj.ID === extensionID
        )
        if (idx !== -1) {
          state.userExtensions.splice(idx, 1)
        }
      } else {
        const idx = state.parentExtension.complex.findIndex(
          obj => obj.ID === extensionID
        )
        if (idx !== -1) {
          state.parentExtension.complex.splice(idx, 1)
        }
      }
    }
    if (state.extensionType === 'ilmd') {
      if (!state.parentIlmd) {
        const idx = state.ilmd.findIndex(obj => obj.ID === extensionID)
        if (idx !== -1) {
          state.ilmd.splice(idx, 1)
        }
      } else {
        const idx = state.parentIlmd.complex.findIndex(
          obj => obj.ID === extensionID
        )
        if (idx !== -1) {
          state.parentIlmd.complex.splice(idx, 1)
        }
      }
    }
    if (state.extensionType === 'ErrorExtension') {
      if (!state.errorParentExtension) {
        const idx = state.errorExtensions.findIndex(
          obj => obj.ID === extensionID
        )
        if (idx !== -1) {
          state.errorExtensions.splice(idx, 1)
        }
      } else {
        const idx = state.errorParentExtension.complex.findIndex(
          obj => obj.ID === extensionID
        )
        if (idx !== -1) {
          state.errorParentExtension.complex.splice(idx, 1)
        }
      }
    }
  },
  // During modification Nodeevent info populate the Extensions store info with raw data of the Identifiers
  populateRawData (state, payload) {
    state.userExtensions = payload.userExtensions !== undefined ? payload.userExtensions : []
    state.ilmd = payload.ilmd !== undefined ? payload.ilmd : []
    state.errorExtensions = payload.errorExtensions !== undefined ? payload.errorExtensions : []

    // Update the counter values for addition of the next values do not conflict with existing values
    state.userExtensionCount = state.userExtensions.length > 0 ? state.userExtensions.at(-1).ID + 1 : 0
    state.ilmdCount = state.ilmd.length > 0 ? state.ilmd.at(-1).ID + 1 : 0
    state.errorExtensionCount = state.errorExtensions.length > 0 ? state.errorExtensions.at(-1).ID + 1 : 0
  }
}

export const actions = {}
