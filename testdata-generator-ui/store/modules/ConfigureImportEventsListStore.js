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
    eventsListModal: false,
    eventsList: []
  }
}

export const state = () => getDefaultState()

export const mutations = {
  // Function to Show the Events Modal on click of ImportEvents button in DesignTestDataCanvas page
  showEventsListModal (state) {
    state.eventsListModal = true
  },
  // Function to Hide the Events Modal on click of Submit button in ConfigureEventsListModal.vue
  hideEventsListModal (state) {
    state.eventsListModal = false
  },
  // Function to extract the information from the eventsList and convert it to front-end values
  eventsExtractor (state, { dispatch, rootState, payload }) {
    // Assign the values to eventsList
    state.eventsList = JSON.parse(payload.eventsData.events)

    // EventsList to store the modified eventsList
    const generatedEventsList = []

    // Loop through the events and obtain the corresponding information
    for (let eventCount = 1; eventCount <= state.eventsList.length; eventCount++) {
      // Add the eventId and create an Object to store all the values
      const eventInfo = state.eventsList[eventCount - 1]

      // Create an Object to store each node/event information from list of events
      const nodeObj = { eventId: eventCount, eventType: eventInfo.type }

      // Create an eventInfo Object within which type of eventObject is created
      const eventObj = nodeObj.eventInfo = { }

      // Variable to store the context information present in the event
      let context = []

      // Variable to store the user extensions information present in the event
      eventObj.userExtensions = {}

      // Loop through each property in eventInfo and accordingly assign the values to respective field, if unknown values found then assign them to extension
      for (const eventProperty in eventInfo) {
        switch (eventProperty) {
          // Obtain the context information associated with event for ILMD/User Extension/Error Extension
          case '@context':
            context = eventInfo['@context']
            break
          // Obtain the type of event and error/normal event
          case 'type':
            // Set the eventType to the type of event in the eventInfo
            eventObj.eventType = eventInfo.type

            // Set the eventCount, identifier & vocabularySyntax to its default value
            eventObj.eventCount = 1
            eventObj.identifierSyntax = 'URN'
            eventObj.vocabularySyntax = 'URN'
            eventObj.importEvent = true

            // If the errorDeclaration is present in event then change the ordinaryEvent to false
            eventObj.ordinaryEvent = eventInfo.errorDeclaration === undefined

            // Set the default values for some of the fields if they are not present in the eventInfo

            // Default value for recordTime if not present in event
            if (eventInfo.recordTime === undefined) {
              eventObj.RecordTimeOption = 'no'
            }

            // Default value for readPoint if not present in event
            if (eventInfo.readPoint === undefined) {
              eventObj.readpointselector = null
              eventObj.readPoint = { gcpLength: null }
            }

            // Default value for bizLocation if not present in event
            if (eventInfo.bizLocation === undefined) {
              eventObj.businesslocationselector = null
              eventObj.bizLocation = { gcpLength: null }
            }

            // Default value for bizStep if not present in event
            if (eventInfo.bizStep === undefined) {
              eventObj.businessStep = null
            }

            // Default value for disposition if not present in event
            if (eventInfo.disposition === undefined) {
              eventObj.disposition = null
            }

            // Default value for sourceList if not present in event
            if (eventInfo.sourceList === undefined || eventInfo.sourceList.length === 0) {
              eventObj.sources = { type: null, glnType: 'SGLN', gcpLength: null }
            }

            // Default value for destinationList if not present in event
            if (eventInfo.destinationList === undefined || eventInfo.destinationList.length === 0) {
              eventObj.destinations = { type: null, glnType: 'SGLN', gcpLength: null }
            }

            // Default value for eventId if not present in event
            if (eventInfo.eventID === undefined) {
              eventObj.eventID = false
            }

            // Default value for action if not present in event
            if (eventInfo.action === undefined) {
              eventObj.action = 'ADD'
            }

            // If error information is not present then assign the default value for error fields
            if (eventInfo.errorDeclaration === undefined) {
              eventObj.error = { ErrorTimeZone: '+02:00', ErrorDeclarationTimeSelector: 'SpecificTime', errorCorrectiveIdsList: [], ErrorReasonType: null, errorCorrectiveCount: 0 }
            }

            // If Persistent Disposition is not present then add empty array
            if (eventInfo.persistentDisposition === undefined) {
              eventObj.persistentDispositionCount = 0
              eventObj.persistentDispositionList = []
            }

            // If bizTransactionList is not present then add empty array
            if (eventInfo.bizTransactionList === undefined) {
              eventObj.bizTransactionCount = 0
              eventObj.businessTransactionList = []
            }

            // If the ILMD information is not present then add an empty array
            if (eventInfo.ilmd === undefined) {
              eventObj.ilmd = []
            }

            // For ObjectEvent add empty array if identifiers do not exist
            if (eventInfo.type === 'ObjectEvent') {
              // if epcList is not present then add empty array
              if (eventInfo.epcList === undefined) {
                eventObj.epcList = []
              }

              // if quantityList is not present then add empty array
              if (eventInfo.quantityList === undefined) {
                eventObj.quantityList = []
              }

              // Default empty values for ParentID/OutputEPC/OutputQuantities
              eventObj.parentIdentifier = ''
              eventObj.outputEPCList = []
              eventObj.outputQuantityList = []
            } else if (eventInfo.type === 'AggregationEvent' || eventInfo.type === 'AssociationEvent') {
              // if parentID is not present then add empty array
              if (eventInfo.parentID === undefined) {
                eventObj.parentIdentifier = ''
              }

              // if childEPCs is not present then add empty array
              if (eventInfo.childEPCs === undefined) {
                eventObj.epcList = []
              }

              // if childQuantityList is not present then add empty array
              if (eventInfo.childQuantityList === undefined) {
                eventObj.quantityList = []
              }

              // Default empty values for OutputEPC/OutputQuantities
              eventObj.outputEPCList = []
              eventObj.outputQuantityList = []
            } else if (eventInfo.type === 'TransactionEvent') {
              // if parentID is not present then add empty array
              if (eventInfo.parentID === undefined) {
                eventObj.parentIdentifier = ''
              }

              // if epcList is not present then add empty array
              if (eventInfo.epcList === undefined) {
                eventObj.epcList = []
              }

              // if quantityList is not present then add empty array
              if (eventInfo.quantityList === undefined) {
                eventObj.quantityList = []
              }

              // Default empty values for OutputEPC/OutputQuantities
              eventObj.outputEPCList = []
              eventObj.outputQuantityList = []
            } else if (eventInfo.type === 'TransformationEvent') {
              // if inputEPCList is not present then add empty array
              if (eventInfo.inputEPCList === undefined) {
                eventObj.epcList = []
              }

              // if outputEPCList is not present then add empty array
              if (eventInfo.outputEPCList === undefined) {
                eventObj.outputEPCList = []
              }

              // if outputQuantityList is not present then add empty array
              if (eventInfo.outputEPCList === undefined) {
                eventObj.outputEPCList = []
              }

              // if outputQuantityList is not present then add empty array
              if (eventInfo.outputQuantityList === undefined) {
                eventObj.outputQuantityList = []
              }

              // Default empty values for parent ID
              eventObj.parentIdentifier = ''

              // if transformationID is not present then add empty array
              if (eventInfo.transformationID === undefined) {
                eventObj.transformationXformId = ''
              }
            }

            // Add the empty referenced identifiers object to match the other design schema
            eventObj.referencedIdentifier = []
            eventObj.outputReferencedIdentifier = []
            eventObj.parentReferencedIdentifier = {}

            break

          // Obtain the information associated with eventTime and eventTimeZoneOffset
          case 'eventTime':
          case 'eventTimeZoneOffset':
            if (eventObj.eventTime === undefined) {
              // Set the eventTime to Specific time for all the events
              eventObj.eventTimeSelector = 'SpecificTime'

              // Create an Object to store the eventTime information
              eventObj.eventTime = {}
              let eventTime = new Date(eventInfo.eventTime)
              eventTime.setMinutes(eventTime.getMinutes() - eventTime.getTimezoneOffset())
              eventTime.setSeconds(eventTime.getSeconds(), 0)
              eventTime = eventTime.toISOString().slice(0, -1)
              eventObj.eventTime.specificTime = eventTime
            } else if (eventObj.eventTime !== undefined) {
              // Add the timezone offset information to existing eventTime object of the event
              eventObj.eventTime.timeZoneOffset = eventInfo.eventTimeZoneOffset !== undefined ? eventInfo.eventTimeZoneOffset : '+02:00'
            }
            break

            // Set the recordTime Yes/No based on if the information is present within the eventInfo
          case 'recordTime':
            eventObj.RecordTimeOption = 'yes'
            eventObj.recordTimeType = 'SAME_AS_EVENT_TIME'
            break

          // Set the readPoint information if the ReadPoint information is present within the eventInfo
          case 'readPoint':
            eventObj.readpointselector = 'manually'
            eventObj.readPoint = { manualURI: eventInfo.readPoint.id }
            break

          // Set the bizLocation information if the BizLocation information is present in eventInfo
          case 'bizLocation':
            eventObj.businesslocationselector = 'manually'
            eventObj.bizLocation = { manualURI: eventInfo.bizLocation.id }
            break

          // If BizStep is present then add it
          case 'bizStep':
            eventObj.businessStep = eventInfo.bizStep.toUpperCase()
            break

          // If disposition is present then add it
          case 'disposition':
            eventObj.disposition = eventInfo.disposition.toUpperCase()
            break

          // If Source information is present then add it
          case 'sourceList':
            eventObj.sources = { type: 'OTHER', glnType: 'SGLN', gcpLength: null }
            eventObj.sources.OtherSourceURI1 = eventInfo.sourceList[0].type.split(':').pop()
            eventObj.sources.OtherSourceURI2 = eventInfo.sourceList[0].source.split(':').pop()
            break

          // If Destination information is present then add it
          case 'destinationList':
            eventObj.destinations = { type: 'OTHER', glnType: 'SGLN', gcpLength: null }
            eventObj.destinations.OtherDestinationURI1 = eventInfo.destinationList[0].type.split(':').pop()
            eventObj.destinations.OtherDestinationURI2 = eventInfo.destinationList[0].destination.split(':').pop()
            break

          // If EventID is present then make the eventID as true
          case 'eventID':
            eventObj.eventID = true
            break

          // If action value is present then add it
          case 'action':
            eventObj.action = eventInfo.action
            break

          // If the error information are present then add the associated Error Information
          case 'errorDeclaration':{
            eventObj.error = {}
            eventObj.error.errorExtensions = {}

            for (const errorProperty in eventInfo.errorDeclaration) {
              // Modify the eventTime to remove the timezone offset and format so as to display in input datetime-local
              if (errorProperty === 'declarationTime') {
                eventObj.error.ErrorTimeZone = '+02:00'
                eventObj.error.ErrorDeclarationTimeSelector = 'SpecificTime'

                let errorDeclarationTime = new Date(eventInfo.errorDeclaration.declarationTime)
                errorDeclarationTime.setMinutes(errorDeclarationTime.getMinutes() - errorDeclarationTime.getTimezoneOffset())
                errorDeclarationTime.setSeconds(errorDeclarationTime.getSeconds(), 0)
                errorDeclarationTime = errorDeclarationTime.toISOString().slice(0, -1)
                eventObj.error.ErrorDeclarationTime = errorDeclarationTime
              } else if (errorProperty === 'reason') {
                eventObj.error.ErrorReasonType = eventInfo.errorDeclaration.reason !== undefined ? eventInfo.errorDeclaration.reason.split(':').pop().toUpperCase() : null
              } else if (errorProperty === 'correctiveEventIDs') {
                // If correctiveEventIDs are present then add it to the event
                let correctiveEventIDsCount = 0
                const errorCorrectiveIdsList = []

                for (const correctiveID of eventInfo.errorDeclaration.correctiveEventIDs) {
                  const correctiveIdObj = { ID: correctiveEventIDsCount, value: correctiveID }
                  errorCorrectiveIdsList.push(correctiveIdObj)
                  correctiveEventIDsCount++
                }
                eventObj.error.errorCorrectiveIdsList = errorCorrectiveIdsList
              } else {
                // If none of the pre-defined properties match then assign the values to extension
                eventObj.error.errorExtensions[errorProperty] = eventInfo.errorDeclaration[errorProperty]
              }
            }

            // If the error extensions are present then format them according to the required format
            const errorExtensionCount = 0
            eventObj.error.errorExtensions = extensionsReader(context, eventObj.error.errorExtensions, 'ErrorExtension', errorExtensionCount)
          }
            break

          // If Persistent Disposition values are present then add it
          case 'persistentDisposition':{
            const persistentDispositionList = []
            let persistentDispositionCount = 0

            // Check for SET values and add it to the array
            if (eventInfo.persistentDisposition.set !== undefined && eventInfo.persistentDisposition.set.length > 0) {
              for (const setItem of eventInfo.persistentDisposition.set) {
                const pdObj = { ID: persistentDispositionCount, type: 'set', value: setItem.toUpperCase() }
                persistentDispositionList.push(pdObj)
                persistentDispositionCount++
              }
            }

            // Check for UNSET values and add it to the array
            if (eventInfo.persistentDisposition.unset !== undefined && eventInfo.persistentDisposition.unset.length > 0) {
              for (const unsetItem of eventInfo.persistentDisposition.unset) {
                const pdObj = { ID: persistentDispositionCount, type: 'unset', value: unsetItem.toUpperCase() }
                persistentDispositionList.push(pdObj)
                persistentDispositionCount++
              }
            }

            eventObj.persistentDispositionCount = persistentDispositionCount
            eventObj.persistentDispositionList = persistentDispositionList
          }
            break

          // If Business Transaction information is present then add it
          case 'bizTransactionList':{
            const bizTransactionList = []
            let bizTransactionCount = 0

            // Loop through the elements and add information
            for (const bizTransactionItem of eventInfo.bizTransactionList) {
              const bizTransactionObj = { ID: bizTransactionCount }
              bizTransactionObj.type = bizTransactionItem.type.split(':').pop()
              bizTransactionObj.bizTransaction = bizTransactionItem.bizTransaction
              bizTransactionList.push(bizTransactionObj)
              bizTransactionCount++
            }

            eventObj.bizTransactionCount = bizTransactionCount
            eventObj.businessTransactionList = bizTransactionList
          }
            break

          // If sensor information is present then add it
          case 'sensorElementList':{
            const sensorElementList = []
            let sensorItemCount = 1

            // Loop through all sensor elements and add the ID and change the datetime
            for (const sensorItem of eventInfo.sensorElementList) {
              sensorItem.ID = sensorItemCount
              sensorItemCount++

              if (sensorItem.sensorMetadata !== undefined) {
                // If the time value is present within the SensorMetaData then add the UTC Offset to it
                if (sensorItem.sensorMetadata.time !== undefined) {
                  let sensorTime = new Date(sensorItem.sensorMetadata.time)
                  sensorTime.setMinutes(sensorTime.getMinutes() - sensorTime.getTimezoneOffset())
                  sensorTime.setSeconds(sensorTime.getSeconds(), 0)
                  sensorTime = sensorTime.toISOString().slice(0, -1)
                  sensorItem.sensorMetadata.time = sensorTime
                }

                // If the startTime value is present within the SensorMetaData then add the UTC Offset to it
                if (sensorItem.sensorMetadata.startTime !== undefined) {
                  let sensorTime = new Date(sensorItem.sensorMetadata.startTime)
                  sensorTime.setMinutes(sensorTime.getMinutes() - sensorTime.getTimezoneOffset())
                  sensorTime.setSeconds(sensorTime.getSeconds(), 0)
                  sensorTime = sensorTime.toISOString().slice(0, -1)
                  sensorItem.sensorMetadata.startTime = sensorTime
                }

                // If the endTime value is present within the SensorMetaData then add the UTC Offset to it
                if (sensorItem.sensorMetadata.endTime !== undefined) {
                  let sensorTime = new Date(sensorItem.sensorMetadata.endTime)
                  sensorTime.setMinutes(sensorTime.getMinutes() - sensorTime.getTimezoneOffset())
                  sensorTime.setSeconds(sensorTime.getSeconds(), 0)
                  sensorTime = sensorTime.toISOString().slice(0, -1)
                  sensorItem.sensorMetadata.endTime = sensorTime
                }
              }

              if (sensorItem.sensorReport !== undefined && sensorItem.sensorReport.length > 0) {
                // Loop through report elements and add the ID and change the datetime format
                let sensorReportCount = 0
                const sensorReportList = []
                for (const sensorReportItem of sensorItem.sensorReport) {
                  // If the time value is present within the SensorReport then add the UTC Offset to it
                  if (sensorReportItem.time !== undefined) {
                    let sensorTime = new Date(sensorReportItem.time)
                    sensorTime.setMinutes(sensorTime.getMinutes() - sensorTime.getTimezoneOffset())
                    sensorTime.setSeconds(sensorTime.getSeconds(), 0)
                    sensorTime = sensorTime.toISOString().slice(0, -1)
                    sensorReportItem.time = sensorTime
                  }

                  sensorReportItem.ID = sensorReportCount
                  sensorReportCount++
                  sensorReportList.push(sensorReportItem)
                }
                // Assign the modified sensor report to the sensor item
                sensorItem.sensorReport = sensorReportList
              }
              sensorElementList.push(sensorItem)
            }

            eventObj.sensorElementList = sensorElementList
          }
            break

          case 'epcList':
            eventObj.epcList = eventInfo.epcList
            break

          case 'quantityList':
            eventObj.quantityList = eventInfo.quantityList
            break

          case 'ilmd':
            eventObj.ilmd = extensionsReader(context, eventInfo.ilmd, 'ilmd', 1)
            break

          case 'parentID':
            eventObj.parentIdentifier = eventInfo.parentID
            break

          case 'childEPCs':
            eventObj.epcList = eventInfo.childEPCs
            break

          case 'childQuantityList':
            eventObj.quantityList = eventInfo.childQuantityList
            break

          case 'inputEPCList':
            eventObj.epcList = eventInfo.inputEPCList
            break

          case 'inputQuantityList':
            eventObj.quantityList = eventInfo.inputQuantityList
            break

          case 'outputEPCList':
            eventObj.outputEPCList = eventInfo.outputEPCList
            break

          case 'outputQuantityList':
            eventObj.outputQuantityList = eventInfo.outputQuantityList
            break

          case 'transformationID':
            eventObj.transformationXformId = eventInfo.transformationID
            break

          default:
            eventObj.userExtensions[eventProperty] = eventInfo[eventProperty]
            break
        }
      }

      // After reading all user extension information modify them to required format by calling the formatter function
      eventObj.userExtensions = extensionsReader(context, eventObj.userExtensions, 'userExtension', 1)

      // After all modification store the modified event in the List
      generatedEventsList.push(nodeObj)
    }

    // After completing all the events information modification assign the modified values to nodeEventInfoArray in ConfigureNodeEventInfoStore
    const parameter = { eventInfoArray: generatedEventsList, finalNodeId: generatedEventsList.length }
    dispatch('modules/ConfigureNodeEventInfoStore/addNodeEventInfoArray', parameter, { root: true })
  }
}

export const actions = {
  eventsExtractor ({ dispatch, commit, rootState }, payload) {
    // Extract the data from the events list according to front-end values
    commit('eventsExtractor', { dispatch, rootState, payload })
  }
}

function extensionsReader (context, extensions, type, extensionCount) {
  const extensionsArray = []

  // Loop through extensions and divide them into complex and simple extensions
  for (const key in extensions) {
    // Find the Localname and Namespace URI from the context information
    const namespace = key.substring(0, key.lastIndexOf(':'))
    const localName = key.substring(key.lastIndexOf(':') + 1)

    // If the value of any element is Object then it is a complex type, so call the method recursively
    if (extensions[key] instanceof Object) {
      const extensionObj = { dataType: 'complex', namespace: contextNamespaceFinder(context, namespace), localName, text: '', complex: extensionsReader(context, extensions[key], type, extensionCount), ID: extensionCount }
      extensionCount++
      extensionsArray.push(extensionObj)
    } else {
      // If the value of any element is not an Object then it is a simple type so directly add the value
      const extensionObj = { dataType: 'string', namespace: contextNamespaceFinder(context, namespace), localName, text: extensions[key], complex: null, ID: extensionCount }
      extensionCount++
      extensionsArray.push(extensionObj)
    }
  }

  return extensionsArray
}

function contextNamespaceFinder (context, namespace) {
  let namespaceURI = namespace
  // If the event has context then only find the namespace URI
  if (context !== undefined && context.length > 0) {
    context.forEach((obj) => {
      Object.keys(obj).forEach((key) => {
        if (key === namespace) {
          namespaceURI = obj[key]
        }
      })
    })
  }
  return namespaceURI
}
