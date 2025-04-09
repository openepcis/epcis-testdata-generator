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
package io.openepcis.testdata.generator.format;

import io.openepcis.model.epcis.BizTransactionList;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class BizTransactionsFormatter {

  public static BizTransactionList format(final BizTransactionList input) {
    try {
      final var bizFormatted = new BizTransactionList();
      bizFormatted.setType(input.getType());
      bizFormatted.setBizTransaction(input.getBizTransaction());
      return bizFormatted;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of BizTransaction, Please check the values provided for BizTransactions : "
              + ex.getMessage(),
          ex);
    }
  }
}
