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
package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

public class CompanyPrefixFormatter {

  public static GcpFormatter gcpFormatterWithReplace(String gcpInput, int gcpLength) {
    return new GcpFormatterWithReplace(gcpInput, gcpLength);
  }

  public static GcpFormatter gcpFormatterNormal(String gcpInput, int gcpLength) {
    return new GcpFormatterNormal(gcpInput, gcpLength);
  }

  public interface GcpFormatter {}

  // CompanyPrefix formatter by replacing the first character after decimal point
  @AllArgsConstructor
  private static final class GcpFormatterWithReplace implements GcpFormatter {
    @NotNull private String gcpInput;

    @Min(value = 6, message = "GCP Length cannot be less than 6")
    @Max(value = 12, message = "GCP Length cannot be more than 12")
    @NotNull
    private int gcpLength;

    @Override
    public String toString() {
      try {
        return gcpInput.substring(1, gcpLength + 1)
            + "."
            + gcpInput.charAt(0)
            + gcpInput.substring(gcpLength + 1, gcpInput.length() - 1);
      } catch (Exception ex) {
        throw new TestDataGeneratorException(
            "Exception occurred during formatting of the GCP with replace, Please check the values provided for GCP Length : "
                + ex.getMessage());
      }
    }
  }

  // CompanyPrefix Formatter Normal by using the GCP Prefix point
  @AllArgsConstructor
  private static final class GcpFormatterNormal implements GcpFormatter {

    @NotNull private String gcpInput;

    @Min(value = 6, message = "GCP Length cannot be less than 6")
    @Max(value = 12, message = "GCP Length cannot be more than 12")
    @NotNull
    private int gcpLength;

    @Override
    public String toString() {
      try {
        return gcpInput.substring(0, gcpLength)
            + "."
            + gcpInput.substring(gcpLength, gcpInput.length() - 1);
      } catch (Exception ex) {
        throw new TestDataGeneratorException(
            "Exception occurred during formatting of the GCP, Please check the values provided for GCP Length : "
                + ex.getMessage());
      }
    }
  }
}
