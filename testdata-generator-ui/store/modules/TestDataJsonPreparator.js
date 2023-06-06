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
  jsonPreparation (state, { rootState, payload }) {
    const offset = new Date().getTimezoneOffset()
    const utcOffset =
      (offset < 0 ? '+' : '-') +
      ('00' + Math.floor(Math.abs(offset) / 60)).slice(-2) +
      ':' +
      ('00' + (Math.abs(offset) % 60)).slice(-2)

    const events = []
    let eventsObj = {
      nodeId: 1,
      eventType: rootState.modules.DesignTestDataStore.eventType
    }

    const eventFormData = {
      eventCount: parseInt(rootState.modules.DesignTestDataStore.eventCount),
      locationPartyIdentifierSyntax: payload.formData.vocabularySyntax,
      eventType: rootState.modules.DesignTestDataStore.eventType,
      ordinaryEvent: payload.formData.ordinaryEvent,
      action: payload.formData.action,
      eventID: payload.formData.eventID
    }

    // Based on the eventId set the type of eventId and Hash Algorithm
    if (payload.formData.eventID) {
      eventFormData.eventIdType =
        payload.formData.eventIdType !== undefined
          ? payload.formData.eventIdType
          : 'UUID'

      // For Hash Algorithm set the hashAlgorithmType
      if (
        payload.formData.eventIdType !== undefined &&
        payload.formData.eventIdType === 'HashId'
      ) {
        eventFormData.hashAlgorithm =
          payload.formData.hashAlgorithm !== undefined
            ? payload.formData.hashAlgorithm
            : 'sha-256'
      }
    }

    // Set the Event time
    // eventTime.specificTime = new Date(payload.formData.eventTime.specificTime).getSeconds() !== 0 ? payload.formData.eventTime.specificTime + utcOffset : payload.formData.eventTime.specificTime + ':00' + utcOffset
    const eventTime = {}
    eventTime.timeZoneOffset = payload.formData.eventTime.timeZoneOffset
    if (payload.formData.eventTimeSelector === 'SpecificTime') {
      eventTime.specificTime =
        payload.formData.eventTime.specificTime + utcOffset
    } else {
      eventTime.fromTime = payload.formData.eventTime.fromTime + utcOffset
      eventTime.toTime = payload.formData.eventTime.toTime + utcOffset
    }
    eventFormData.eventTime = eventTime

    // Set the record time if provided
    if (payload.formData.RecordTimeOption === 'yes') {
      eventFormData.recordTimeType = payload.formData.recordTimeType
    }

    // Check if Business Step is null if not then add to the Testdata input JSON
    if (payload.formData.businessStep != null) {
      if (payload.formData.businessStep !== 'BUSINESSSTEPENTER') {
        eventFormData.businessStep = payload.formData.businessStep
      } else {
        eventFormData.businessStep = payload.formData.EnterBusinessStepText
      }
    }

    // Check if Disposition is null if not then add to the Testdata input JSON
    if (payload.formData.disposition != null) {
      if (payload.formData.disposition !== 'DISPOSITIONENTER') {
        eventFormData.disposition = payload.formData.disposition
      } else {
        eventFormData.disposition = payload.formData.EnterDispositionText
      }
    }

    // Check if the readPoint value has been provided if so add to TestData input
    if (
      payload.formData.readpointselector != null &&
      payload.formData.readpointselector !== 'null'
    ) {
      if (payload.formData.readpointselector === 'SGLN') {
        eventFormData.readPoint = payload.formData.readPoint
      } else {
        const readPointObj = {}
        readPointObj.manualURI = payload.formData.readPoint.manualURI
        eventFormData.readPoint = readPointObj
      }
    }

    // Check if bizLocation value has been provided if so add it to Test data input
    if (
      payload.formData.businesslocationselector != null &&
      payload.formData.businesslocationselector !== 'null'
    ) {
      if (payload.formData.businesslocationselector === 'SGLN') {
        eventFormData.bizLocation = payload.formData.bizLocation
      } else {
        const bizLocationObj = {}
        bizLocationObj.manualURI = payload.formData.bizLocation.manualURI
        eventFormData.bizLocation = bizLocationObj
      }
    }

    // Check if error event if so add the error declaration information
    if (!payload.formData.ordinaryEvent) {
      const errorDeclaration = {}
      const declarationTime = {}

      // Set the error declaration time
      if (
        payload.formData.error.ErrorDeclarationTimeSelector === 'SpecificTime'
      ) {
        declarationTime.specificTime =
          payload.formData.error.ErrorDeclarationTime + utcOffset
      } else {
        declarationTime.fromTime =
          payload.formData.error.ErrorDeclarationTimeFrom + utcOffset
        declarationTime.toTime =
          payload.formData.error.ErrorDeclarationTimeTo + utcOffset
      }
      declarationTime.timeZoneOffset = payload.formData.error.ErrorTimeZone
      errorDeclaration.declarationTime = declarationTime

      // Set the declarationReason
      if (payload.formData.ErrorReasonType !== 'Other') {
        if (payload.formData.error.ErrorReasonType !== null) {
          errorDeclaration.declarationReason =
            payload.formData.error.ErrorReasonType
        }
      } else {
        errorDeclaration.declarationReason =
          payload.formData.error.ErrorReasonOther
      }

      // Add the Error corrective ids
      if (payload.formData.errorCorrectiveIdsList.length > 0) {
        const correctiveIds = []
        for (const correctiveID of payload.formData.errorCorrectiveIdsList) {
          correctiveIds.push(correctiveID.correctiveId)
        }
        errorDeclaration.correctiveIds = correctiveIds
      }

      // Add the Error extensions if user has provided them
      if (rootState.modules.ExtensionDataStore.errorExtensions.length > 0) {
        errorDeclaration.extensions =
          rootState.modules.ExtensionDataStore.errorExtensions
      }

      eventFormData.errorDeclaration = errorDeclaration
    }

    // Check if Persistent Disposition values have been provided if so add it to the Test data input
    if (payload.formData.persistentDispositionList.length > 0) {
      const persistentDisposition = {}
      const set = []
      const unset = []

      for (const pd of payload.formData.persistentDispositionList) {
        if (pd.type === 'set') {
          set.push(pd.value)
        } else if (pd.type === 'unset') {
          unset.push(pd.value)
        }
      }

      persistentDisposition.set = set
      persistentDisposition.unset = unset
      eventFormData.persistentDisposition = persistentDisposition
    }

    // Check if BizTransactions values have been provided if so add it to the Test data input
    if (payload.formData.businessTransactionList.length > 0) {
      eventFormData.bizTransactions = payload.formData.businessTransactionList
    }

    // Check if the Sources values have been provided if so add it to the Test Data Input
    if (
      rootState.modules.SourceDestinationStore.sources != null &&
      rootState.modules.SourceDestinationStore.sources.length > 0
    ) {
      eventFormData.sources = rootState.modules.SourceDestinationStore.sources
    }

    // Check if the Destinations values have been provided if so add it to the Test Data input
    if (
      rootState.modules.SourceDestinationStore.destinations != null &&
      rootState.modules.SourceDestinationStore.destinations.length > 0
    ) {
      eventFormData.destinations =
        rootState.modules.SourceDestinationStore.destinations
    }

    // Check if Sensor Informaiton have been provided if so add it to the Test data input
    if (
      rootState.modules.SensorElementsStore.sensorElementList !== undefined &&
      rootState.modules.SensorElementsStore.sensorElementList.length > 0
    ) {
      const sensorElementList = []

      for (const sensorItem of rootState.modules.SensorElementsStore
        .sensorElementList) {
        const sensorReportArray = []
        const sensorMetaData = JSON.parse(
          JSON.stringify(sensorItem.sensorMetadata)
        )

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

        if (sensorItem.sensorReport !== undefined) {
          // Get all the sensor report elements
          const sensorReportOld = JSON.parse(
            JSON.stringify(sensorItem.sensorReport)
          )

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

        const sensorObj = { ID: sensorItem.ID }

        // Check if sensorMetadata has any values if so add it to sensorObj else skip
        if (Object.keys(sensorMetaData).length > 0) {
          sensorObj.sensorMetadata = sensorMetaData
        }

        // check if sensorReport has any values if so add it to sensorObj else skip
        if (sensorReportArray.length > 0) {
          sensorObj.sensorReport = sensorReportArray
        }

        sensorElementList.push(sensorObj)
      }
      eventFormData.sensorElementList = sensorElementList
    }

    // If certificate info has been added then add it to the JSON
    if (
      payload.formData.certificationInfo !== undefined &&
      payload.formData.certificationInfo !== null
    ) {
      eventFormData.certificationInfo = payload.formData.certificationInfo
    }

    // If user extensions has been added then add it to the jSON
    if (rootState.modules.ExtensionDataStore.userExtensions.length > 0) {
      eventFormData.userExtensions =
        rootState.modules.ExtensionDataStore.userExtensions
    }

    // If event is Object/Transformation then add the ILMD if provided
    if (
      rootState.modules.DesignTestDataStore.eventType === 'ObjectEvent' ||
      rootState.modules.DesignTestDataStore.eventType === 'TransformationEvent'
    ) {
      if (rootState.modules.ExtensionDataStore.ilmd.length > 0) {
        eventFormData.ilmd = rootState.modules.ExtensionDataStore.ilmd
      }
    }

    // If event is TransformationEvent then add the TransformationID to it
    if (
      rootState.modules.DesignTestDataStore.eventType === 'TransformationEvent'
    ) {
      eventFormData.transformationID = payload.formData.transformationXformId
    }

    // Add the reference identifiers information if the Instance/Class identifiers informations are provided
    const referencedIdentifier = []
    const instanceIdentifiersList = JSON.parse(
      JSON.stringify(rootState.modules.IdentifiersStore.instanceIdentifiersList)
    )
    const classIdentifiersList = JSON.parse(
      JSON.stringify(rootState.modules.IdentifiersStore.classIdentifiersList)
    )

    if (instanceIdentifiersList.length > 0) {
      // Loop though the identifiersList and add the information to the referencedIdentifiers
      for (const instanceEpc of instanceIdentifiersList) {
        const epc = {
          identifierId: instanceEpc.ID,
          epcCount: 0,
          classCount: 0
        }
        referencedIdentifier.push(epc)
      }
    } else if (classIdentifiersList.length > 0) {
      // Loop though the class Identifiers list and add the information to the referencedIdentifiers
      for (const classIdentifiers of classIdentifiersList) {
        const quantity = {
          identifierId: classIdentifiers.ID,
          epcCount: 0,
          classCount: 0
        }
        referencedIdentifier.push(quantity)
      }
    }

    eventFormData.referencedIdentifier = referencedIdentifier

    // Add the class identifiers
    const parentReferencedIdentifier = {}
    const parentIdentifiersList = JSON.parse(
      JSON.stringify(rootState.modules.IdentifiersStore.parentIdentifiersList)
    )

    if (parentIdentifiersList.length > 0) {
      // Loop though the identifiersList and add the information to the referencedIdentifiers
      parentReferencedIdentifier.identifierId = parentIdentifiersList[0].ID
      parentReferencedIdentifier.parentCount = 0
    }
    eventFormData.parentReferencedIdentifier = parentReferencedIdentifier

    // Add the class identifiers
    const outputReferencedIdentifier = []
    const outputInstanceIdentifiersList = JSON.parse(
      JSON.stringify(
        rootState.modules.IdentifiersStore.outputInstanceIdentifiersList
      )
    )
    const OutputlassIdentifiersList = JSON.parse(
      JSON.stringify(
        rootState.modules.IdentifiersStore.outputclassIdentifiersList
      )
    )

    if (outputInstanceIdentifiersList.length > 0) {
      // Loop though the identifiersList and add the information to the referencedIdentifiers
      for (const outputInstanceEpc of outputInstanceIdentifiersList) {
        const epc = {
          identifierId: outputInstanceEpc.ID,
          epcCount: 0,
          classCount: 0
        }
        outputReferencedIdentifier.push(epc)
      }
    } else if (OutputlassIdentifiersList.length > 0) {
      // Loop though the class Identifiers list and add the information to the referencedIdentifiers
      for (const outputClassIdentifier of OutputlassIdentifiersList) {
        const quantity = {
          identifierId: outputClassIdentifier.ID,
          epcCount: 0,
          classCount: 0
        }
        outputReferencedIdentifier.push(quantity)
      }
    }

    eventFormData.outputReferencedIdentifier = outputReferencedIdentifier

    eventsObj = { ...eventsObj, ...eventFormData }
    events.push(eventsObj)
    state.eventsInputData = events
  },

  // Format the identifiers data
  identifiersData (state, { rootState, payload }) {
    const inputIdentifiers = []
    const outputIdentifiers = []
    const instanceIdentifiersList = JSON.parse(
      JSON.stringify(rootState.modules.IdentifiersStore.instanceIdentifiersList)
    )
    const classIdentifiersList = JSON.parse(
      JSON.stringify(rootState.modules.IdentifiersStore.classIdentifiersList)
    )
    const parentIdentifiersList = JSON.parse(
      JSON.stringify(rootState.modules.IdentifiersStore.parentIdentifiersList)
    )
    const outputInstanceIdentifiersList = JSON.parse(
      JSON.stringify(
        rootState.modules.IdentifiersStore.outputInstanceIdentifiersList
      )
    )
    const outputclassIdentifiersList = JSON.parse(
      JSON.stringify(
        rootState.modules.IdentifiersStore.outputclassIdentifiersList
      )
    )

    // Check if Instance EPC identifiers have the data if so add the values to the identifiers list
    if (instanceIdentifiersList.length > 0) {
      // Loop through all the identifiers within the instanceIdentifiersList and add to identifiers list
      for (const instanceEPC in instanceIdentifiersList) {
        const identifiersObj = {
          identifierId: instanceIdentifiersList[instanceEPC].ID,
          objectIdentifierSyntax:
            rootState.modules.IdentifiersStore.identifierSyntax,
          instanceData: {},
          classData: null,
          parentData: null
        }
        identifiersObj.instanceData[instanceIdentifiersList[instanceEPC].type] =
          instanceIdentifiersList[instanceEPC][
            Object.keys(instanceIdentifiersList[instanceEPC])[3]
          ]
        inputIdentifiers.push(identifiersObj)
      }
    }

    // Check if Class identifiers have the data if so add the values to the identifiers list
    if (classIdentifiersList.length > 0) {
      // Loop through all the identifiers within the classIdentifiersList and add to identifiers list
      for (const quantityClass in classIdentifiersList) {
        // If the identifiers list already has the associated value added from above then append the class data to the same
        if (inputIdentifiers[quantityClass] !== undefined) {
          inputIdentifiers[quantityClass].classData = {}
          inputIdentifiers[quantityClass].classData[
            classIdentifiersList[quantityClass].type
          ] =
            classIdentifiersList[quantityClass][
              Object.keys(classIdentifiersList[quantityClass])[3]
            ]
        } else {
          // If identifiers list does not contain the associated value then create a new object and add the new one
          const identifiersObj = {
            identifierId: classIdentifiersList[quantityClass].ID,
            objectIdentifierSyntax:
              rootState.modules.IdentifiersStore.identifierSyntax,
            instanceData: null,
            classData: {},
            parentData: null
          }
          identifiersObj.classData[classIdentifiersList[quantityClass].type] =
            classIdentifiersList[quantityClass][
              Object.keys(classIdentifiersList[quantityClass])[3]
            ]
          inputIdentifiers.push(identifiersObj)
        }
      }
    }

    // Check if Parent identifiers have the data if so add the values to the identifiers list
    if (parentIdentifiersList.length > 0) {
      // Loop through all the identifiers within the parentIdentifiersList and add to identifiers list
      for (const parentClass in parentIdentifiersList) {
        // If identifiers list does not contain the associated value then create a new object and add the new one
        const identifiersObj = {
          identifierId: parentIdentifiersList[parentClass].ID,
          objectIdentifierSyntax:
            rootState.modules.IdentifiersStore.identifierSyntax,
          instanceData: null,
          classData: null,
          parentData: {}
        }
        identifiersObj.parentData[parentIdentifiersList[parentClass].type] =
          parentIdentifiersList[parentClass][
            Object.keys(parentIdentifiersList[parentClass])[3]
          ]
        inputIdentifiers.push(identifiersObj)
      }
    }

    // Check if Output EPC identifiers have the data if so add the values to the identifiers list
    if (outputInstanceIdentifiersList.length > 0) {
      // Loop through all the identifiers within the outputInstanceIdentifiersList and add to identifiers list
      for (const outputEPC in outputInstanceIdentifiersList) {
        const identifiersObj = {
          identifierId: outputInstanceIdentifiersList[outputEPC].ID,
          objectIdentifierSyntax:
            rootState.modules.IdentifiersStore.identifierSyntax,
          instanceData: {},
          classData: null,
          parentData: null
        }
        identifiersObj.instanceData[
          outputInstanceIdentifiersList[outputEPC].type
        ] =
          outputInstanceIdentifiersList[outputEPC][
            Object.keys(outputInstanceIdentifiersList[outputEPC])[3]
          ]
        outputIdentifiers.push(identifiersObj)
      }
    }

    // Check if outputclassIdentifiersList have the data if so add the values to the identifiers list
    if (outputclassIdentifiersList.length > 0) {
      // Loop through all the identifiers within the outputclassIdentifiersList and add to identifiers list
      for (const outputClass in outputclassIdentifiersList) {
        // If the identifiers list already has the associated value added from above then append the class data to the same
        if (outputIdentifiers[outputClass] !== undefined) {
          outputIdentifiers[outputClass].classData = {}
          outputIdentifiers[outputClass].classData[
            outputclassIdentifiersList[outputClass].type
          ] =
            outputclassIdentifiersList[outputClass][
              Object.keys(outputclassIdentifiersList[outputClass])[3]
            ]
        } else {
          // If identifiers list does not contain the associated value then create a new object and add the new one
          const identifiersObj = {
            identifierId: outputclassIdentifiersList[outputClass].ID,
            objectIdentifierSyntax:
              rootState.modules.IdentifiersStore.identifierSyntax,
            instanceData: null,
            classData: {},
            parentData: null
          }
          identifiersObj.classData[
            outputclassIdentifiersList[outputClass].type
          ] =
            outputclassIdentifiersList[outputClass][
              Object.keys(outputclassIdentifiersList[outputClass])[3]
            ]
          outputIdentifiers.push(identifiersObj)
        }
      }
    }

    const identifiersList = inputIdentifiers.concat(outputIdentifiers)

    // Get the event information provided by the user and modify the values for
    const eventsInfoList = JSON.parse(JSON.stringify(state.eventsInputData))
    const referencedIdentifiers = eventsInfoList[0].referencedIdentifier

    // Check if the event info has the referenced instance data identifiers information if so then add the instance data info from identifiers array
    if (
      referencedIdentifiers !== undefined &&
      Object.keys(referencedIdentifiers).length !== 0 &&
      identifiersList.length > 0
    ) {
      // Find the matching instance identifier based on reference identifier identifierId and identifiersList identifierId and get the instance data
      for (const refId of referencedIdentifiers) {
        const identifierInfo = identifiersList.find(
          idObj => idObj.identifierId === refId.identifierId
        )

        // If the values are present then based on the type of identifiers assign the EPCCount, ClassCount, ParentCount etc. to the Instance Data
        if (
          identifierInfo !== undefined &&
          identifierInfo.instanceData != null &&
          identifierInfo.instanceData !== undefined &&
          Object.keys(identifierInfo.instanceData).length !== 0
        ) {
          if (
            identifierInfo.instanceData[
              Object.keys(identifierInfo.instanceData)
            ].serialType === 'range'
          ) {
            refId.epcCount = parseInt(
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].count
            )
          } else if (
            identifierInfo.instanceData[
              Object.keys(identifierInfo.instanceData)[0]
            ].serialType === 'random'
          ) {
            refId.epcCount = parseInt(
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].randomCount
            )
          } else if (
            identifierInfo.instanceData[
              Object.keys(identifierInfo.instanceData)[0]
            ].serialType === 'none'
          ) {
            refId.epcCount = parseInt(1)
          } else if (
            Object.keys(identifierInfo.instanceData)[0] === 'manualURI'
          ) {
            if (
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].manualUriType === 'dynamic'
            ) {
              refId.epcCount = parseInt(
                identifierInfo.instanceData[
                  Object.keys(identifierInfo.instanceData)[0]
                ].manualUriRangeTo -
                  identifierInfo.instanceData[
                    Object.keys(identifierInfo.instanceData)[0]
                  ].manualUriRangeFrom
              )
            } else if (
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].manualUriType === 'static'
            ) {
              refId.epcCount = parseInt(1)
            }
          }
        }

        // If the values are present then based on the type of identifiers assign the EpcCount, ClassCount, ParentCount etc. to Class Data
        if (
          identifierInfo !== undefined &&
          identifierInfo.classData != null &&
          identifierInfo.classData !== undefined &&
          Object.keys(identifierInfo.classData).length !== 0
        ) {
          if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)]
              .serialType === 'range'
          ) {
            refId.classCount = parseInt(
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .count
            )
          } else if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
              .serialType === 'random'
          ) {
            refId.classCount = parseInt(
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .randomCount
            )
          } else if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
              .serialType === 'none'
          ) {
            refId.classCount = parseInt(1)
          } else if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
              .classIdentifiersCount !== undefined
          ) {
            refId.classCount = parseInt(
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .classIdentifiersCount
            )
          } else if (Object.keys(identifierInfo.classData)[0] === 'manualURI') {
            if (
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .manualUriType === 'dynamic'
            ) {
              refId.classCount = parseInt(
                identifierInfo.classData[
                  Object.keys(identifierInfo.classData)[0]
                ].manualUriRangeTo -
                  identifierInfo.classData[
                    Object.keys(identifierInfo.classData)[0]
                  ].manualUriRangeFrom
              )
            } else if (
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .manualUriType === 'static'
            ) {
              refId.classCount = parseInt(1)
            }
          }
        }
      }
    }

    const parentReferencedIdentifier =
      eventsInfoList[0].parentReferencedIdentifier
    // Check if the event info has the referenced parent data identifiers information if so then add the parent data info from identifiers array to the parentReferencedIdentifier
    if (
      parentReferencedIdentifier != null &&
      parentReferencedIdentifier !== undefined &&
      Object.keys(parentReferencedIdentifier).length !== 0
    ) {
      // Find the matching instance identifier based on reference identifier identifierId and identifiersList identifierId and get the instance data
      const identifierInfo = identifiersList.find(
        idObj =>
          idObj.identifierId === parentReferencedIdentifier.identifierId
      )

      // If the parentData within eventInfo has values then add the parent count to parentReferencedIdentifier
      if (
        identifierInfo !== undefined &&
        identifierInfo.parentData !== undefined &&
        Object.keys(identifierInfo.parentData).length !== 0
      ) {
        parentReferencedIdentifier.parentCount = parseInt(
          eventsInfoList[0].eventCount
        )
      }
    }

    const outputReferencedIdentifier =
      eventsInfoList[0].outputReferencedIdentifier
    // Check if the eventInfo has the outputReferencedIdentifier if so then add the output data info from identifiers array to the outputReferencedIdentifier
    if (
      outputReferencedIdentifier !== undefined &&
      Object.keys(outputReferencedIdentifier).length !== 0 &&
      identifiersList.length > 0
    ) {
      // Find the matching instance identifier based on reference identifier identifierId and identifiersList identifierId and get the instance data
      for (const refId of outputReferencedIdentifier) {
        const identifierInfo = identifiersList.find(
          idObj => idObj.identifierId === refId.identifierId
        )

        // If the values are present then based on the type of identifiers assign the EPCCount, ClassCount, ParentCount etc. to the Instance Data
        if (
          identifierInfo !== undefined &&
          identifierInfo.instanceData != null &&
          identifierInfo.instanceData !== undefined &&
          Object.keys(identifierInfo.instanceData).length !== 0
        ) {
          if (
            identifierInfo.instanceData[
              Object.keys(identifierInfo.instanceData)
            ].serialType === 'range'
          ) {
            refId.epcCount = parseInt(
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].count
            )
          } else if (
            identifierInfo.instanceData[
              Object.keys(identifierInfo.instanceData)[0]
            ].serialType === 'random'
          ) {
            refId.epcCount = parseInt(
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].randomCount
            )
          } else if (
            identifierInfo.instanceData[
              Object.keys(identifierInfo.instanceData)[0]
            ].serialType === 'none'
          ) {
            refId.epcCount = parseInt(1)
          } else if (
            Object.keys(identifierInfo.instanceData)[0] === 'manualURI'
          ) {
            if (
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].manualUriType === 'dynamic'
            ) {
              refId.epcCount = parseInt(
                identifierInfo.instanceData[
                  Object.keys(identifierInfo.instanceData)[0]
                ].manualUriRangeTo -
                  identifierInfo.instanceData[
                    Object.keys(identifierInfo.instanceData)[0]
                  ].manualUriRangeFrom
              )
            } else if (
              identifierInfo.instanceData[
                Object.keys(identifierInfo.instanceData)[0]
              ].manualUriType === 'static'
            ) {
              refId.epcCount = parseInt(1)
            }
          }
        }

        // If the values are present then based on the type of identifiers assign the EpcCount, ClassCount, ParentCount etc. to Class Data
        if (
          identifierInfo !== undefined &&
          identifierInfo.classData != null &&
          identifierInfo.classData !== undefined &&
          Object.keys(identifierInfo.classData).length !== 0
        ) {
          if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)]
              .serialType === 'range'
          ) {
            refId.classCount = parseInt(
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .count
            )
          } else if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
              .serialType === 'random'
          ) {
            refId.classCount = parseInt(
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .randomCount
            )
          } else if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
              .serialType === 'none'
          ) {
            refId.classCount = parseInt(1)
          } else if (
            identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
              .classIdentifiersCount !== undefined
          ) {
            refId.classCount = parseInt(
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .classIdentifiersCount
            )
          } else if (Object.keys(identifierInfo.classData)[0] === 'manualURI') {
            if (
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .manualUriType === 'dynamic'
            ) {
              refId.classCount = parseInt(
                identifierInfo.classData[
                  Object.keys(identifierInfo.classData)[0]
                ].manualUriRangeTo -
                  identifierInfo.classData[
                    Object.keys(identifierInfo.classData)[0]
                  ].manualUriRangeFrom
              )
            } else if (
              identifierInfo.classData[Object.keys(identifierInfo.classData)[0]]
                .manualUriType === 'static'
            ) {
              refId.classCount = parseInt(1)
            }
          }
        }
      }
    }

    state.eventsInputData = eventsInfoList
    state.identifiersInputData = identifiersList
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
    commit('jsonPreparation', { rootState, payload })

    // Prepare the JSON for identifiers data
    commit('identifiersData', { rootState, payload })

    // Prepare the JSON test data input template
    commit('inputTemplatePreparation')
  }
}
