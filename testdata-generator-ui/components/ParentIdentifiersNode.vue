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
    <div class="parentIdentifiersNode text-center">
      Parent Identifier
    </div>
    <div class="nodeContainer">
      <div style="font-size:12px;text-align:center;">
        <input
          :id="`identifierTypeURN-${ID}`"
          :data="parentIdentifierSyntax"
          value="URN"
          type="radio"
          :name="`instanceIdentifier-${ID}`"
          :checked="parentIdentifierSyntax === 'URN'"
          @input="parentIdentifierSyntaxChange($event, 'URN')"
        >
        <label class="radio-inline control-label" :for="`identifierTypeURN-${ID}`">URN</label>
        <input
          :id="`identifierTypeWebURI-${ID}`"
          :data="parentIdentifierSyntax"
          value="WebURI"
          type="radio"
          :name="`instanceIdentifier-${ID}`"
          :checked="parentIdentifierSyntax === 'WebURI'"
          @input="parentIdentifierSyntaxChange($event, 'WebURI')"
        >
        <label class="radio-inline control-label" :for="`identifierTypeWebURI-${ID}`">WebURI</label>
      </div>

      <div class="col text-center">
        <button
          ref="Btn"
          class="btn btn-info rounded-pill"
          df-ID
          :value="ID"
          title="Add Parent Identifiers"
          @click="parentIdentifiersModal(ID)"
        >
          <em class="bi bi-plus-circle" /> Parent ID
        </button>
        <pre>{{ parentIdentifierType }}</pre>
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
      parentIdentifierSyntax: '',
      parentIdentifierType: ''
    }
  },
  mounted () {
    this.$nextTick(() => {
      const id = this.$el.parentElement.parentElement.id
      const data = this.$df.getNodeFromId(id.slice(5))
      this.ID = data.data.ID
      this.allNodeInfo = JSON.parse(JSON.stringify(this.$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray, null, 4))
      const identifierNode = this.allNodeInfo.find(node => node.identifiersId === this.ID)

      if (identifierNode !== undefined) {
        this.parentIdentifierSyntax = identifierNode.identifierSyntax
      }

      // Watch for the changes on the identifiers array if the values are changed then add the respective information onto the identifiers node
      this.$watch('$store.state.modules.ConfigureIdentifiersInfoStore.identifiersArray', (val) => {
        const currentIdentifiersNode = val.find(node => node.identifiersId === this.ID)

        if (currentIdentifiersNode !== undefined && currentIdentifiersNode !== null) {
          this.parentIdentifierType = currentIdentifiersNode.parentData !== undefined ? currentIdentifiersNode.parentData.identifierType.toUpperCase() : ''

          // Update the connections so it is always attached to the port
          this.$df.updateConnectionNodes('node-' + this.ID)
        }
      }, { immediate: true, deep: true })
    })
  },
  methods: {
    // On click of the button show the Instance Identifiers Modal
    parentIdentifiersModal (nodeId) {
      // Store the current Parent IdentifiersNode information based on the click of the respective Parent Identifiers node
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/populateCurrentIdentifierInfo', { identifierType: 'parentIdentifier', nodeId })

      // Set the parentFlag in store to indicate the information provided is for ParentID
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/parentIdentifierFlagToggle')

      // Store the CurrentNodeid so it can be tracked which node is clicked
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/populateCurrentNodeId', nodeId)

      // Show the Instance/Parent identifiers modal and obtain the respective information
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/showInstanceIdentifiersModal')
    },
    // On change of the IdentifierSyntax change, change the value in the respective node info
    parentIdentifierSyntaxChange (event, syntaxValue) {
      // Change the value based on changes
      this.parentIdentifierSyntax = syntaxValue
      // Change the value of the respective syntax within the Node information in IdentifiersNode array
      this.$store.commit('modules/ConfigureIdentifiersInfoStore/identifiersSyntaxChange', { nodeId: this.ID, syntaxValue })
    }
  }
}
</script>

<style>
</style>
