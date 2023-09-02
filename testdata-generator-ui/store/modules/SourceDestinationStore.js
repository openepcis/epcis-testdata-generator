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
const getDefaultState = () => {
  return {
    sourceDestinationType: '',
    sourceCount: 0,
    sources: [],
    source: {},
    destinationCount: 0,
    destinations: [],
    destination: {},
    currentSourceDestinationInfo: null,
    currentSourceDestinationId: null,
    vocabularySyntax: 'URN'
  }
}

export const state = () => getDefaultState()

export const mutations = {
  // Set the vocabulary syntax
  setVocabularySyntax (state, vocabularySyntax) {
    state.vocabularySyntax = vocabularySyntax
  },

  // Function to reset all SourceDestination data
  resetSourceDestinationData (state) {
    // Reset all state variables to its default value for every node event during the design
    Object.assign(state, getDefaultState())
  },

  sourceDestinationTypePopulator (state, sourceDestinationType) {
    // On click of the add Source/Destination button populate the sourceDestinationType
    state.sourceDestinationType = sourceDestinationType
  },

  sourceDestinationAddition (state, sourceDestination) {
    // Function to add the source/destination information on submit of the modal
    // If value is source add to source
    if (state.sourceDestinationType === 'source') {
      // On submit of the modal if user is adding new Source element
      if (
        state.currentSourceDestinationId == null &&
        state.currentSourceDestinationInfo == null
      ) {
        sourceDestination.ID = state.sourceCount
        state.sources.push(sourceDestination)
        state.sourceCount++
      } else {
        // If user is trying to modify existing Source element information then replace particular source element
        const replacementSourceInfo = state.sources.find(
          obj => obj.ID === state.currentSourceDestinationId
        )
        Object.assign(replacementSourceInfo, sourceDestination)
        state.currentSourceDestinationInfo = null
        state.currentSourceDestinationId = null
      }
    } else if (state.sourceDestinationType === 'destination') {
      // On submit of the modal if user is adding new Destination element
      if (
        state.currentSourceDestinationId == null &&
        state.currentSourceDestinationInfo == null
      ) {
        sourceDestination.ID = state.destinationCount
        state.destinations.push(sourceDestination)
        state.destinationCount++
      } else {
        // If user is trying to modify existing Destination element information then replace particular Destination element
        const replacementDestinationInfo = state.destinations.find(
          obj => obj.ID === state.currentSourceDestinationId
        )
        Object.assign(replacementDestinationInfo, sourceDestination)
        state.currentSourceDestinationInfo = null
        state.currentSourceDestinationId = null
      }
    }
  },

  modifySourceDestination (state, sourceDestinationID) {
    // Based on user selection populate the respective Source/Destination information to display the modal with current information
    state.currentSourceDestinationId = sourceDestinationID

    if (state.sourceDestinationType === 'source') {
      state.currentSourceDestinationInfo = state.sources.find(
        obj => obj.ID === sourceDestinationID
      )
    } else if (state.sourceDestinationType === 'destination') {
      state.currentSourceDestinationInfo = state.destinations.find(
        obj => obj.ID === sourceDestinationID
      )
    }
  },

  deleteSourceDestination (state, sourceDestinationID) {
    // Based on user selection delete the respective source/destination ID
    if (state.sourceDestinationType === 'source') {
      const idx = state.sources.findIndex(
        obj => obj.ID === sourceDestinationID
      )
      if (idx !== -1) {
        state.sources.splice(idx, 1)
      }
    } else if (state.sourceDestinationType === 'destination') {
      const idx = state.destinations.findIndex(
        obj => obj.ID === sourceDestinationID
      )
      if (idx !== -1) {
        state.destinations.splice(idx, 1)
      }
    }
  },

  // During modification Nodeevent info populate the SourceDestination store info with raw data
  populateRawData (state, payload) {
    state.sources =
      payload.sources !== undefined && Array.isArray(payload.sources)
        ? payload.sources
        : []
    state.destinations =
      payload.destinations !== undefined && Array.isArray(payload.destinations)
        ? payload.destinations
        : []

    // Update the counter values for addition of the next values do not conflict with existing values
    state.sourceCount =
      state.sources.length > 0 && Array.isArray(payload.sources)
        ? state.sources.at(-1).ID + 1
        : 0
    state.destinationCount =
      state.destinations.length > 0 && Array.isArray(payload.destinations)
        ? state.destinations.at(-1).ID + 1
        : 0
  },

  resetCurrentSourceDestination (state) {
    // Reset the current SourceDestination on pressing escape or cancel button in modal
    state.currentSourceDestinationInfo = null
    state.currentSourceDestinationId = null
  }
}
