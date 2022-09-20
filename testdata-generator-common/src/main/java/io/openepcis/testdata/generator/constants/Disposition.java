/*
 * Copyright 2022 benelog GmbH & Co. KG
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
public enum Disposition {
  NULL(null),
  ACTIVE("active"),
  AVAILABLE("available"),
  COMPLETENESS_VERIFIED("completeness_verified"),
  COMPLETENESS_INFERRED("completeness_inferred"),
  CONFORMANT("conformant"),
  CONTAINER_CLOSED("container_closed"),
  CONTAINER_OPEN("container_open"),
  DAMAGED("damaged"),
  DESTROYED("destroyed"),
  DISPENSED("dispensed"),
  DISPOSED("disposed"),
  ENCODED("encoded"),
  EXPIRED("expired"),
  IN_PROGRESS("in_progress"),
  IN_TRANSIT("in_transit"),
  INACTIVE("inactive"),
  MISMATCH_INSTANCE("mismatch_instance"),
  MISMATCH_CLASS("mismatch_class"),
  MISMATCH_QUANTITY("mismatch_quantity"),
  NEEDS_REPLACEMENT("needs_replacement"),
  NON_CONFORMANT("non_conformant"),
  NON_SELLABLE_OTHER("non_sellable_other"),
  PARTIALLY_DISPENSED("partially_dispensed"),
  RECALLED("recalled"),
  RESERVED("reserved"),
  RETAIL_SOLD("retail_sold"),
  RETURNED("returned"),
  SELLABLE_ACCESSIBLE("sellable_accessible"),
  SELLABLE_NOT_ACCESSIBLE("sellable_not_accessible"),
  STOLEN("stolen"),
  UNAVAILABLE("unavailable"),
  UNKNOWN("unknown"),
  USER_SPECIFIC("DispositionEnter");

  private final String cbvDisposition;

  Disposition(String disposition) {
    this.cbvDisposition = disposition;
  }

  @Override
  public String toString() {
    return cbvDisposition;
  }
}
