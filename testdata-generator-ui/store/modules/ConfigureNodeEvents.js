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
export const state = () => ({
  testDataInputTemplate: {},
  eventsInputData: {},
  identifiersInputData: {}
})

export const mutations = {
  jsonPreparation (state, payload) {
    const offset = new Date().getTimezoneOffset()
    const utcOffset = (offset < 0 ? '+' : '-') + ('00' + Math.floor(Math.abs(offset) / 60)).slice(-2) + ':' + ('00' + (Math.abs(offset) % 60)).slice(-2)

    const events = []

    for (const parentNode in payload.eventsData) {
      const parentNodeInfo = payload.eventsData[parentNode]
      let parentObj = { nodeId: parentNodeInfo.eventId, eventType: parentNodeInfo.eventType }

      // Add all the children and parent event node info data to the parent
      parentObj.parent = parentNodeInfo.parent
      parentObj.children = parentNodeInfo.children

      // Extract all other information and format them and then add it to the
      const parentEventData = parentNodeInfo.formData[Object.keys(parentNodeInfo.formData)[0]]

      if (parentEventData !== undefined) {
        const parentFormData = {
          eventCount: parseInt(parentEventData.eventCount),
          locationPartyIdentifierSyntax: parentEventData.vocabularySyntax,
          ordinaryEvent: parentEventData.ordinaryEvent,
          action: parentEventData.action,
          eventID: parentEventData.eventID,
          eventType: parentEventData.eventType
        }

        // Based on the eventId set the type of eventId and Hash Algorithm
        if (parentEventData.eventID) {
          parentFormData.eventIdType = parentEventData.eventIdType !== undefined ? parentEventData.eventIdType : 'UUID'

          // For Hash Algorithm set the hashAlgorithmType
          if (parentEventData.eventIdType !== undefined && parentEventData.eventIdType === 'HashId') {
            parentFormData.hashAlgorithm = parentEventData.hashAlgorithm !== undefined ? parentEventData.hashAlgorithm : 'sha-256'
          }
        }

        // Set the Event time
        const eventTime = {}
        eventTime.timeZoneOffset = parentEventData.eventTime.timeZoneOffset
        if (parentEventData.eventTimeSelector === 'SpecificTime') {
          // If the seconds is not provided then add along with offset
          eventTime.specificTime = parentEventData.eventTime.specificTime + utcOffset
        } else {
          eventTime.fromTime = parentEventData.eventTime.fromTime + utcOffset
          eventTime.toTime = parentEventData.eventTime.toTime + utcOffset
        }
        parentFormData.eventTime = eventTime

        // Set the record time if provided
        if (parentEventData.RecordTimeOption === 'yes') {
          parentFormData.recordTimeType = parentEventData.recordTimeType
        }

        // Check if Business Step is null if not then add to the Testdata input JSON
        if (parentEventData.businessStep != null) {
          if (parentEventData.businessStep !== 'BUSINESSSTEPENTER') {
            parentFormData.businessStep = parentEventData.businessStep
          } else {
            parentFormData.businessStep = parentEventData.EnterBusinessStepText
          }
        }

        // Check if Disposition is null if not then add to the Testdata input JSON
        if (parentEventData.disposition != null) {
          if (parentEventData.disposition !== 'DISPOSITIONENTER') {
            parentFormData.disposition = parentEventData.disposition
          } else {
            parentFormData.disposition = parentEventData.EnterDispositionText
          }
        }

        // Check if the readPoint value has been provided if so add to TestData input
        if (parentEventData.readpointselector != null && parentEventData.readpointselector !== 'null') {
          if (parentEventData.readpointselector === 'SGLN') {
            parentFormData.readPoint = parentEventData.readPoint
          } else {
            const readPointObj = {}
            readPointObj.manualURI = parentEventData.readPoint.manualURI
            parentFormData.readPoint = readPointObj
          }
        }

        // Check if bizLocation value has been provided if so add it to Test data input
        if (parentEventData.businesslocationselector != null && parentEventData.businesslocationselector !== 'null') {
          if (parentEventData.businesslocationselector === 'SGLN') {
            parentFormData.bizLocation = parentEventData.bizLocation
          } else {
            const bizLocationObj = {}
            bizLocationObj.manualURI = parentEventData.bizLocation.manualURI
            parentFormData.bizLocation = bizLocationObj
          }
        }

        // Check if error event if so add the error declaration information
        if (!parentEventData.ordinaryEvent) {
          const errorDeclaration = {}
          const declarationTime = {}

          // Set the error declaration time
          if (parentEventData.error.ErrorDeclarationTimeSelector === 'SpecificTime') {
            declarationTime.specificTime = parentEventData.error.ErrorDeclarationTime + utcOffset
          } else {
            declarationTime.fromTime = parentEventData.error.ErrorDeclarationTimeFrom + utcOffset
            declarationTime.toTime = parentEventData.error.ErrorDeclarationTimeTo + utcOffset
          }
          declarationTime.timeZoneOffset = parentEventData.error.ErrorTimeZone
          errorDeclaration.declarationTime = declarationTime

          // Set the declarationReason
          if (parentEventData.error.ErrorReasonType !== 'Other') {
            if (parentEventData.error.ErrorReasonType !== null) {
              errorDeclaration.declarationReason = parentEventData.error.ErrorReasonType
            }
          } else {
            errorDeclaration.declarationReason = parentEventData.error.ErrorReasonOther
          }

          // Add the Error corrective ids
          if (parentEventData.error.errorCorrectiveIdsList !== undefined && parentEventData.error.errorCorrectiveIdsList.length > 0) {
            const correctiveIds = []
            for (const correctiveID of parentEventData.error.errorCorrectiveIdsList) {
              correctiveIds.push(correctiveID.value)
            }
            errorDeclaration.correctiveIds = correctiveIds
          }

          // Add the Error extensions if user has provided them
          if (parentEventData.error.errorExtensions !== undefined && parentEventData.error.errorExtensions.length > 0) {
            errorDeclaration.extensions = parentEventData.error.errorExtensions
          }

          parentFormData.errorDeclaration = errorDeclaration
        }

        // Check if Persistent Disposition values have been provided if so add it to the Test data input
        if (parentEventData.persistentDispositionList.length > 0) {
          const persistentDisposition = {}
          const set = []
          const unset = []

          for (const pd of parentEventData.persistentDispositionList) {
            if (pd.type === 'set') {
              set.push(pd.value)
            } else if (pd.type === 'unset') {
              unset.push(pd.value)
            }
          }

          persistentDisposition.set = set
          persistentDisposition.unset = unset
          parentFormData.persistentDisposition = persistentDisposition
        }

        // Check if BizTransactions values have been provided if so add it to the Test data input
        if (parentEventData.businessTransactionList.length > 0) {
          parentFormData.bizTransactions = parentEventData.businessTransactionList
        }

        // Check if the Sources values have been provided if so add it to the Test Data Input
        if (parentEventData.sources.type != null) {
          const sources = []
          const sourceObj = {}
          sourceObj.type = parentEventData.sources.type

          if (parentEventData.sources.type === 'OWNING_PARTY' || parentEventData.sources.type === 'PROCESSING_PARTY') {
            sourceObj.glnType = parentEventData.sources.glnType
          }

          if (parentEventData.sources.type === 'OWNING_PARTY' || parentEventData.sources.type === 'PROCESSING_PARTY' || parentEventData.sources.type === 'LOCATION') {
            sourceObj.gln = parentEventData.sources.gln
            sourceObj.extension = parentEventData.sources.extension
            sourceObj.gcpLength = parentEventData.sources.gcpLength
          } else {
            sourceObj.manualType = parentEventData.sources.OtherSourceURI1
            sourceObj.manualURI = parentEventData.sources.OtherSourceURI2
          }

          sources.push(sourceObj)
          parentFormData.sources = sources
        }

        // Check if the Destinations values have been provided if so add it to the Test Data input
        if (parentEventData.destinations.type != null) {
          const destinations = []
          const destinationObj = {}
          destinationObj.type = parentEventData.destinations.type

          if (parentEventData.destinations.type === 'OWNING_PARTY' || parentEventData.destinations.type === 'PROCESSING_PARTY') {
            destinationObj.glnType = parentEventData.destinations.glnType
          }

          if (parentEventData.destinations.type === 'OWNING_PARTY' || parentEventData.destinations.type === 'PROCESSING_PARTY' || parentEventData.destinations.type === 'LOCATION') {
            destinationObj.gln = parentEventData.destinations.gln
            destinationObj.extension = parentEventData.destinations.extension
            destinationObj.gcpLength = parentEventData.destinations.gcpLength
          } else {
            destinationObj.manualType = parentEventData.destinations.OtherDestinationURI1
            destinationObj.manualURI = parentEventData.destinations.OtherDestinationURI2
          }

          destinations.push(destinationObj)
          parentFormData.destinations = destinations
        }

        // Check if Sensor Informaiton have been provided if so add it to the Test data input
        if (parentEventData.sensorElementList !== undefined && parentEventData.sensorElementList.length > 0) {
          const sensorElementList = []

          for (const sensorItem of parentEventData.sensorElementList) {
            const sensorReportArray = []
            const sensorObj = { ID: sensorItem.ID }

            if (sensorItem.sensorMetadata !== undefined) {
              const sensorMetaData = JSON.parse(JSON.stringify(sensorItem.sensorMetadata))

              // If the time value is present within the SensorMetaData then add the UTC Offset to it
              if (sensorMetaData.time !== undefined) {
                sensorMetaData.time = sensorMetaData.time + utcOffset
              }

              // If the startTime value is present within the SensorMetaData then add the UTC Offset to it
              if (sensorMetaData.startTime !== undefined) {
                sensorMetaData.startTime = sensorMetaData.startTime + utcOffset
              }

              // If the endTime value is present within the SensorMetaData then add the UTC Offset to it
              if (sensorMetaData.endTime !== undefined) {
                sensorMetaData.endTime = sensorMetaData.endTime + utcOffset
              }

              sensorObj.sensorMetadata = sensorMetaData
            }

            if (sensorItem.sensorReport !== undefined) {
              // Get all the sensor report elements
              const sensorReportOld = JSON.parse(JSON.stringify(sensorItem.sensorReport))

              // Loop through all sensor report and change the time
              for (const reportItem of sensorReportOld) {
                const sensorReport = JSON.parse(JSON.stringify(reportItem))
                // If the time value is present within the SensorMetaData then add the UTC Offset to it
                if (sensorReport.time !== undefined) {
                  sensorReport.time = sensorReport.time + utcOffset
                }

                sensorReportArray.push(sensorReport)
              }
            }

            sensorObj.sensorReport = sensorReportArray
            sensorElementList.push(sensorObj)
          }
          parentFormData.sensorElementList = sensorElementList
        }

        // Add the modified Instance/Class identifiers information into the ChildObject
        parentFormData.referencedIdentifier = parentEventData.referencedIdentifier
        parentFormData.outputReferencedIdentifier = parentEventData.outputReferencedIdentifier
        parentFormData.parentReferencedIdentifier = parentEventData.parentReferencedIdentifier

        // If user has imported existing event and it the identifiers are present in them then add it
        parentFormData.epcList = parentEventData.epcList
        parentFormData.quantityList = parentEventData.quantityList
        parentFormData.parentIdentifier = parentEventData.parentIdentifier
        parentFormData.outputEPCList = parentEventData.outputEPCList
        parentFormData.outputQuantityList = parentEventData.outputQuantityList

        // If user extensions has been added then add it to the jSON
        if (parentEventData.userExtensions.length > 0) {
          parentFormData.userExtensions = parentEventData.userExtensions
        }

        // If event is Object/Transformation then add the ILMD if provided
        if (parentEventData.eventType === 'ObjectEvent' || parentEventData.eventType === 'TransformationEvent') {
          if (parentEventData.ilmd !== undefined && parentEventData.ilmd.length > 0) {
            parentFormData.ilmd = parentEventData.ilmd
          }
        }

        parentObj = { ...parentObj, ...parentFormData }
        events.push(parentObj)
      }
    }

    state.eventsInputData = events
  },

  // Format the identifiers data
  identifiersData (state, payload) {
    const identifiers = []

    for (const identifierCount in payload.identifiersData) {
      const identifiersInfo = payload.identifiersData[identifierCount]
      const identifiersObj = { identifierId: identifiersInfo.identifiersId, objectIdentifierSyntax: identifiersInfo.identifierSyntax, instanceData: {}, classData: {}, parentData: {} }

      // Add the instanceData if provided
      if (identifiersInfo.instanceData !== undefined && identifiersInfo.instanceData !== {}) {
        identifiersObj.instanceData[identifiersInfo.instanceData.identifierType] = identifiersInfo.instanceData
      } else {
        identifiersObj.instanceData = null
      }

      // Add the classData if provided
      if (identifiersInfo.classData !== undefined && identifiersInfo.classData !== {}) {
        identifiersObj.classData[identifiersInfo.classData.identifierType] = identifiersInfo.classData
      } else {
        identifiersObj.classData = null
      }

      // Add the parentData if provided
      if (identifiersInfo.parentData !== undefined && identifiersInfo.parentData !== {}) {
        identifiersObj.parentData[identifiersInfo.parentData.identifierType] = identifiersInfo.parentData
      } else {
        identifiersObj.parentData = null
      }

      identifiers.push(identifiersObj)
    }

    state.identifiersInputData = identifiers
  },
  // Merge the Events Input Data and Identifiers Input Data into a single Test Data Input Template in JSON
  inputTemplatePreparation (state) {
    state.testDataInputTemplate.events = state.eventsInputData
    state.testDataInputTemplate.identifiers = state.identifiersInputData
  }
}

export const actions = {
  jsonPreparation ({ commit, rootState }, payload) {
    // Prepare the JSON for test input data
    commit('jsonPreparation', { eventsData: rootState.modules.RelationsBuilder.eventsData })

    // Prepare the JSON for identifiers data
    commit('identifiersData', { identifiersData: rootState.modules.RelationsBuilder.identifiersData })

    // Prepare the JSON test data input template
    commit('inputTemplatePreparation')
  }
}
