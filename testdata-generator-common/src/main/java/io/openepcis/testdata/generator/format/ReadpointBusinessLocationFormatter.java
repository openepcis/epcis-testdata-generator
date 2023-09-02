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
package io.openepcis.testdata.generator.format;

import io.openepcis.testdata.generator.constants.IdentifierVocabularyType;
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
  private static final String WEBURI_PREFIX = "https://id.gs1.org/414/";

  public static String format(
      final IdentifierVocabularyType syntax, final ReadPointBizLocationSyntax input) {
    // If ReadPoint/BizLocation is provided using ManualURI then return the same as
    // ReadPoint/BizLocation
    if (input.getManualURI() != null) {
      return input.getManualURI();
    } else {
      // If ReadPoint/Biz location is provided as an CBV based values then format them accordingly
      // based on URN/WebURI
      if (IdentifierVocabularyType.WEBURI == syntax) {
        return formatWebURI(input);
      } else {
        return formatURN(input);
      }
    }
  }

  private static String formatURN(final ReadPointBizLocationSyntax input) {
    try {
      var formattedLocation = "";

      if (input.getGln() != null) {
        String gln = input.getGln();
        gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
        gln =
            gln.substring(0, input.getGcpLength())
                + "."
                + gln.substring(input.getGcpLength(), gln.length() - 1);

        // Based on the type of extension add the extension to ReadPoint/BizLocation
        if (input.getExtensionType() != null
            && input.getExtensionType().equalsIgnoreCase("static")) {
          // For static extension type, format the extension and return the gln with extension
          formattedLocation =
              URN_PREFIX
                  + gln
                  + (input.getExtension() != null ? ("." + input.getExtension()) : ".0");
        } else if (input.getExtensionType() != null
            && input.getExtensionType().equalsIgnoreCase("dynamic")) {
          // for dynamic extension format the extension, update the extension value and return the
          // gln with dynamic extension from value

          // If user has provided desired extension format for the dynamic ReadPoint/BizLocation
          // then format according to it if not then only add the dynamic extension
          formattedLocation =
              URN_PREFIX
                  + gln
                  + (input.getExtensionFrom() != null
                      ? ("."
                          + extensionFormatter(
                              input.getExtensionFrom(), input.getExtensionFormat()))
                      : "");
          input.setExtensionFrom(input.getExtensionFrom() + 1);
        }
      }
      return formattedLocation;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of Readpoint/BizLocation URN, Please check the values provided for ReadPoint/BizLocation :  "
              + ex.getMessage());
    }
  }

  private static String formatWebURI(final ReadPointBizLocationSyntax input) {
    try {
      String gln = input.getGln();
      gln = gln.substring(0, 12) + UPCEANLogicImpl.calcChecksum(gln.substring(0, 12));
      var formattedLocation = "";

      // Based on the type of extension add the extension to ReadPoint/BizLocation
      if (input.getExtensionType() != null && input.getExtensionType().equalsIgnoreCase("static")) {
        formattedLocation =
            WEBURI_PREFIX
                + gln
                + (input.getExtension() != null ? ("/254/" + input.getExtension()) : "");
      } else if (input.getExtensionType() != null
          && input.getExtensionType().equalsIgnoreCase("dynamic")) {
        // for dynamic extension format the extension, update the extension value and return the gln
        // with dynamic extension from value
        formattedLocation =
            WEBURI_PREFIX
                + gln
                + (input.getExtensionFrom() != null
                    ? ("/254/"
                        + extensionFormatter(input.getExtensionFrom(), input.getExtensionFormat()))
                    : "");
        input.setExtensionFrom(input.getExtensionFrom() + 1);
      }
      return formattedLocation;
    } catch (Exception ex) {
      throw new TestDataGeneratorException(
          "Exception occurred during formatting of Readpoint/BizLocation WebURI, Please check the values provided for ReadPoint/BizLocation : "
              + ex.getMessage());
    }
  }

  // Method to format the dynamic extension based on the user provided formatter
  private static String extensionFormatter(final Integer input, final String extensionFormat) {
    // If extension formatter contains the formatting value then based on it extension value is
    // formatted else return the dynamic extension value
    if (extensionFormat != null && !extensionFormat.equalsIgnoreCase("")) {
      return String.format(extensionFormat, input);
    } else {
      return input.toString();
    }
  }
}
