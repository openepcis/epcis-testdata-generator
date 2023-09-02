/*
 * Copyright 2022-2023 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.testdata.generator.constants;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum BusinessStep {
  NULL(null),
  ACCEPTING("accepting"),
  ARRIVING("arriving"),
  ASSEMBLING("assembling"),
  COLLECTING("collecting"),
  COMMISSIONING("commissioning"),
  CONSIGNING("consigning"),
  CREATING_CLASS_INSTANCE("creating_class_instance"),
  CYCLE_COUNTING("cycle_counting"),
  DECOMMISSIONING("decommissioning"),
  DEPARTING("departing"),
  DESTROYING("destroying"),
  DISASSEMBLING("disassembling"),
  DISPENSING("dispensing"),
  ENCODING("encoding"),
  ENTERING_EXITING("entering_exiting"),
  HOLDING("holding"),
  INSPECTING("inspecting"),
  INSTALLING("installing"),
  KILLING("killing"),
  LOADING("loading"),
  OTHER("other"),
  PACKING("packing"),
  PICKING("picking"),
  RECEIVING("receiving"),
  REMOVING("removing"),
  REPACKAGING("repackaging"),
  REPAIRING("repairing"),
  REPLACING("replacing"),
  RESERVING("reserving"),
  RETAIL_SELLING("retail_selling"),
  SAMPLING("sampling"),
  SENSOR_REPORTING("sensor_reporting"),
  SHIPPING("shipping"),
  STAGING_OUTBOUND("staging_outbound"),
  STOCK_TAKING("stock_taking"),
  STOCKING("stocking"),
  STORING("storing"),
  TRANSPORTING("transporting"),
  UNLOADING("unloading"),
  UNPACKING("unpacking"),
  VOID_SHIPPING("void_shipping"),
  USER_SPECIFIC("");

  private final String cbvBusinessStep;

  BusinessStep(String businessStep) {
    this.cbvBusinessStep = businessStep;
  }

  @Override
  public String toString() {
    return this.cbvBusinessStep;
  }
}
