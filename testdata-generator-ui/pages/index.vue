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
    <main id="main">
      <section id="featured-services" class="featured-services">
        <div class="container" data-aos="fade-up">
          <div class="row">
            <div class="col-md-6 col-lg-3 d-flex align-items-stretch mb-5 mb-lg-0">
              <router-link to="/CreateTestData">
                <div class="icon-box" data-aos="fade-up" data-aos-delay="300">
                  <div class="icon">
                    <em class="bi bi-pencil-fill" />
                  </div>
                  <h4 class="title">
                    <a>Create Test Data</a>
                  </h4>
                  <p class="description">
                    Generate test data events quickly by providing required information.
                  </p>
                </div>
              </router-link>
            </div>
            <div class="col-lg-1" />
            <div class="col-md-6 col-lg-3 d-flex align-items-stretch mb-5 mb-lg-0">
              <router-link to="/DesignTestData">
                <div class="icon-box" data-aos="fade-up" data-aos-delay="300">
                  <div class="icon">
                    <em class="bi bi-brush-fill" />
                  </div>
                  <h4 class="title">
                    <a>Design Test Data</a>
                  </h4>
                  <p class="description">
                    Design/Import supply chain and generate events using detailed information.
                  </p>
                </div>
              </router-link>
            </div>
            <div class="col-lg-1" />
            <div class="col-md-6 col-lg-3 d-flex align-items-stretch mb-5 mb-lg-0">
              <router-link to="/GenerateEvents">
                <div class="icon-box" data-aos="fade-up" data-aos-delay="200">
                  <div class="icon">
                    <em class="bi bi-gear-fill" />
                  </div>
                  <h4 class="title">
                    <a>Generate Test Data</a>
                  </h4>
                  <p class="description">
                    Generate EPCIS test data using the existing JSON input template.
                  </p>
                </div>
              </router-link>
            </div>
          </div>
          <div class="row d-flex">
            <div class="footerBar">
              <p>
                Copyright © 2023 <a href="https://openepcis.io/" target="_blank">OpenEPCIS.io </a> - All Rights Reserved.
              </p>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
export default {
  name: 'OpenEpcisHome'
}
</script>

<style scoped>
a{
  color: black;
}

router-link{
  text-decoration: none;
}

.footerBar {
  text-align: center;
  position: absolute;
  bottom: 0px;
  width: 60%;
  margin: auto;
}
</style>
