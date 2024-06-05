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

import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
import io.openepcis.testdata.generator.constants.ReadPointBizLocationType;
import io.openepcis.testdata.generator.constants.TestDataGeneratorException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class ReadpointBusinessLocationFormatter {

  private static final String URN_PREFIX = "urn:epc:id:sgln:";

  public static String format(final IdentifierVocabularyType syntax, final ReadPointBizLocationSyntax input, final String dlURL) {
    // If ReadPoint/BizLocation is provided using ManualURI then return the same
    if (input.getType() != null && input.getType().equals(ReadPointBizLocationType.MANUALLY) && input.getManualURI() != null) {
      return input.getManualURI();
    } else if(input.getType() != null && input.getType().equals(ReadPointBizLocationType.SGLN)) {
      // If ReadPoint/BizLocation is provided as an CBV based values then format them accordingly
      if (IdentifierVocabularyType.WEBURI == syntax) {
        return formatWebURI(input, dlURL);
      } else {
        return formatURN(input);
      }
    }
    return null;
  }

  private static String formatURN(final ReadPointBizLocationSyntax input) {
    try {
      var formattedLocation = "";

      if (input.getGln() != null) {
        String gln = input.getGln();
        gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
        gln = gln.substring(0, input.getGcpLength()) + "." + gln.substring(input.getGcpLength(), gln.length() - 1);
        formattedLocation = URN_PREFIX + gln + (input.getExtension() != null && !input.getExtension().isEmpty() ? ("." + input.getExtension()) : ".0");
      }
      return formattedLocation;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of Readpoint/BizLocation URN, Please check the values provided for ReadPoint/BizLocation :  "
              + ex.getMessage(), ex);
    }
  }

  private static String formatWebURI(final ReadPointBizLocationSyntax input, final String dlURL) {
    final String WEBURI_PREFIX = dlURL + "/414/";

    try {
      String gln = input.getGln();
      gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
      var formattedLocation = "";
      formattedLocation = WEBURI_PREFIX + gln + (input.getExtension() != null && !input.getExtension().isEmpty() ? ("/254/" + input.getExtension()) : "");
      return formattedLocation;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of Readpoint/BizLocation WebURI, Please check the values provided for ReadPoint/BizLocation : "
              + ex.getMessage(), ex);
    }
  }
}
