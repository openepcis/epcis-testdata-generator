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
    diagram: {},
    allEventsInfoArray: []
  }
}

export const state = () => getDefaultState()

export const mutations = {
  // Assign the value of diagram drawflow
  populateDiagramInfo (state, diagram) {
    state.diagram = diagram.drawflow.Home.data
  },

  // Clear the final array to build fresh relations
  clearAllEventsArray (state) {
    state.allEventsInfoArray = []
  }
}

export const actions = {
  buildRelations ({ context, commit, rootState, state, dispatch }, payload) {
    // Empty the allEventsInfoArray for each new operation
    const allEventsInfoArray = []

    // Get all the information relevant to Event Node
    const eventNodeInfo = JSON.parse(JSON.stringify(rootState.modules.ConfigureNodeEventInfoStore.nodeEventInfoArray))

    // Get all the information relevant to Connector modal
    const connectorInfo = JSON.parse(JSON.stringify(rootState.modules.ConnectorStore.connectorArray))

    // Get all the information relevant to the Identifiers Node
    const identifiersNodeInfo = JSON.parse(JSON.stringify(rootState.modules.ConfigureIdentifiersInfoStore.identifiersArray))

    // Loop through all Nodes within the Diagram and if Node is Events then add it to the final array
    for (const eventNode in state.diagram) {
      // Check if the node is of EventNode if so add it to the allEventsInfoArray
      if (state.diagram[eventNode].name === 'Events') {
        const eventObj = { eventId: state.diagram[eventNode].id, eventType: state.diagram[eventNode].data.eventType, formData: {} }

        // Find the corresponding information provided by the user to the Event node using the Vuex Store eventNodeInfo
        // const eventFormData = eventNodeInfo.find(eventInfo => parseInt(eventInfo.eventId) === parseInt(state.diagram[eventNode].id))
        const eventFormData = eventNodeInfo.find(eventInfo => parseInt(eventInfo.eventId) === parseInt(state.diagram[eventNode].data.ID))

        // Check if user has provided the values by filling the form
        if (eventFormData !== undefined && eventFormData.eventInfo !== undefined) {
          // Add the information to the allEventsInfoArray
          eventObj.formData[eventFormData.eventInfo.eventType] = eventFormData.eventInfo
        }

        // Add all the relevant information info the final allEventsInfoArray
        allEventsInfoArray.push(eventObj)
      }
    }

    // Loop through all the Nodes within Diagram and if Node is Identifiers/ParentIdentifiers if so add the corresponding values to the Node
    for (const identifierNode in state.diagram) {
      // Check if the node is IdentifiersNode if so get the connections values associated with it
      if (state.diagram[identifierNode].name === 'Identifiers' || state.diagram[identifierNode].name === 'ParentIdentifiers') {
        // Get the connectorInfo associated with the IdentifiersNode
        const identifiersConnections = state.diagram[identifierNode].outputs.output_1.connections

        // Loop over connections and identify the
        for (const connect in identifiersConnections) {
          const targetConnectId = state.diagram[identifierNode].outputs.output_1.connections[connect].node
          const connectionInfo = connectorInfo.find(connect => parseInt(connect.source) === parseInt(state.diagram[identifierNode].id) && parseInt(connect.target) === parseInt(targetConnectId))

          // Get the eventNode information from allEventsInfoArray based on the targetConnectId
          const eventFormDataInfo = allEventsInfoArray.find(node => parseInt(node.eventId) === parseInt(targetConnectId))

          if (eventFormDataInfo !== undefined) {
            // For Identifiers node add the information into referencedIdentifier of the target node if its eventNode
            if (state.diagram[identifierNode].name === 'Identifiers' && Object.keys(eventFormDataInfo.formData)[0] !== undefined) {
              const identifierObj = { identifierId: state.diagram[identifierNode].id, epcCount: connectionInfo.epcCount, classCount: connectionInfo.classCount, quantity: connectionInfo.quantity }

              // If the eventType is TransformationEvent and if the identifiers node is connected to input_1 then treat it as input EPC/Quantity
              if ((Object.keys(eventFormDataInfo.formData)[0] === 'TransformationEvent' && identifiersConnections[connect].output === 'input_1') || Object.keys(eventFormDataInfo.formData)[0] !== 'TransformationEvent') {
                // If the eventType is TransformationEvent and if the identifiers node is connected to input_2 then treat it as input EPC/Quantity
                eventFormDataInfo.formData[Object.keys(eventFormDataInfo.formData)[0]].referencedIdentifier.push(identifierObj)
              } else if (Object.keys(eventFormDataInfo.formData)[0] === 'TransformationEvent' && identifiersConnections[connect].output === 'input_2') {
                // If the eventType is TransformationEvent and if the identifiers node is connected to input_2 then treat it as input EPC/Quantity
                eventFormDataInfo.formData[Object.keys(eventFormDataInfo.formData)[0]].outputReferencedIdentifier.push(identifierObj)
              }
            }

            // If the node is ParentIdentifiers then add it as parentData
            if (state.diagram[identifierNode].name === 'ParentIdentifiers') {
              let parentEventCount = 0
              let nodeCount = 0

              // Find all the source event nodes connected to the eventsnode to find the parent count
              const inputConnections = state.diagram[targetConnectId].inputs

              // Loop through inputConnections and check if the node is eventNode if so then get the eventCount in the respective event
              for (const parentCon in inputConnections) {
                const connections = inputConnections[parentCon].connections

                // Loop through all the connections to get the eventNode associated with both input connections
                for (const con in connections) {
                  // Get all the information for the particular event
                  const parentEventNode = allEventsInfoArray.find(node => parseInt(node.eventId) === parseInt(connections[con].node))

                  if (parentEventNode !== undefined && parentEventNode.formData !== undefined) {
                    parentEventCount = parentEventNode.formData[Object.keys(parentEventNode.formData)[0]] !== undefined ? parseInt(parentEventCount) + parseInt(parentEventNode.formData[Object.keys(parentEventNode.formData)[0]].eventCount) : 0
                    nodeCount++
                  }
                }
              }

              // For ParentIdentifiers node add the information into parentReferencedIdentifier of the target node eventInfo
              // Add the parentReferencedIdentifier only for Aggregation/Transaction/AssociationEvent
              const eventData = eventFormDataInfo.formData[Object.keys(eventFormDataInfo.formData)[0]]
              nodeCount = parseInt(nodeCount) !== 0 ? parseInt(nodeCount) : 1
              parentEventCount = parseInt(parentEventCount) !== 0 ? parseInt(parentEventCount) : 1

              if (eventData !== undefined && (eventData.eventType === 'AggregationEvent' || eventData.eventType === 'TransactionEvent' || eventData.eventType === 'AssociationEvent')) {
                const parentObj = { identifierId: state.diagram[identifierNode].id, parentCount: parseInt(parseInt(eventData.eventCount) * parseInt(parentEventCount) / nodeCount) }
                eventData.parentReferencedIdentifier = parentObj
              }
            }
          }
        }
      }
    }

    // Loop through all Nodes within the Diagram and build the relationship between the EventNodes
    for (const eventNode in state.diagram) {
      // Check if the node is of EventNode if so find the target node associated with it
      if (state.diagram[eventNode].name === 'Events') {
        // Get the parentNode information
        const parentNodeInfo = allEventsInfoArray.find(node => parseInt(node.eventId) === parseInt(state.diagram[eventNode].id))

        // Get all output connections associated with the Events node to build the Parent-Child relationship
        const outputEventNodeConnections = state.diagram[eventNode].outputs.output_1.connections

        // Loop through the outputEventNodeConnections to find all children nodes associated with eventNode and check if its EventNode as well
        for (const connection in outputEventNodeConnections) {
          // Check if the targetNode associated with eventNode is of EventNode
          const childNodeInfo = allEventsInfoArray.find(node => parseInt(node.eventId) === parseInt(outputEventNodeConnections[connection].node))

          if (childNodeInfo !== undefined) {
            // Add the parent node identifiers information into the child node
            const connectionInfo = connectorInfo.find(connect => parseInt(connect.source) === parseInt(state.diagram[eventNode].id) && parseInt(connect.target) === parseInt(childNodeInfo.eventId))

            let parentReferenceIdentifiers = []

            // Based on type of event get the parent reference identifiers
            if (parentNodeInfo.formData !== undefined) {
              if (parentNodeInfo.eventType === 'TransformationEvent' && Object.keys(parentNodeInfo.formData)[0] !== undefined) {
                // For TransformationEvent obtain the identifiers from outputReferencedIdentifier
                parentReferenceIdentifiers = parentNodeInfo.formData[Object.keys(parentNodeInfo.formData)[0]].outputReferencedIdentifier
              } else if (Object.keys(parentNodeInfo.formData)[0] !== undefined) {
                // For all other eventType obtain the identifiers from referencedIdentifier
                parentReferenceIdentifiers = parentNodeInfo.formData[Object.keys(parentNodeInfo.formData)[0]].referencedIdentifier
              }

              // If the EventNode is directly connected to another EventNode then add the ParentEventNode to respective events reference identifiers
              parentReferenceIdentifiers = parentReferenceIdentifiers.map(function (identifier) {
                const refIdObj = { parentNodeId: parentNodeInfo.eventId, epcCount: connectionInfo.epcCount, classCount: connectionInfo.classCount, quantity: connectionInfo.quantity }
                return refIdObj
              })

              // If there are duplicate ParentNodeId then remove them
              parentReferenceIdentifiers = parentReferenceIdentifiers.filter((tag, index, array) => array.findIndex(t => t.parentNodeId === tag.parentNodeId) === index)
            }

            if (Object.keys(childNodeInfo.formData)[0] !== undefined) {
              // Check if the parent node if is already present if not then add it
              childNodeInfo.formData[Object.keys(childNodeInfo.formData)[0]].referencedIdentifier = childNodeInfo.formData[Object.keys(childNodeInfo.formData)[0]].referencedIdentifier.concat(parentReferenceIdentifiers)
            }
          }
        }
      }
    }

    dispatch('modules/ConfigureNodeEvents/jsonPreparation', { eventsData: allEventsInfoArray, identifiersData: identifiersNodeInfo }, { root: true })
  }
}
