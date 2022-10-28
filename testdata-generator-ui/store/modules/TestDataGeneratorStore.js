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
import beautifier from 'js-beautify'

export const state = () => ({
  testDataInput: '',
  testDataOutput: ''
})

export const mutations = {
  populateTestDataInput (state, inputTemplate) {
    state.testDataInput = beautifier.js_beautify(inputTemplate, { indent_size: 4 })
  },
  populateTestDataOutput (state, generatedTestData) {
    state.testDataOutput = beautifier.js_beautify(generatedTestData, { indent_size: 4 })
  }
}

export const actions = {
  testdataGenerator ({ commit, state, dispatch }) {
    // If user has provided template then validate and generate the Test Data events
    if (state.testDataInput !== '' && state.testDataInput !== '{}' && Object.keys(state.testDataInput).length !== 0) {
      // Check if the input Events template is an valida JSON
      try {
        JSON.parse(state.testDataInput)
      } catch (e) {
        const message = 'Invalid entries in Input Template JSON, Please check and provide Testdata input template in valid JSON Format. Error: ' + e
        commit('populateTestDataOutput', message)
        return
      }

      // Make the call to service to generate the test data
      const headers = { 'Content-Type': 'application/json' }

      this.$axios.post('/generateTestData', { ...JSON.parse(state.testDataInput) }, { headers })
        .then((response) => {
          commit('populateTestDataOutput', response.data)
        })
        .catch((error) => {
          console.log(JSON.stringify(error, null, 4))
          if (error.response !== undefined && error.response.data !== undefined && error.response.data.type !== undefined && error.response.data.detail !== undefined) {
            commit('populateTestDataOutput', error.response.data.type + ', ' + error.response.data.title + ':\n' + error.response.data.detail.replace(/(?![^\n]{1,75}$)([^\n]{1,75})\s/g, '$1\n'))
          } else if (error.response !== undefined) {
            commit('populateTestDataOutput', 'Exception occured during the events generation, Error : ' + JSON.stringify(error.response, null, 4))
          } else {
            commit('populateTestDataOutput', 'Unable to generate events something went wrong, Please save/export the InputTemplate and/or Design data for further validations')
          }
        })
    }
  }
}

// error.response.data.detail.replace(/(?![^\n]{1,68}$)([^\n]{1,68})\s/g, '$1\n')
