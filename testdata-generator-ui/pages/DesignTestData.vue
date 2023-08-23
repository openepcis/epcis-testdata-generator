<!--

    Copyright 2022 benelog GmbH & Co. KG

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.

-->
<template>
  <div id="designTestData">
    <div class="wrapper">
      <div class="col" style="margin-top: 2%">
        <div
          v-for="n in listNodes"
          :key="n.ID"
          draggable="true"
          :data-node="n.item"
          class="drag-drawflow"
          @dragstart="drag($event, n.name)"
        >
          <div
            class="node d-flex justify-content-center"
            :style="`background: ${n.color}`"
          >
            <span>{{ n.name }}</span>
          </div>
          <pre />
        </div>
        <div style="padding-left: 29%; padding-top: 5%">
          <button
            class="btn btn-success rounded-pill"
            @click="buildEventRelations($event)"
          >
            Submit
          </button>
        </div>
      </div>

      <div class="col-right">
        <div
          id="drawflow"
          ref="drawflow"
          class="gridImage"
          @drop="drop($event)"
          @dragover="allowDrop($event)"
        >
          <div
            class="drawflowButton btn-export rounded-pill"
            title="Export the design to local system"
            @click="exportDesignInfo()"
          >
            Export
          </div>
          <span>
            <input
              ref="readDrawflowInfo"
              type="file"
              hidden
              accept=".txt, .json"
              @change="importDesignInfo()"
            >
            <div
              class="drawflowButton btn-import rounded-pill"
              title="Import design template from local"
              @click="$refs.readDrawflowInfo.click()"
            >
              Import Template
            </div>
          </span>
          <div
            class="drawflowButton btn-importEvents rounded-pill"
            title="Import the list of events to design"
            @click="importEventsList($event)"
          >
            Import Events
          </div>
          <!-- <div class="drawflowButton btn-importInputTemplate rounded-pill" title="Import design template from remote URL" @click="importInputTemplate($event)">
            Import Template
          </div> -->
          <div
            class="drawflowButton btn-clear rounded-pill"
            title="Clear the design"
            @click="clearDesignInfo()"
          >
            Clear
          </div>
        </div>
      </div>
      <div class="bar-zoom">
        <button class="btn btn-light" title="Enable/Disable grid lines">
          <input type="checkbox" checked @change="onChangeGridLines($event)">
        </button>

        <button
          class="btn btn-light"
          title="Zoom-in Design"
          @click="zoomDesignInfo('in')"
        >
          <em class="bi bi-zoom-in" />
        </button>

        <button
          class="btn btn-light"
          title="Reset Zoom"
          @click="zoomDesignInfo('reset')"
        >
          <em class="bi bi-search" />
        </button>

        <button
          class="btn btn-light"
          title="Zoom-out Design"
          @click="zoomDesignInfo('out')"
        >
          <em class="bi bi-zoom-out" />
        </button>
      </div>
    </div>
    <ConfigureInstanceIdentifiersModal
      v-if="
        $store.state.modules.ConfigureIdentifiersInfoStore
          .instanceIdentifiersModal
      "
    />
    <ConfigureClassIdentifiersModal
      v-if="
        $store.state.modules.ConfigureIdentifiersInfoStore.classIdentifiersModal
      "
    />
    <ConfigureNodeEventInfoModal
      v-if="$store.state.modules.ConfigureNodeEventInfoStore.nodeEventInfoModal"
    />
    <ConfigureConnectorModal
      v-if="$store.state.modules.ConnectorStore.connectorModal"
    />
    <ConfigureImportEventsListModal
      v-if="$store.state.modules.ConfigureImportEventsListStore.eventsListModal"
    />
    <ImportDesignInfoModal
      v-if="$store.state.modules.DesignTestDataStore.showImportDesignModal"
    />
  </div>
</template>

<script>
import Vue from 'vue'
import Identifiers from '@/components/IdentifiersNode.vue'
import ParentIdentifiers from '@/components/ParentIdentifiersNode.vue'
import Events from '~/components/EventNode.vue'
import ConfigureNodeEventInfoModal from '@/components/ConfigureNodeEventInfoModal.vue'
import ConfigureConnectorModal from '~/components/ConfigureConnectorModal.vue'
import ConfigureInstanceIdentifiersModal from '~/components/ConfigureInstanceIdentifiersModal.vue'
import ConfigureClassIdentifiersModal from '~/components/ConfigureClassIdentifiersModal.vue'
import ConfigureImportEventsListModal from '~/components/ConfigureImportEventsListModal.vue'
import ImportDesignInfoModal from '~/components/ImportDesignInfoModal.vue'

export default {
  name: 'DesignTestDataCanvas',
  components: {
    ConfigureInstanceIdentifiersModal,
    ConfigureClassIdentifiersModal,
    ConfigureNodeEventInfoModal,
    ConfigureConnectorModal,
    ConfigureImportEventsListModal,
    ImportDesignInfoModal
  },
  data () {
    return {
      dragEventType: '',
      nodeType: '',
      selectedNodeInfo: {},
      allEventsInfoArray: [],
      connectorCounter: 1,
      dialogData: {},
      parentIdEventTypes: [
        'AggregationEvent',
        'AssociationEvent',
        'TransactionEvent'
      ],
      listNodes: [
        {
          ID: 1,
          type: 'Identifier',
          name: 'Identifiers',
          eventType: 'Identifiers',
          color: '#D98880',
          item: 'Identifiers',
          input: 0,
          output: 1
        },
        {
          ID: 2,
          type: 'Identifier',
          name: 'ParentID',
          eventType: 'Parent Identifier',
          color: '#C39BD3',
          item: 'ParentIdentifiers',
          input: 0,
          output: 1
        },
        {
          ID: 3,
          type: 'Event',
          name: 'ObjectEvent',
          eventType: 'Object Event',
          color: '#7FB3D5',
          item: 'Events',
          input: 1,
          output: 1
        },
        {
          ID: 4,
          type: 'Event',
          name: 'AggregationEvent',
          eventType: 'Aggregation Event',
          color: '#76D7C4',
          item: 'Events',
          input: 2,
          output: 1
        },
        {
          ID: 5,
          type: 'Event',
          name: 'TransactionEvent',
          eventType: 'Transaction Event',
          color: '#EDBB99',
          item: 'Events',
          input: 2,
          output: 1
        },
        {
          ID: 6,
          type: 'Event',
          name: 'TransformationEvent',
          eventType: 'Transformation Event',
          color: '#BFC9CA',
          item: 'Events',
          input: 2,
          output: 1
        },
        {
          ID: 7,
          type: 'Event',
          name: 'AssociationEvent',
          eventType: 'Association Event',
          color: '#F7DC6F',
          item: 'Events',
          input: 2,
          output: 1
        }
      ]
    }
  },
  watch: {
    '$store.state.modules.DesignTestDataStore.importedDesignData' (value) {
      if (
        value !== '' &&
        typeof value === 'string' &&
        value.startsWith('Unable')
      ) {
        this.$alertify.alert('Design template import failed', value)
      } else if (value !== '') {
        this.populateImportDesignData(value)
      }
    }
  },
  async mounted () {
    const vm = this
    const importedModule = await import('drawflow')
    const Drawflow = importedModule.default
    Vue.prototype.$df = new Drawflow(this.$refs.drawflow, Vue, this)

    this.$df.reroute = false
    this.$df.force_first_input = true
    this.$df.start()

    this.$df.registerNode('Identifiers', Identifiers, {}, {})
    this.$df.registerNode('ParentIdentifiers', ParentIdentifiers, {}, {})
    this.$df.registerNode('Events', Events, {}, {})

    // On Node addition add the info to nodeArray
    this.$df.on('nodeCreated', function (nodeId) {
      if (vm.nodeType === 'Event') {
        const nodeObj = {
          eventId:
            vm.$store.state.modules.ConfigureNodeEventInfoStore.nodeCounter,
          eventType: vm.$df.getNodeFromId(nodeId).data.eventType
        }

        // Add the respective created node information in nodeStore
        vm.$store.commit(
          'modules/ConfigureNodeEventInfoStore/populateNodeInfo',
          nodeObj
        )
      } else if (vm.nodeType === 'Identifier') {
        const identifierNodeObj = {
          identifiersId:
            vm.$store.state.modules.ConfigureNodeEventInfoStore.nodeCounter,
          identifierSyntax: 'URN'
        }

        // Add the respective created node information in nodeStore
        vm.$store.commit(
          'modules/ConfigureIdentifiersInfoStore/populateIdentifiersInfo',
          identifierNodeObj
        )
      }
    })

    // If the node is selected then get the respective node info
    this.$df.on('nodeSelected', function (nodeId) {
      const selectedNodeInfo = vm.$df.getNodeFromId(nodeId)
      vm.selectedNodeInfo.nodeType = selectedNodeInfo.html
      vm.selectedNodeInfo.nodeId = selectedNodeInfo.data.ID
      vm.selectedNodeInfo.eventType = selectedNodeInfo.data.eventType
    })

    // If Node is removed then delete the Node
    this.$df.on('nodeRemoved', function (nodeID) {
      if (vm.selectedNodeInfo.nodeType === 'Events') {
        // Remove the respective node information from Events Node
        vm.$store.commit(
          'modules/ConfigureNodeEventInfoStore/removeNodeEventInfo',
          vm.selectedNodeInfo.nodeId
        )
      } else if (
        vm.selectedNodeInfo.nodeType === 'Identifiers' ||
        vm.selectedNodeInfo.nodeType === 'ParentIdentifiers'
      ) {
        // Remove the respective node  from Identifiers Node
        vm.$store.commit(
          'modules/ConfigureIdentifiersInfoStore/removeIdentifiersInfo',
          vm.selectedNodeInfo.nodeId
        )
      }
    })

    // On Connector addition add the info to connectorArray with source and destination node
    this.$df.on('connectionCreated', function (connectObj) {
      // Check if connection is already present for same source and target Node
      const connectorArray = JSON.parse(
        JSON.stringify(vm.$store.state.modules.ConnectorStore.connectorArray)
      )
      const connect = connectorArray.find(
        con =>
          parseInt(con.source) === parseInt(connectObj.output_id) &&
          parseInt(con.target) === parseInt(connectObj.input_id)
      )

      // Cancel the connection from same Source Node to same Target Node
      if (connect !== undefined) {
        alert(
          'Connection already exists for the Nodes : ' +
            'Node : ' +
            connectObj.id_output +
            'and Node : ' +
            connectObj.id_input
        )
        vm.$df.removeSingleConnection(
          connectObj.id_output,
          connectObj.id_input,
          connectObj.output_class,
          connectObj.input_class
        )
      } else {
        // If connection do not exist then add the values to connectorArray
        const connectorObj = {}
        connectorObj.ID = vm.connectorCounter
        connectorObj.name = 'connector' + vm.connectorCounter
        connectorObj.source = connectObj.output_id
        connectorObj.target = connectObj.input_id
        connectorObj.hideInheritParentCount = false
        vm.connectorCounter++

        // Add connector information within the connector store for storing the EPC/Parent/Quantity
        vm.$store.commit(
          'modules/ConnectorStore/saveConnectorInfo',
          connectorObj
        )
      }
    })

    // If Connector is removed then delete the Connector from connectorArray
    this.$df.on('connectionRemoved', function (connectObj) {
      // Remove the connector information from the connector store for EPC/Parent/Quantity
      vm.$store.commit(
        'modules/ConnectorStore/populateCurrentConnector',
        connectObj
      )
      vm.$store.commit(
        'modules/ConnectorStore/removeConnectorInfo',
        connectObj
      )
    })

    // On selection of the Connector get show the modal for getting the information
    this.$df.on('connectionSelected', function (connectObj) {
      vm.connectObj = connectObj

      // After selection of connector based on the node to which its connected decide if ParentCount need to be displayed or not
      const connectorArray = JSON.parse(
        JSON.stringify(vm.$store.state.modules.ConnectorStore.connectorArray)
      )
      const connectorData = connectorArray.find(
        con =>
          parseInt(con.source) === parseInt(connectObj.output_id) &&
          parseInt(con.target) === parseInt(connectObj.input_id)
      )

      for (const eventNode in vm.$df.export().drawflow.Home.data) {
        const nodeInfo = vm.$df.export().drawflow.Home.data[eventNode]
        if (
          nodeInfo.name === 'Events' &&
          parseInt(nodeInfo.id) === parseInt(connectorData.source)
        ) {
          vm.$store.commit(
            'modules/ConnectorStore/populateHideInheritParentCount',
            true
          )
        }
      }
    })

    // On Double click on Connector, show the modal for getting the connector information and store it.
    this.$df.on('click', function (e) {
      if (e.detail === 2 && e.target.classList[0] === 'main-path') {
        // If the connector is clicked for the ParentIdentifiers then do not show the modal for epcCount and quantityCount
        if (
          vm.$df.getNodeFromId(vm.connectObj.output_id).name !==
          'ParentIdentifiers'
        ) {
          // After the population of the parent EPCs/Quantities show the modal with all information
          vm.$store.commit('modules/ConnectorStore/showConnectorModal')
          vm.$store.commit(
            'modules/ConnectorStore/populateCurrentConnector',
            vm.connectObj
          )
        }
      }
    })

    // During the import of events events-design after generating the required syntax call the method to design the nodes on drawflow
    this.$root.$on('drawSupplyChainDesign', () => {
      this.designSupplychainFromEvents()
    })

    // If user has passed query parameter along with the page URL then query for the provided url and obtain the data and load the design
    if (this.$route.query.url !== undefined && this.$route.query.url !== null) {
      this.$store.dispatch(
        'modules/DesignTestDataStore/obtainURLData',
        this.$route.query.url
      )
      this.$store.commit('modules/DesignTestDataStore/hideImportDesignModal')
    }
  },
  methods: {
    drag (event, eventType) {
      event.dataTransfer.setData(
        'node',
        event.target.getAttribute('data-node')
      )
      this.dragEventType = eventType
    },

    drop (event) {
      event.preventDefault()
      const data = event.dataTransfer.getData('node')
      this.addNodeToDrawFlow(data, event.clientX, event.clientY)
    },

    allowDrop (event) {
      event.preventDefault()
    },

    addNodeToDrawFlow (name, posX, posY) {
      // Find the position of the event
      posX =
        posX *
          (this.$df.precanvas.clientWidth /
            (this.$df.precanvas.clientWidth * this.$df.zoom)) -
        this.$df.precanvas.getBoundingClientRect().x *
          (this.$df.precanvas.clientWidth /
            (this.$df.precanvas.clientWidth * this.$df.zoom))
      posY =
        posY *
          (this.$df.precanvas.clientHeight /
            (this.$df.precanvas.clientHeight * this.$df.zoom)) -
        this.$df.precanvas.getBoundingClientRect().y *
          (this.$df.precanvas.clientHeight /
            (this.$df.precanvas.clientHeight * this.$df.zoom))

      const nodeSelected = this.listNodes.find(
        ele => ele.name === this.dragEventType
      )
      this.nodeType = nodeSelected.type

      // If type is Identifiers then add the node and increase identifierCode
      this.$df.addNode(
        name,
        nodeSelected.input,
        nodeSelected.output,
        posX,
        posY,
        nodeSelected.name,
        {
          ID: this.$store.state.modules.ConfigureNodeEventInfoStore.nodeCounter,
          eventType: this.dragEventType
        },
        name,
        'vue'
      )

      // Increment the value of the nodeCounter in store after adding the node to drawflow canvas for unique node value
      this.$store.commit(
        'modules/ConfigureNodeEventInfoStore/incrementNodeCounter'
      )
    },

    // On click of the Submit button build the relationship between the Nodes/Events based on Connection
    buildEventRelations (event) {
      // Remove the highlightNode class from all the nodes initially during the submit button click
      Array.from(document.querySelectorAll('.highlightNode')).forEach(el =>
        el.classList.remove('highlightNode')
      )

      // Populate the diagram info into the mutation in Vuex store
      this.$store.commit(
        'modules/RelationsBuilder/populateDiagramInfo',
        this.$df.export()
      )

      // Call the Vuex store action to perform the operations based on the diagram values
      this.$store.dispatch('modules/RelationsBuilder/buildRelations', {})

      // Check if there are any mismatch during the inheritance of identifiers count in Child event/node from Parent event/node.
      const identifiersInheritError = JSON.parse(
        JSON.stringify(
          this.$store.state.modules.RelationsBuilder.identifiersInheritError
        )
      )
      if (identifiersInheritError.length === 0) {
        this.eventGenerator()
      } else {
        // Add the class to all the nodes which have mismatch in identifiers count during the inheriting process.
        for (const errorNode in identifiersInheritError) {
          document
            .getElementById(
              'node-' + identifiersInheritError[errorNode].eventId
            )
            .classList.add('highlightNode')
        }

        // Display the alert message with all the text information
        this.$alertify.confirmWithTitle(
          'Test data generator warning',
          'Identifiers inherited by child event/node exceeds the identifiers available in parent event/node,<br/> would you still like to continue further?',
          () => this.eventGenerator()
        )
      }
    },

    eventGenerator () {
      // Call the method to prepare the JSON/InputTemplate based on design
      this.$store.dispatch('modules/ConfigureNodeEvents/jsonPreparation', {
        root: true
      })

      // Send the data to the Generate Test Data
      this.$store.commit(
        'modules/TestDataGeneratorStore/populateTestDataInput',
        JSON.stringify(
          this.$store.state.modules.ConfigureNodeEvents.testDataInputTemplate,
          null,
          4
        )
      )

      // Clear the previous output
      this.$store.commit(
        'modules/TestDataGeneratorStore/populateTestDataOutput',
        ''
      )

      // Call the action to convert the provided Test Data JSON input to Test Data events
      this.$router.push('/GenerateEvents')

      this.$store.dispatch('modules/TestDataGeneratorStore/testdataGenerator')
    },

    // On click of the Export button build JSON with information associated to EventNodes, IdentifiersNodes, Connector Information and Drawflow diagrams
    exportDesignInfo () {
      // Get all the information relevant to Event Nodes in diagram
      const eventNodeInfo = JSON.parse(
        JSON.stringify(
          this.$store.state.modules.ConfigureNodeEventInfoStore
            .nodeEventInfoArray
        )
      )

      // Get all the information relevant to the Identifiers Node in diagram
      const identifiersNodeInfo = JSON.parse(
        JSON.stringify(
          this.$store.state.modules.ConfigureIdentifiersInfoStore
            .identifiersArray
        )
      )

      // Get all the information relevant to Connectors in diagram
      const connectorsInfo = JSON.parse(
        JSON.stringify(this.$store.state.modules.ConnectorStore.connectorArray)
      )

      // Get the drawflow diagram information
      const drawflowInfo = this.$df.export()

      // Build a single JSON with all the above information
      const diagramExportData = {
        eventNodeInfo,
        identifiersNodeInfo,
        connectorsInfo,
        drawflowInfo
      }

      const DateTime = new Date()
        .toISOString()
        .replace('Z', '')
        .replace('T', '')
      const textFileAsBlob = new Blob(
        [JSON.stringify(diagramExportData, null, 4)],
        { type: 'text/json' }
      )
      const downloadLink = document.createElement('a')
      downloadLink.download =
        'Test_Data_Generator_Design_' + DateTime + '.json'
      downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob)
      downloadLink.click()
    },

    // On click of the import button import information associated with EventNodes, IdentifiersNodes, Connector Information and Drawflow diagram
    importDesignInfo () {
      const reader = new FileReader()
      this.file = this.$refs.readDrawflowInfo.files[0]
      this.$refs.readDrawflowInfo.value = null
      reader.onload = (res) => {
        // Parse the JSON to get the information related to each of the components in diagram such as EventNodes, IdentifiersNodes, Connector and Drawflow diagram
        const importData = JSON.parse(res.target.result)

        // Call the function to populate the data in respective variables
        this.populateImportDesignData(importData)
      }
      reader.onerror = (err) => {
        alert(
          'Error during the import of the TestData Generator Design : ' + err
        )
      }
      reader.readAsText(this.file)
    },

    // Function to populate the respective data in store and variables during the import of design info
    populateImportDesignData (importData) {
      // Populate identifiers node related information from import data
      if (importData.identifiersNodeInfo !== undefined) {
        this.$store.commit(
          'modules/ConfigureIdentifiersInfoStore/populateIdentifiersArray',
          importData.identifiersNodeInfo
        )
      }

      // Populate event node related information from the import data
      const finalNodeId =
        importData.drawflowInfo !== undefined
          ? Object.keys(importData.drawflowInfo.drawflow.Home.data).pop()
          : 1
      if (importData.eventNodeInfo !== undefined) {
        this.$store.commit(
          'modules/ConfigureNodeEventInfoStore/populateNodeEventInfoArray',
          { eventInfoArray: importData.eventNodeInfo, finalNodeId }
        )
      }

      // Populate connector related information from import data
      if (importData.connectorsInfo !== undefined) {
        this.$store.commit(
          'modules/ConnectorStore/populateConnectorArray',
          importData.connectorsInfo
        )
      }

      // populate the drawflow diagram with the diagram info
      if (importData.drawflowInfo !== undefined) {
        this.$df.import(importData.drawflowInfo)
      }
    },

    // On click of the Import Events button show the modal with text area to get the EPCIS events list information from the user
    importEventsList (event) {
      // Clear existing design from the page while importing the event
      this.$df.clear()

      // Toggle the flag to display the ConfigureImportEventsListModal
      this.$store.commit(
        'modules/ConfigureImportEventsListStore/showEventsListModal'
      )
    },

    // Call the method from ConfigureImportEventsListModal component after modifying the request to required format
    designSupplychainFromEvents () {
      // Clear existing design from the page while importing the event
      this.$df.clear()

      // Obtain all the events which are formatted according to front-end requirement
      const eventsList = JSON.parse(
        JSON.stringify(
          this.$store.state.modules.ConfigureNodeEventInfoStore
            .nodeEventInfoArray
        )
      )
      let newPosX = 0

      // Loop through all the eventsList and create the respective eventNode
      for (const event of eventsList) {
        const { width, height } = this.$df.container.getBoundingClientRect()
        const x = width / 2
        const y = height / 2

        let posX =
          x *
            (this.$df.precanvas.clientWidth /
              (this.$df.precanvas.clientWidth * this.$df.zoom)) -
          this.$df.precanvas.getBoundingClientRect().x *
            (this.$df.precanvas.clientWidth /
              (this.$df.precanvas.clientWidth * this.$df.zoom)) -
          97
        let posY =
          y *
            (this.$df.precanvas.clientHeight /
              (this.$df.precanvas.clientHeight * this.$df.zoom)) -
          this.$df.precanvas.getBoundingClientRect().y *
            (this.$df.precanvas.clientHeight /
              (this.$df.precanvas.clientHeight * this.$df.zoom)) -
          37

        posX = posX + newPosX
        posY = posY - 200
        newPosX = newPosX + 250

        const nodeSelected = this.listNodes.find(
          ele => ele.name === event.eventType
        )
        this.$df.addNode(
          'Events',
          nodeSelected.input,
          nodeSelected.output,
          posX,
          posY,
          nodeSelected.name,
          { ID: event.eventId, eventType: event.eventType },
          'Events',
          'vue'
        )
      }
    },
    // Function to clear all the design and reset the Drawflow
    clearDesignInfo () {
      this.$df.clear()
    },
    // Function to Zoom-in/out/reset the Drawflow design
    zoomDesignInfo (type) {
      // Based on the type of Zoom option clicked by user either zoom-out/zoom-in/reset the Drawflow design
      if (type === 'out') {
        this.$df.zoom_out()
      } else if (type === 'reset') {
        this.$df.zoom_reset()
      } else if (type === 'in') {
        this.$df.zoom_in()
      }
    },

    // Function to enable/disable the grid lines within the Drawflow
    onChangeGridLines (e) {
      // If false then remove gridlines class
      if (e.target.checked === false) {
        this.$refs.drawflow.classList.remove('gridImage')
      } else {
        // Else enable the grid lines
        this.$refs.drawflow.classList.add('gridImage')
      }
    },

    // Function to display modal and import the design from remote URL
    importInputTemplate (e) {
      this.$store.commit('modules/DesignTestDataStore/showImportDesignModal')
    }
  }
}
</script>

<style>
@import "@/assets/css/drawflow.css";
</style>
