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
    <header id="header" class="d-flex align-items-center">
      <div class="container d-flex align-items-center justify-content-between">
        <h1 class="logo">
          <router-link to="/">
            <a class="logo"><img src="../assets/img/Logo.png" alt=""></a>
          </router-link>
        </h1>
        <nav id="navbar" class="navbar">
          <ul>
            <li>
              <nuxt-link class="nav-link" to="/" active-class="active" exact>
                <em class="bi bi-house-fill icon" />
                Home
              </nuxt-link>
            </li>
            <li>
              <nuxt-link class="nav-link" to="/CreateTestData" active-class="active">
                <em class="bi bi-pencil-fill" />
                Create Test Data
              </nuxt-link>
            </li>
            <li>
              <nuxt-link class="nav-link" to="/DesignTestData" active-class="active">
                <em class="bi bi-brush-fill" />
                Design Test Data
              </nuxt-link>
            </li>

            <li>
              <nuxt-link class="nav-link" to="/GenerateEvents" active-class="active">
                <em class="bi bi-gear-fill icon" />
                Generate Events
              </nuxt-link>
            </li>
          </ul>
          <em class="bi bi-list mobile-nav-toggle" />
        </nav>
      </div>
    </header>
    <section id="topbar" class="d-flex align-items-center" />
  </div>
</template>

<script>
export default {
  name: 'OpenEpcisHeader',
  data () {
    return {
    }
  }
}
</script>

<style scoped>
.router-link-active{
  color: #106eea;
}

.router-link-exact-active{
  color: #106eea;
}

.icon{
  margin-right: 3%;
}
</style>
