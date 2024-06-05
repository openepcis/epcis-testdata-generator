/*
 * Copyright 2022-2024 benelog GmbH & Co. KG
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
public enum QuantityUOM {
  NULL("null"),
  MASS("Mass"),
  KGM("KGM"),
  KTN("KTN"),
  LTN("LTN"),
  MEGAGRAM("Megagram"),
  TNE("TNE"),
  STN("STN"),
  DTN("DTN"),
  STI("STI"),
  LBR("LBR"),
  HGM("HGM"),
  ONZ("ONZ"),
  DJ("DJ"),
  APZ("APZ"),
  GRM("GRM"),
  DG("DG"),
  CGM("CGM"),
  MGM("MGM"),
  MC("MC"),
  F13("F13"),
  CWI("CWI"),
  CWA("CWA"),
  PFUND("Pfund"),
  NET_KILOGRAM("58"),
  DRA("DRA"),
  DRI("DRI"),
  E4("E4"),
  GRN("GRN"),
  PN("PN"),
  DWT("DWT"),
  LENGTH("Length"),
  MTR("MTR"),
  A11("A11"),
  A71("A71"),
  C45("C45"),
  MICROMETER("4H"),
  A12("A12"),
  DMT("DMT"),
  CMT("CMT"),
  MMT("MMT"),
  INH("INH"),
  FOT("FOT"),
  YRD("YRD"),
  NMI("NMI"),
  A45("A45"),
  HMT("HMT"),
  KMT("KMT"),
  B57("B57"),
  AK("AK"),
  M50("M50"),
  M49("M49"),
  X1("X1"),
  M51("M51"),
  HL("HL"),
  LF("LF"),
  LK("LK"),
  LM("LM"),
  SMI("SMI");

  private final String uom;

  QuantityUOM(String uom) {
    this.uom = uom;
  }

  @Override
  public String toString() {
    return uom;
  }
}
