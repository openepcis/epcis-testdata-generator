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
  <div>
    <div class="row">
      <div class="col-md-5" />
      <div class="col-md-4">
        <h3>Generate Test Data</h3>
      </div>
    </div>
    <div class="row">
      <div class="col-md-1" />
      <div class="col-md-5">
        <div class="row">
          <div class="col-md-4" />
          <div class="col-md-4">
            <h5>Input Template</h5>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
            <div id="button-container">
              <button
                class="btn btn-primary utility-button"
                title="Sample Test Data Template"
                @click="getSampleFile()"
              >
                <em class="bi bi-file-earmark" />
              </button>
              <button
                class="btn btn-primary utility-button"
                title="Copy Test Data Template"
                @click="copyTestdataTemplate()"
              >
                <em class="bi bi-clipboard" />
              </button>
              <button class="btn btn-primary utility-button" title="Download Test Data Input Template" @click="downloadInputTemplate()">
                <em class="bi bi-file-arrow-down-fill" />
              </button>
              <span>
                <input
                  ref="testDataInputTemplate"
                  type="file"
                  hidden
                  accept=".txt, .json"
                  @change="readTemplateFile()"
                >
                <button
                  class="btn btn-primary utility-button"
                  title="Upload Test Data Template from Local"
                  @click="$refs.testDataInputTemplate.click()"
                >
                  <em class="bi bi-arrow-up-square-fill" />
                </button>
              </span>
              <button
                class="btn btn-primary utility-button"
                title="Clear Test Data Template"
                @click="clearTestDataInput()"
              >
                <em class="bi bi-trash-fill" />
              </button>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
            <textarea
              ref="testDataInput"
              :value="$store.state.modules.TestDataGeneratorStore.testDataInput"
              class="form-control"
              placeholder="Input Test Data Template"
              spellcheck="false"
              data-gramm="false"
            />
          </div>
        </div>
      </div>
      <div class="col-md-5">
        <div class="row">
          <div class="col-md-4" />
          <div class="col-md-4">
            <h5>Generated Events</h5>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
            <div id="button-container">
              <button
                class="btn btn-primary utility-button"
                title="Copy Generated Test Data"
                @click="copyGeneratedTestData()"
              >
                <em class="bi bi-clipboard" />
              </button>

              <button class="btn btn-primary utility-button" title="Download Test Data Events" @click="downloadEvents()">
                <em class="bi bi-file-arrow-down-fill" />
              </button>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-md-12">
            <textarea
              ref="testDataOutput"
              :value="$store.state.modules.TestDataGeneratorStore.testDataOutput"
              class="form-control"
              placeholder="Generated EPCIS Test data events"
              spellcheck="false"
              data-gramm="false"
            />
          </div>
        </div>
      </div>
      <div class="col-md-1" />
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import axios from 'axios'
import beautifier from 'js-beautify'
let CodeMirror = null

if (typeof window !== 'undefined' && typeof window.navigator !== 'undefined') {
  CodeMirror = require('codemirror')
  require('codemirror/mode/xml/xml.js')
  require('codemirror/mode/javascript/javascript.js')
  require('codemirror/lib/codemirror.css')
  require('codemirror/addon/display/autorefresh.js')
  require('codemirror/addon/lint/lint.js')
  require('codemirror/addon/lint/lint.css')
  require('codemirror/addon/lint/json-lint')
  require('codemirror/addon/lint/javascript-lint.js')
  require('codemirror/addon/hint/javascript-hint.js')
  window.jsonlint = require('jsonlint-mod')
}

export default {
  name: 'GenerateEvents',
  data () {
    return {
      vm: null,
      testDataInputEditor: null,
      testDataOutputEditor: null
    }
  },
  computed: mapState([
    'modules.TestDataGeneratorStore.testDataInput',
    'modules.TestDataGeneratorStore.testDataOutput'
  ]),
  watch: {
    '$store.state.modules.TestDataGeneratorStore.testDataInput' (value) {
      const vm = this
      if (value !== this.testDataInputEditor.getValue()) {
        this.testDataInputEditor.setValue(value)

        // Refresh the CodeMirror to reflect the changes
        setTimeout(function () {
          vm.testDataInputEditor.refresh()
        }, 100)
      }
    },
    '$store.state.modules.TestDataGeneratorStore.testDataOutput' (value) {
      if (value !== this.testDataOutputEditor.getValue()) {
        this.testDataOutputEditor.setValue(JSON.stringify(value, null, 4))
      }
    }
  },
  mounted () {
    // Add the CodeMirror styles to the TestData Generator input
    this.testDataInputEditor = CodeMirror.fromTextArea(this.$refs.testDataInput, {
      mode: 'applicaton/ld+json',
      beautify: { initialBeautify: true, autoBeautify: true },
      lineNumbers: true,
      indentWithTabs: true,
      autofocus: true,
      tabSize: 2,
      gutters: ['CodeMirror-lint-markers'],
      autoCloseBrackets: true,
      autoCloseTags: true,
      styleActiveLine: true,
      styleActiveSelected: true
    })

    // On change of TestData Generator input call the function to generate Test Data events
    this.testDataInputEditor.on('change', this.createTestData)

    // Set the height for the TestData Generator input CodeMirror
    this.testDataInputEditor.setSize(null, '75vh')

    // Add the CodeMirror styles to the TestData Generator output
    this.testDataOutputEditor = CodeMirror.fromTextArea(this.$refs.testDataOutput, {
      mode: 'applicaton/ld+json',
      beautify: { initialBeautify: true, autoBeautify: true },
      lineNumbers: true,
      indentWithTabs: true,
      autofocus: true,
      tabSize: 2,
      gutters: ['CodeMirror-lint-markers'],
      autoCloseBrackets: true,
      autoCloseTags: true,
      styleActiveLine: true,
      styleActiveSelected: true
      // readOnly: 'nocursor'
    })

    // Set the height for the TestData Generator output CodeMirror
    this.testDataOutputEditor.setSize(null, '75vh')

    // Add the border for all the CodeMirror textarea
    for (const s of document.getElementsByClassName('CodeMirror')) {
      s.style.border = '1px solid black'
    }
  },
  methods: {
    ...mapActions(['modules/TestDataGeneratorStore/testdataGenerator']),

    // Function to create the test data on receiving the input in the textarea
    createTestData () {
      if (this.$store.state.modules.TestDataGeneratorStore.testDataInput !== this.testDataInputEditor.getValue()) {
        this.$store.commit('modules/TestDataGeneratorStore/populateTestDataInput', this.testDataInputEditor.getValue())
        this.$store.dispatch('modules/TestDataGeneratorStore/testdataGenerator')
      }
    },

    // Function to get the sample file from project and create test data
    getSampleFile () {
      axios.get('SampleEvents/SampleInputTemplate.json').then((response) => {
        this.$store.commit('modules/TestDataGeneratorStore/populateTestDataInput', JSON.stringify(response.data, undefined, '\t'))
        this.$store.dispatch('modules/TestDataGeneratorStore/testdataGenerator')
      })
    },

    // Function to copy the test data template
    copyTestdataTemplate () {
      if (this.$store.state.modules.TestDataGeneratorStore.testDataInput !== '' && this.testDataInputEditor !== null) {
        // If the website is secure then copy to clipboard directly
        if (window.isSecureContext && navigator.clipboard) {
          this.testDataInputEditor.execCommand('selectAll')
          navigator.clipboard.writeText(this.testDataInputEditor.getValue())
        } else {
          // If the website is not secure and in development then copy to clipboard using JS style
          this.unsecuredCopyToClipboard(this.testDataInputEditor.getValue())
        }
      }
    },

    // Function to download the test data input template
    downloadInputTemplate () {
      if (this.$store.state.modules.TestDataGeneratorStore.testDataInput !== '') {
        const DateTime = new Date().toISOString().replace('Z', '').replace('T', '')
        const textFileAsBlob = new Blob([JSON.stringify(JSON.parse(this.$store.state.modules.TestDataGeneratorStore.testDataInput), null, 4)], { type: 'text/json' })
        const downloadLink = document.createElement('a')
        downloadLink.download = 'Test_Data_Generator_Input_' + DateTime + '.json'
        downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob)
        downloadLink.click()
      }
    },

    // Function to copy the generated Test data to clipboard
    copyGeneratedTestData () {
      if (this.$store.state.modules.TestDataGeneratorStore.testDataOutput !== '' && this.testDataOutputEditor !== null) {
        // If the website is secure then copy to clipboard directly
        if (window.isSecureContext && navigator.clipboard) {
          this.testDataOutputEditor.execCommand('selectAll')
          navigator.clipboard.writeText(this.testDataOutputEditor.getValue())
        } else {
          // If the website is not secure and in development then copy to clipboard using JS style
          this.unsecuredCopyToClipboard(this.testDataOutputEditor.getValue())
        }
      }
    },

    // Function to copy the value to clipboard using the textarea used during the development
    unsecuredCopyToClipboard (text) {
      const copyTextArea = document.createElement('textarea')
      copyTextArea.value = text
      document.body.appendChild(copyTextArea)
      copyTextArea.focus()
      copyTextArea.select()
      try {
        document.execCommand('copy')
      } catch (err) {
        console.error('Unable to copy to clipboard', err)
      }
      document.body.removeChild(copyTextArea)
    },

    readTemplateFile () {
      this.$store.commit('modules/TestDataGeneratorStore/populateTestDataOutput', '')
      const reader = new FileReader()
      this.file = this.$refs.testDataInputTemplate.files[0]
      this.$refs.testDataInputTemplate.value = null
      reader.onload = (res) => {
        this.testDataInputEditor.setValue(beautifier.js_beautify(res.target.result, { indent_size: 2 }))
        this.createTestData()
      }
      reader.onerror = (err) => {
        this.$store.commit('modules/TestDataGeneratorStore/populateTestDataInput', err)
      }
      reader.readAsText(this.file)
    },

    // Clear the EPCIS test data input
    clearTestDataInput () {
      if (this.$store.state.modules.TestDataGeneratorStore.testDataInput !== '') {
        this.testDataInputEditor.setValue('')
        this.testDataOutputEditor.setValue('')
        this.resetCodeMirrror()
      }
    },

    // Download the generated Test Data events
    downloadEvents () {
      if (this.$store.state.modules.TestDataGeneratorStore.testDataOutput !== '') {
        const DateTime = new Date().toISOString().replace('Z', '').replace('T', '')
        const textFileAsBlob = new Blob([JSON.stringify(this.$store.state.modules.TestDataGeneratorStore.testDataOutput, null, 4)], { type: 'text/json' })
        const downloadLink = document.createElement('a')
        downloadLink.download = 'Test_Data_Generator_Output_' + DateTime + '.json'
        downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob)
        downloadLink.click()
      }
    },

    // Based on User selection reset the value for the CodeMirror to Null
    resetCodeMirrror () {
      // Remove the CodeMirror formatting for XML Textarea and make it normal Textarea
      if (this.testDataInputEditor != null) {
        this.testDataInputEditor.setValue('')
      }

      // Remove the CodeMirror formatting for JSON-LD Textarea and make it normal Textarea
      if (this.testDataOutputEditor != null) {
        this.testDataOutputEditor.setValue('')
      }
    }
  }
}
</script>

<style scoped>
::-webkit-input-placeholder {
  color: #f1948a;
  text-align: center;
  line-height: 700px;
}

textarea {
  height: 70vh;
}

#button-container {
  display: flex;
}

.utility-button {
  margin-right: 10px;
  margin-bottom: 5px;
}

h6,h3{
  margin-top: 2%;
  color: black;
}
</style>
