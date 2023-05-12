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

import axios from 'axios'

const getDefaultState = () => {
  return {
    eventCount: 1,
    eventType: null,
    commonDropdownInfos: {},
    showImportDesignModal: false,
    importedDesignData: ''
  }
}

export const state = () => getDefaultState()

export const mutations = {
  resetDesignData () {
    // Reset all state variables to its default value on change of the eventType
    Object.assign(state, getDefaultState())
  },
  eventCountPopulator (state, eventCount) {
    state.eventCount = eventCount
  },
  eventTypePopulator (state, event) {
    state.eventType = event
  },
  commonDropdownInfosPopulate (state, dropdownInfo) {
    state.commonDropdownInfos = dropdownInfo
  },
  // During import info populate the Design Test Data store info with raw data
  populateRawData (state, payload) {
    state.eventCount = payload.eventCount
  },
  // On click of the show import design modal show the modal to import design
  showImportDesignModal (state) {
    state.showImportDesignModal = true
  },
  // On click of submit button of the modal hide the modal to import the design
  hideImportDesignModal (state) {
    state.showImportDesignModal = false
  },
  // Populate the imported design data
  populateImportDesignData (state, urlData) {
    state.importedDesignData = urlData
  },
  // Clear the imported data
  clearImportDesignData (state) {
    state.importedDesignData = ''
  }
}

export const actions = {
  readStaticData ({ commit, state, dispatch }) {
    commit('commonDropdownInfosPopulate', require('~/static/EpcisData/CommonDropdown.js'))
  },

  // On submit of the Import URL design fetch the data from URL
  obtainURLData ({ commit }, inputURL) {
    axios.get(inputURL)
      .then((response) => {
        commit('populateImportDesignData', response.data)
      })
      .catch((error) => {
        const message = 'Unable to obtain data, request failed. \n' + error + '\n Please check the URL : ' + inputURL
        commit('populateImportDesignData', message)
      })
  }
}
