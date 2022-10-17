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
export default {
  ssr: false,
  router: {
    base: '/ui/'
  },
  // Global page headers: https://go.nuxtjs.dev/config-head
  head: {
    title: "OpenEPCIS | Test Data Generator",
    htmlAttrs: {
      lang: "en"
    },
    meta: [
      { charset: "utf-8" },
      { name: "viewport", content: "width=device-width, initial-scale=1" },
      { hid: "description", name: "description", content: "" },
      { name: "format-detection", content: "telephone=no" }
    ],
    script: [],
    link: [
      { rel: "icon", type: "image/x-icon", href: "/Logo.ico" },
      {
        rel: "stylesheet",
        href: "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.3.0/font/bootstrap-icons.css"
      },
      {
        rel: "stylesheet",
        href: "https://unpkg.com/vue-multiselect@2.1.0/dist/vue-multiselect.min.css"
      }
    ]
  },

  // Global CSS: https://go.nuxtjs.dev/config-css
  css: ["@/assets/css/styles.css"],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
    { src: "~/plugins/element-ui", mode:"client" },
    { src: "~/plugins/alert", mode:"client" }
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [
    // https://go.nuxtjs.dev/eslint
    [
      "@nuxtjs/eslint-module",
      {
        fix: true
      }
    ],
    ["@nuxtjs/dotenv"]
  ],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: ["@nuxtjs/axios", "bootstrap-vue/nuxt"],

  // Axios module configuration: https://go.nuxtjs.dev/config-axios
  axios: {
    baseURL: process.env.API_URL,
    headers: {
      "Content-Type": "text/plain"
    }
  },

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {
    transpile: ["drawflow"]
  },

  server: {
    host: '0.0.0.0',
    port: 5000
  },

  vue: {
    config: {
      productionTip: false,
      devtools: true
    }
  }
};
