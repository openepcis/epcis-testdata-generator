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
    connectorModal: false,
    connectInfo: [],
    currentConnector: {},
    connectorArray: []
  }
}

export const state = () => getDefaultState()

export const mutations = {
  // Function to Show the Connector Modal
  showConnectorModal (state) {
    state.connectorModal = true
  },
  // Function to Hide the Connector Modal
  hideConnectorModal (state) {
    state.connectorModal = false
  },
  // Function to populate current connector
  populateCurrentConnector (state, connectObj) {
    state.currentConnector = connectObj
  },
  // Function to save the connector modal information
  saveConnectorInfo (state, connectObj) {
    state.connectorArray.push(connectObj)
  },
  // Function to populate the connector Array information with modal values
  populateConnectorInfo (state, connectorInfo) {
    const connect = state.connectorArray.find(
      con =>
        parseInt(con.source) === parseInt(state.currentConnector.output_id) &&
        parseInt(con.target) === parseInt(state.currentConnector.input_id)
    )
    connect.epcCount = parseInt(connectorInfo.epcCount) || 0
    connect.inheritParentCount =
      parseInt(connectorInfo.inheritParentCount) || 0
    connect.classCount = parseInt(connectorInfo.classCount) || 0
    connect.quantity = parseInt(connectorInfo.quantity) || 0
    connect.hideInheritParentCount =
      parseInt(connectorInfo.hideInheritParentCount) || 0
  },
  // Function to set the hideInheritParentCount based on the Source Node connected to Connector
  populateHideInheritParentCount (state, displayValue) {
    if (state.currentConnector !== undefined) {
      const connect = state.connectorArray.find(
        con =>
          parseInt(con.source) === parseInt(state.currentConnector.output_id) &&
          parseInt(con.target) === parseInt(state.currentConnector.input_id)
      )
      if (connect !== undefined) {
        connect.hideInheritParentCount = displayValue
      }
    }
  },
  // Function to remove the connector information from connector Array if the connector is deleted
  removeConnectorInfo (state, connectorInfo) {
    state.connectorArray.splice(
      state.connectorArray.findIndex(
        con =>
          parseInt(con.source) === parseInt(connectorInfo.output_id) &&
          parseInt(con.target) === parseInt(connectorInfo.input_id)
      ),
      1
    )
  },
  // Function to populate all the Connector information into the connectorArray during the import of the supply chain template
  populateConnectorArray (state, connectorArray) {
    state.connectorArray = connectorArray
  }
}
