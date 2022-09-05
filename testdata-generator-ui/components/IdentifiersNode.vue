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
  <div class="identifiersContainer">
    <div class="identifiersNode text-center">
      Identifier
    </div>
    <div class="nodeContainer">
      <div style="font-size:12px;text-align:center;">
        <input
          :id="`identifierTypeURN-${ID}`"
          value="URN"
          type="radio"
          :name="`instanceIdentifier-${ID}`"
          :checked="identifierSyntax === 'URN'"
          @input="instanceIdentifiersSyntaxChange($event, 'URN')"
        >
        <label class="radio-inline control-label" :for="`identifierTypeURN-${ID}`">URN</label>
        <input
          :id="`identifierTypeWebURI-${ID}`"
          value="WebURI"
          type="radio"
          :name="`instanceIdentifier-${ID}`"
          :checked="identifierSyntax === 'WebURI'"
          @input="instanceIdentifiersSyntaxChange($event, 'WebURI')"
        >
        <label class="radio-inline control-label" :for="`identifierTypeWebURI-${ID}`">WebURI</label>
      </div>

      <div class="col text-center" style="">
        <button
          ref="Btn"
          class="btn btn-info"
          df-ID
          :value="ID"
          title="Add Instance Identifiers"
          @click="instanceIdentifiersModal(ID)"
        >
          <em class="bi bi-pencil-square" /> EPC List
        </button>
        <pre>{{ instanceIdentifierType }}</pre>
        <button
          ref="Btn"
          class="btn btn-info"
          df-ID
          :value="ID"
          title="Add Class Identifiers"
          @click="classIdentifiersModal(ID)"
        >
          <em class="bi bi-pencil-square" /> Class List
        </button>
        <pre>{{ classIdentifierType }}</pre>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  data () {
    return {
      ID: '',
      eventCount: '',
      bizStep: '',
      allNodeInfo: [],
      identifierSyntax: '',
      instanceIdentifierType: '',
      classIdentifierType: ''
    }
  },
  mounted () {
    this.$nextTick(() => {
      const id = this.$el.parentElement.parentElement.id
      const data = this.$df.getNodeFromId(id.slice(5))
      this.ID = data.data.ID

      this.allNodeInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray, null, 4))
      const identifierNode = this.allNodeInfo.find(node => parseInt(node.identifiersId) === parseInt(this.ID))

      if (identifierNode !== undefined) {
        this.identifierSyntax = identifierNode.identifierSyntax

        if (identifierNode.instanceData !== undefined) {
          this.instanceIdentifierType = identifierNode.instanceData.identifierType
        }
      }

      // Watch for the changes on the identifiers array if the values are changed then add the respective information onto the identifiers node
      this.$watch('$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray', (val) => {
        const currentIdentifiersNode = val.find(node => node.identifiersId === this.ID)

        if (currentIdentifiersNode !== undefined && currentIdentifiersNode !== null) {
          this.instanceIdentifierType = currentIdentifiersNode.instanceData !== undefined && currentIdentifiersNode.instanceData !== null ? currentIdentifiersNode.instanceData.identifierType.toUpperCase() : ''
          this.classIdentifierType = currentIdentifiersNode.classData !== undefined && currentIdentifiersNode.classData !== null ? currentIdentifiersNode.classData.identifierType.toUpperCase() : ''

          // Update the connections so it is always attached to the port
          this.$df.updateConnectionNodes('node-' + this.ID)
        }
      }, { immediate: true, deep: true })
    })
  },
  methods: {
    // On click of the button show the modal
    instanceIdentifiersModal (nodeId) {
      // Store the current IdentifiersNode information based on the click of the respective Identifiers node
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/populateCurrentIdentifierInfo', { identifierType: 'identifier', nodeId })

      // Store the CurrentNodeid so it can be tracked which node is clicked
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/populateCurrentNodeId', nodeId)

      // Show the Instance identifiers modal and obtain the respective information
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/showInstanceIdentifiersModal')
    },

    // On click of the button show class identifiers modal
    classIdentifiersModal (nodeId) {
      // Store the current IdentifiersNode information based on the click of the respective Identifiers node
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/populateCurrentIdentifierInfo', { identifierType: 'identifier', nodeId })

      // Store the CurrentNodeid so it can be tracked which node is clicked
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/populateCurrentNodeId', nodeId)

      // Show the Class identifiers modal and obtain the respective information
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/showClassIdentifiersModal')
    },

    // On change of the IdentifierSyntax change, change the value in the respective node info
    instanceIdentifiersSyntaxChange (event, syntaxValue) {
      // Change the value based on changes
      this.identifierSyntax = syntaxValue
      // Change the value of the respective syntax within the Node information in IdentifiersNode array
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/identifiersSyntaxChange', { nodeId: this.ID, syntaxValue })
    }
  }
}
</script>

<style>
</style>
